package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.Config;
import org.rapidoid.config.ConfigImpl;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.JsonResponseRenderer;
import org.rapidoid.http.handler.HttpHandler;
import org.rapidoid.http.impl.*;
import org.rapidoid.http.processor.AbstractHttpProcessor;
import org.rapidoid.io.Upload;
import org.rapidoid.log.Log;
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

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class FastHttp extends AbstractHttpProcessor {

	private static final HttpParser HTTP_PARSER = new HttpParser();

	private static final String INTERNAL_SERVER_ERROR = "Internal Server Error!";

	private final HttpRoutesImpl[] routeGroups;

	private final Map<String, Object> attributes = Coll.synchronizedMap();

	public FastHttp(HttpRoutesImpl... routeGroups) {
		this(routeGroups, new ConfigImpl());
	}

	public FastHttp(HttpRoutesImpl[] routeGroups, Config serverConfig) {
		super(null);

		U.must(routeGroups.length > 0, "Routes are missing!");
		this.routeGroups = routeGroups;
	}

	@Override
	public void onRequest(Channel channel, boolean isGet, boolean isKeepAlive, BufRange xbody, BufRange xverb, BufRange xuri,
	                      BufRange xpath, BufRange xquery, BufRange xprotocol, BufRanges hdrs) {

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

		HttpRoutesImpl matchingRoutes = null;
		Route matchingRoute = null;
		HandlerMatch match = null;

		for (HttpRoutesImpl r : routeGroups) {
			match = r.findHandler(buf, isGet, xverb, xpath);
			if (match != null) {
				matchingRoutes = r;
				matchingRoute = match.getRoute();
				break;
			}
		}

		if (match == null && isGet) {
			for (HttpRoutesImpl r : routeGroups) {
				match = r.staticResourcesHandler();
				if (match != null) {
					matchingRoutes = r;
					matchingRoute = match.getRoute();
					break;
				}
			}
		}

		HttpHandler handler = match != null ? match.getHandler() : null;
		boolean noReq = (handler != null && !handler.needsParams());

		ReqImpl req = null;

		if (!noReq) {
			req = createReq(channel, isGet, isKeepAlive, xbody, xverb, xuri, xpath, xquery, hdrs,
				helper, buf, matchingRoutes, matchingRoute, match, handler);
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

	@SuppressWarnings("unchecked")
	public ReqImpl createReq(Channel channel, boolean isGet, boolean isKeepAlive, BufRange xbody, BufRange xverb, BufRange xuri, BufRange xpath, BufRange xquery, BufRanges hdrs, RapidoidHelper helper, Buf buf, HttpRoutesImpl matchingRoutes, Route matchingRoute, HandlerMatch match, HttpHandler handler) {
		ReqImpl req;
		KeyValueRanges paramsKV = helper.pairs1.reset();
		KeyValueRanges headersKV = helper.pairs2.reset();
		KeyValueRanges cookiesKV = helper.pairs5.reset();

		HTTP_PARSER.parseParams(buf, paramsKV, xquery);
		Map<String, String> params = U.cast(paramsKV.toMap(buf, true, true, false));

		if (match != null && match.getParams() != null) {
			params.putAll(match.getParams());
		}

		HTTP_PARSER.parseHeadersIntoKV(buf, hdrs, headersKV, cookiesKV, helper);
		Map<String, String> headers = U.cast(headersKV.toMap(buf, false, false, true));
		Map<String, String> cookies = U.cast(cookiesKV.toMap(buf, false, false, false));

		byte[] body;
		Map<String, Object> posted;
		Map<String, List<Upload>> files;
		boolean pendingBodyParsing = false;

		if (!isGet && !xbody.isEmpty()) {
			KeyValueRanges postedKV = helper.pairs3.reset();

			body = xbody.bytes(buf);

			// parse posted body as data
			posted = U.map();
			files = U.map();

			pendingBodyParsing = !HTTP_PARSER.parsePosted(buf, headersKV, xbody, postedKV, files, helper, posted);

			posted = Collections.synchronizedMap(posted);
			files = Collections.synchronizedMap(files);

		} else {
			posted = Collections.EMPTY_MAP;
			files = Collections.EMPTY_MAP;
			body = null;
		}

		String verb = xverb.str(buf);
		String uri = xuri.str(buf);
		String path = Msc.urlDecode(xpath.str(buf));
		String query = Msc.urlDecodeOrKeepOriginal(xquery.str(buf));
		String zone = null;

		MediaType contentType = MediaType.HTML_UTF_8;

		if (handler != null) {
			contentType = handler.contentType();
			zone = handler.options().zone();
		}

		zone = U.or(zone, "main");

		params = Collections.synchronizedMap(params);
		headers = Collections.synchronizedMap(headers);
		cookies = Collections.synchronizedMap(cookies);

		req = new ReqImpl(this, channel, isKeepAlive, verb, uri, path, query, body, params, headers, cookies,
			posted, files, pendingBodyParsing, contentType, zone, matchingRoutes, matchingRoute);

		if (!attributes.isEmpty()) {
			req.attrs().putAll(attributes);
		}

		channel.setRequest(req);
		return req;
	}

	private HttpStatus handleIfFound(Channel channel, boolean isKeepAlive, HttpHandler handler, ReqImpl req) {
		try {
			return handler.handle(channel, isKeepAlive, req, null);
		} catch (NotFound nf) {
			return HttpStatus.NOT_FOUND;
		}
	}

	protected void internalServerError(Channel channel, boolean isKeepAlive, Req req) {
		MediaType contentType = req != null ? req.contentType() : MediaType.HTML_UTF_8;

		HttpIO.startResponse(channel, 500, isKeepAlive, contentType);

		JsonResponseRenderer jsonRenderer = Customization.of(req).jsonResponseRenderer();
		byte[] bytes = HttpUtils.responseToBytes(req, INTERNAL_SERVER_ERROR, contentType, jsonRenderer);

		HttpIO.writeContentLengthAndBody(channel, bytes);
		HttpIO.done(channel, isKeepAlive);
	}

	private boolean handleError(Channel channel, boolean isKeepAlive, Req req, Throwable e) {
		if (req != null) {
			if (!((ReqImpl) req).isStopped()) {
				try {
					HttpIO.errorAndDone(req, e);
				} catch (Exception e1) {
					Log.error("HTTP error handler error!", e1);
					internalServerError(channel, isKeepAlive, req);
				}
			}
			return true;

		} else {
			Log.error("Low-level HTTP handler error!", e);
			internalServerError(channel, isKeepAlive, req);
		}

		return false;
	}

	private void handleNotFound(Channel channel, boolean isKeepAlive, Req req) {
		handleError(channel, isKeepAlive, req, new NotFound());
	}

	public Customization custom() {
		return routes()[0].custom();
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
		for (HttpRoutesImpl routes : routeGroups) {

			// trying with different routes
			req.routes(routes);

			for (HttpHandler handler : routes.genericHandlers()) {
				HttpStatus status = handleIfFound(channel, isKeepAlive, handler, req);

				if (status != HttpStatus.NOT_FOUND) {
					return status;
				}
			}
		}

		req.routes(null);

		return HttpStatus.NOT_FOUND;
	}

	public synchronized void resetConfig() {
		for (HttpRoutesImpl route : routeGroups) {
			route.reset();
			route.custom().reset();
		}
	}

	public void notFound(Channel ctx, boolean isKeepAlive, MediaType contentType, HttpHandler fromHandler, Req req) {
		HttpStatus status = HttpStatus.NOT_FOUND;

		tryRoutes:
		for (HttpRoutesImpl route : routeGroups) {
			List<HttpHandler> genericHandlers = route.genericHandlers();
			int count = genericHandlers.size();

			for (int i = 0; i < count; i++) {
				HttpHandler handler = genericHandlers.get(i);
				if (handler == fromHandler) {
					// a generic handler returned "not found" -> go to the next one
					if (i < count - 1) {
						// trying with different routes
						ReqImpl reqi = (ReqImpl) req;
						reqi.routes(route);

						HttpHandler nextHandler = genericHandlers.get(i + 1);
						status = handleIfFound(ctx, isKeepAlive, nextHandler, reqi);

						break tryRoutes;
					}
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

	public HttpRoutesImpl[] routes() {
		return routeGroups;
	}

	public boolean hasRouteOrResource(HttpVerb verb, String uri) {
		for (HttpRoutesImpl route : routeGroups) {
			if (route.hasRouteOrResource(verb, uri)) return true;
		}
		return false;
	}

}
