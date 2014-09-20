package org.rapidoid;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.config.CLIConfig;
import org.rapidoid.config.DefaultServerConfig;
import org.rapidoid.config.ServerConfig;
import org.rapidoid.net.Exchange;
import org.rapidoid.net.RapidoidClientLoop;
import org.rapidoid.net.RapidoidHelper;
import org.rapidoid.net.RapidoidServerLoop;
import org.rapidoid.net.StatsThread;
import org.rapidoid.util.U;

public class Rapidoid {

	private static final ServerConfig DEFAULT_CONFIG = new CLIConfig(new DefaultServerConfig());

	public static final int WRITE = 0;

	public static RapidoidServer start(Protocol protocol) {
		return start(protocol, null, null);
	}

	public static RapidoidServer start(Protocol protocol, Class<? extends Exchange> exchangeClass) {
		return start(protocol, null, null);
	}

	public static RapidoidServer start(Protocol protocol, ServerConfig config) {
		return start(protocol, config, null, RapidoidHelper.class);
	}

	public static RapidoidServer start(Protocol protocol, ServerConfig config, Class<? extends Exchange> exchangeClass) {
		return start(protocol, config, exchangeClass, RapidoidHelper.class);
	}

	public static RapidoidServer start(Protocol protocol, ServerConfig config, Class<? extends Exchange> exchangeClass,
			Class<? extends RapidoidHelper> helperClass) {

		if (config == null) {
			config = DEFAULT_CONFIG;
		}

		if (!config.nostats()) {
			U.inject(StatsThread.class).execute();
		}

		RapidoidServer server = new RapidoidServerLoop(config, protocol, exchangeClass, helperClass);
		server.start();

		return server;
	}

	public static RapidoidClient connect(int connections, Protocol protocol, String host, int port) {
		return connect(connections, protocol, null, null, host, port);
	}

	public static RapidoidClient connect(int connections, Protocol protocol, Class<? extends Exchange> exchangeClass,
			String host, int port) {
		return connect(connections, protocol, null, null, host, port);
	}

	public static RapidoidClient connect(int connections, Protocol protocol, ServerConfig config, String host, int port) {
		return connect(connections, protocol, config, null, RapidoidHelper.class, host, port);
	}

	public static RapidoidClient connect(int connections, Protocol protocol, ServerConfig config,
			Class<? extends Exchange> exchangeClass, String host, int port) {
		return connect(connections, protocol, config, exchangeClass, RapidoidHelper.class, host, port);
	}

	public static RapidoidClient connect(int connections, Protocol protocol, ServerConfig config,
			Class<? extends Exchange> exchangeClass, Class<? extends RapidoidHelper> helperClass, String host, int port) {

		if (config == null) {
			config = DEFAULT_CONFIG;
		}

		if (!config.nostats()) {
			U.inject(StatsThread.class).execute();
		}

		RapidoidClient client = new RapidoidClientLoop(connections, config, protocol, exchangeClass, helperClass, host,
				port);
		client.start();

		return client;
	}
}
