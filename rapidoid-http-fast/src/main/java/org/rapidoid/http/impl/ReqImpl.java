package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.cache.Cache;
import org.rapidoid.cls.Cls;
import org.rapidoid.collection.ChangeTrackingMap;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Str;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.BeanParameterFactory;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.customize.JsonRequestBodyParser;
import org.rapidoid.http.customize.SessionManager;
import org.rapidoid.http.impl.lowlevel.HttpIO;
import org.rapidoid.io.Upload;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.abstracts.IRequest;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Msc;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
@Since("5.0.2")
public class ReqImpl extends RapidoidThing implements Req, Constants, HttpMetadata, IRequest, MaybeReq {

	public static final long UNDEFINED = Long.MAX_VALUE;

	private final FastHttp http;

	private final Channel channel;

	private volatile boolean stopped = false;

	private volatile boolean isKeepAlive;

	private volatile String verb;

	private volatile String uri;

	private volatile String path;

	private volatile String query;

	private volatile String zone;

	private volatile String contextPath;

	private volatile byte[] body;

	private final Map<String, String> params;

	private final Map<String, String> headers;

	private final Map<String, String> cookies;

	private final Map<String, Object> posted;

	private final Map<String, List<Upload>> files;

	private final Map<String, Object> attrs = Collections.synchronizedMap(new HashMap<String, Object>());

	private volatile Map<String, Object> data;

	private volatile ChangeTrackingMap<String, Serializable> token;

	final AtomicBoolean tokenChanged = new AtomicBoolean();

	private volatile TokenStatus tokenStatus = TokenStatus.PENDING;

	private volatile ChangeTrackingMap<String, Serializable> session;

	private final AtomicBoolean sessionChanged = new AtomicBoolean();

	private volatile RespImpl response;

	private volatile boolean rendering;

	private volatile long posContentLengthValue;

	private volatile long posBeforeBody = UNDEFINED;

	private volatile boolean async;

	private volatile boolean done;

	private volatile boolean completed;

	private volatile boolean pendingBodyParsing;

	private final MediaType defaultContentType;

	private final HttpRoutesImpl routes;

	private final Route route;

	private final Customization custom;

	private final HTTPCacheKey cacheKey;

	private volatile boolean cached;

	private final long connId;

	private final long handle;

	private final long requestId;

	public ReqImpl(FastHttp http, Channel channel, boolean isKeepAlive, String verb, String uri, String path,
	               String query, byte[] body, Map<String, String> params, Map<String, String> headers,
	               Map<String, String> cookies, Map<String, Object> posted, Map<String, List<Upload>> files,
	               boolean pendingBodyParsing, MediaType defaultContentType, String zone, Route route) {

		this.http = http;
		this.channel = channel;
		this.isKeepAlive = isKeepAlive;
		this.verb = verb;
		this.uri = uri;
		this.path = path;
		this.query = query;
		this.body = body;
		this.params = params;
		this.headers = headers;
		this.cookies = cookies;
		this.posted = posted;
		this.files = files;
		this.pendingBodyParsing = pendingBodyParsing;
		this.defaultContentType = defaultContentType;
		this.zone = zone;
		this.routes = http.routes();
		this.route = route;
		this.connId = channel.connId();
		this.handle = channel.handle();
		this.requestId = channel.requestId();
		this.custom = http.custom();
		this.cacheKey = createCacheKey();
	}

	private HTTPCacheKey createCacheKey() {
		return isCacheable() ? new HTTPCacheKey(host(), uri()) : null;
	}

	@Override
	public String verb() {
		return verb;
	}

	public Req verb(String verb) {
		this.verb = verb;
		return this;
	}

	@Override
	public String uri() {
		return uri;
	}

	public Req uri(String uri) {
		this.uri = uri;
		return this;
	}

	@Override
	public String path() {
		return path;
	}

	public Req path(String path) {
		this.path = path;
		return this;
	}

	@Override
	public String query() {
		return query;
	}

	public Req query(String query) {
		this.query = query;
		return this;
	}

	@Override
	public byte[] body() {
		return body;
	}

