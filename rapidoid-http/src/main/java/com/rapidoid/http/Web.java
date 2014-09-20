package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import org.rapidoid.Rapidoid;
import org.rapidoid.RapidoidServer;
import org.rapidoid.util.U;

public class Web {

	private static final Router ROUTER = new HttpRouter();

	public static void handle(String cmd, String url, Handler handler) {
		ROUTER.route(cmd, url, handler);
	}

	public static void handle(Handler handler) {
		ROUTER.generic(handler);
	}

	public static void get(String url, Handler handler) {
		handle("GET", url, handler);
	}

	public static void post(String url, Handler handler) {
		handle("POST", url, handler);
	}

	public static void put(String url, Handler handler) {
		handle("PUT", url, handler);
	}

	public static void delete(String url, Handler handler) {
		handle("DELETE", url, handler);
	}

	public static RapidoidServer start() {
		return start(null);
	}

	public static RapidoidServer start(WebConfig config) {
		if (config == null) {
			config = new CLIWebConfig(new DefaultWebConfig());
		}

		RapidoidServer server = Rapidoid.start(new HttpProtocol(config, ROUTER), config, WebExchangeImpl.class);

		return server;
	}

	public static RapidoidServer bootstrap() {
		U.info("Bootstrapping Rapidoid...");

		registerEmbeddedHandlers();

		return start(new CLIWebConfig(new DefaultWebConfig()));
	}

	private static void registerEmbeddedHandlers() {
		serve("Hello World!");
	}

	public static void serve(String response) {
		final byte[] bytes = response.getBytes();
		ROUTER.generic(new Handler() {
			@Override
			public Object handle(WebExchange x) {
				return bytes;
			}
		});
		start();
	}

	public static void serve(Handler handler) {
		handle(handler);
		start();
	}

}
