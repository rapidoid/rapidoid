package org.rapidoidx.http.client;

/*
 * #%L
 * rapidoid-x-http-client
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.data.Range;
import org.rapidoidx.data.Ranges;
import org.rapidoidx.net.abstracts.Channel;
import org.rapidoidx.net.impl.FiniteStateProtocol;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class HttpClientProtocol extends FiniteStateProtocol {

	private final String request;

	private final HttpClientCallback callback;

	public HttpClientProtocol(String request, HttpClientCallback callback) {
		super(2);
		this.request = request;
		this.callback = callback;
	}

	/*
	 * Send request.
	 */
	@Override
	protected int state0(Channel ctx) {
		ctx.log("sending request");

		ctx.write(request.getBytes());

		return 1;
	}

	/*
	 * Receive response.
	 */
	@Override
	protected int state1(Channel ctx) {
		ctx.log("receiving response");

		final Ranges head = ctx.helper().ranges1.reset();
		final Ranges body = ctx.helper().ranges2.reset();

		Buf in = ctx.input();
		in.scanLnLn(head);

		Map<String, String> headers = head.toMap(in.bytes(), 1, head.count - 1, "\\s*\\:\\s*");
		Map<String, String> headersLow = UTILS.lowercase(headers);

		if ("chunked".equals(headersLow.get("transfer-encoding"))) {

			ctx.log("got chunked encoding");
			parseChunkedBody(in, body);
			callback.onResult(in, head, body);

		} else if (headersLow.containsKey("content-length")) {

			ctx.log("got content length");
			int clength = Integer.parseInt(headersLow.get("content-length"));

			parseBodyByContentLength(in, body, clength);
			callback.onResult(in, head, body);

		} else {
			// no content length is provided, read until connection is closed
			ctx.log("read until closed");

			readBodyUntilClosed(ctx, body);
			callback.onResult(in, head, body);
		}

		ctx.log("done");
		ctx.close(); // improve: keep-alive
		return STOP;
	}

	private void parseChunkedBody(Buf in, Ranges chunks) {
		int count;
		do {
			String cnt = in.readLn();
			count = Integer.parseInt(cnt, 16);

			if (count > 0) {
				Range chunk = chunks.ranges[chunks.add()];
				in.scanN(count, chunk);

				// each chunk is terminated with a new line
				String line = in.readLn();
				U.must(line.isEmpty());
			}

		} while (count > 0);
	}

	private void parseBodyByContentLength(Buf in, Ranges body, int clength) {
		body.add(); // there will be only 1 body part
		in.scanN(clength, body.ranges[0]);
	}

	private void readBodyUntilClosed(Channel ctx, Ranges body) {
		ctx.waitUntilClosing();

		body.add(); // there will be only 1 body part
		Buf in = ctx.input();
		in.scanN(in.remaining(), body.ranges[0]);
	}

}
