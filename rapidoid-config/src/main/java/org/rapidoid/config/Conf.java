package org.rapidoid.config;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * #%L
 * rapidoid-config
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class Conf {

	private static boolean initialized = false;

	private static final ConcurrentMap<String, Object> CFG = new ConcurrentHashMap<String, Object>();

	private static synchronized void init() {
		if (!initialized) {
			initialized = true;
			Properties props = new Properties();

			try {
				URL config = resource("config");
				if (config != null) {
					props.load(config.openStream());
				}

				config = resource("config.private");
				if (config != null) {
					props.load(config.openStream());
				}

				for (Entry<Object, Object> e : props.entrySet()) {
					set(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
				}
			} catch (IOException e) {
				throw new RuntimeException("Cannot load config!", e);
			}
		}
	}

	private static URL resource(String name) {
		return Thread.currentThread().getContextClassLoader().getResource(name);
	}

	public static void args(String... args) {
		args(null, args);
	}

	public static synchronized void args(String[] mainArgs, String... extraArgs) {
		init();

		if (mainArgs != null) {
			for (String arg : mainArgs) {
				processArg(arg);
			}
		}

		if (extraArgs != null) {
			for (String arg : extraArgs) {
				processArg(arg);
			}
		}
	}

	private static void processArg(String arg) {
		String name, value;

		int pos = arg.indexOf('=');
		if (pos > 0) {
			name = arg.substring(0, pos);
			value = arg.substring(pos + 1);
		} else {
			name = arg;
			value = "true";
		}

		set(name, value);
	}

	public static void set(String name, String value) {
		init();
		String[] parts = value.split(",");
		Object val = parts.length > 1 ? Arrays.asList(parts) : value;
		CFG.put(name, val);
	}

	public static void unconfigure(String name) {
		init();
		CFG.remove(name);
	}

	public static Object option(String name) {
		init();
		return CFG.get(name);
	}

	public static String option(String name, String defaultValue) {
		init();
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

	public static boolean has(String name, String value) {
		Object opt = option(name);
		return opt == value || (opt != null && opt.equals(value));
	}

	public static boolean is(String name) {
		return has(name, "true");
	}

	public static boolean contains(String name, String value) {
		Object opt = option(name);

		if (opt != null) {
			if (opt instanceof Collection) {
				return ((Collection<?>) opt).contains(value);
			}

			return opt.equals(value);
		}

		return opt == value;
	}

	public static int cpus() {
		return option("cpus", Runtime.getRuntime().availableProcessors());
	}

	public static boolean micro() {
		return has("size", "micro");
	}

	@SuppressWarnings("unchecked")
	public static List<String> oauth() {
		Object oauth = option("oauth");

		if (oauth instanceof List<?>) {
			return (List<String>) oauth;
		} else {
			List<String> lst = new ArrayList<String>();
			lst.add(String.valueOf(oauth));
			return lst;
		}
	}

	public static boolean production() {
		return has("mode", "production");
	}

	public static boolean dev() {
		if (production()) {
			return false;
		}

		assert configureDevMode(); // in debug mode

		return has("mode", "dev") || !production();
	}

	private static boolean configureDevMode() {
		set("mode", "dev");
		return true;
	}

	public static String secret() {
		return option("secret", null);
	}

	public static String system(String key) {
		String value = option(key, null);

		if (value == null) {
			value = System.getProperty(key);
		}

		if (value == null) {
			value = System.getenv(key);
		}

		return value;
	}

	public static String JAVA_HOME() {
		return system("JAVA_HOME");
	}

	public static String HOSTNAME() {
		return system("HOSTNAME");
	}

	public static String IP_ADDRESS() {
		return system("IP_ADDRESS");
	}

}
