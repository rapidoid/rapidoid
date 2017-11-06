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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.deploy.AppDeployment;
import org.rapidoid.test.RapidoidIntegrationTest;

import java.io.File;

@Authors("Nikolche Mihajlovski")
@Since("5.4.6")
public class PlatformTestCommons extends RapidoidIntegrationTest {

	static String appPath(String name) {
		return new File("rapidoid-platform/src/test/" + name).getAbsolutePath();
	}

	static void runMain(String... args) {
		Main.main(args);
	}

	static void makeAppDeployment(String name) {
		AppDeployment app = AppDeployment.create(name);

		app.stage("config.yml", makeAppConfig(name).getBytes());
	}

	static String makeAppConfig(String appId) {
		String config = "" +
			"pages:\n" +
			"  /: ''\n\n" +
			"gui:\n" +
			"  brand: " + appId + "\n\n";

		return config;
	}

}
