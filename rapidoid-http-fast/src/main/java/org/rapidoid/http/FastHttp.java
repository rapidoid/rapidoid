package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.cache.Cache;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.Config;
import org.rapidoid.config.ConfigImpl;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.JsonResponseRenderer;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.impl.*;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.http.processor.AbstractHttpProcessor;
import org.rapidoid.io.Upload;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-http-fast
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class FastHttp extends AbstractHttpProcessor {

	private static final HttpParser HTTP_PARSER = new HttpParser();

	private static final String INTERNAL_SERVER_ERROR = "Internal Server Error!";

	private static final byte[] BUILT_IN_RES_PATH = "/_rapidoid/".getBytes();

	private final HttpRoutesImpl routes;

	private final Map<String, Object> attributes = Coll.synchronizedMap();

	public FastHttp(HttpRoutesImpl routes) {
		this(routes, new ConfigImpl());
	}

	public FastHttp(HttpRoutesImpl routes, @SuppressWarnings("unused") Config serverConfig) {
		super(null);
		this.routes = routes;
	}

	@Override
	public void onRequest(Channel channel, RapidoidHelper data) {

		Buf buf = channel.input();

		boolean isGet = data.isGet.value;
		boolean isKeepAlive = data.isKeepAlive.value;

		BufRange verb = data.verb;
		BufRange uri = data.uri;
		BufRange path = data.path;

		HttpIO.INSTANCE.removeTrailingSlash(buf, path);
		HttpIO.INSTANCE.removeTrailingSlash(buf, uri);

		String err = validateRequest(buf, verb, uri);
		if (err != null) {
			HttpIO.INSTANCE.writeBadRequest(channel);
			return;
		}

		HttpStatus status = HttpStatus.NOT_FOUND;

		Route matchingRoute = null;
		HandlerMatch match;

		if (isGet && shouldServeBuiltInResources(buf, path)) {
			match = routes.builtInResourcesHandler();
			if (match != null) {
				matchingRoute = match.getRoute();
			}

		} else {

			match = routes.findHandler(buf, isGet, verb, path);
			if (match != null) {
				matchingRoute = match.getRoute();
			}

			if (match == null && isGet) {
				match = routes.staticResourcesHandler();
				if (match != null) {
					matchingRoute = match.getRoute();
				}
			}
		}

		HttpHandler handler = match != null ? match.getHandler() : null;
		boolean noReq = (handler != null && !handler.needsParams());

		ReqImpl req = null;

		if (!noReq) {
			req = createReq(channel, isGet, isKeepAlive, data, buf, matchingRoute, match, handler);

			if (serveFromCache(req)) return;
		}

		try {
			if (handler != null) {
				status = handleIfFound(channel, isKeepAlive, handler, req);
			}

			if (status == HttpStatus.NOT_FOUND) {
				status = tryGenericHandlers(channel, isKeepAlive, req);
			}

		} catch (Throwable e) {
			if (handleError(channel, isKeepAlive, req, e)) return;
		}

		if (status == HttpStatus.NOT_FOUND) {
			handleNotFound(channel, isKeepAlive, req);
			return;
		}

		if (status != HttpStatus.ASYNC) {
			channel.closeIf(!isKeepAlive);
		}
	}

	private boolean shouldServeBuiltInResources(Buf buf, BufRange path) {
		return path.length > BUILT_IN_RES_PATH.length
			&& buf.get(path.start + 1) == '_' // quick check
			&& BytesUtil.startsWith(buf.bytes(), path, BUILT_IN_RES_PATH, true);
	}

	@Override
	public void waitToInitialize() {
		routes.waitToStabilize();
	}

	private boolean serveFromCache(ReqImpl req) {

		// if the HTTP request is not cacheable, the cache key will be null
		HTTPCacheKey cacheKey = req.cacheKey();

		Route route = req.route();

		if (route != null) {
			Cache<HTTPCacheKey, CachedResp> cache = route.cache();

			if (cache != null) {
				if (cacheKey != null) {
					CachedResp resp = cache.getIfExists(cacheKey);

					if (resp != null) {
						serveCached(req, resp);
						return true;
					}

				} else {
					cache.bypass(); // notify it's not cacheable
				}
			}
		}

		return false;
	}

	private void serveCached(ReqImpl req, CachedResp resp) {
		Channel channel = req.channel();

		req.cached(true);

		HttpIO.INSTANCE.respond(HttpUtils.req(req), channel, -1, -1, resp.statusCode,
			req.isKeepAlive(), resp.contentType, resp.body.duplicate(), resp.headers, null);

		channel.send().closeIf(!req.isKeepAlive());
	}

	@SuppressWarnings("unchecked")
	public ReqImpl createReq(Channel channel, boolean isGet, boolean isKeepAlive,
	                         RapidoidHelper helper, Buf buf,
	                         Route matchingRoute, HandlerMatch match, HttpHandler handler) {

		ReqImpl req;
		KeyValueRanges paramsKV = helper.params.reset();
		KeyValueRanges headersKV = helper.headersKV.reset();
		KeyValueRanges cookiesKV = helper.cookies.reset();

		HTTP_PARSER.parseParams(buf, paramsKV, helper.query);
		Map<String, String> params = U.cast(paramsKV.toMap(buf, true, true, false));

		if (match != null && match.getParams() != null) {
			params.putAll(match.getParams());
		}

		HTTP_PARSER.parseHeadersIntoKV(buf, helper.headers, headersKV, cookiesKV, helper);
		Map<String, String> headers = U.cast(headersKV.toMap(buf, false, false, true));
		Map<String, String> cookies = U.cast(cookiesKV.toMap(buf, false, false, false));

		byte[] body;
		Map<String, Object> posted;
		Map<String, List<Upload>> files;
		boolean pendingBodyParsing = false;

		if (!isGet && !helper.body.isEmpty()) {
			KeyValueRanges postedKV = helper.pairs3.reset();

			body = helper.body.bytes(buf);

			// parse posted body as data
			posted = U.map();
			files = U.map();

			pendingBodyParsing = !HTTP_PARSER.parsePosted(buf, headersKV, helper.body, postedKV, files, helper, posted);

			posted = Collections.synchronizedMap(posted);
			files = Collections.synchronizedMap(files);

		} else {
			posted = Collections.EMPTY_MAP;
			files = Collections.EMPTY_MAP;
			body = null;
		}

		String verb = helper.verb.str(buf);
		String uri = helper.uri.str(buf);
		String path = Msc.urlDecode(helper.path.str(buf));
		String query = Msc.urlDecodeOrKeepOriginal(helper.query.str(buf));
		String zone = null;

		MediaType contentType = HttpUtils.getDefaultContentType();

		if (handler != null) {
			contentType = handler.contentType();
			zone = handler.options().zone();
		}

		zone = U.or(zone, "main");

		params = Collections.synchronizedMap(params);
		headers = Collections.synchronizedMap(headers);
		cookies = Collections.synchronizedMap(cookies);

		req = new ReqImpl(this, channel, isKeepAlive, verb, uri, path, query, body, params, headers, cookies,
			posted, files, pendingBodyParsing, contentType, zone, matchingRoute);

		if (!attributes.isEmpty()) {
			req.attrs().putAll(attributes);
		}

		channel.setRequest(req);
		return req;
	}

	private HttpStatus handleIfFound(Channel channel, boolean isKeepAlive, HttpHandler handler, Req req) {
		try {
			return handler.handle(channel, isKeepAlive, req, null);
		} catch (NotFound nf) {
			return HttpStatus.NOT_FOUND;
		}
	}

	private void internalServerError(final Channel channel, final boolean isKeepAlive, final Req req) {
		MediaType contentType = req != null ? req.contentType() : HttpUtils.getDefaultContentType();

		JsonResponseRenderer jsonRenderer = Customization.of(req).jsonResponseRenderer();
		byte[] body = HttpUtils.responseToBytes(req, INTERNAL_SERVER_ERROR, contentType, jsonRenderer);

		HttpIO.INSTANCE.respond(HttpUtils.maybe(req), channel, -1, -1, 500, isKeepAlive, contentType, body, null, null);
	}

	private boolean handleError(Channel channel, boolean isKeepAlive, Req req, Throwable e) {
		if (req != null) {
			if (!((ReqImpl) req).isStopped()) {
				try {
					HttpIO.INSTANCE.errorAndDone(req, e, LogLevel.ERROR);
				} catch (Exception e1) {
					Log.error("HTTP error handler error!", e1);
					internalServerError(channel, isKeepAlive, req);
				}
			}
			return true;

		} else {
			Log.error("Low-level HTTP handler error!", e);
			internalServerError(channel, isKeepAlive, null);
		}

		return false;
	}

	private void handleNotFound(Channel channel, boolean isKeepAlive, Req req) {
		handleError(channel, isKeepAlive, req, new NotFound());
	}

	public Customization custom() {
		return routes.custom();
	}

	private String validateRequest(Buf input, BufRange verb, BufRange uri) {
		if (verb.isEmpty()) {
			return "HTTP verb cannot be empty!";
		}

		if (!BytesUtil.isValidURI(input.bytes(), uri)) {
			return "Invalid HTTP URI!";
		}

		return null; // OK, no error
	}

	private HttpStatus tryGenericHandlers(Channel channel, boolean isKeepAlive, ReqImpl req) {

		for (HttpHandler handler : routes.genericHandlers()) {
			HttpStatus status = handleIfFound(channel, isKeepAlive, handler, req);

			if (status != HttpStatus.NOT_FOUND) {
				return status;
			}
		}

		return HttpStatus.NOT_FOUND;
	}

	public synchronized void resetConfig() {
		routes.reset();
	}

	public void notFound(Channel ctx, boolean isKeepAlive, MediaType contentType, HttpHandler fromHandler, Req req) {
		HttpStatus status = HttpStatus.NOT_FOUND;

		List<HttpHandler> genericHandlers = routes.genericHandlers();
		int count = genericHandlers.size();

		for (int i = 0; i < count; i++) {
			HttpHandler handler = genericHandlers.get(i);
			if (handler == fromHandler) {
				// a generic handler returned "not found" -> go to the next one
				if (i < count - 1) {

					HttpHandler nextHandler = genericHandlers.get(i + 1);
					status = handleIfFound(ctx, isKeepAlive, nextHandler, req);

					break;
				}
			}
		}

		if (status == HttpStatus.NOT_FOUND) {
			handleNotFound(ctx, isKeepAlive, req);
		}
	}

	public Map<String, Object> attributes() {
		return attributes;
	}

	public HttpRoutesImpl routes() {
		return routes;
	}

	public boolean hasRouteOrResource(HttpVerb verb, String uri) {
		return routes.hasRouteOrResource(verb, uri);
	}

}
