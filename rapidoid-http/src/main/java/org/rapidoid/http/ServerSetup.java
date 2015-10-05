package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.fast.FastHttp;
import org.rapidoid.net.Serve;
import org.rapidoid.net.TCPServer;

/*
 * #%L
 * rapidoid-main
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

	public TCPServer listen(int port) {
		return listen("*", port);
	}

	public TCPServer listen(String address, int port) {
		TCPServer server = Serve.server().protocol(fastHttp).build();
		server.start();
		return server;
	}

	public OnAction get(String path) {
		return new OnAction(this, httpImpls(), "GET", path);
	}

	public OnAction post(String path) {
		return new OnAction(this, httpImpls(), "POST", path);
	}

	public OnAction put(String path) {
		return new OnAction(this, httpImpls(), "PUT", path);
	}

	public OnAction delete(String path) {
		return new OnAction(this, httpImpls(), "DELETE", path);
	}

	public OnAction options(String path) {
		return new OnAction(this, httpImpls(), "OPTIONS", path);
	}

	private FastHttp[] httpImpls() {
		return new FastHttp[] { fastHttp };
	}

}
