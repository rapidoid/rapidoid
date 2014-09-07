package org.rapidoid.demo.http;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.Ctx;
import org.rapidoid.Protocol;
import org.rapidoid.buffer.Buf;
import org.rapidoid.data.Range;
import org.rapidoid.util.U;

public class SimpleHttpProtocol implements Protocol {

	private static final byte[] HTTP_200_OK = "HTTP/1.1 200 OK\r\n".getBytes();

	private static final byte[] CONN_KEEP_ALIVE = "Connection: keep-alive\r\n".getBytes();

	private static final byte[] CONN_CLOSE = "Connection: close\r\n".getBytes();

	private static final byte[] CONN_CLOSE1 = "Connection: close".getBytes();
	private static final byte[] CONN_CLOSE2 = "Connection:close".getBytes();

	private static final byte[] SERVER_X = "Server: X\r\n".getBytes();

	private static final byte[] CONTENT_LENGTH_HDR = "Content-Length: ".getBytes();

	private static final byte[] CONTENT_TYPE_PLAIN = "Content-Type: text/plain; charset=UTF-8\r\n".getBytes();

	private static final byte[] RESPONSE = "Hello".getBytes();

	private static final byte[] DATE_HDR = "Date: ".getBytes();

	private static final byte[] RESPONSE_LENGTH = String.valueOf(RESPONSE.length).getBytes();

	private static final byte[] PLAIN = "/plain".getBytes();

	public void process(Ctx ctx) {

		Buf buf = ctx.input();

		Range[] ranges = ctx.helper().ranges;

		Range verb = ranges[ranges.length - 1];
		Range uri = ranges[ranges.length - 2];

		buf.scanTo(SPACE, verb, true);
		buf.scanTo(SPACE, uri, true);
		buf.scanLnLn(ranges);

		boolean isKeepAlive = true;

		ctx.write(HTTP_200_OK);

		ctx.write(CONTENT_LENGTH_HDR);
		ctx.write(RESPONSE_LENGTH);
		ctx.write(CR_LF);

		ctx.write(isKeepAlive ? CONN_KEEP_ALIVE : CONN_CLOSE);

		ctx.write(SERVER_X);

		ctx.write(DATE_HDR);
		ctx.write(U.getDateTimeBytes());
		ctx.write(CR_LF);

		if (buf.matches(uri, PLAIN, true)) {
			ctx.write(CONTENT_TYPE_PLAIN);
		}

		ctx.write(CR_LF);

		ctx.write(RESPONSE);

		ctx.complete(!isKeepAlive);
	}

}
