package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.data.JSON;
import org.rapidoid.io.Upload;
import org.rapidoid.u.U;
import org.rapidoid.util.Expectation;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;

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
public class HttpReq extends RapidoidThing {

	private final HttpClient client;

	private volatile HttpVerb verb = null;

	private volatile String url = null;

	private volatile byte[] body = null;

	private final Map<String, String> headers = Coll.synchronizedMap();

	private final Map<String, String> cookies = Coll.synchronizedMap();

	private final Map<String, Object> data = Coll.synchronizedMap();

	private final Map<String, List<Upload>> files = Coll.synchronizedMap();

	private volatile String contentType = null;

	private volatile boolean raw = false;

	private volatile int socketTimeout;

	private volatile int connectTimeout;

	private volatile int connectionRequestTimeout;

	public HttpReq(HttpClient client) {
		this.client = client;
		this.socketTimeout = client.timeout();
		this.connectTimeout = client.timeout();
		this.connectionRequestTimeout = client.timeout();
	}

	public HttpReq verb(HttpVerb verb) {
		this.verb = verb;
		return this;
	}

	public HttpReq verb(String verb) {
		return verb(HttpVerb.from(verb.toUpperCase()));
	}

	public HttpVerb verb() {
		return this.verb;
	}

	public HttpReq url(String url) {
		this.url = url;
		return this;
	}

	public String url() {
		return this.url;
	}

	public HttpReq body(byte[] body) {
		this.body = body;
		return this;
	}

	public byte[] body() {
		return this.body;
	}

	public HttpReq headers(Map<String, String> headers) {
		Coll.assign(this.headers, headers);
		return this;
	}

	public Map<String, String> headers() {
		return this.headers;
	}

	public HttpReq cookies(Map<String, String> cookies) {
		Coll.assign(this.cookies, cookies);
		return this;
	}

	public Map<String, String> cookies() {
		return this.cookies;
	}

	public HttpReq data(Map<String, ?> data) {
		Coll.assign(this.data, data);
		return this;
	}

	public Map<String, Object> data() {
		return this.data;
	}

	public HttpReq files(Map<String, List<Upload>> files) {
		Coll.assign(this.files, files);
		return this;
	}

	public Map<String, List<Upload>> files() {
		return this.files;
	}

	public HttpReq contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public String contentType() {
		return this.contentType;
	}

	public String fetch() {
		return execute().body();
	}

	public String fetchRaw() {
		raw(true);
		try {
			return execute().body();
		} finally {
			raw(false);
		}
	}

	public HttpReq raw(boolean raw) {
		this.raw = raw;
		return this;
	}

	public boolean raw() {
		return this.raw;
	}

	public HttpReq header(String name, String value) {
		headers().put(name, value);
		return this;
	}

	public HttpReq cookie(String name, String value) {
		cookies().put(name, value);
		return this;
	}

	public HttpReq data(String name, Object value) {
		data().put(name, value);
		return this;
	}

	public HttpReq file(String name, List<Upload> files) {
		files().put(name, files);
		return this;
	}

	public HttpReq socketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		return this;
	}

	public int socketTimeout() {
		return this.socketTimeout;
	}

	public HttpReq connectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public int connectTimeout() {
		return this.connectTimeout;
	}

	public HttpReq connectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
		return this;
	}

	public int connectionRequestTimeout() {
		return this.connectionRequestTimeout;
	}

	public <T> T parse() {
		return JSON.parse(fetch());
	}

	public void print() {
		U.print(fetch());
	}

	public Map<String, Object> toMap() {
		return parse();
	}

	public Expectation expect() {
		return new Expectation(fetch());
	}

	public HttpReq expect(String expectedValue) {
		expect().value(expectedValue);
		return this;
	}

	public <T> T toBean(Class<T> beanClass) {
		return JSON.MAPPER.convertValue(toMap(), beanClass);
	}

	public HttpResp execute() {
		return client.executeRequest(this, null).get();
	}

	public Future<HttpResp> execute(Callback<HttpResp> callback) {
		return client.executeRequest(this, callback);
	}

	public HttpReq get(String url) {
		return verb(HttpVerb.GET).url(url);
	}

	public HttpReq post(String url) {
		return verb(HttpVerb.POST).url(url);
	}

	public HttpReq put(String url) {
		return verb(HttpVerb.PUT).url(url);
	}

	public HttpReq delete(String url) {
		return verb(HttpVerb.DELETE).url(url);
	}

	public HttpReq patch(String url) {
		return verb(HttpVerb.PATCH).url(url);
	}

	public HttpReq options(String url) {
		return verb(HttpVerb.OPTIONS).url(url);
	}

	public HttpReq head(String url) {
		return verb(HttpVerb.HEAD).url(url);
	}

	public HttpReq trace(String url) {
		return verb(HttpVerb.TRACE).url(url);
	}

	public void benchmark(int rounds, int threads, int requests) {

		final HttpClient client = HTTP.client().reuseConnections(true).keepAlive(true)
			.maxConnTotal(threads).maxConnPerRoute(threads).keepCookies(false);

		final HttpReq req = this;

		for (int i = 0; i < rounds; i++) {
			Msc.benchmarkMT(threads, "req", requests, new Runnable() {
				@Override
				public void run() {
					HttpResp resp = client.executeRequest(req, null).get();
					U.notNull(resp, "HTTP response");
				}
			});
		}

		client.close();
	}

}
