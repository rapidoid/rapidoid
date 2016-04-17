package org.rapidoid.http;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Coll;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.data.JSON;
import org.rapidoid.io.Upload;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-http-client
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
@Since("5.1.0")
public class HttpClient extends RapidoidThing {

	private volatile HttpVerb verb = null;

	private volatile String url = null;

	private volatile byte[] body = null;

	private final Map<String, String> cookies = Coll.synchronizedMap();

	private final Map<String, String> headers = Coll.synchronizedMap();

	private final Map<String, Object> data = Coll.synchronizedMap();

	private final Map<String, List<Upload>> files = Coll.synchronizedMap();

	private volatile String contentType = null;

	private volatile String userAgent = null;

	private volatile boolean followRedirects = false;

	private volatile boolean keepAlive = false;

	private volatile boolean keepCookies = false;

	private volatile boolean reuseConnections = false;

	private volatile boolean decompress = true;

	private volatile int maxConnPerRoute = 0;

	private volatile int maxConnTotal = 0;

	private volatile int socketTimeout = 5000;

	private volatile int connectTimeout = 5000;

	private volatile int connectionRequestTimeout = 5000;

	private volatile int maxRedirects = 5;

	private volatile boolean raw = false;

	private volatile CloseableHttpAsyncClient client;

	private volatile boolean dontClose = false;

	public HttpClient verb(HttpVerb verb) {
		this.verb = verb;
		return this;
	}

	public HttpVerb verb() {
		return this.verb;
	}

	public HttpClient url(String url) {
		this.url = url;
		return this;
	}

	public String url() {
		return this.url;
	}

	public HttpClient body(byte[] body) {
		this.body = body;
		return this;
	}

	public byte[] body() {
		return this.body;
	}

	public HttpClient cookies(Map<String, String> cookies) {
		Coll.assign(this.cookies, cookies);
		return this;
	}

	public Map<String, String> cookies() {
		return this.cookies;
	}

	public HttpClient headers(Map<String, String> headers) {
		Coll.assign(this.headers, headers);
		return this;
	}

	public Map<String, String> headers() {
		return this.headers;
	}

	public HttpClient data(Map<String, ?> data) {
		Coll.assign(this.data, data);
		return this;
	}

	public Map<String, Object> data() {
		return this.data;
	}

	public HttpClient files(Map<String, List<Upload>> files) {
		Coll.assign(this.files, files);
		return this;
	}

	public Map<String, List<Upload>> files() {
		return this.files;
	}

	public HttpClient contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public String contentType() {
		return this.contentType;
	}

	public HttpClient userAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public String userAgent() {
		return this.userAgent;
	}

	public HttpClient followRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}

	public boolean followRedirects() {
		return this.followRedirects;
	}

	public HttpClient keepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}

	public boolean keepAlive() {
		return this.keepAlive;
	}

	public HttpClient keepCookies(boolean keepCookies) {
		this.keepCookies = keepCookies;
		return this;
	}

	public boolean keepCookies() {
		return this.keepCookies;
	}

	public HttpClient reuseConnections(boolean reuseConnections) {
		this.reuseConnections = reuseConnections;
		return this;
	}

	public boolean reuseConnections() {
		return this.reuseConnections;
	}

	public HttpClient decompress(boolean decompress) {
		this.decompress = decompress;
		return this;
	}

	public boolean decompress() {
		return this.decompress;
	}

	public HttpClient maxConnPerRoute(int maxConnPerRoute) {
		this.maxConnPerRoute = maxConnPerRoute;
		return this;
	}

	public int maxConnPerRoute() {
		return this.maxConnPerRoute;
	}

	public HttpClient maxConnTotal(int maxConnTotal) {
		this.maxConnTotal = maxConnTotal;
		return this;
	}

	public int maxConnTotal() {
		return this.maxConnTotal;
	}

	public HttpClient socketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		return this;
	}

	public int socketTimeout() {
		return this.socketTimeout;
	}

	public HttpClient connectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public int connectTimeout() {
		return this.connectTimeout;
	}

	public HttpClient connectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
		return this;
	}

	public int connectionRequestTimeout() {
		return this.connectionRequestTimeout;
	}

	public HttpClient maxRedirects(int maxRedirects) {
		this.maxRedirects = maxRedirects;
		return this;
	}

	public int maxRedirects() {
		return this.maxRedirects;
	}

	public HttpClient raw(boolean raw) {
		this.raw = raw;
		return this;
	}

	public boolean raw() {
		return this.raw;
	}

	public HttpClient cookie(String name, String value) {
		cookies().put(name, value);
		return this;
	}

	public HttpClient header(String name, String value) {
		headers().put(name, value);
		return this;
	}

	public HttpClient data(String name, Object value) {
		data().put(name, value);
		return this;
	}

	public HttpClient file(String name, List<Upload> files) {
		files().put(name, files);
		return this;
	}

	public HttpClient dontClose() {
		this.dontClose = true;
		return this;
	}

	public String fetch() {
		return new String(execute());
	}

	public String fetchRaw() {
		raw(true);
		try {
			return new String(execute());
		} finally {
			raw(false);
		}
	}

	public <T> T parse() {
		return JSON.parse(fetch());
	}

	public byte[] execute() {
		return executeRequest(null).get();
	}

	public Future<byte[]> execute(Callback<byte[]> callback) {
		return executeRequest(callback);
	}

	private Future<byte[]> executeRequest(Callback<byte[]> callback) {
		if (client == null) {
			synchronized (this) {
				if (client == null) {
					client = HttpClientUtil.client(this);
					client.start();
				}
			}
		}

		U.notNull(client, "HTTP client");

		return HttpClientUtil.request(this, client, callback, !dontClose);
	}

	public synchronized void close() {
		HttpClientUtil.close(client);
	}

	public HttpClient get(String url) {
		return verb(HttpVerb.GET).url(url);
	}

	public HttpClient post(String url) {
		return verb(HttpVerb.POST).url(url);
	}

	public HttpClient put(String url) {
		return verb(HttpVerb.PUT).url(url);
	}

	public HttpClient delete(String url) {
		return verb(HttpVerb.DELETE).url(url);
	}

	public HttpClient patch(String url) {
		return verb(HttpVerb.PATCH).url(url);
	}

	public HttpClient options(String url) {
		return verb(HttpVerb.OPTIONS).url(url);
	}

	public HttpClient head(String url) {
		return verb(HttpVerb.HEAD).url(url);
	}

	public HttpClient trace(String url) {
		return verb(HttpVerb.TRACE).url(url);
	}

}
