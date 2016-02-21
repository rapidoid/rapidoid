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
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.MediaType;
import org.rapidoid.data.JSON;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.UTILS;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

@Authors("Nikolche Mihajlovski")
@Since("5.0.2")
public class ReqImpl implements Req, Constants, HttpMetadata {

	private final FastHttp http;

	private final Channel channel;

	private volatile boolean isKeepAlive;

	private volatile String verb;

	private volatile String uri;

	private volatile String path;

	private volatile String query;

	private volatile byte[] body;

	private final Map<String, String> params;

	private final Map<String, String> headers;

	private final Map<String, String> cookies;

	private final Map<String, Object> posted;

	private final Map<String, byte[]> files;

	private final Map<String, Object> attrs = Collections.synchronizedMap(new HashMap<String, Object>());

	private volatile Map<String, Object> data;

	private volatile Map<String, Serializable> cookiepack;

	private volatile Resp response;

	private volatile boolean rendering;

	private volatile int posConLen;

	private volatile int posBefore;

	private volatile boolean async;

	private volatile boolean done;

	private volatile boolean completed;

	private final MediaType defaultContentType;

	public ReqImpl(FastHttp http, Channel channel, boolean isKeepAlive, String verb, String uri, String path,
	               String query, byte[] body, Map<String, String> params, Map<String, String> headers,
	               Map<String, String> cookies, Map<String, Object> posted, Map<String, byte[]> files,
	               MediaType defaultContentType) {

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
	}

	@Override
	public String verb() {
		return verb;
	}

	@Override
	public Req verb(String verb) {
		this.verb = verb;
		return this;
	}

	@Override
	public String uri() {
		return uri;
	}

	@Override
	public Req uri(String uri) {
		this.uri = uri;
		return this;
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public Req path(String path) {
		this.path = path;
		return this;
	}

	@Override
	public String query() {
		return query;
	}

	@Override
	public Req query(String query) {
		this.query = query;
		return this;
	}

	@Override
	public byte[] body() {
		return body;
	}

	@Override
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
	public Map<String, byte[]> files() {
		return files;
	}

	@Override
	public String clientIpAddress() {
		return channel.address();
	}

	@Override
	public String host() {
		return header(HttpHeaders.HOST.name());
	}

	@Override
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
	public String header(String name) {
		return U.notNull(headers().get(name), "HEADERS[%s]", name);
	}

	@Override
	public String header(String name, String defaultValue) {
		return U.or(headers().get(name), defaultValue);
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
	public byte[] file(String name) {
		return U.notNull(files().get(name), "FILES[%s]", name);
	}

	@Override
	public byte[] file(String name, byte[] defaultValue) {
		return U.or(files().get(name), defaultValue);
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
			value = file(name, null);

			if (value == null) {
				value = param(name, null);
			}
		}

		return withDefault(value, defaultValue);
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

	void startRendering(int code) {
		if (!isRendering()) {
			synchronized (this) {
				if (!isRendering()) {
					startResponse(code);
					rendering = true;
				}
			}
		}
	}

	private void startResponse(int code) {
		MediaType contentType = MediaType.HTML_UTF_8;

		if (cookiepack != null) {
			HttpUtils.saveCookipackBeforeClosingHeaders(this, cookiepack);
		}

		if (response != null) {
			contentType = U.or(response.contentType(), MediaType.HTML_UTF_8);
		}

		renderResponseHeaders(code, contentType);
	}

	private void renderResponseHeaders(int code, MediaType contentType) {
		http.startResponse(channel, code, isKeepAlive, contentType);

		if (response != null) {
			renderCustomHeaders();
		}

		Buf out = channel.output();

		channel.write(FastHttp.CONTENT_LENGTH_UNKNOWN);

		posConLen = out.size();
		channel.write(CR_LF);

		// finishing the headers
		channel.write(CR_LF);

		posBefore = out.size();
	}

	private void completeResponse() {
		Buf out = channel.output();

		int posAfter = out.size();
		int contentLength = posAfter - posBefore;

		out.putNumAsText(posConLen, contentLength, false);
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
		if (!rendering) {
			renderResponse();
		}

		if (!completed) {
			completeResponse();
			completed = true;
		}

		finish();
	}

	private void renderResponse() {
		String err = validateResponse();

		if (response != null) {
			HttpUtils.postProcessResponse(response);
		}

		if (err != null) {
			startRendering(500);
			http.renderBody(channel, 500, MediaType.HTML_UTF_8, err.getBytes());

		} else if (response.raw() != null) {
			byte[] bytes = UTILS.toBytes(response.raw());
			channel.write(bytes);
			completed = true;

		} else {
			renderResponseBody();
		}
	}

	private void renderResponseBody() {
		byte[] bytes;

		try {
			if (response.content() != null) {
				bytes = serializeResponse();

			} else if (response.body() != null) {
				bytes = UTILS.toBytes(response.body());

			} else {
				throw U.rte("There's no HTTP response body to render!");
			}

		} catch (Throwable e) {
			http.error(channel, isKeepAlive, this, e);
			completed = true;
			return;
		}

		startRendering(response.code());
		http.renderBody(channel, response.code(), response.contentType(), bytes);
	}

	private byte[] serializeResponse() {
		Object content = response.content();

		if (U.eq(response.contentType(), MediaType.JSON_UTF_8)) {
			return JSON.stringifyToBytes(content);
		} else {
			return UTILS.toBytes(content);
		}
	}

	private String validateResponse() {
		if (response == null) {
			return "Response wasn't provided!";
		}

		if (response.content() == null && response.body() == null && response.redirect() == null && response.file() == null && response.raw() == null) {
			return "Response content wasn't provided!";
		}

		if (response.contentType() == null && response.raw() == null) {
			return "Response content type wasn't provided!";
		}

		return null;
	}

	private void renderCustomHeaders() {
		for (Entry<String, String> e : response.headers().entrySet()) {
			http.addCustomHeader(channel, e.getKey().getBytes(), e.getValue().getBytes());
		}

		for (Entry<String, String> e : response.cookies().entrySet()) {
			String cookie = e.getKey() + "=" + e.getValue();
			http.addCustomHeader(channel, HttpHeaders.SET_COOKIE.getBytes(), cookie.getBytes());
		}
	}

	private void finish() {
		http.done(channel, isKeepAlive);
	}

	@Override
	public boolean isDone() {
		return done;
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
					HttpUtils.setCookie(this, SESSION_COOKIE, sessionId, "path=/");
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

	/* COOKIEPACK: */

	@Override
	public boolean hasCookiepack() {
		return cookie(COOKIEPACK_COOKIE, null) != null;
	}

	@Override
	public Map<String, Serializable> cookiepack() {
		if (cookiepack == null) {
			synchronized (this) {
				if (cookiepack == null) {
					cookiepack = Collections.synchronizedMap(U.safe(HttpUtils.initAndDeserializeCookiePack(this)));
				}
			}
		}

		return cookiepack;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T cookiepack(String name) {
		Serializable value = hasCookiepack() ? cookiepack().get(name) : null;
		return (T) U.notNull(value, "COOKIEPACK[%s]", name);
	}

	@Override
	public <T extends Serializable> T cookiepack(String name, T defaultValue) {
		Serializable value = hasCookiepack() ? cookiepack().get(name) : null;
		return withDefault(value, defaultValue);
	}

	@Override
	public String toString() {
		return verb() + " " + uri();
	}

	Channel channel() {
		return channel;
	}

	FastHttp http() {
		return http;
	}

}
