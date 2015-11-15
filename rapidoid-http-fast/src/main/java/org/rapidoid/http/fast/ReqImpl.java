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

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.cls.Cls;
import org.rapidoid.http.Req;
import org.rapidoid.http.Response;
import org.rapidoid.mime.MediaType;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;

@Authors("Nikolche Mihajlovski")
@Since("5.0.2")
public class ReqImpl implements Req, Constants {

	private final FastHttp http;

	private final Channel channel;

	private volatile boolean isKeepAlive;

	private volatile String verb;

	private volatile String uri;

	private volatile String path;

	private volatile byte[] body;

	private final Map<String, String> params;

	private final Map<String, String> headers;

	private final Map<String, String> cookies;

	private final Map<String, Object> posted;

	private final Map<String, byte[]> files;

	private final Map<String, Object> attrs = Collections.synchronizedMap(new HashMap<String, Object>());

	private volatile Response response;

	private volatile boolean rendering;

	private volatile int posConLen;

	private volatile int posBefore;

	private volatile boolean done;

	private final MediaType defaultContentType;

	public ReqImpl(FastHttp http, Channel channel, boolean isKeepAlive, String verb, String uri, String path,
			byte[] body, Map<String, String> params, Map<String, String> headers, Map<String, String> cookies,
			Map<String, Object> posted, Map<String, byte[]> files, MediaType defaultContentType) {

		this.http = http;
		this.channel = channel;
		this.isKeepAlive = isKeepAlive;
		this.verb = verb;
		this.uri = uri;
		this.path = path;
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
	public String forwardedForAddress() {
		return header(HttpHeaders.X_FORWARDED_FOR.name());
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
		Map<String, Object> data = new HashMap<String, Object>();

		data.putAll(params);
		data.putAll(files);
		data.putAll(posted);

		return Collections.unmodifiableMap(data);
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

	/************************** RESPONSE **************************/

	@Override
	public synchronized Response response() {
		if (response == null) {
			response = new HttpResponse(this);
			if (defaultContentType != null) {
				response.contentType(defaultContentType);
			}
		}

		return response;
	}

	@Override
	public OutputStream out() {
		U.must(response == null || response.content() == null,
				"The response content was already set, so cannot render the response in OutputStream, too!");

		if (!isRendering()) {
			startRendering();
		}

		return channel.output().asOutputStream();
	}

	private void startRendering() {
		if (!isRendering()) {
			synchronized (this) {
				if (!isRendering()) {
					rendering = true;
					startResponse();
				}
			}
		}
	}

	private void startResponse() {
		int code = 200;
		MediaType contentType = MediaType.HTML_UTF_8;

		if (response != null) {
			HttpUtils.postProcessResponse(response);

			code = response.code();
			contentType = U.or(response.contentType(), MediaType.HTML_UTF_8);
		}

		startResponseRendering(code, contentType);
	}

	private void startResponseRendering(int code, MediaType contentType) {
		http.startResponse(channel, code, isKeepAlive, contentType);

		renderCustomHeaders();

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
	}

	public boolean isRendering() {
		return rendering;
	}

	@Override
	public synchronized Req done() {
		if (!done) {
			done = true;
			onDone();
		}
		return this;
	}

	private void onDone() {
		boolean wasRendering = rendering;
		startRendering();

		if (!wasRendering) {
			U.must(response != null && response.content() != null, "Response content wasn't provided!");
			U.must(response.contentType() != null, "Response content type wasn't provided!");
			http.renderBody(channel, response.code(), response.contentType(), response.content());
		}

		completeResponse();
		finish();
	}

	private void renderCustomHeaders() {
		for (Entry<String, String> e : headers().entrySet()) {
			http.addCustomHeader(channel, e.getKey().getBytes(), e.getValue().getBytes());
		}

		for (Entry<String, String> e : cookies().entrySet()) {
			String cookie = e.getKey() + "=" + e.getValue();
			http.addCustomHeader(channel, HttpHeaders.SET_COOKIE.getBytes(), cookie.getBytes());
		}
	}

	private void finish() {
		http.done(channel, isKeepAlive);
	}

	public boolean isDone() {
		return done;
	}

}