	public Req body(byte[] body) {
		this.body = body;
		return this;
	}

	@Override
	public Map<String, String> params() {
		return params;
	}

	@Override
	public Map<String, String> headers() {
		return headers;
	}

	@Override
	public Map<String, String> cookies() {
		return cookies;
	}

	@Override
	public Map<String, Object> posted() {
		if (pendingBodyParsing) {
			synchronized (this) {
				if (pendingBodyParsing) {
					pendingBodyParsing = false;
					parseJsonBody();
				}
			}
		}

		return posted;
	}

	@Override
	public Map<String, List<Upload>> files() {
		return files;
	}

	@Override
	public String clientIpAddress() {
		return channel.address();
	}

	@Override
	public String realIpAddress() {
		return HttpUtils.inferRealIpAddress(this);
	}

	@Override
	public String host() {
		return header(HttpHeaders.HOST.name(), null);
	}

	public Req host(String host) {
		headers().put(HttpHeaders.HOST.name(), host);
		return this;
	}

	@Override
	public long connectionId() {
		return connId;
	}

	@Override
	public long requestId() {
		return requestId;
	}

	@Override
	public String param(String name) {
		return U.notNull(params().get(name), "PARAMS[%s]", name);
	}

	@Override
	public String param(String name, String defaultValue) {
		return withDefault(params().get(name), defaultValue);
	}

	@Override
	public <T> T param(Class<T> beanType) {
		return beanFrom(beanType, params());
	}

	@Override
	public String header(String name) {
		return U.notNull(headers().get(name.toLowerCase()), "HEADERS[%s]", name);
	}

	@Override
	public String header(String name, String defaultValue) {
		return U.or(headers().get(name.toLowerCase()), defaultValue);
	}

	@Override
	public String cookie(String name) {
		return U.notNull(cookies().get(name), "COOKIES[%s]", name);
	}

