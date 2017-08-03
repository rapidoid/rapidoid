package org.rapidoid.http;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.log.Log;
import org.rapidoid.util.LazyInit;
import org.rapidoid.util.MscOpts;

import java.util.Map;
import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-http-client
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
@Since("5.1.0")
public class HttpClient extends RapidoidThing {

	private volatile String host = null;

	private volatile String userAgent = null;

	private volatile boolean followRedirects = false;

	private volatile boolean keepAlive = false;

	private volatile boolean keepCookies = false;

	private volatile boolean reuseConnections = false;

	private volatile boolean decompress = true;

	private volatile int maxConnPerRoute = 0;

	private volatile int maxConnTotal = 0;

	private volatile int maxRedirects = 5;

	private volatile boolean validateSSL = !MscOpts.isTLSEnabled();

	private volatile int timeout = 5000;

	private final Map<String, String> cookies = Coll.synchronizedMap();

	private final LazyInit<CloseableHttpAsyncClient> client = new LazyInit<CloseableHttpAsyncClient>(
		new Callable<CloseableHttpAsyncClient>() {

			@Override
			public CloseableHttpAsyncClient call() throws Exception {
				CloseableHttpAsyncClient client = HttpClientUtil.client(HttpClient.this);
				client.start();
				return client;
			}

		});

	public Future<HttpResp> executeRequest(HttpReq req, Callback<HttpResp> callback) {
		return HttpClientUtil.request(req, client.get(), callback, false);
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

	public HttpClient maxRedirects(int maxRedirects) {
		this.maxRedirects = maxRedirects;
		return this;
	}

	public int maxRedirects() {
		return this.maxRedirects;
	}

	public boolean validateSSL() {
		return validateSSL;
	}

	public HttpClient validateSSL(boolean validateSSL) {
		this.validateSSL = validateSSL;
		return this;
	}

	public HttpClient cookie(String name, String value) {
		cookies().put(name, value);
		return this;
	}

	public String host() {
		return host;
	}

	public HttpClient host(String host) {
		this.host = host;
		return this;
	}

	public HttpClient cookies(Map<String, String> cookies) {
		Coll.assign(this.cookies, cookies);
		return this;
	}

	public Map<String, String> cookies() {
		return this.cookies;
	}

	public int timeout() {
		return timeout;
	}

	public HttpClient timeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public HttpReq req() {
		return new HttpReq(this);
	}

	public HttpReq get(String url) {
		return req().verb(HttpVerb.GET).url(url);
	}

	public HttpReq post(String url) {
		return req().verb(HttpVerb.POST).url(url);
	}

	public HttpReq put(String url) {
		return req().verb(HttpVerb.PUT).url(url);
	}

	public HttpReq delete(String url) {
		return req().verb(HttpVerb.DELETE).url(url);
	}

	public HttpReq patch(String url) {
		return req().verb(HttpVerb.PATCH).url(url);
	}

	public HttpReq options(String url) {
		return req().verb(HttpVerb.OPTIONS).url(url);
	}

	public HttpReq head(String url) {
		return req().verb(HttpVerb.HEAD).url(url);
	}

	public HttpReq trace(String url) {
		return req().verb(HttpVerb.TRACE).url(url);
	}

	public synchronized void close() {
		try {
			client.resetAndClose();
		} catch (Exception e) {
			Log.error("Error while closing the HTTP client!", e);
		}
	}

	public synchronized void reset() {
		close();
	}
}
