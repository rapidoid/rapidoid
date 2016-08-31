package org.rapidoid.commons;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.List;
import java.util.Set;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Env extends RapidoidThing {

	private static final Environment env = new Environment();

	public static void reset() {
		env.reset();
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

}