	@Override
	public String cookie(String name, String defaultValue) {
		return U.or(cookies().get(name), defaultValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T posted(String name) {
		return (T) U.notNull(posted().get(name), "POSTED[%s]", name);
	}

	@Override
	public <T extends Serializable> T posted(String name, T defaultValue) {
		return withDefault(posted().get(name), defaultValue);
	}

	@Override
	public <T> T posted(Class<T> beanType) {
		return beanFrom(beanType, posted());
	}

	@Override
	public List<Upload> files(String name) {
		return U.notNull(files().get(name), "FILES[%s]", name);
	}

	@Override
	public Upload file(String name) {
		List<Upload> uploads = files(name);

		U.must(uploads.size() == 1, "Expected exactly 1 uploaded file for parameter '%s', but found %s!", name, uploads.size());

		return uploads.get(0);
	}

	@Override
	public Map<String, Object> data() {
		if (data == null) {
			synchronized (this) {
				if (data == null) {
					Map<String, Object> allData = U.map();

					allData.putAll(params);
					allData.putAll(files);
					allData.putAll(posted());

					data = Collections.unmodifiableMap(allData);
				}
			}
		}

		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T data(String name) {
		return (T) U.notNull(data(name, null), "DATA[%s]", name);
	}

	@Override
	public <T> T data(String name, T defaultValue) {
		Object value = posted(name, null);

		if (value == null) {
			value = files().get(name);

			if (value == null) {
				value = param(name, null);
			}
		}

		return withDefault(value, defaultValue);
	}

	@Override
	public <T> T data(Class<T> beanType) {
		return beanFrom(beanType, data());
	}

	@SuppressWarnings("unchecked")
	private <T> T beanFrom(Class<T> beanType, Map<String, ?> properties) {
		String paramName = Str.uncapitalized(beanType.getSimpleName());
		BeanParameterFactory beanParameterFactory = custom().beanParameterFactory();

		try {
			return (T) beanParameterFactory.getParamValue(this, beanType, paramName, (Map<String, Object>) properties);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't instantiate a bean of type: " + beanType.getName());
		}
	}

	@Override
	public Map<String, Object> attrs() {
		return attrs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T attr(String name) {
		return (T) U.notNull(attrs().get(name), "ATTRS[%s]", name);
	}

	@Override
	public <T> T attr(String name, T defaultValue) {
		return withDefault(attrs().get(name), defaultValue);
	}

	@SuppressWarnings("unchecked")
	private <T> T withDefault(Object value, T defaultValue) {
		if (value != null) {
			return (T) (defaultValue != null ? Cls.convert(value, Cls.of(defaultValue)) : value);
		} else {
			return defaultValue;
		}
	}

	/* RESPONSE */

	@Override
	public synchronized Resp response() {
		if (response == null) {
			response = new RespImpl(this);
			if (defaultContentType != null) {
				response.contentType(defaultContentType);
			}
		}

		return response;
	}

	void doRendering(int code, byte[] responseBody) {
		if (!isRendering()) {
			synchronized (this) {
				if (!isRendering()) {
					respond(code, responseBody);
				}
			}
		}
	}

	private void respond(int code, byte[] responseBody) {
		MediaType contentType = HttpUtils.getDefaultContentType();

		if (tokenChanged.get()) {
			HttpUtils.saveTokenBeforeRenderingHeaders(this, token);
		}

		if (sessionChanged.get()) {
			saveSession(session.decorated());
		}

		if (response != null) {
			contentType = U.or(response.contentType(), contentType);
		}

		renderResponse(code, contentType, responseBody);
	}

	private void renderResponse(int code, MediaType contentType, byte[] responseBody) {
		rendering = true;
		completed = responseBody != null;

		HttpIO.INSTANCE.respond(
			HttpUtils.maybe(this), channel, connId, handle,
			code, isKeepAlive, contentType, responseBody,
			response != null ? response.headers() : null,
			response != null ? response.cookies() : null
		);
	}

	public void responded(long posContentLengthValue, long posBeforeBody, boolean completed) {
		this.posContentLengthValue = posContentLengthValue;
		this.posBeforeBody = posBeforeBody;
		this.completed = completed;
	}

	public void onHeadersCompleted() {
		posBeforeBody = channel.output().size();
	}

	public boolean isRendering() {
		return rendering;
	}

	public ReqImpl completed(boolean completed) {
		this.completed = completed;
		return this;
	}

	@Override
	public synchronized Req done() {
		if (!done) {
			onDone();
			done = true;
		}

		return this;
	}

	private void onDone() {
		if (stopped) {
			return;
		}

		boolean willBeDone = true;
		if (!rendering) {
			renderResponseOrError();
			willBeDone = false;
		}

		if (!completed) {
			// FIXME is this still required?
			completed = true;
		}

		if (response != null) {
			response.finish();
		}

		if (willBeDone) {
			HttpIO.INSTANCE.done(ReqImpl.this);
		}
	}

	private void renderResponseOrError() {
		String err = validateResponse();

		if (err != null) {
			doRendering(500, err.getBytes());

		} else {
			renderResponse();
		}
	}

	private void renderResponse() {
		HttpUtils.postProcessResponse(response);

		if (response.raw() != null) {
			int posBeforeResponse = channel.output().size();

			byte[] bytes = Msc.toBytes(response.raw());
			channel.write(bytes);

			if (willSaveToCache()) posBeforeBody = posBeforeResponse + HttpUtils.findBodyStart(bytes);

			completed = true;
			HttpIO.INSTANCE.done(this);

		} else {
			// first serialize the response to bytes (with error handling)
			byte[] bytes = responseToBytes();

			// then rendering
			doRendering(response.code(), bytes);
		}
	}

	private byte[] responseToBytes() {
		try {
			return response.renderToBytes();

		} catch (Throwable e) {
			HttpIO.INSTANCE.error(this, e, LogLevel.ERROR);

			try {
				return response.renderToBytes();

			} catch (Exception e1) {
				Log.error("Internal rendering error!", e1);
				return HttpUtils.getErrorMessageAndSetCode(response, e1).getBytes();
			}
		}
	}

	private String validateResponse() {
		if (response == null) {
			return "Response wasn't provided!";
		}

		if (response.result() == null && response.body() == null && response.redirect() == null
			&& response.file() == null && response.raw() == null && !response().mvc()) {
			return "Response content wasn't provided!";
		}

		if (response.contentType() == null && response.raw() == null) {
			return "Response content type wasn't provided!";
		}

		return null;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public HttpRoutes routes() {
		return routes;
	}

	@Override
	public Route route() {
		return route;
	}

	@Override
	public Customization custom() {
		return custom;
	}

	@Override
	public Req async() {
		this.async = true;

		if (channel.onSameThread()) channel.async();

		return this;
	}

	@Override
	public boolean isAsync() {
		return async;
	}

	/* SESSION: */

	@Override
	public String sessionId() {
		String sessionId = cookie(SESSION_COOKIE, null);

		if (U.isEmpty(sessionId)) {
			sessionId = UUID.randomUUID().toString();
			synchronized (cookies) {
				if (cookie(SESSION_COOKIE, null) == null) {
					cookies.put(SESSION_COOKIE, sessionId);
					response().cookie(SESSION_COOKIE, sessionId, "HttpOnly");
				}
			}
		}

		return sessionId;
	}

	@Override
	public boolean hasSession() {
		return cookie(SESSION_COOKIE, null) != null;
	}

	@Override
	public Map<String, Serializable> session() {
		if (session == null) {
			synchronized (this) {
				if (session == null) {
					session = Coll.trackChanges(loadSession(), sessionChanged);
				}
			}
		}

		return session;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T session(String name) {
		Serializable value = hasSession() ? session().get(name) : null;
		return (T) U.notNull(value, "SESSION[%s]", name);
	}

	@Override
	public <T extends Serializable> T session(String name, T defaultValue) {
		Serializable value = hasSession() ? session().get(name) : null;
		return withDefault(value, defaultValue);
	}

	/* TOKEN: */

	@Override
	public boolean hasToken() {
		// FIXME don't deserialize token, just check if it exists
		return U.notEmpty(token()); // try to find and deserialize the token
	}

	@Override
	public Map<String, Serializable> token() {
		if (tokenStatus == TokenStatus.PENDING) {
			synchronized (this) {
				if (tokenStatus == TokenStatus.PENDING) {

					Map<String, Serializable> tokenData = null;

					try {
						tokenData = HttpUtils.initAndDeserializeToken(this);

						tokenStatus(tokenData != null ? TokenStatus.LOADED : TokenStatus.NONE);

					} catch (Exception e) {
						Log.error("Token deserialization error!", e);
						tokenStatus(TokenStatus.INVALID);
					}

					token = Coll.trackChanges(Collections.synchronizedMap(U.safe(tokenData)), tokenChanged);
				}
			}
		}

		return token;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T token(String name) {
		Serializable value = hasToken() ? token().get(name) : null;
		return (T) U.notNull(value, "TOKEN[%s]", name);
	}

	@Override
	public <T extends Serializable> T token(String name, T defaultValue) {
		Serializable value = hasToken() ? token().get(name) : null;
		return withDefault(value, defaultValue);
	}

	public TokenStatus tokenStatus() {
		return tokenStatus;
	}

	public Req tokenStatus(TokenStatus tokenStatus) {
		this.tokenStatus = tokenStatus;
		return this;
	}

	@Override
	public String zone() {
		return zone;
	}

	public Req zone(String zone) {
		this.zone = zone;
		return this;
	}

	@Override
	public String contextPath() {
		if (contextPath == null) {
			synchronized (this) {
				if (contextPath == null) {
					contextPath = HttpUtils.getContextPath(this);
				}
			}
		}
		return contextPath;
	}

	public Req contextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}

	@Override
	public String toString() {
		String info = verb() + " " + path();

		if (U.notEmpty(params)) {
			info += "?" + U.join("&", Msc.protectSensitiveInfo(params, "<...>").entrySet());
		}

		return info;
	}

	public Channel channel() {
		return channel;
	}

	public FastHttp http() {
		return http;
	}

	@Override
	public void stop() {
		this.stopped = true;
	}

	@Override
	public boolean isStopped() {
		return stopped;
	}

	@Override
	public void revert() {
		rendering = false;
		posContentLengthValue = 0;
		posBeforeBody = 0;
		async = false;
		done = false;
		completed = false;
		response = null;
	}

	private void parseJsonBody() {
		if (U.notEmpty(body())) {

			Map<String, ?> jsonData = null;
			JsonRequestBodyParser parser = custom().jsonRequestBodyParser();

			try {
				jsonData = parser.parseJsonBody(this, body);
			} catch (Exception e) {
				Log.error("The attempt to parse the request body as JSON failed. Please make sure the correct content type is specified in the request header!", e);
			}

			if (jsonData != null) {
				posted.putAll(jsonData);
			}
		}
	}

	public Map<String, Serializable> loadSession() {
		SessionManager sessionManager = U.notNull(custom().sessionManager(), "session manager");

		try {
			return sessionManager.loadSession(this, sessionId());
		} catch (Exception e) {
			throw U.rte("Error occurred while loading the session!", e);
		}
	}

	public void saveSession(Map<String, Serializable> session) {
		SessionManager sessionManager = U.notNull(custom().sessionManager(), "session manager");

		try {
			sessionManager.saveSession(this, sessionId(), session);
		} catch (Exception e) {
			throw U.rte("Error occurred while saving the session!", e);
		}
	}

	@Override
	public OutputStream out() {
		return response().out();
	}

	@Override
	public MediaType contentType() {
		MediaType contentType = response != null ? response.contentType() : null;
		return U.or(contentType, defaultContentType);
	}

	@Override
	public long handle() {
		return handle;
	}

	public boolean isKeepAlive() {
		return isKeepAlive;
	}

	public void doneProcessing() {
		done = true;

		if (willSaveToCache()) saveToCache();
	}

	private void saveToCache() {
		U.must(posBeforeBody != UNDEFINED);

		Buf out = channel.output();
		int posAfterBody = out.size();
		int bodyLength = (int) (posAfterBody - posBeforeBody);

		// FIXME validate '\r\n\r\n' before the start position of the response body

		Cache<HTTPCacheKey, CachedResp> cache = route.cache();
		U.notNull(cache, "route.cache");

		SimpleHttpResp proxyResp = new SimpleHttpResp();
		proxyResp.cookies = U.map(U.safe(response != null ? response.cookies() : null));

		Map<String, String> headers = response != null ? response.headers() : Collections.<String, String>emptyMap();
		HttpUtils.proxyResponseHeaders(headers, proxyResp);

		proxyResp.code = response != null ? response.code() : 200;

		if (proxyResp.contentType == null) {
			proxyResp.contentType = response != null ? response.contentType() : defaultContentType;
		}

		// don't cache the response if it contains cookies or token data
		if (U.notEmpty(proxyResp.cookies) || hasToken()) return;

		ByteBuffer body = writeBodyToBuf(out, bodyLength);
		CachedResp cached = new CachedResp(proxyResp.code, proxyResp.contentType, proxyResp.headers, body);

		cache.set(cacheKey, cached);
	}

	private ByteBuffer writeBodyToBuf(Buf out, int bodyLength) {
		ByteBuffer body = ByteBuffer.allocateDirect(bodyLength);
		out.writeTo(body, (int) posBeforeBody, bodyLength);
		body.flip();
		return body;
	}

	@Override
	public Req getReqOrNull() {
		return this;
	}

	private boolean willSaveToCache() {
		return cacheKey != null && !cached;
	}

	public HTTPCacheKey cacheKey() {
		return cacheKey;
	}

	public boolean cached() {
		return cached;
	}

	public ReqImpl cached(boolean cached) {
		this.cached = cached;
		return this;
	}

	private boolean isCacheable() {
		return route != null
			&& HttpUtils.isGetReq(this)
			&& route.cache() != null
			&& cookies.isEmpty()
			&& U.notEmpty(host())
			&& !hasToken();
	}

}
