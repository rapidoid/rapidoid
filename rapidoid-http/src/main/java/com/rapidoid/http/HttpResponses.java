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

import java.util.List;

import org.rapidoid.util.U;

public class HttpResponses {

	// DIMENSIONS:
	// connection [0=close, 1=keep-alive]

	private static final String[] CONNS = { "close", "keep-alive" };

	private HttpResponse[] responses = new HttpResponse[2];

	public HttpResponses(boolean withServerHeader, boolean withDateHeader) {
		init(withServerHeader, withDateHeader);
	}

	private void init(boolean withServerHeader, boolean withDateHeader) {
		for (int conn = 0; conn < 2; conn++) {
			responses[conn] = newResponse(withServerHeader, withDateHeader, conn);
		}
	}

	private HttpResponse newResponse(boolean withServerHeader, boolean withDateHeader, int conn) {
		List<String> lines = U.list("HTTP/1.1 200 OK");

		if (withServerHeader) {
			lines.add("Server: Rapidoid");
		}

		if (withDateHeader) {
			lines.add("Date: x                              ");
		}

		lines.add("Content-Length:           ");
		lines.add("Connection: " + CONNS[conn]);

		String cnt = U.join("\r\n", lines) + "\r\n";
		return new HttpResponse(cnt);
	}

	public HttpResponse get(boolean keepAlive) {
		return responses[keepAlive ? 1 : 0];
	}

}
