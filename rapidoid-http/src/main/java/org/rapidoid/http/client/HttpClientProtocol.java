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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.lambda.Callback;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.FiniteStateProtocol;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class HttpClientProtocol extends FiniteStateProtocol {

	private final String request;

	private final Callback<String> callback;

	public HttpClientProtocol(String request, Callback<String> callback) {
		super(2);
		this.request = request;
		this.callback = callback;
	}

	/*
	 * Send request.
	 */
	@Override
	protected int state0(Channel ctx) {
		ctx.write(request.getBytes());
		return 1;
	}

	/*
	 * Receive response.
	 */
	@Override
	protected int state1(Channel ctx) {
		final Ranges head = ctx.helper().ranges1;
		final Range body = ctx.helper().ranges2.ranges[0];

		head.reset();
		body.reset();

		Buf in = ctx.input();
		in.scanLnLn(head);

		Map<String, String> headers = head.toMap(in.bytes(), 1, head.count - 1, "\\s*\\:\\s*");
		Map<String, String> headersLow = UTILS.lowercase(headers);

		if ("chunked".equals(headersLow.get("transfer-encoding"))) {
			parseChunkedBody(in, body);
			callback.onDone(body.str(in), null);
		} else if (headersLow.containsKey("content-length")) {
			int clength = Integer.parseInt(headersLow.get("content-length"));
			parseBodyByContentLength(in, body, clength);
			callback.onDone(body.str(in), null);
		} else {
			callback.onDone(null, U.rte("Invalid HTTP response!"));
		}

		ctx.close(); // improve: keep-alive
		return STOP;
	}

	private void parseChunkedBody(Buf in, Range body) {
		int count;
		do {
			String cnt = in.readLn();
			count = Integer.parseInt(cnt, 16);

			if (count > 0) {
				in.scanN(count, body);
				String line = in.readLn();
				U.must(line.isEmpty());
			}

		} while (count > 0);
	}

	private void parseBodyByContentLength(Buf in, Range body, int clength) {
		in.scanN(clength, body);
	}

}
