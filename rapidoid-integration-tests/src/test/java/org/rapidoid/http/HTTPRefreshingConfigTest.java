package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.Conf;
import org.rapidoid.config.ConfigChanges;
import org.rapidoid.data.JSON;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.setup.App;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HTTPRefreshingConfigTest extends IsolatedIntegrationTest {

	@Test
	public void testRefreshingConfig() {
		String cfgDir = createTempDir("cfgDir-test");

		Conf.reset();
		Conf.setPath(cfgDir);

		App.registerConfigListeners();

		List<ConfigChanges> rootChanges = Coll.synchronizedList();
		List<ConfigChanges> apiChanges = Coll.synchronizedList();

		Conf.ROOT.addChangeListener(changes -> {
			U.print("******** ", changes);

			if (changes.initial) {
				eq(changes.added, Conf.ROOT.toMap());
			} else {
				rootChanges.add(changes);
			}
		});

		Conf.API.addChangeListener(changes -> {
			U.print("*** API  ", changes);

			if (changes.initial) {
				eq(changes.added, Conf.API.toMap());
			} else {
				apiChanges.add(changes);
			}

			if (changes.initial) fail("There's not initial API config!");
		});

		Conf.HTTP.addChangeListener(changes -> {
			U.print("*** HTTP  ", changes);

			if (changes.initial) {
				eq(changes.added, Conf.HTTP.toMap());
			} else {
				fail("There are not additional HTTP config changes!");
			}
		});

		Conf.ROOT.sub("abcd").addChangeListener(changes -> {
			fail("The 'abcd' config change is not expected!");
		});

		String cfgFile = Msc.path(cfgDir, "config.yml");

		eq(Conf.API.toMap(), U.map());
		eq(Conf.ROOT.getChangesSince(null).added, Conf.ROOT.toMap());
		eq(Conf.API.getChangesSince(null).added, Conf.API.toMap());
		eq(Conf.HTTP.getChangesSince(null).added, Conf.HTTP.toMap());

		U.sleep(2000); // give the Watch service some time to start

		for (int step = 1; step <= 5; step++) {
			exerciseConfigChanges(step, cfgFile);
		}

		verify("root-changes", JSON.prettify(rootChanges));
		verify("api-changes", JSON.prettify(apiChanges));
	}

	private void exerciseConfigChanges(int step, String cfg) {
		changeConfig(cfg, "cfg-" + step + ".yml");

		verify("cfg-api-" + step, JSON.prettify(Conf.API.toMap()));
		// verifyRoutes("routes-" + step);
	}

	private void changeConfig(String cfg, String loadFrom) {
		Log.info("Updating configuration", "file", cfg);
		IO.save(cfg, IO.load(loadFrom));
		U.sleep(2000); // wait 2 seconds, lastModified might have 1 second resolution
	}

}
