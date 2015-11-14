package org.rapidoid.http.fast;

/*
 * #%L
 * rapidoid-http-fast
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

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

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
import org.rapidoid.log.Log;
import org.rapidoid.mime.MediaType;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.u.U;
import org.rapidoid.wire.Wire;
import org.rapidoid.wrap.BoolWrap;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class FastHttp implements Protocol, HttpMetadata {

	private static final byte[] HTTP_200_OK = "HTTP/1.1 200 OK\r\n".getBytes();

	private static final byte[] HTTP_500_ERROR = "HTTP/1.1 500 Internal Server Error\r\n".getBytes();

	private static final byte[] HTTP_404_NOT_FOUND = "HTTP/1.1 404 Not Found\r\nContent-Length: 10\r\n\r\nNot found!"
			.getBytes();

	private static final byte[] HEADER_SEP = ": ".getBytes();

	private static final byte[] CONN_KEEP_ALIVE = "Connection: keep-alive\r\n".getBytes();

	private static final byte[] CONN_CLOSE = "Connection: close\r\n".getBytes();

	private static final byte[] SERVER_HEADER = "Server: Rapidoid\r\n".getBytes();

	private static final byte[] CONTENT_LENGTH_IS = "Content-Length: ".getBytes();

	private static final byte[] CONTENT_LENGTH_UNKNOWN = "Content-Length:           ".getBytes();

	private static final int CONTENT_LENGTHS_SIZE = 5000;

	private static final byte[] DATE_IS = "Date: ".getBytes();

	public static final byte[] CONTENT_TYPE_PLAIN = MediaType.PLAIN_TEXT_UTF_8.asHttpHeader();

	public static final byte[] CONTENT_TYPE_HTML = MediaType.HTML_UTF_8.asHttpHeader();

	public static final byte[] CONTENT_TYPE_JSON = MediaType.JSON_UTF_8.asHttpHeader();

	public static final byte[] CONTENT_TYPE_BINARY = MediaType.BINARY.asHttpHeader();

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

	private volatile byte[] path1, path2, path3;

	private volatile FastHttpHandler handler1, handler2, handler3;

	private volatile FastHttpHandler staticResourcesHandler = new FastStaticResourcesHandler(this);

	private final FastHttpListener listener;

	static {
		for (int len = 0; len < CONTENT_LENGTHS.length; len++) {
			CONTENT_LENGTHS[len] = (new String(CONTENT_LENGTH_IS) + len + new String(CR_LF)).getBytes();
		}
	}

	public FastHttp(FastHttpListener listener) {
		this.listener = listener;
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
		Ranges hdrs = helper.ranges2;

		BoolWrap isGet = helper.booleans[0];
		BoolWrap isKeepAlive = helper.booleans[1];

		Range verb = ranges[ranges.length - 1];
		Range uri = ranges[ranges.length - 2];
		Range path = ranges[ranges.length - 3];
		Range query = ranges[ranges.length - 4];
		Range protocol = ranges[ranges.length - 5];
		Range body = ranges[ranges.length - 6];

		HTTP_PARSER.parse(buf, isGet, isKeepAlive, body, verb, uri, path, query, protocol, hdrs, helper);

		// the listener may override all the request dispatching and handler execution
		if (!listener.request(this, ctx, isGet, isKeepAlive, body, verb, uri, path, query, protocol, hdrs)) {
			return;
		}

		HttpStatus status = HttpStatus.NOT_FOUND;

		FastHttpHandler handler = findFandler(ctx, buf, isGet, verb, path);

		if (handler != null) {
			Map<String, Object> params = null;

			if (handler.needsParams()) {
				params = U.map();

				KeyValueRanges paramsKV = helper.pairs1.reset();
				KeyValueRanges headersKV = helper.pairs2.reset();

				HTTP_PARSER.parseParams(buf, paramsKV, query);

				// parse URL parameters as data
				Map<String, Object> data = U.cast(paramsKV.toMap(buf, true, true));

				if (!isGet.value) {
					KeyValueRanges postedKV = helper.pairs3.reset();
					KeyValueRanges filesKV = helper.pairs4.reset();

					// parse posted body as data
					HTTP_PARSER.parsePosted(buf, headersKV, body, postedKV, filesKV, helper, data);
				}

				// filter special data values
				Map<String, Object> special = findSpecialData(data);

				if (special != null) {
					data.keySet().removeAll(special.keySet());
				} else {
					special = U.cast(Collections.EMPTY_MAP);
				}

				// put all data directly as parameters
				params.putAll(data);

				params.put(DATA, data);
				params.put(SPECIAL, special);

				// finally, the HTTP info
				params.put(VERB, verb.str(buf));
				params.put(URI, uri.str(buf));
				params.put(PATH, path.str(buf));

				params.put(CLIENT_ADDRESS, ctx.address());

				if (handler.needsHeadersAndCookies()) {
					KeyValueRanges cookiesKV = helper.pairs5.reset();
					HTTP_PARSER.parseHeadersIntoKV(buf, hdrs, headersKV, cookiesKV, helper);

					Map<String, Object> headers = U.cast(headersKV.toMap(buf, true, true));
					Map<String, Object> cookies = U.cast(cookiesKV.toMap(buf, true, true));

					params.put(HEADERS, headers);
					params.put(COOKIES, cookies);

					params.put(HOST, U.get(headers, "Host", null));
					params.put(FORWARDED_FOR, U.get(headers, "X-Forwarded-For", null));
				}
			}

			status = handler.handle(ctx, isKeepAlive.value, params);
		}

		if (status == HttpStatus.NOT_FOUND) {
			ctx.write(HTTP_404_NOT_FOUND);
			listener.notFound(this, ctx, isGet, isKeepAlive, body, verb, uri, path, query, protocol, hdrs);
		}

		if (status != HttpStatus.ASYNC) {
			ctx.closeIf(!isKeepAlive.value);
		}
	}

	private Map<String, Object> findSpecialData(Map<String, Object> data) {
		Map<String, Object> special = null;

		for (Entry<String, Object> param : data.entrySet()) {
			String name = param.getKey();

			if (name.startsWith("$")) {
				special = U.safe(special);
				special.put(name, param.getValue());
			}
		}

		return special;
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
				FastHttpHandler getHandler = getHandlers.get(buf, path);

				if (getHandler == null) {
					getHandler = staticResourcesHandler;
				}

				return getHandler;
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

		addDefaultHeaders(ctx, isKeepAlive, contentType);
	}

	private void start500(Channel ctx, boolean isKeepAlive, byte[] contentType) {
		ctx.write(HTTP_500_ERROR);

		addDefaultHeaders(ctx, isKeepAlive, contentType);
	}

	private void addDefaultHeaders(Channel ctx, boolean isKeepAlive, byte[] contentType) {
		ctx.write(isKeepAlive ? CONN_KEEP_ALIVE : CONN_CLOSE);

		ctx.write(SERVER_HEADER);

		ctx.write(DATE_IS);
		ctx.write(Dates.getDateTimeBytes());
		ctx.write(CR_LF);

		ctx.write(contentType);
	}

	private void addCustomHeader(Channel ctx, byte[] name, byte[] value) {
		ctx.write(name);
		ctx.write(HEADER_SEP);
		ctx.write(value);
		ctx.write(CR_LF);
	}

	public void write200(Channel ctx, boolean isKeepAlive, byte[] contentTypeHeader, byte[] content) {
		start200(ctx, isKeepAlive, contentTypeHeader);
		writeContent(ctx, content);
		listener.onOkResponse(contentTypeHeader, content);
	}

	public void write500(Channel ctx, boolean isKeepAlive, byte[] contentTypeHeader, byte[] content) {
		start500(ctx, isKeepAlive, contentTypeHeader);
		writeContent(ctx, content);
		listener.onErrorResponse(500, contentTypeHeader, content);
	}

	public HttpStatus error(Channel ctx, boolean isKeepAlive, Throwable error) {
		Log.error("Error while processing request!", error);

		start500(ctx, isKeepAlive, CONTENT_TYPE_HTML);
		writeContent(ctx, HttpUtils.getErrorMessage(error).getBytes());

		return HttpStatus.ERROR;
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

		int posConLen = out.size();
		ctx.write(CR_LF);
		ctx.write(CR_LF);

		int posBefore = out.size();

		JSON.stringify(value, out.asOutputStream());

		int posAfter = out.size();
		int contentLength = posAfter - posBefore;

		out.putNumAsText(posConLen, contentLength, false);
	}

	public void addCookie(Channel ctx, String name, String value, String... extras) {
		value = HttpUtils.cookieValueWithExtras(value, extras);
		String cookie = name + "=" + value;
		addCustomHeader(ctx, HttpHeaders.SET_COOKIE.getBytes(), cookie.getBytes());
	}

	public void done(Channel ctx, boolean isKeepAlive) {
		ctx.done();
		ctx.closeIf(!isKeepAlive);
	}

	public FastHttpListener getListener() {
		return listener;
	}
}
