package org.rapidoid.http.fast;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.net.Serve;
import org.rapidoid.net.TCPServer;

/*
 * #%L
 * rapidoid-http-fast
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

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class ServerSetup {

	private final FastHttp fastHttp = new FastHttp();

	private int port = Conf.port();

	private String address = "0.0.0.0";

	private HttpWrapper[] wrappers;

	public TCPServer listen() {
		TCPServer server = Serve.server().protocol(fastHttp).address(address).port(port).build();
		server.start();
		return server;
	}

	public OnAction get(String path) {
		return new OnAction(this, httpImpls(), "GET", path).wrap(wrappers);
	}

	public OnAction post(String path) {
		return new OnAction(this, httpImpls(), "POST", path).wrap(wrappers);
	}

	public OnAction put(String path) {
		return new OnAction(this, httpImpls(), "PUT", path).wrap(wrappers);
	}

	public OnAction delete(String path) {
		return new OnAction(this, httpImpls(), "DELETE", path).wrap(wrappers);
	}

	public OnAction options(String path) {
		return new OnAction(this, httpImpls(), "OPTIONS", path).wrap(wrappers);
	}

	public OnPage page(String path) {
		return new OnPage(this, httpImpls(), path).wrap(wrappers);
	}

	private FastHttp[] httpImpls() {
		return new FastHttp[] { fastHttp };
	}

	public ServerSetup port(int port) {
		this.port = port;
		return this;
	}

	public ServerSetup address(String address) {
		this.address = address;
		return this;
	}

	public ServerSetup defaultWrap(HttpWrapper[] wrappers) {
		this.wrappers = wrappers;
		return this;
	}

}
