package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Env;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.scan.ClasspathUtil;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

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

	public static final Config ROOT = new ConfigImpl("config", true);

	public static final Config USERS = section("users");
	public static final Config JOBS = section("jobs");
	public static final Config OAUTH = section("oauth");
	public static final Config JDBC = section("jdbc");
	public static final Config HIBERNATE = section("hibernate");
	public static final Config C3P0 = section("c3p0");
	public static final Config APP = section("app");
	public static final Config HTTP = section("http");
	public static final Config ON = section("on");
	public static final Config ADMIN = section("admin");
	public static final Config TOKEN = section("token");
	public static final Config PROXY = section("proxy");
	public static final Config SQL = section("sql");
	public static final Config LOG = section("log");

	static void applyConfig(Config config) {

		if (Env.isInitialized()) {
			if (!Env.production()) {
				Log.setStyled(true);
			}
		}

		if (config == ROOT) {
			String root = Env.root();

			if (Msc.dockerized()) {
				U.must(U.notEmpty(root), "The root must be configured in a Dockerized environment!");

				if (!APP.has("jar")) APP.set("jar", Msc.path(root, "app.jar"));
			}

			String appJar = APP.entry("jar").str().getOrNull();
			if (U.notEmpty(appJar)) {
				ClasspathUtil.appJar(appJar);
			}

			boolean fancy = LOG.entry("fancy").bool().or(Msc.hasConsole());
			if (fancy) {
				Log.setStyled(true);
			}

			String logLevel = LOG.entry("level").or("info");
			Log.setLogLevel(LogLevel.valueOf(logLevel.toUpperCase()));
		}
	}

	public static synchronized void reset() {
		ROOT.reset();
	}

	public static synchronized Config section(String name) {
		return SECTIONS.get(name);
	}

	public static synchronized Config section(Class<?> clazz) {
		return section(clazz.getSimpleName());
	}

	private static Config createSection(String name) {
		return ROOT.sub(name);
	}

	public static boolean isInitialized() {
		return ROOT.isInitialized();
	}

	public static void setFilenameBase(String filenameBase) {
		ROOT.setFilenameBase(filenameBase);
	}

}
