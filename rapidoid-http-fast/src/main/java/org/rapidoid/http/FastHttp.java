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
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.commons.Coll;
import org.rapidoid.commons.MediaType;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.handler.FastHttpHandler;
import org.rapidoid.http.impl.HandlerMatch;
import org.rapidoid.http.processor.AbstractHttpProcessor;
import org.rapidoid.log.Log;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class FastHttp extends AbstractHttpProcessor {

	private static final HttpParser HTTP_PARSER = new HttpParser();

	private final HttpRoutes routes;
	private final Customization customization;

	private final Map<String, Object> attributes = Coll.synchronizedMap();
	private final Map<String, Map<String, Serializable>> sessions = Coll.mapOfMaps();

	public FastHttp(HttpRoutes routes, Customization customization) {
		super(null);
		this.routes = routes;
		this.customization = customization;
	}

	public FastHttp(Customization customization) {
		this(new HttpRoutes(customization), customization);
	}

	public synchronized void on(String verb, String path, FastHttpHandler handler) {
		routes.on(verb, path, handler);
	}

	public synchronized void on(String verb, String path, ReqHandler handler) {
		routes.on(verb, path, handler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRequest(Channel channel, boolean isGet, boolean isKeepAlive, Range xbody, Range xverb, Range xuri,
	                      Range xpath, Range xquery, Range xprotocol, Ranges hdrs) {

		RapidoidHelper helper = channel.helper();
		Buf buf = channel.input();

		HttpIO.removeTrailingSlash(buf, xpath);
		HttpIO.removeTrailingSlash(buf, xuri);

		String err = validateRequest(buf, xverb, xuri);
		if (err != null) {
			channel.write(HttpIO.HTTP_400_BAD_REQUEST);
			channel.close();
			return;
		}

		HttpStatus status = HttpStatus.NOT_FOUND;
		HandlerMatch match = routes.findHandler(buf, isGet, xverb, xpath);

		FastHttpHandler handler = match != null ? match.getHandler() : null;
		boolean noReq = (handler != null && !handler.needsParams());

		ReqImpl req = null;
		MediaType contentType = MediaType.HTML_UTF_8;

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

			if (!isGet && !xbody.isEmpty()) {
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

			if (handler != null) {
				contentType = handler.contentType();
			}

			params = Collections.synchronizedMap(params);
			headers = Collections.synchronizedMap(headers);
			cookies = Collections.synchronizedMap(cookies);

			req = new ReqImpl(this, channel, isKeepAlive, verb, uri, path, query, body, params, headers, cookies,
					posted, files, contentType);

			if (!attributes.isEmpty()) {
				req.attrs().putAll(attributes);
			}
		}

		try {
			if (handler != null) {
				status = handler.handle(channel, isKeepAlive, req, null);
			}

			if (status == HttpStatus.NOT_FOUND) {
				status = tryGenericHandlers(channel, isKeepAlive, req);
			}

		} catch (Throwable e) {
			if (handleError(channel, isKeepAlive, req, contentType, e)) return;
		}

		if (status == HttpStatus.NOT_FOUND) {
			HttpIO.write404(channel, isKeepAlive);
		}

		if (status != HttpStatus.ASYNC) {
			channel.closeIf(!isKeepAlive);
		}
	}

	private boolean handleError(Channel channel, boolean isKeepAlive, ReqImpl req, MediaType contentType, Throwable e) {
		if (req != null) {
			HttpIO.errorAndDone(req, e, customization.errorHandler());
			return true;

		} else {
			Log.error("Low-level HTTP handler error!", e);
			HttpIO.startResponse(channel, 500, isKeepAlive, contentType);
			byte[] bytes = HttpUtils.responseToBytes("Internal Server Error!", contentType, custom().jsonResponseRenderer());
			HttpIO.writeContentLengthAndBody(channel, bytes);
			HttpIO.done(channel, isKeepAlive);
		}

		return false;
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

	private HttpStatus tryGenericHandlers(Channel channel, boolean isKeepAlive, Req req) {
		for (FastHttpHandler handler : routes.genericHandlers) {

			HttpStatus status = handler.handle(channel, isKeepAlive, req, null);

			if (status != HttpStatus.NOT_FOUND) {
				return status;
			}
		}

		return HttpStatus.NOT_FOUND;
	}

	public synchronized void resetConfig() {
		routes.reset();
		customization.reset();
	}

	public void notFound(Channel ctx, boolean isKeepAlive, FastHttpHandler fromHandler, Req req) {
		List<FastHttpHandler> genericHandlers = routes.genericHandlers;
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
			HttpIO.write404(ctx, isKeepAlive);
			HttpIO.done(ctx, isKeepAlive);
		}
	}

	public Map<String, Object> attributes() {
		return attributes;
	}

	public Map<String, Serializable> session(String sessionId) {
		return sessions.get(sessionId);
	}

	public Customization custom() {
		return customization;
	}

	public HttpRoutes getRoutes() {
		return routes;
	}

}
