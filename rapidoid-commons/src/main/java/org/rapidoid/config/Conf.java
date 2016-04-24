package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.commons.Coll;
import org.rapidoid.commons.Env;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
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
public class Conf extends RapidoidThing {

	private static final Map<String, Config> SECTIONS = Coll.autoExpandingMap(new Mapper<String, Config>() {
		@Override
		public Config map(String name) throws Exception {
			return createSection(name);
		}
	});

	public static final Config ROOT = new Config();

	public static final Config USERS = section("users");
	public static final Config JOBS = section("jobs");
	public static final Config OAUTH = section("oauth");

	public static final Config JDBC = section("jdbc");
	public static final Config HIBERNATE = section("hibernate");

	public static final Config APP = section("app");

	public static final Config ON = section("on");
	public static final Config ADMIN = section("admin");

	static {
		reload();
	}

	private static volatile String path = "";

	public static synchronized void args(String... args) {
		ConfigHelp.processHelp(args);
		ROOT.args(args);

		String profiles = ROOT.entry("profiles").str().getOrNull();

		if (profiles != null) {
			Coll.assign(Env.profiles(), profiles.split("\\s*\\,\\s*"));
			Log.info("Configuring active profiles", "profiles", Env.profiles());
			reload();
		} else {
			if (Env.profiles().isEmpty()) {
				Env.profiles().add("default");
				Log.info("No profiles were specified, configuring the 'default' profile", "profiles", Env.profiles());
				reload();
			}
		}
	}

	public static synchronized void profiles(String... profiles) {
		Coll.assign(Env.profiles(), profiles);
		Log.info("Overriding active profiles", "profiles", Env.profiles());
		reload();
	}

	public static boolean micro() {
		return ROOT.is("micro");
	}

	public static void reset() {
		ROOT.clear();
	}

	public static Config section(String name) {
		return SECTIONS.get(name);
	}

	private static Config createSection(String name) {
		Config config = ROOT.sub(name);
		ConfigUtil.load(filename(config.keys()), config);
		return config;
	}

	public static Config section(Class<?> clazz) {
		return section(clazz.getSimpleName());
	}

	public static int cpus() {
		return ROOT.entry("cpus").or(Runtime.getRuntime().availableProcessors());
	}

	public static void setPath(String path) {
		Conf.path = path;
		reload();
	}

	public static void reload() {
		List<List<String>> detached = ConfigUtil.untrack();

		reset();

		ConfigUtil.load(Msc.path("default", "config.y?ml"), ROOT);

		for (String profile : Env.profiles()) {
			ConfigUtil.load(Msc.path("default", U.frmt("profile-%s.y?ml", profile)), ROOT);
		}

		ConfigUtil.load(Msc.path(path, "application.y?ml"), ROOT);
		ConfigUtil.load(Msc.path(path, "config.y?ml"), ROOT);

		for (String profile : Env.profiles()) {
			ConfigUtil.load(Msc.path(path, U.frmt("application-%s.y?ml", profile)), ROOT);
			ConfigUtil.load(Msc.path(path, U.frmt("profile-%s.y?ml", profile)), ROOT);
		}

		for (Config sub : SECTIONS.values()) {
			ConfigUtil.load(filename(sub.keys()), sub);
		}

		for (List<String> keys : detached) {
			autoRefresh(keys.isEmpty() ? ROOT : ROOT.sub(keys));
		}
	}

	private static void autoRefresh(Config... configs) {
		for (Config config : configs) {
			List<String> keys = config.keys();
			ConfigUtil.autoRefresh(config, filename(keys));
		}
	}

	private static String filename(List<String> keys) {
		U.must(keys.size() < 2);
		String configName = keys.isEmpty() ? "config" : keys.get(0);
		return Msc.path(path, configName + ".y?ml");
	}

}
