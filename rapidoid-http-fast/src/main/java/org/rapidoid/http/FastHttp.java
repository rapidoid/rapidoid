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
import org.rapidoid.buffer.Buf;
import org.rapidoid.bufstruct.BufMap;
import org.rapidoid.bufstruct.BufMapImpl;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.commons.*;
import org.rapidoid.data.JSON;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.http.handler.FastHttpHandler;
import org.rapidoid.http.handler.FastStaticResourcesHandler;
import org.rapidoid.http.impl.HandlerMatch;
import org.rapidoid.http.impl.HandlerMatchWithParams;
import org.rapidoid.http.listener.FastHttpListener;
import org.rapidoid.log.Log;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.UTILS;
import org.rapidoid.wire.Wire;
import org.rapidoid.wrap.BoolWrap;

import java.io.Serializable;
import java.util.*;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class FastHttp implements Protocol, HttpMetadata {

	public static final String[] DEFAULT_STATIC_FILES_LOCATIONS = {"static", "rapidoid/static"};

	private static final byte[] HTTP_200_OK = "HTTP/1.1 200 OK\r\n".getBytes();

	private static final byte[] HTTP_400_BAD_REQUEST = "HTTP/1.1 404 Bad Request\r\nContent-Length: 12\r\n\r\nBad Request!"
			.getBytes();

	private static final byte[] HTTP_404_NOT_FOUND = "HTTP/1.1 404 Not Found\r\nContent-Length: 10\r\n\r\nNot found!"
			.getBytes();

	private static final byte[] HEADER_SEP = ": ".getBytes();

	private static final byte[] CONN_KEEP_ALIVE = "Connection: keep-alive\r\n".getBytes();

	private static final byte[] CONN_CLOSE = "Connection: close\r\n".getBytes();

	private static final byte[] SERVER_HEADER = "Server: Rapidoid\r\n".getBytes();

	private static final byte[] CONTENT_LENGTH_IS = "Content-Length: ".getBytes();

	static final byte[] CONTENT_LENGTH_UNKNOWN = "Content-Length:           ".getBytes();

	private static final int CONTENT_LENGTHS_SIZE = 5000;

	private static final byte[] DATE_IS = "Date: ".getBytes();

	private static final HttpParser HTTP_PARSER = Wire.singleton(HttpParser.class);

	private static final byte[] _POST = Constants.POST.getBytes();
	private static final byte[] _PUT = Constants.PUT.getBytes();
	private static final byte[] _DELETE = Constants.DELETE.getBytes();
	private static final byte[] _PATCH = Constants.PATCH.getBytes();
	private static final byte[] _OPTIONS = Constants.OPTIONS.getBytes();
	private static final byte[] _HEAD = Constants.HEAD.getBytes();
	private static final byte[] _TRACE = Constants.TRACE.getBytes();

	private static final byte[][] CONTENT_LENGTHS = new byte[CONTENT_LENGTHS_SIZE][];

	private final HttpResponseCodes responseCodes = new HttpResponseCodes();

	private final BufMap<FastHttpHandler> getHandlers = new BufMapImpl<FastHttpHandler>();
	private final BufMap<FastHttpHandler> postHandlers = new BufMapImpl<FastHttpHandler>();
	private final BufMap<FastHttpHandler> putHandlers = new BufMapImpl<FastHttpHandler>();
	private final BufMap<FastHttpHandler> deleteHandlers = new BufMapImpl<FastHttpHandler>();
	private final BufMap<FastHttpHandler> patchHandlers = new BufMapImpl<FastHttpHandler>();
	private final BufMap<FastHttpHandler> optionsHandlers = new BufMapImpl<FastHttpHandler>();
	private final BufMap<FastHttpHandler> headHandlers = new BufMapImpl<FastHttpHandler>();
	private final BufMap<FastHttpHandler> traceHandlers = new BufMapImpl<FastHttpHandler>();

	private final Map<PathPattern, FastHttpHandler> paternGetHandlers = new LinkedHashMap<PathPattern, FastHttpHandler>();
	private final Map<PathPattern, FastHttpHandler> paternPostHandlers = new LinkedHashMap<PathPattern, FastHttpHandler>();
	private final Map<PathPattern, FastHttpHandler> paternPutHandlers = new LinkedHashMap<PathPattern, FastHttpHandler>();
	private final Map<PathPattern, FastHttpHandler> paternDeleteHandlers = new LinkedHashMap<PathPattern, FastHttpHandler>();
	private final Map<PathPattern, FastHttpHandler> paternPatchHandlers = new LinkedHashMap<PathPattern, FastHttpHandler>();
	private final Map<PathPattern, FastHttpHandler> paternOptionsHandlers = new LinkedHashMap<PathPattern, FastHttpHandler>();
	private final Map<PathPattern, FastHttpHandler> paternHeadHandlers = new LinkedHashMap<PathPattern, FastHttpHandler>();
	private final Map<PathPattern, FastHttpHandler> paternTraceHandlers = new LinkedHashMap<PathPattern, FastHttpHandler>();

	private volatile byte[] path1, path2, path3;

	private volatile FastHttpHandler handler1, handler2, handler3;

	private final List<FastHttpHandler> genericHandlers = Coll.synchronizedList();

	private volatile FastHttpHandler staticResourcesHandler = new FastStaticResourcesHandler(this);

	private volatile String[] staticFilesLocations = DEFAULT_STATIC_FILES_LOCATIONS;

	private volatile FastHttpHandler errorHandler;

	private volatile ViewRenderer renderer;

	private final FastHttpListener listener;

	private final Map<String, Object> attributes = Coll.synchronizedMap();

	private final Map<String, Map<String, Serializable>> sessions = Coll.mapOfMaps();

	static {
		for (int len = 0; len < CONTENT_LENGTHS.length; len++) {
			CONTENT_LENGTHS[len] = (new String(CONTENT_LENGTH_IS) + len + new String(CR_LF)).getBytes();
		}
	}

	public FastHttp(FastHttpListener listener) {
		this.listener = listener;
	}

	public synchronized void on(String verb, String path, FastHttpHandler handler) {
		addOrRemove(true, verb, path, handler);
	}

	public synchronized void remove(String verb, String path) {
		addOrRemove(false, verb, path, null);
	}

	private void addOrRemove(boolean add, String verbs, String path, FastHttpHandler handler) {
		U.notNull(verbs, "HTTP verbs");
		U.notNull(path, "HTTP path");

		if (add) {
			U.notNull(handler, "HTTP handler");
		}

		verbs = verbs.toUpperCase();
		if (path.length() > 1) {
			path = Str.trimr(path, "/");
		}

		if (add) {
			Log.info("Registering handler", "verbs", verbs, "path", path, "handler", handler);
		} else {
			Log.info("Deregistering handler", "verbs", verbs, "path", path);
		}

		for (String verb : verbs.split(",")) {
			if (add) {
				deregister(HttpVerb.from(verb), path);
				register(HttpVerb.from(verb), path, handler);
			} else {
				deregister(HttpVerb.from(verb), path);
			}
		}
	}

	private void register(HttpVerb verb, String path, FastHttpHandler handler) {
		boolean isPattern = isPattern(path);
		PathPattern pathPattern = isPattern ? PathPattern.from(path) : null;

		switch (verb) {
			case GET:
				if (!isPattern) {
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
				} else {
					paternGetHandlers.put(pathPattern, handler);
				}
				break;

			case POST:
				if (!isPattern) {
					postHandlers.put(path, handler);
				} else {
					paternPostHandlers.put(pathPattern, handler);
				}
				break;

			case PUT:
				if (!isPattern) {
					putHandlers.put(path, handler);
				} else {
					paternPutHandlers.put(pathPattern, handler);
				}
				break;

			case DELETE:
				if (!isPattern) {
					deleteHandlers.put(path, handler);
				} else {
					paternDeleteHandlers.put(pathPattern, handler);
				}
				break;

			case PATCH:
				if (!isPattern) {
					patchHandlers.put(path, handler);
				} else {
					paternPatchHandlers.put(pathPattern, handler);
				}
				break;

			case OPTIONS:
				if (!isPattern) {
					optionsHandlers.put(path, handler);
				} else {
					paternOptionsHandlers.put(pathPattern, handler);
				}
				break;

			case HEAD:
				if (!isPattern) {
					headHandlers.put(path, handler);
				} else {
					paternHeadHandlers.put(pathPattern, handler);
				}
				break;

			case TRACE:
				if (!isPattern) {
					traceHandlers.put(path, handler);
				} else {
					paternTraceHandlers.put(pathPattern, handler);
				}
				break;

			default:
				throw Err.notExpected();
		}
	}

	private void deregister(HttpVerb verb, String path) {
		boolean isPattern = isPattern(path);
		PathPattern pathPattern = isPattern ? PathPattern.from(path) : null;

		switch (verb) {
			case GET:
				if (!isPattern) {
					if (path1 != null && new String(path1).equals(path)) {
						path1 = null;
					}

					if (path2 != null && new String(path2).equals(path)) {
						path2 = null;
					}

					if (path3 != null && new String(path3).equals(path)) {
						path3 = null;
					}

					getHandlers.remove(path);
				} else {
					paternGetHandlers.remove(pathPattern);
				}
				break;

			case POST:
				if (!isPattern) {
					postHandlers.remove(path);
				} else {
					paternPostHandlers.remove(pathPattern);
				}
				break;

			case PUT:
				if (!isPattern) {
					putHandlers.remove(path);
				} else {
					paternPutHandlers.remove(pathPattern);
				}
				break;

			case DELETE:
				if (!isPattern) {
					deleteHandlers.remove(path);
				} else {
					paternDeleteHandlers.remove(pathPattern);
				}
				break;

			case PATCH:
				if (!isPattern) {
					patchHandlers.remove(path);
				} else {
					paternPatchHandlers.remove(pathPattern);
				}
				break;

			case OPTIONS:
				if (!isPattern) {
					optionsHandlers.remove(path);
				} else {
					paternOptionsHandlers.remove(pathPattern);
				}
				break;

			case HEAD:
				if (!isPattern) {
					headHandlers.remove(path);
				} else {
					paternHeadHandlers.remove(pathPattern);
				}
				break;

			case TRACE:
				if (!isPattern) {
					traceHandlers.remove(path);
				} else {
					paternTraceHandlers.remove(pathPattern);
				}
				break;

			default:
				throw Err.notExpected();
		}

	}

	private boolean isPattern(String path) {
		return path.contains("{") || path.contains("}");
	}

	public void addGenericHandler(FastHttpHandler handler) {
		genericHandlers.add(handler);
	}

	public void removeGenericHandler(FastHttpHandler handler) {
		genericHandlers.remove(handler);
	}

	@SuppressWarnings("unchecked")
	public void process(Channel channel) {
		if (channel.isInitial()) {
			return;
		}

		Buf buf = channel.input();
		RapidoidHelper helper = channel.helper();

		Range[] ranges = helper.ranges1.ranges;
		Ranges hdrs = helper.ranges2;

		BoolWrap isGet = helper.booleans[0];
		BoolWrap isKeepAlive = helper.booleans[1];

		Range xverb = ranges[ranges.length - 1];
		Range xuri = ranges[ranges.length - 2];
		Range xpath = ranges[ranges.length - 3];
		Range xquery = ranges[ranges.length - 4];
		Range xprotocol = ranges[ranges.length - 5];
		Range xbody = ranges[ranges.length - 6];

		HTTP_PARSER.parse(buf, isGet, isKeepAlive, xbody, xverb, xuri, xpath, xquery, xprotocol, hdrs, helper);

		removeTrailingSlash(buf, xpath);
		removeTrailingSlash(buf, xuri);

		String err = validateRequest(buf, xverb, xuri);
		if (err != null) {
			channel.write(HTTP_400_BAD_REQUEST);
			channel.close();
			return;
		}

		// the listener may override all the request dispatching and handler execution
		if (!listener.request(this, channel, isGet, isKeepAlive, xbody, xverb, xuri, xpath, xquery, xprotocol, hdrs)) {
			return;
		}

		HttpStatus status = HttpStatus.NOT_FOUND;
		HandlerMatch match = findHandler(buf, isGet, xverb, xpath);

		FastHttpHandler handler = match != null ? match.getHandler() : null;
		boolean noReq = (handler != null && !handler.needsParams());

		ReqImpl req = null;

		if (!noReq) {
			KeyValueRanges paramsKV = helper.pairs1.reset();
			KeyValueRanges headersKV = helper.pairs2.reset();
			KeyValueRanges cookiesKV = helper.pairs5.reset();

			HTTP_PARSER.parseParams(buf, paramsKV, xquery);
			Map<String, String> params = U.cast(paramsKV.toMap(buf, true, true));

			if (match != null && match.getParams() != null) {
				params.putAll(match.getParams());
			}

			HTTP_PARSER.parseHeadersIntoKV(buf, hdrs, headersKV, cookiesKV, helper);
			Map<String, String> headers = U.cast(headersKV.toMap(buf, true, true));
			Map<String, String> cookies = U.cast(cookiesKV.toMap(buf, true, true));

			byte[] body;
			Map<String, Object> posted;
			Map<String, byte[]> files;

			if (!isGet.value && !xbody.isEmpty()) {
				KeyValueRanges postedKV = helper.pairs3.reset();
				KeyValueRanges filesKV = helper.pairs4.reset();

				body = xbody.bytes(buf);

				// parse posted body as data
				posted = new HashMap<String, Object>();
				HTTP_PARSER.parsePosted(buf, headersKV, xbody, postedKV, filesKV, helper, posted);
				posted = Collections.synchronizedMap(posted);

				files = Collections.synchronizedMap(filesKV.toBinaryMap(buf, true));

			} else {
				posted = Collections.EMPTY_MAP;
				files = Collections.EMPTY_MAP;
				body = null;
			}

			String verb = xverb.str(buf);
			String uri = xuri.str(buf);
			String path = UTILS.urlDecode(xpath.str(buf));
			String query = UTILS.urlDecode(xquery.str(buf));

			MediaType contentType = handler != null ? handler.contentType() : MediaType.HTML_UTF_8;

			params = Collections.synchronizedMap(params);
			headers = Collections.synchronizedMap(headers);
			cookies = Collections.synchronizedMap(cookies);

			req = new ReqImpl(this, channel, isKeepAlive.value, verb, uri, path, query, body, params, headers, cookies,
					posted, files, contentType);

			if (!attributes.isEmpty()) {
				req.attrs().putAll(attributes);
			}
		}

		if (handler != null) {
			status = handler.handle(channel, isKeepAlive.value, req, null);
		}

		if (status == HttpStatus.NOT_FOUND) {
			status = tryGenericHandlers(channel, isKeepAlive.value, req);
		}

		if (status == HttpStatus.NOT_FOUND) {
			channel.write(HTTP_404_NOT_FOUND);
			listener.notFound(this, channel, isGet, isKeepAlive, xbody, xverb, xuri, xpath, xquery, xprotocol, hdrs);
		}

		if (status != HttpStatus.ASYNC) {
			channel.closeIf(!isKeepAlive.value);
		}
	}

	private HttpStatus tryGenericHandlers(Channel channel, boolean isKeepAlive, Req req) {
		for (FastHttpHandler handler : genericHandlers) {

			HttpStatus status = handler.handle(channel, isKeepAlive, req, null);

			if (status != HttpStatus.NOT_FOUND) {
				return status;
			}
		}

		return HttpStatus.NOT_FOUND;
	}

	private HandlerMatch findHandler(Buf buf, BoolWrap isGet, Range verb, Range path) {
		Bytes bytes = buf.bytes();

		if (isGet.value) {
			if (path1 != null && BytesUtil.matches(bytes, path, path1, true)) {
				return handler1;
			} else if (path2 != null && BytesUtil.matches(bytes, path, path2, true)) {
				return handler2;
			} else if (path3 != null && BytesUtil.matches(bytes, path, path3, true)) {
				return handler3;
			} else {
				HandlerMatch handler = getHandlers.get(buf, path);

				if (handler == null && !paternGetHandlers.isEmpty()) {
					handler = matchByPattern(paternGetHandlers, buf.get(path));
				}

				if (handler == null) {
					handler = staticResourcesHandler;
				}

				return handler;
			}

		} else if (BytesUtil.matches(bytes, verb, _POST, true)) {
			HandlerMatch handler = postHandlers.get(buf, path);

			if (handler == null && !paternPostHandlers.isEmpty()) {
				handler = matchByPattern(paternPostHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _PUT, true)) {
			HandlerMatch handler = putHandlers.get(buf, path);

			if (handler == null && !paternPutHandlers.isEmpty()) {
				handler = matchByPattern(paternPutHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _DELETE, true)) {
			HandlerMatch handler = deleteHandlers.get(buf, path);

			if (handler == null && !paternDeleteHandlers.isEmpty()) {
				handler = matchByPattern(paternDeleteHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _PATCH, true)) {
			HandlerMatch handler = patchHandlers.get(buf, path);

			if (handler == null && !paternPatchHandlers.isEmpty()) {
				handler = matchByPattern(paternPatchHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _OPTIONS, true)) {
			HandlerMatch handler = optionsHandlers.get(buf, path);

			if (handler == null && !paternOptionsHandlers.isEmpty()) {
				handler = matchByPattern(paternOptionsHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _HEAD, true)) {
			HandlerMatch handler = headHandlers.get(buf, path);

			if (handler == null && !paternHeadHandlers.isEmpty()) {
				handler = matchByPattern(paternHeadHandlers, buf.get(path));
			}

			return handler;

		} else if (BytesUtil.matches(bytes, verb, _TRACE, true)) {
			HandlerMatch handler = traceHandlers.get(buf, path);

			if (handler == null && !paternTraceHandlers.isEmpty()) {
				handler = matchByPattern(paternTraceHandlers, buf.get(path));
			}

			return handler;
		}

		return null; // no handler
	}

	private HandlerMatch matchByPattern(Map<PathPattern, FastHttpHandler> handlers, String path) {
		for (Map.Entry<PathPattern, FastHttpHandler> e : handlers.entrySet()) {

			PathPattern pattern = e.getKey();
			Map<String, String> params = pattern.match(path);

			if (params != null) {
				return new HandlerMatchWithParams(e.getValue(), params);
			}
		}

		return null;
	}

	public void start200(Channel ctx, boolean isKeepAlive, MediaType contentType) {
		ctx.write(HTTP_200_OK);
		addDefaultHeaders(ctx, isKeepAlive, contentType);
	}

	public void startResponse(Channel ctx, int code, boolean isKeepAlive, MediaType contentType) {
		ctx.write(responseCodes.get(code));
		addDefaultHeaders(ctx, isKeepAlive, contentType);
	}

	private void addDefaultHeaders(Channel ctx, boolean isKeepAlive, MediaType contentType) {
		ctx.write(isKeepAlive ? CONN_KEEP_ALIVE : CONN_CLOSE);

		ctx.write(SERVER_HEADER);

		ctx.write(DATE_IS);
		ctx.write(Dates.getDateTimeBytes());
		ctx.write(CR_LF);

		ctx.write(contentType.asHttpHeader());
	}

	void addCustomHeader(Channel ctx, byte[] name, byte[] value) {
		ctx.write(name);
		ctx.write(HEADER_SEP);
		ctx.write(value);
		ctx.write(CR_LF);
	}

	public void write200(Channel ctx, boolean isKeepAlive, MediaType contentTypeHeader, byte[] content) {
		start200(ctx, isKeepAlive, contentTypeHeader);
		writeContent(ctx, content);
		listener.onOkResponse(contentTypeHeader, content);
	}

	public void error(Channel ctx, boolean isKeepAlive, Req req, Throwable error) {
		if (errorHandler != null) {
			errorHandler.handle(ctx, isKeepAlive, req, error);
		} else {
			defaultErrorHandling(ctx, isKeepAlive, error);
		}
	}

	private void defaultErrorHandling(Channel ctx, boolean isKeepAlive, Throwable error) {
		Log.error("Error while processing request!", error);

		startResponse(ctx, 500, isKeepAlive, MediaType.HTML_UTF_8);
		writeContent(ctx, HttpUtils.getErrorMessage(error).getBytes());

		done(ctx, isKeepAlive);
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
		start200(ctx, isKeepAlive, MediaType.JSON_UTF_8);

		Buf out = ctx.output();

		ctx.write(CONTENT_LENGTH_UNKNOWN);

		int posConLen = out.size();
		ctx.write(CR_LF);

		// finishing the headers
		ctx.write(CR_LF);

		int posBefore = out.size();

		JSON.stringify(value, out.asOutputStream());

		int posAfter = out.size();
		int contentLength = posAfter - posBefore;

		out.putNumAsText(posConLen, contentLength, false);
	}

	public void done(Channel ctx, boolean isKeepAlive) {
		ctx.done();
		ctx.closeIf(!isKeepAlive);
	}

	public FastHttpListener getListener() {
		return listener;
	}

	public synchronized void resetConfig() {
		path1 = path2 = path3 = null;
		handler1 = handler2 = handler3 = null;

		staticFilesLocations = DEFAULT_STATIC_FILES_LOCATIONS;
		staticResourcesHandler = new FastStaticResourcesHandler(this);
		errorHandler = null;
		renderer = null;

		getHandlers.clear();
		postHandlers.clear();
		putHandlers.clear();
		deleteHandlers.clear();
		optionsHandlers.clear();
		genericHandlers.clear();

		paternGetHandlers.clear();
		paternPostHandlers.clear();
		paternPutHandlers.clear();
		paternDeleteHandlers.clear();
		paternPatchHandlers.clear();
		paternOptionsHandlers.clear();
		paternHeadHandlers.clear();
		paternTraceHandlers.clear();
	}

	public void renderBody(Channel ctx, int code, MediaType contentType, byte[] body) {
		ctx.write(body);

		if (code == 200) {
			listener.onOkResponse(contentType, body);
		} else {
			listener.onErrorResponse(code, contentType, body);
		}
	}

	public void notFound(Channel ctx, boolean isKeepAlive, FastHttpHandler fromHandler, Req req) {
		int count = genericHandlers.size();

		HttpStatus status = HttpStatus.NOT_FOUND;

		for (int i = 0; i < count; i++) {
			FastHttpHandler handler = genericHandlers.get(i);
			if (handler == fromHandler) {
				if (i < count - 1) {
					FastHttpHandler nextHandler = genericHandlers.get(i + 1);
					status = nextHandler.handle(ctx, isKeepAlive, req, null);
					break;
				}
			}
		}

		if (status == HttpStatus.NOT_FOUND) {
			ctx.write(HTTP_404_NOT_FOUND);
			done(ctx, isKeepAlive);
		}
	}

	private static void removeTrailingSlash(Buf buf, Range range) {
		if (range.length > 1 && buf.get(range.last()) == '/') {
			range.length--;
		}
	}

	private static String validateRequest(Buf input, Range verb, Range uri) {
		if (verb.isEmpty()) {
			return "HTTP verb cannot be empty!";
		}

		if (!BytesUtil.isValidURI(input.bytes(), uri)) {
			return "Invalid HTTP URI!";
		}

		return null; // OK, no error
	}

	public void setStaticResourcesHandler(FastHttpHandler staticResourcesHandler) {
		this.staticResourcesHandler = staticResourcesHandler;
	}

	public FastHttpHandler getStaticResourcesHandler() {
		return staticResourcesHandler;
	}

	public Map<String, Object> attributes() {
		return attributes;
	}

	public Map<String, Serializable> session(String sessionId) {
		return sessions.get(sessionId);
	}

	public void setErrorHandler(FastHttpHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public FastHttpHandler getErrorHandler() {
		return errorHandler;
	}

	public void setStaticFilesLocations(String... staticFilesLocations) {
		this.staticFilesLocations = staticFilesLocations;
	}

	public String[] getStaticFilesLocations() {
		return staticFilesLocations;
	}

	public void setRenderer(ViewRenderer renderer) {
		this.renderer = renderer;
	}

	public ViewRenderer getRenderer() {
		return renderer;
	}

}
