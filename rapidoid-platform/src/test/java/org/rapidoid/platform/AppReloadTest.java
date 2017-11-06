package org.rapidoid.platform;

/*
 * #%L
 * rapidoid-platform
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
import org.rapidoid.http.HTTP;
import org.rapidoid.http.Self;
import org.rapidoid.io.IO;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import static org.rapidoid.test.TestCommons.createTempDir;

@Authors("Nikolche Mihajlovski")
@Since("5.4.7")
public class AppReloadTest extends PlatformTestCommons {

	@Test
	public void testAppReload() {
		String appsDir = createTempDir("app");
		String configYml = Msc.path(appsDir, "config.yml");

		PlatformOpts.singleAppPath(appsDir);

		isFalse(Msc.isPlatform());
		isTrue(PlatformOpts.isSingleApp());

		IO.save(configYml, makeAppConfig("my-great-app"));

		runMain("dev");

		// the proxy will wait for the app server to start
		isTrue(Self.get("/").fetch().contains("my-great-app"));

		isFalse(Self.get("/rapidoid/status").fetch().contains("my-great-app"));
		isTrue(HTTP.get("localhost:10000/").fetch().contains("my-great-app"));

		isFalse(Self.get("/rapidoid/status").fetch().contains("my-cool-app"));
		isFalse(HTTP.get("localhost:10000/").fetch().contains("my-cool-app"));

		IO.save(configYml, makeAppConfig("my-cool-app"));

		// give the platform some time to detect the changes and restart the app
		U.sleep(3000);

		// the proxy will wait for the app server to start
		isTrue(Self.get("/").fetch().contains("my-cool-app"));

		isFalse(Self.get("/rapidoid/status").fetch().contains("my-cool-app"));
		isTrue(HTTP.get("localhost:10000/").fetch().contains("my-cool-app"));

		isFalse(Self.get("/rapidoid/status").fetch().contains("my-great-app"));
		isFalse(HTTP.get("localhost:10000/").fetch().contains("my-great-app"));
	}

}
