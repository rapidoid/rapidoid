package org.rapidoid.env;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Env extends RapidoidThing {

	private static final Environment env = new Environment();

	private static volatile String root;

	public static void reset() {
		env.reset();
		root = null;
		RapidoidEnv.reset();
	}

	public static boolean production() {
		return mode() == EnvMode.PRODUCTION;
	}

	public static boolean test() {
		return mode() == EnvMode.TEST;
	}

	public static boolean dev() {
		return mode() == EnvMode.DEV;
	}

	public static boolean isInitialized() {
		return env.isInitialized();
	}

	public static EnvMode mode() {
		return env.mode();
	}

	public static Set<String> profiles() {
		return env.profiles();
	}

	public static void setProfiles(String... profiles) {
		env.setProfiles(profiles);
	}

	public static void setArgs(String... args) {
		env.setArgs(args);
		processInitialConfig();
	}

	private static void processInitialConfig() {
		String config = initial("config");
		String root = initial("root");

		if (Msc.dockerized()) {
			if (U.isEmpty(root)) root = "/app";
		}

		if (root != null) {
			setRoot(root);
		}

		if (config != null) {
			Conf.setFilenameBase(config);
		}
	}

	static String initial(String key) {
		Map<String, Object> envAndArgs = Env.argsAsMap();
		return (String) U.or(envAndArgs.get(key), Env.properties().get(key));
	}

	static boolean hasInitial(String key, Object value) {
		return String.valueOf(initial(key)).equalsIgnoreCase(String.valueOf(value));
	}

	public static List<String> args() {
		return env.args();
	}

	public static boolean hasProfile(String profileName) {
		return env.hasProfile(profileName);
	}

	public static boolean hasAnyProfile(String... profileNames) {
		return env.hasAnyProfile(profileNames);
	}

	public static EnvProperties properties() {
		return env.properties();
	}

	public static Map<String, Object> argsAsMap() {
		return env.argsAsMap();
	}

	public static void setRoot(String root) {
		if (U.neq(Env.root, root)) {
			File dir = new File(root);

			if (dir.exists()) {
				if (dir.isDirectory()) {
					File[] files = dir.listFiles();

					if (files != null) {
						List<File> content = U.list();

						for (File file : files) {
							if (Msc.isAppResource(file.getName())) content.add(file);
						}

						Log.info("Setting application root", "!root", root, "!content", content);
					} else {
						Log.error("Couldn't access the application root!", "!root", root);
					}

				} else {
					Log.error("The configured application root must be a folder!", "!root", root);
				}
			} else {
				Log.error("The configured application root folder doesn't exist!", "!root", root);
			}

			Env.root = root;
		}
	}

	public static String root() {
		return root;
	}
}
