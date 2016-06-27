package org.rapidoid.http.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.MediaType;
import org.rapidoid.commons.Str;
import org.rapidoid.http.*;
import org.rapidoid.http.customize.BeanParameterFactory;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.io.Upload;
import org.rapidoid.log.Log;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.abstracts.IRequest;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Msc;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

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
@Since("5.0.2")
public class ReqImpl extends RapidoidThing implements Req, Constants, HttpMetadata, IRequest {

	private final FastHttp http;

	private final Channel channel;

	private volatile boolean stopped = false;

	private volatile boolean isKeepAlive;

	private volatile String verb;

	private volatile String uri;

	private volatile String path;

	private volatile String query;

	private volatile String segment;

	private volatile String contextPath;

	private volatile byte[] body;

	private final Map<String, String> params;

	private final Map<String, String> headers;

	private final Map<String, String> cookies;

	private final Map<String, Object> posted;

	private final Map<String, List<Upload>> files;

	private final Map<String, Object> attrs = Collections.synchronizedMap(new HashMap<String, Object>());

	private volatile Map<String, Object> data;

	private volatile Map<String, Serializable> token;

	private volatile RespImpl response;

	private volatile boolean rendering;

	private volatile int posConLen;

	private volatile int posBefore;

	private volatile boolean async;

	private volatile boolean done;

	private volatile boolean completed;

	private final MediaType defaultContentType;

	private volatile HttpRoutesImpl routes;

	public ReqImpl(FastHttp http, Channel channel, boolean isKeepAlive, String verb, String uri, String path,
	               String query, byte[] body, Map<String, String> params, Map<String, String> headers,
	               Map<String, String> cookies, Map<String, Object> posted, Map<String, List<Upload>> files,
	               MediaType defaultContentType, String segment, HttpRoutesImpl routes) {

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
		this.defaultContentType = defaultContentType;
		this.segment = segment;
		this.routes = routes;
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
	public String host() {
		return header(HttpHeaders.HOST.name(), null);
	}

	public Req host(String host) {
		headers().put(HttpHeaders.HOST.name(), host);
		return this;
	}

	@Override
	public long connectionId() {
		return channel.connId();
	}

	@Override
	public long requestId() {
		return channel.requestId();
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
					allData.putAll(posted);

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

	public void startRendering(int code, boolean unknownContentLength) {
		if (!isRendering()) {
			synchronized (this) {
				if (!isRendering()) {
					startResponse(code, unknownContentLength);
				}
			}
		}
	}

	private void startResponse(int code, boolean unknownContentLength) {
		MediaType contentType = MediaType.HTML_UTF_8;

		if (token != null) {
			HttpUtils.saveCookipackBeforeRenderingHeaders(this, token);
		}

		if (response != null) {
			contentType = U.or(response.contentType(), MediaType.HTML_UTF_8);
		}

		renderResponseHeaders(code, contentType, unknownContentLength);
	}

	private void renderResponseHeaders(int code, MediaType contentType, boolean unknownContentLength) {
		rendering = true;
		HttpIO.startResponse(channel, code, isKeepAlive, contentType);

		if (response != null) {
			renderCustomHeaders();
		}

		if (unknownContentLength) {
			Buf out = channel.output();
			HttpIO.writeContentLengthUnknown(channel);

			posConLen = out.size() - 1;
			channel.write(CR_LF);

			// finishing the headers
			channel.write(CR_LF);

			posBefore = out.size();
		}
	}

	private void writeResponseLength() {
		Buf out = channel.output();

		int posAfter = out.size();
		int contentLength = posAfter - posBefore;

		if (!stopped && out.size() > 0) {
			out.putNumAsText(posConLen, contentLength, false);
		}

		completed = true;
	}

	public boolean isRendering() {
		return rendering;
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

		if (!rendering) {
			renderResponseOrError();
		}

		if (!completed) {
			writeResponseLength();
			completed = true;
		}

		finish();
	}

	private void renderResponseOrError() {
		String err = validateResponse();

		if (err != null) {
			startRendering(500, false);
			writeContentLengthAndBody(err.getBytes());

		} else {
			renderResponse();
		}
	}

	private void renderResponse() {
		HttpUtils.postProcessResponse(response);

		if (response.raw() != null) {
			byte[] bytes = Msc.toBytes(response.raw());
			channel.write(bytes);
			completed = true;

		} else {
			// first serialize the response to bytes (with error handling)
			byte[] bytes = responseToBytes();

			// then start rendering
			startRendering(response.code(), false);
			writeContentLengthAndBody(bytes);
		}
	}

	private void writeContentLengthAndBody(byte[] bytes) {
		HttpIO.writeContentLengthAndBody(channel, bytes);
		completed = true;
	}

	private byte[] responseToBytes() {
		try {
			return response.renderToBytes();

		} catch (Throwable e) {
			HttpIO.error(this, e, custom().errorHandler());

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

	private void renderCustomHeaders() {
		for (Entry<String, String> e : response.headers().entrySet()) {
			HttpIO.addCustomHeader(channel, e.getKey().getBytes(), e.getValue().getBytes());
		}

		for (Entry<String, String> e : response.cookies().entrySet()) {
			String cookie = e.getKey() + "=" + e.getValue();
			HttpIO.addCustomHeader(channel, HttpHeaders.SET_COOKIE.getBytes(), cookie.getBytes());
		}
	}

	private void finish() {
		HttpIO.done(channel, isKeepAlive);
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public HttpRoutes routes() {
		return routes;
	}

	public ReqImpl routes(HttpRoutesImpl routes) {
		this.routes = routes;
		return this;
	}

	@Override
	public Customization custom() {
		return routes.custom();
	}

	@Override
	public Req async() {
		this.async = true;
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
					response().cookie(SESSION_COOKIE, sessionId, "path=/", "HttpOnly");
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
		return http.session(sessionId());
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
		return U.notEmpty(token) || cookie(TOKEN, null) != null || data(TOKEN, null) != null;
	}

	@Override
	public Map<String, Serializable> token() {
		if (token == null) {
			synchronized (this) {
				if (token == null) {
					Map<String, Serializable> cpack = null;

					try {
						cpack = HttpUtils.initAndDeserializeTOKEN(this);
					} catch (Exception e) {
						Log.warn("Cookie-pack deserialization error! Maybe the secret was changed?");
						Log.debug("Cookie-pack deserialization error!", e);
					}

					token = Collections.synchronizedMap(U.safe(cpack));
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

	@Override
	public String segment() {
		return segment;
	}

	public Req segment(String segment) {
		this.segment = segment;
		return this;
	}

	@Override
	public String contextPath() {
		if (contextPath == null) {
			synchronized (this) {
				if (contextPath == null) {
					contextPath = HttpUtils.getContextPath(custom(), segment());
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

}
