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
import org.rapidoid.config.Conf;
import org.rapidoid.data.JSON;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HTTPRefreshingConfigTest extends IsolatedIntegrationTest {

	@Test
	public void testRefreshingConfig() {
		String cfgDir = createTempDir("cfgDir-test");

		Conf.reset();
		Conf.setPath(cfgDir);

		String cfgFile = Msc.path(cfgDir, "config.yml");

		eq(Conf.API.toMap(), U.map());

		U.sleep(2000); // give the Watch service some time to start

		for (int i = 0; i < 5; i++) {
			exerciseConfigChanges(1, cfgFile);
			exerciseConfigChanges(2, cfgFile);
		}
	}

	private void exerciseConfigChanges(int step, String cfg) {
		changeConfig(cfg, "cfg-" + step + ".yml");

		verify("cfg-api-" + step, JSON.prettify(Conf.API.toMap()));
	}

	private void changeConfig(String cfg, String loadFrom) {
		Log.info("Updating configuration", "file", cfg);
		IO.save(cfg, IO.load(loadFrom));
		U.sleep(2000); // wait 2 seconds, lastModified might have 1 second resolution
	}

}
