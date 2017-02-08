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

import org.apache.maven.cli.MavenCli;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.File;
import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class MavenUtil extends RapidoidThing {

	public static void findAndBuildAndDeploy(String location) {
		List<String> locations = IO.find("pom.xml").files().in(location).recursive().getLocations();

		if (!locations.isEmpty()) {
			for (String basedir : locations) {
				build(basedir, "/data/.m2/repository", U.list("-e", "-X", "-DskipTests=true", "clean", "org.rapidoid:app:build"));
			}
		} else {
			Log.warn("Didn't find any pom.xml file!", "location", location);
		}
	}

	public static int build(String basedir, String mavenRepo, List<String> mvnArgs) {
		Log.info("Building Maven project", "location", basedir);

		System.setProperty("maven.repo.local", mavenRepo);
		System.setProperty("request.baseDirectory", basedir);
		System.setProperty("maven.multiModuleProjectDirectory", basedir);

		// make sure the Maven repository folder exists
		new File(mavenRepo).mkdirs();

		MavenCli cli = new MavenCli();
		String[] args = U.arrayOf(String.class, mvnArgs);

		int result = cli.doMain(args, basedir, System.out, System.err);

		if (result != 0) {
			Log.error("The Maven build failed!", "status", result);
		}

		return result;
	}

}
