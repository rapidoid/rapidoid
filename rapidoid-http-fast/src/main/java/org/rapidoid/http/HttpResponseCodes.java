package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.0.2")
public class HttpResponseCodes {

	private static final String[] RESPONSE_CODES = {"100 Continue", "101 Switching Protocols", "200 OK",
			"201 Created", "202 Accepted", "203 Non-Authoritative Information", "204 No Content", "205 Reset Content",
			"206 Partial Content", "300 Multiple Choices", "301 Moved Permanently", "302 Found", "303 See Other",
			"304 Not Modified", "305 Use Proxy", "307 Temporary Redirect", "400 Bad Request", "401 Unauthorized",
			"402 Payment Required", "403 Forbidden", "404 Not Found", "405 Method Not Allowed", "406 Not Acceptable",
			"407 Proxy Authentication Required", "408 Request Timeout", "409 Conflict", "410 Gone",
			"411 Length Required", "412 Precondition Failed", "413 Request Entity Too Large",
			"414 Request-URI Too Long", "415 Unsupported Media Type", "416 Requested Range Not Satisfiable",
			"417 Expectation Failed", "500 Internal Server Error", "501 Not Implemented", "502 Bad Gateway",
			"503 Service Unavailable", "504 Gateway Timeout", "505 HTTP Version Not Supported"};

	private final byte[][] responses = new byte[600][];

	public HttpResponseCodes() {
		for (String respCode : RESPONSE_CODES) {
			init(respCode);
		}
	}

	private void init(String responseCode) {
		int code = U.num(responseCode.split(" ")[0]);
		String response = "HTTP/1.1 " + responseCode + "\r\n";
		responses[code] = response.getBytes();
	}

	public byte[] get(int code) {
		if (responses[code] != null) {
			return responses[code];
		} else {
			throw U.rte("Invalid HTTP response code: " + code);
		}
	}

}
