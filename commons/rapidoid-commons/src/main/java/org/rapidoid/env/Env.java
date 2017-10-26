package org.rapidoid.env;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

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

	private static volatile RootContext rootCtx;

	public static void reset() {
		env.reset();
		rootCtx = null;
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
		U.must(!Env.isInitialized(), "The environment was already initialized!");

		U.must(Env.args().isEmpty(), "The application start-up arguments were already assigned!");
		env.setArgs(args);

		processInitialConfig();
	}

	private static void processInitialConfig() {
		setRoot(initial("root"));

		String config = initial("config");
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
		Env.rootCtx = RootContext.from(root);
	}

	public static String root() {
		if (Env.rootCtx != null) {
			return rootCtx.root();

		} else {
			U.must(!Msc.isPlatform(), "The root context must be initialized for the platform!");
			return null;
		}
	}
}
