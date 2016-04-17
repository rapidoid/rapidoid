package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.u.U;

import java.util.List;

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

	public static final List<ConfigOption> ALL = configOptions();

	private static List<ConfigOption> configOptions() {
		List<ConfigOption> opts = U.list();

		opts.add(opt("dev", "configure DEV mode", "auto-detected"));
		opts.add(opt("production", "configure PRODUCTION mode", "auto-detected"));
		opts.add(opt("secret=<SECRET>", "configure app-specific secret token for encryption", "(random)"));

		opts.add(opt("app.port=<P>", "the default (App) server will listen at port P", 8888));
		opts.add(opt("app.address=<ADDR>", "the default (App) server will listen at address ADDR", "0.0.0.0"));

		opts.add(opt("dev.port=<P>", "the Dev server will listen at port P", 7777));
		opts.add(opt("dev.address=<ADDR>", "the Dev server will listen at address ADDR", "127.0.0.1"));

		opts.add(opt("admin.port=<P>", "the Admin server will listen at port P", 9999));
		opts.add(opt("admin.address=<ADDR>", "the Admin server will listen at address ADDR", "0.0.0.0"));

		// opts.add(opt("stateless", "Run in stateless mode, session becomes cookiepack", false));
		opts.add(opt("cpus=<C>", "optimize for C number of CPUs", "the actual number of the CPUs"));
//		opts.add(opt("workers=<W>", "start W number of I/O workers", "the configured number of CPUs - cpus options"));
//		opts.add(opt("nodelay", "set the TCP_NODELAY flag to disable Nagle's algorithm", false));
//		opts.add(opt("blockingAccept", "accept connection in BLOCKING mode", false));
//		opts.add(opt("bufSizeKB=<SIZE>", "TCP socket buffer size in KB", 16));

		return opts;
	}

	private static ConfigOption opt(String name, String desc, Object def) {
		return new ConfigOption(name, desc, def);
	}

}
