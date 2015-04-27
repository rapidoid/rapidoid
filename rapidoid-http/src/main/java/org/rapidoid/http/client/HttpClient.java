package org.rapidoid.http.client;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Callback;
import org.rapidoid.net.TCP;
import org.rapidoid.net.TCPClient;

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class HttpClient {

	private final TCPClient clients = TCP.client().build().start();

	public void get(String host, int port, String request, Callback<String> callback) {
		clients.connect(host, port, new HttpClientProtocol(request, callback));
	}

	public void shutdown() {
		clients.shutdown();
	}

}
