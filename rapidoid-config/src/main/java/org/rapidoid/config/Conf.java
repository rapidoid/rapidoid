package org.rapidoid.config;

import org.rapidoid.log.Log;

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

	private static final Config ROOT = new Config();

	private static String rootPath = rootPathDefault();
	private static String staticPath = staticPathDefault();
	private static String dynamicPath = dynamicPathDefault();
	private static String configPath = configPathDefault();

	public static synchronized void args(String... args) {
		init(args, (Object[]) null);
	}

	public static synchronized void init(String[] mainArgs, Object... extraArgs) {
		if (mainArgs != null) {
			for (String arg : mainArgs) {
				processArg(arg);
			}
		}

		if (extraArgs != null) {
			for (Object arg : extraArgs) {
				if (arg instanceof String) {
					processArg((String) arg);
				}
			}
		}
	}

	private static void processArg(String arg) {
		String name;
		Object value;

		int pos = arg.indexOf('=');
		if (pos > 0) {
			name = arg.substring(0, pos);
			value = arg.substring(pos + 1);
		} else {
			name = arg;
			value = true;
		}

		ROOT.put(name, value);
		processArgIfSpecial(name, value);
	}

	private static void processArgIfSpecial(String name, Object value) {
		if (name.equals("path")) {
			setRootPath(value.toString());
		} else if (name.equals("static")) {
			setStaticPath(value.toString());
		} else if (name.equals("dynamic")) {
			setDynamicPath(value.toString());
		} else if (name.equals("config")) {
			setConfigPath(value.toString());
		}
	}

	public static void unconfigure(String name) {
		ROOT.remove(name);
	}

	public static Object option(String name) {
		return ROOT.get(name);
	}

	public static String option(String name, String defaultValue) {
		return ROOT.containsKey(name) ? (String) ROOT.get(name) : defaultValue;
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

	public static int cpus() {
		return option("cpus", Runtime.getRuntime().availableProcessors());
	}

	public static boolean micro() {
		return has("size", "micro");
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
		ROOT.put("mode", "dev");
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

	public static String rootPathDefault() {
		return "rapidoid";
	}

	public static String rootPath() {
		return rootPath;
	}

	public static String configPathDefault() {
		return rootPathDefault();
	}

	public static String configPath() {
		return configPath;
	}

	public static String staticPathDefault() {
		return rootPathDefault() + "/static";
	}

	public static String staticPath() {
		return staticPath;
	}

	public static String dynamicPathDefault() {
		return rootPathDefault() + "/dynamic";
	}

	public static String dynamicPath() {
		return dynamicPath;
	}

	private static String cleanPath(String path) {
		if (path.endsWith("/") || path.endsWith("\\")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	public static Config sub(String name) {
		return root().sub(name);
	}

	public static void setRootPath(String rootPath) {
		Log.info("Setting 'root' application path", "path", rootPath);
		Conf.rootPath = cleanPath(rootPath);
		setStaticPath(Conf.rootPath + "/static");
		setDynamicPath(Conf.rootPath + "/dynamic");
		setConfigPath(Conf.rootPath);
	}

	public static void setStaticPath(String staticPath) {
		Log.info("Setting 'static' application path", "path", staticPath);
		Conf.staticPath = cleanPath(staticPath);
	}

	public static void setDynamicPath(String dynamicPath) {
		Log.info("Setting 'dynamic' application path", "path", dynamicPath);
		Conf.dynamicPath = cleanPath(dynamicPath);
	}

	public static void setConfigPath(String configPath) {
		Log.info("Setting 'config' application path", "path", configPath);
		Conf.configPath = cleanPath(configPath);
	}

}
