package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.List;
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
@Since("5.0.2")
public class ConfigOptions extends RapidoidThing {

	public static final Set<String> SERVICE_NAMES = U.set();

	public static final List<ConfigOption> ALL = configOptions();

	public static final List<ConfigOption> SERVICES = serviceOptions();

	public static final List<ConfigOption> COMMANDS = commandOptions();

	private static List<ConfigOption> configOptions() {
		List<ConfigOption> opts = U.list();

		opts.add(opt("config", "configuration filename prefix", "config"));

		opts.add(opt("dev", "run in DEV mode", "auto-detected"));
		opts.add(opt("production", "run in PRODUCTION mode", "auto-detected"));
		opts.add(opt("test", "run in TEST mode", "auto-detected"));

		opts.add(opt("secret=<SECRET>", "configure secret key for cryptography", "random"));
		opts.add(opt("profiles=<P1,P2...>", "comma-separated list of application profiles (e.g. mysql,prod)", "the 'default' profile"));

		opts.add(opt("on.port=<P>", "the default App server will listen at port P", 8888));
		opts.add(opt("on.address=<ADDR>", "the default App server will listen at address ADDR", "0.0.0.0"));

		opts.add(opt("admin.port=<P>", "the Admin server will listen at port P", "same as on.port"));
		opts.add(opt("admin.address=<ADDR>", "the Admin server will listen at address ADDR", "on.address"));

		opts.add(opt("app.services=<S1,S2...>", "comma-separated list of services to bootstrap on the App server", "none"));
		opts.add(opt("admin.services=<S1,S2...>", "comma-separated list of services to bootstrap on the Admin server", "none"));

		return opts;
	}

	private static List<ConfigOption> serviceOptions() {
		List<ConfigOption> opts = U.list();

		opts.add(srvOpt("center", "Admin Center"));
		opts.add(srvOpt("ping", "Simple ping"));
		opts.add(srvOpt("status", "Application and system status"));
		opts.add(srvOpt("overview", "General overview"));
		opts.add(srvOpt("application", "Application management"));
		opts.add(srvOpt("lifecycle", "Lifecycle management"));
		opts.add(srvOpt("processes", "Process management"));
		opts.add(srvOpt("manageables", "Manageable objects"));
		opts.add(srvOpt("jmx", "JMX overview"));
		opts.add(srvOpt("metrics", "Metrics"));
		opts.add(srvOpt("deploy", "Application deployment"));
		opts.add(srvOpt("auth", "Authentication"));
		opts.add(srvOpt("oauth", "OAuth authentication"));
		opts.add(srvOpt("entities", "JPA entities management"));
		opts.add(srvOpt("welcome", "Welcome page"));
		opts.add(srvOpt("discovery", "Peer discovery (transient state)"));
		opts.add(srvOpt("echo", "Echo of the request (for debugging)"));

		return opts;
	}

	private static List<ConfigOption> commandOptions() {
		List<ConfigOption> opts = U.list();

		opts.add(cmd("dev", "CLI shortcut for convenient local development setup"));
		opts.add(cmd("installer", "Print installation script for Rapidoid"));
		opts.add(cmd("password", "Generate salted password hash"));
		opts.add(cmd("help", "Show help"));

		return opts;
	}

	private static ConfigOption opt(String name, String desc, Object def) {
		return new ConfigOption(name, desc, def);
	}

	private static ConfigOption srvOpt(String name, String desc) {
		SERVICE_NAMES.add(name);
		return new ConfigOption(name, desc, null);
	}

	private static ConfigOption cmd(String name, String desc) {
		return new ConfigOption(name, desc, null);
	}

}
