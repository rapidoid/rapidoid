package org.rapidoid.log;

/*
 * #%L
 * rapidoid-essentials
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.u.U;

/**
 * @author Nikolche Mihajlovski
 * @since 5.2.5
 */
public class GlobalCfg extends RapidoidThing {

	public static final String UNIFORM_OUTPUT = "UNIFORM_OUTPUT";
	public static final String MANAGED_BY = "MANAGED_BY";

	private static final boolean uniformOutput = is(UNIFORM_OUTPUT);
	private static final String managedBy = get(MANAGED_BY);

	public static boolean uniformOutput() {
		return uniformOutput;
	}

	public static boolean quiet() {
		return is("QUIET");
	}

	public static String managedBy() {
		return managedBy;
	}

	public static boolean is(String name) {
		return "true".equalsIgnoreCase(get(name));
	}

	public static String get(String name) {
		String value = U.or(System.getProperty(name), System.getenv(name));
		if (value != null) return value;

		value = U.or(System.getProperty(name.toUpperCase()), System.getenv(name.toUpperCase()));
		if (value != null) return value;

		value = U.or(System.getProperty(name.toLowerCase()), System.getenv(name.toLowerCase()));
		return value;
	}

}
