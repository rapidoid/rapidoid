package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpResponses {

	// DIMENSIONS:
	// - response code [100, 101, 200, ... 505]
	// - connection [0=close, 1=keep-alive]

	private static final String[] RESPONSE_CODES = { "100 Continue", "101 Switching Protocols", "200 OK",
			"201 Created", "202 Accepted", "203 Non-Authoritative Information", "204 No Content", "205 Reset Content",
			"206 Partial Content", "300 Multiple Choices", "301 Moved Permanently", "302 Found", "303 See Other",
			"304 Not Modified", "305 Use Proxy", "307 Temporary Redirect", "400 Bad Request", "401 Unauthorized",
			"402 Payment Required", "403 Forbidden", "404 Not Found", "405 Method Not Allowed", "406 Not Acceptable",
			"407 Proxy Authentication Required", "408 Request Timeout", "409 Conflict", "410 Gone",
			"411 Length Required", "412 Precondition Failed", "413 Request Entity Too Large",
			"414 Request-URI Too Long", "415 Unsupported Media Type", "416 Requested Range Not Satisfiable",
			"417 Expectation Failed", "500 Internal Server Error", "501 Not Implemented", "502 Bad Gateway",
			"503 Service Unavailable", "504 Gateway Timeout", "505 HTTP Version Not Supported" };

	private static final String[] CONNS = { "close", "keep-alive" };

	private final HttpResponse[][] responses = new HttpResponse[600][];

	public HttpResponses(boolean withServerHeader, boolean withDateHeader) {
		for (String respCode : RESPONSE_CODES) {
			init(respCode, withServerHeader, withDateHeader);
		}
	}

	private void init(String responseCode, boolean withServerHeader, boolean withDateHeader) {
		int code = U.num(responseCode.split(" ")[0]);
		responses[code] = new HttpResponse[2];
		for (int conn = 0; conn < 2; conn++) {
			responses[code][conn] = newResponse(responseCode, withServerHeader, withDateHeader, conn);
		}
	}

	private HttpResponse newResponse(String responseCode, boolean withServerHeader, boolean withDateHeader, int conn) {
		List<String> lines = U.list("HTTP/1.1 " + responseCode);

		if (withServerHeader) {
			lines.add("Server: Rapidoid");
		}

		if (withDateHeader) {
			lines.add("Date: x                              ");
		}

		lines.add("Content-Length:          0");
		lines.add("Connection: " + CONNS[conn]);

		String cnt = U.join("\r\n", lines) + "\r\n";
		return new HttpResponse(cnt);
	}

	public HttpResponse get(int code, boolean keepAlive) {
		if (responses[code] != null) {
			return responses[code][keepAlive ? 1 : 0];
		} else {
			throw U.rte("Invalid HTTP response code: " + code);
		}
	}

}
