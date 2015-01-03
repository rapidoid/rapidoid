package org.rapidoid.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

/*
 * #%L
 * rapidoid-u
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

public class Conf {

	private static boolean initialized = false;

	private static final ConcurrentMap<String, Object> CFG = U.concurrentMap();

	public static boolean hasOption(String name) {
		init();
		return CFG.containsKey(name.toLowerCase());
	}

	private static synchronized void init() {
		if (!initialized) {
			initialized = true;
			Properties props = new Properties();

			try {
				URL config = U.resource("config");
				if (config != null) {
					props.load(config.openStream());
				}

				config = U.resource("config.private");
				if (config != null) {
					props.load(config.openStream());
				}

				for (Entry<Object, Object> e : props.entrySet()) {
					configure(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
				}
			} catch (IOException e) {
				throw U.rte("Cannot load config!", e);
			}
		}
	}

	public static synchronized void args(String... args) {
		CFG.clear();
		initialized = false;

		if (args != null) {
			for (String arg : args) {
				String name, value;

				int pos = arg.indexOf('=');
				if (pos > 0) {
					name = arg.substring(0, pos);
					value = arg.substring(pos + 1);
				} else {
					name = arg;
					value = "true";
				}

				configure(name, value);
			}
		}
	}

	private static void configure(String name, String value) {
		String[] parts = value.split(",");
		Object val = parts.length > 1 ? U.list(parts) : value;
		CFG.put(name, val);
	}

	public static Object option(String name) {
		return CFG.get(name);
	}

	public static String option(String name, String defaultValue) {
		return CFG.containsKey(name) ? (String) CFG.get(name) : defaultValue;
	}

	public static int option(String name, int defaultValue) {
		String n = option(name, (String) null);
		return n != null ? Integer.parseInt(n) : defaultValue;
	}

	public static long option(String name, long defaultValue) {
		String n = option(name, (String) null);
		return n != null ? Long.parseLong(n) : defaultValue;
	}

	public static double option(String name, double defaultValue) {
		String n = option(name, (String) null);
		return n != null ? Double.parseDouble(n) : defaultValue;
	}

	public static boolean contains(String name, String value) {
		Object opt = option(name);

		if (opt != null) {
			if (opt instanceof Collection) {
				return ((Collection<?>) opt).contains(value);
			}
		}

		return U.eq(opt, value);
	}

	public static int cpus() {
		return option("cpus", Runtime.getRuntime().availableProcessors());
	}

	public static boolean micro() {
		return hasOption("micro");
	}

	public static boolean production() {
		return hasOption("production");
	}

	public static boolean dev() {
		return hasOption("dev");
	}

}
