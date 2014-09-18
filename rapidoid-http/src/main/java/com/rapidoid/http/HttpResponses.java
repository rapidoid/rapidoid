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

	// dimensions:
	// connection [0=close, 1=keep-alive],
	// content-type [0=plain, 1=html, 2=json]

	private static final String[] conns = { "close", "keep-alive" };

	private static final String[] types = { "text/plain", "text/html", "application/json" };

	private HttpResponse[][] responses = new HttpResponse[2][3];

	public HttpResponses(boolean withServerHeader, boolean withDateHeader) {
		init(withServerHeader, withDateHeader);
	}

	private void init(boolean withServerHeader, boolean withDateHeader) {
		for (int conn = 0; conn < 2; conn++) {
			for (int type = 0; type < 3; type++) {
				responses[conn][type] = newResponse(withServerHeader, withDateHeader, conn, type);
			}
		}
	}

	private HttpResponse newResponse(boolean withServerHeader, boolean withDateHeader, int conn, int type) {
		List<String> lines = U.list("HTTP/1.1 200 OK");

		if (withServerHeader) {
			lines.add("Server: X");
		}

		if (withDateHeader) {
			lines.add("Date: x                              ");
		}

		lines.add("Connection: " + conns[conn]);
		lines.add("Content-Type: " + types[type] + "; charset=utf-8");
		lines.add("Content-Length:           ");

		lines.add("");

		return new HttpResponse(U.join("\r\n", lines) + "\r\n");
	}

	public HttpResponse get(boolean keepAlive, byte type) {
		return responses[keepAlive ? 1 : 0][type];
	}

}
