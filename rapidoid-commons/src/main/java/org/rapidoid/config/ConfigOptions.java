package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.u.U;

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

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.2
 */
public class ConfigOptions extends RapidoidThing {

	public static final Set<String> SERVICE_NAMES = U.set();

	public static final List<ConfigOption> ALL = configOptions();

	public static final List<ConfigOption> SERVICES = serviceOptions();

	private static List<ConfigOption> configOptions() {
		List<ConfigOption> opts = U.list();

		opts.add(opt("config", "configuration filename prefix", "config"));

		opts.add(opt("dev", "run in DEV mode", "auto-detected"));
		opts.add(opt("production", "run in PRODUCTION mode", "auto-detected"));
		opts.add(opt("test", "run in TEST mode", "auto-detected"));

		opts.add(opt("secret=<SECRET>", "configure app-specific secret for encryption", "random"));
		opts.add(opt("profiles=<P1,P2...>", "comma-separated list of application profiles (e.g. mysql,prod)", "the 'default' profile"));

		opts.add(opt("on.port=<P>", "the default App server will listen at port P", 8888));
		opts.add(opt("on.address=<ADDR>", "the default App server will listen at address ADDR", "0.0.0.0"));

		opts.add(opt("admin.port=<P>", "the Admin server will listen at port P", "same as on.port"));
		opts.add(opt("admin.address=<ADDR>", "the Admin server will listen at address ADDR", "on.address"));

		opts.add(opt("app.services=<S1,S2...>", "comma-separated list of services to bootstrap on the App server", "none"));
		opts.add(opt("admin.services=<S1,S2...>", "comma-separated list of services to bootstrap on the Admin server", "none"));

//		opts.add(opt("cpus=<C>", "optimize for C number of CPUs", "the actual number of the CPUs"));
//		opts.add(opt("workers=<W>", "start W number of I/O workers", "the configured number of CPUs - cpus options"));
//		opts.add(opt("nodelay", "set the TCP_NODELAY flag to disable Nagle's algorithm", false));
//		opts.add(opt("blockingAccept", "accept connection in BLOCKING mode", false));
//		opts.add(opt("bufSizeKB=<SIZE>", "TCP socket buffer size in KB", 16));

		return opts;
	}

	private static List<ConfigOption> serviceOptions() {
		List<ConfigOption> opts = U.list();

		opts.add(srvOpt("center", "Admin Center"));
		opts.add(srvOpt("ping", "Ping service"));
		opts.add(srvOpt("status", "Status service"));
		opts.add(srvOpt("overview", "Overview service"));
		opts.add(srvOpt("application", "Application services"));
		opts.add(srvOpt("lifecycle", "Lifecycle services"));
		opts.add(srvOpt("jmx", "JMX services"));
		opts.add(srvOpt("metrics", "Metrics services"));
		opts.add(srvOpt("deploy", "Application deployment services"));
		opts.add(srvOpt("auth", "Authentication services"));
		opts.add(srvOpt("oauth", "OAuth services"));
		opts.add(srvOpt("entities", "JPA Entities services"));
		opts.add(srvOpt("welcome", "Welcome page"));

		return opts;
	}

	private static ConfigOption opt(String name, String desc, Object def) {
		return new ConfigOption(name, desc, def);
	}

	private static ConfigOption srvOpt(String name, String desc) {
		SERVICE_NAMES.add(name);
		return new ConfigOption(name, desc, "disabled");
	}

}
