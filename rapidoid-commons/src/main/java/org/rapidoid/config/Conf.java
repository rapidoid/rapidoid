package org.rapidoid.config;

import org.rapidoid.data.YAML;
import org.rapidoid.io.Res;
import org.rapidoid.log.Log;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;

import java.util.Map;

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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class Conf {

	private static final Config ROOT = new Config();

	static {
		RapidoidInitializer.initialize();
	}

	public static final ConfigParser YAML_PARSER = new ConfigParser() {
		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object> parse(byte[] bytes) {
			return YAML.parse(bytes, Map.class);
		}
	};

	public static synchronized void args(String... args) {
		ConfigHelp.processHelp(args);

		if (args != null) {
			for (String arg : args) {
				processArg(arg);
			}
		}
	}

	private static void processArg(String arg) {
		String[] parts = arg.split("=", 2);

		if (parts.length > 1) {
			ROOT.put(parts[0], parts[1]);
		} else {
			ROOT.put(parts[0], true);
		}
	}

	public static void remove(String name) {
		ROOT.remove(name);
	}

	public static Object option(String name) {
		return ROOT.option(name);
	}

	public static String option(String name, String defaultValue) {
		return ROOT.option(name, defaultValue);
	}

	public static int option(String name, int defaultValue) {
		return ROOT.option(name, defaultValue);
	}

	public static long option(String name, long defaultValue) {
		return ROOT.option(name, defaultValue);
	}

	public static double option(String name, double defaultValue) {
		return ROOT.option(name, defaultValue);
	}

	public static boolean has(String name, Object value) {
		return ROOT.has(name, value);
	}

	public static boolean is(String name) {
		return ROOT.is(name);
	}

	public static boolean contains(String name, Object value) {
		return ROOT.contains(name, value);
	}

	public static int port() {
		return option("port", 8888);
	}

	public static int adminPort() {
		return option("admin.port", 8889);
	}

	public static int devPort() {
		return option("dev.port", 8887);
	}

	public static int cpus() {
		return option("cpus", Runtime.getRuntime().availableProcessors());
	}

	public static boolean micro() {
		return is("micro");
	}

	public static boolean production() {
		return is("production");
	}

	public static boolean dev() {
		return !production() && !ClasspathUtil.getClasspathFolders().isEmpty();
	}

	public static String secret() {
		return option("secret", null);
	}

	public static void reset() {
		ROOT.clear();
	}

	public static Config root() {
		return ROOT;
	}

	public static void set(String key, Object value) {
		ROOT.put(key, value);
	}

	public static void set(String key, String subkey, Object value) {
		ROOT.sub(key).put(subkey, value);
	}

	public static <T> T nested(String... name) {
		return ROOT.nested(name);
	}

	public static Config sub(String name) {
		return root().sub(name);
	}

	public static Config refreshing(String filename) {
		return refreshing(filename, YAML_PARSER);
	}

	public static Config refreshing(final String filename, final ConfigParser parser) {
		Log.info("Initializing auto-refreshing config", "filename", filename);

		final Res res = Res.from(filename);

		Config config = res.attachment();
		if (config == null) {
			config = new Config();
			res.attach(config);
		}

		final Config conf = config;

		Runnable reload = new Runnable() {
			@Override
			public void run() {
				byte[] bytes = res.getBytesOrNull();

				Map<String, Object> configData = null;
				if (bytes != null) {
					if (bytes.length > 0) {
						configData = parser.parse(bytes);
					}
				} else {
					Log.warn("Couldn't find configuration file", "filename", filename);
				}

				conf.assign(U.safe(configData));
			}
		};

		reload.run();
		res.onChange("config", reload);

		res.trackChanges();
		res.exists(); // trigger loading

		return conf;
	}

}
