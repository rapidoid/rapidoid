package org.rapidoidx.demo.http;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.dates.Dates;
import org.rapidoid.http.fast.HttpParser;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.wire.Wire;
import org.rapidoid.wrap.BoolWrap;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class SimpleHttpProtocol implements Protocol {

	private static final byte[] HTTP_200_OK = "HTTP/1.1 200 OK\r\n".getBytes();

	private static final byte[] HTTP_404_NOT_FOUND = "HTTP/1.1 404 Not Found\r\nContent-Length: 10\r\n\r\nNot found!"
			.getBytes();

	private static final byte[] CONN_KEEP_ALIVE = "Connection: keep-alive\r\n".getBytes();

	private static final byte[] CONN_CLOSE = "Connection: close\r\n".getBytes();

	private static final byte[] SERVER_X = "Server: X\r\n".getBytes();

	private static final byte[] CONTENT_LENGTH_HDR = "Content-Length: ".getBytes();

	private static final byte[] CONTENT_TYPE_PLAIN = "Content-Type: text/plain; charset=UTF-8\r\n".getBytes();

	private static final byte[] CONTENT_TYPE_JSON = "Content-Type: application/json; charset=UTF-8\r\n".getBytes();

	private static final byte[] CONTENT_LENGTH = "Content-Length:           ".getBytes();

	private static final byte[] RESPONSE = "Hello, World!".getBytes();

	private static final byte[] DATE_HDR = "Date: ".getBytes();

	private static final byte[] RESPONSE_LENGTH = String.valueOf(RESPONSE.length).getBytes();

	private static final byte[] URI_PLAIN = "/plaintext".getBytes();

	private static final byte[] URI_JSON = "/json".getBytes();

	private static final HttpParser HTTP_PARSER = Wire.singleton(HttpParser.class);

	public void process(Channel ctx) {
		if (ctx.isInitial()) {
			return;
		}

		Buf buf = ctx.input();
		RapidoidHelper helper = ctx.helper();

		Range[] ranges = helper.ranges1.ranges;
		Ranges headers = helper.ranges2;

		BoolWrap isGet = helper.booleans[0];
		BoolWrap isKeepAlive = helper.booleans[1];

		Range verb = ranges[ranges.length - 1];
		Range uri = ranges[ranges.length - 2];
		Range path = ranges[ranges.length - 3];
		Range query = ranges[ranges.length - 4];
		Range protocol = ranges[ranges.length - 5];
		Range body = ranges[ranges.length - 6];

		HTTP_PARSER.parse(buf, isGet, isKeepAlive, body, verb, uri, path, query, protocol, headers, helper);

		response(ctx, buf, path, isGet.value, isKeepAlive.value);
	}

	private void response(Channel ctx, Buf buf, Range path, boolean isGet, boolean isKeepAlive) {
		boolean processed = false;

		if (isGet) {

			ctx.write(HTTP_200_OK);

			ctx.write(isKeepAlive ? CONN_KEEP_ALIVE : CONN_CLOSE);

			ctx.write(SERVER_X);

			ctx.write(DATE_HDR);
			ctx.write(Dates.getDateTimeBytes());
			ctx.write(CR_LF);

			if (BytesUtil.matches(buf.bytes(), path, URI_PLAIN, true) || path.length == 1) {
				handlePlaintext(ctx);
				processed = true;
			} else if (BytesUtil.matches(buf.bytes(), path, URI_JSON, true)) {
				handleJson(ctx);
				processed = true;
			}

			ctx.closeIf(!isKeepAlive);
		}

		if (!processed) {
			ctx.write(HTTP_404_NOT_FOUND);
			ctx.close();
		}
	}

	private void handlePlaintext(Channel ctx) {
		ctx.write(CONTENT_LENGTH_HDR);
		ctx.write(RESPONSE_LENGTH);
		ctx.write(CR_LF);

		ctx.write(CONTENT_TYPE_PLAIN);
		ctx.write(CR_LF);
		ctx.write(RESPONSE);
	}

	private void handleJson(Channel ctx) {
		Buf output = ctx.output();

		ctx.write(CONTENT_TYPE_JSON);
		ctx.write(CONTENT_LENGTH);

		int posConLen = output.size() - 10;
		ctx.write(CR_LF);
		ctx.write(CR_LF);

		int posBefore = output.size();

		ctx.writeJSON(new Msg("Hello, World!"));

		int posAfter = output.size();
		output.putNumAsText(posConLen, posAfter - posBefore, false);
	}

}
