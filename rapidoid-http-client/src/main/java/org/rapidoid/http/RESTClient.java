package org.rapidoid.http;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Callbacks;
import org.rapidoid.concurrent.Future;
import org.rapidoid.concurrent.Futures;

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
@Since("4.1.0")
public class RESTClient extends RapidoidThing {

	private final HttpClient client = HTTP.client();

	/* GET */

	public <T> Future<T> get(String uri, Class<T> resultType, Callback<T> callback) {
		RESTResultMapper<T> mapper = new RESTResultMapper<T>(resultType);
		Callback<HttpResp> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(client.req().get(uri).execute(cb), mapper);
	}

	public <T> T get(String uri, Class<T> resultType) {
		return (T) get(uri, resultType, null).get();
	}

	/* POST */

	public <T> Future<T> post(String uri, Class<T> resultType, Callback<T> callback) {
		RESTResultMapper<T> mapper = new RESTResultMapper<T>(resultType);
		Callback<HttpResp> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(client.req().post(uri).execute(cb), mapper);
	}

	public <T> T post(String uri, Class<T> resultType) {
		return (T) post(uri, resultType, null).get();
	}

	/* PUT */

	public <T> Future<T> put(String uri, Class<T> resultType, Callback<T> callback) {
		RESTResultMapper<T> mapper = new RESTResultMapper<T>(resultType);
		Callback<HttpResp> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(client.req().put(uri).execute(cb), mapper);
	}

	public <T> T put(String uri, Class<T> resultType) {
		return (T) put(uri, resultType, null).get();
	}

	/* DELETE */

	public <T> Future<T> delete(String uri, Class<T> resultType, Callback<T> callback) {
		RESTResultMapper<T> mapper = new RESTResultMapper<T>(resultType);
		Callback<HttpResp> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(client.req().delete(uri).execute(cb), mapper);
	}

	public <T> T delete(String uri, Class<T> resultType) {
		return (T) delete(uri, resultType, null).get();
	}

	/* PATCH */

	public <T> Future<T> patch(String uri, Class<T> resultType, Callback<T> callback) {
		RESTResultMapper<T> mapper = new RESTResultMapper<T>(resultType);
		Callback<HttpResp> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(client.req().patch(uri).execute(cb), mapper);
	}

	public <T> T patch(String uri, Class<T> resultType) {
		return (T) patch(uri, resultType, null).get();
	}

	/* OPTIONS */

	public <T> Future<T> options(String uri, Class<T> resultType, Callback<T> callback) {
		RESTResultMapper<T> mapper = new RESTResultMapper<T>(resultType);
		Callback<HttpResp> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(client.req().options(uri).execute(cb), mapper);
	}

	public <T> T options(String uri, Class<T> resultType) {
		return (T) options(uri, resultType, null).get();
	}

	/* HEAD */

	public <T> Future<T> head(String uri, Class<T> resultType, Callback<T> callback) {
		RESTResultMapper<T> mapper = new RESTResultMapper<T>(resultType);
		Callback<HttpResp> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(client.req().head(uri).execute(cb), mapper);
	}

	public <T> T head(String uri, Class<T> resultType) {
		return (T) head(uri, resultType, null).get();
	}

	/* TRACE */

	public <T> Future<T> trace(String uri, Class<T> resultType, Callback<T> callback) {
		RESTResultMapper<T> mapper = new RESTResultMapper<T>(resultType);
		Callback<HttpResp> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(client.req().trace(uri).execute(cb), mapper);
	}

	public <T> T trace(String uri, Class<T> resultType) {
		return (T) trace(uri, resultType, null).get();
	}

	public void close() {
		client.close();
	}
}
