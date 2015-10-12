package org.rapidoid.http.fast;

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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bufstruct.BufMap;
import org.rapidoid.bufstruct.BufMapImpl;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.data.JSON;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.dates.Dates;
import org.rapidoid.http.HttpParser;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.util.U;
import org.rapidoid.wire.Wire;
import org.rapidoid.wrap.BoolWrap;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class FastHttp implements Protocol {

	private static final byte[] HTTP_200_OK = "HTTP/1.1 200 OK\r\n".getBytes();

	private static final byte[] HTTP_500_ERROR = "HTTP/1.1 500 Internal Server Error\r\n".getBytes();

	private static final byte[] HTTP_404_NOT_FOUND = "HTTP/1.1 404 Not Found\r\nContent-Length: 10\r\n\r\nNot found!"
			.getBytes();

	private static final byte[] CONN_KEEP_ALIVE = "Connection: keep-alive\r\n".getBytes();

	private static final byte[] CONN_CLOSE = "Connection: close\r\n".getBytes();

	private static final byte[] SERVER_X = "Server: X\r\n".getBytes();

	private static final byte[] CONTENT_LENGTH_IS = "Content-Length: ".getBytes();

	private static final byte[] CONTENT_LENGTH_UNKNOWN = "Content-Length:           ".getBytes();

	private static final int CONTENT_LENGTHS_SIZE = 5000;

	private static final byte[] DATE_IS = "Date: ".getBytes();

	public static final byte[] CONTENT_TYPE_PLAIN = "Content-Type: text/plain; charset=UTF-8\r\n".getBytes();

	public static final byte[] CONTENT_TYPE_HTML = "Content-Type: text/html; charset=UTF-8\r\n".getBytes();

	public static final byte[] CONTENT_TYPE_JSON = "Content-Type: application/json; charset=UTF-8\r\n".getBytes();

	public static final byte[] CONTENT_TYPE_BINARY = "Content-Type: application/octet-stream\r\n".getBytes();

	private static final HttpParser HTTP_PARSER = Wire.singleton(HttpParser.class);

	private static final byte[] POST = "POST".getBytes();

	private static final byte[] PUT = "PUT".getBytes();

	private static final byte[] DELETE = "DELETE".getBytes();

	private static final byte[] OPTIONS = "OPTIONS".getBytes();

	private static final byte[][] CONTENT_LENGTHS = new byte[CONTENT_LENGTHS_SIZE][];

	private final BufMap<FastHttpHandler> getHandlers = new BufMapImpl<FastHttpHandler>();

	private final BufMap<FastHttpHandler> postHandlers = new BufMapImpl<FastHttpHandler>();

	private final BufMap<FastHttpHandler> putHandlers = new BufMapImpl<FastHttpHandler>();

	private final BufMap<FastHttpHandler> deleteHandlers = new BufMapImpl<FastHttpHandler>();

	private final BufMap<FastHttpHandler> optionsHandlers = new BufMapImpl<FastHttpHandler>();

	private byte[] path1, path2, path3;

	private FastHttpHandler handler1, handler2, handler3;

	static {
		for (int len = 0; len < CONTENT_LENGTHS.length; len++) {
			CONTENT_LENGTHS[len] = (new String(CONTENT_LENGTH_IS) + len + new String(CR_LF)).getBytes();
		}
	}

	public synchronized void on(String verb, String path, FastHttpHandler handler) {

		if (verb.equals("GET")) {
			if (path1 == null) {
				path1 = path.getBytes();
				handler1 = handler;

			} else if (path2 == null) {
				path2 = path.getBytes();
				handler2 = handler;

			} else if (path3 == null) {
				path3 = path.getBytes();
				handler3 = handler;

			} else {
				getHandlers.put(path, handler);
			}

		} else if (verb.equals("POST")) {
			postHandlers.put(path, handler);

		} else if (verb.equals("PUT")) {
			putHandlers.put(path, handler);

		} else if (verb.equals("DELETE")) {
			deleteHandlers.put(path, handler);

		} else if (verb.equals("OPTIONS")) {
			optionsHandlers.put(path, handler);

		} else {
			throw U.rte("Unsupported HTTP verb: %s", verb);
		}
	}

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

		boolean processed = false;

		FastHttpHandler handler = findFandler(ctx, buf, isGet, verb, path);

		if (handler != null) {
			Map<String, Object> params = null;

			if (handler.needsParams()) {
				KeyValueRanges paramsKV = helper.pairs1;
				paramsKV.reset();
				HTTP_PARSER.parseParams(buf, paramsKV, query);

				params = U.cast(paramsKV.toMap(buf, true, true));

				if (!isGet.value) {
					KeyValueRanges headersKV = helper.pairs2;
					KeyValueRanges posted = helper.pairs3;
					KeyValueRanges files = helper.pairs4;

					HTTP_PARSER.parsePosted(buf, headersKV, body, posted, files, helper, params);
				}
			}

			if (handler.needsHeadersAndCookies()) {
				// KeyValueRanges cookies = helper.pairs5;
			}

			processed = handler.handle(ctx, isKeepAlive.value, params);
		}

		if (!processed) {
			ctx.write(HTTP_404_NOT_FOUND);
		}

		ctx.closeIf(!isKeepAlive.value);
	}

	private FastHttpHandler findFandler(Channel ctx, Buf buf, BoolWrap isGet, Range verb, Range path) {
		Bytes bytes = buf.bytes();

		if (isGet.value) {
			if (path1 != null && BytesUtil.matches(bytes, path, path1, true)) {
				return handler1;
			} else if (path2 != null && BytesUtil.matches(bytes, path, path2, true)) {
				return handler2;
			} else if (path3 != null && BytesUtil.matches(bytes, path, path3, true)) {
				return handler3;
			} else {
				return getHandlers.get(buf, path);
			}

		} else if (BytesUtil.matches(bytes, verb, POST, true)) {
			return postHandlers.get(buf, path);
		} else if (BytesUtil.matches(bytes, verb, PUT, true)) {
			return putHandlers.get(buf, path);
		} else if (BytesUtil.matches(bytes, verb, DELETE, true)) {
			return deleteHandlers.get(buf, path);
		} else if (BytesUtil.matches(bytes, verb, OPTIONS, true)) {
			return optionsHandlers.get(buf, path);
		}

		return null; // no handler
	}

	public void start200(Channel ctx, boolean isKeepAlive, byte[] contentType) {
		ctx.write(HTTP_200_OK);

		addHeaders(ctx, isKeepAlive, contentType);
	}

	private void start500(Channel ctx, boolean isKeepAlive, byte[] contentType) {
		ctx.write(HTTP_500_ERROR);

		addHeaders(ctx, isKeepAlive, contentType);
	}

	private void addHeaders(Channel ctx, boolean isKeepAlive, byte[] contentType) {
		ctx.write(isKeepAlive ? CONN_KEEP_ALIVE : CONN_CLOSE);

		ctx.write(SERVER_X);

		ctx.write(DATE_IS);
		ctx.write(Dates.getDateTimeBytes());
		ctx.write(CR_LF);

		ctx.write(contentType);
	}

	public void write200(Channel ctx, boolean isKeepAlive, byte[] contentType, byte[] content) {
		start200(ctx, isKeepAlive, contentType);
		writeContent(ctx, content);
	}

	public void write500(Channel ctx, boolean isKeepAlive, byte[] contentType, byte[] content) {
		start500(ctx, isKeepAlive, contentType);
		writeContent(ctx, content);
	}

	private void writeContent(Channel ctx, byte[] content) {
		int len = content.length;

		if (len < CONTENT_LENGTHS_SIZE) {
			ctx.write(CONTENT_LENGTHS[len]);
		} else {
			ctx.write(CONTENT_LENGTH_IS);
			Buf out = ctx.output();
			out.putNumAsText(out.size(), len, true);
			ctx.write(CR_LF);
		}

		ctx.write(CR_LF);
		ctx.write(content);
	}

	public void writeSerializedJson(Channel ctx, boolean isKeepAlive, Object value) {
		start200(ctx, isKeepAlive, CONTENT_TYPE_JSON);

		Buf out = ctx.output();

		ctx.write(CONTENT_LENGTH_UNKNOWN);

		int posConLen = out.size() - 10;
		ctx.write(CR_LF);
		ctx.write(CR_LF);

		int posBefore = out.size();

		JSON.stringify(value, out.asOutputStream());

		int posAfter = out.size();
		out.putNumAsText(posConLen, posAfter - posBefore, false);
	}

}
