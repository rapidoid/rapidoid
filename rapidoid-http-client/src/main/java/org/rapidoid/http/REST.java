package org.rapidoid.http;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.config.Config;
import org.rapidoid.config.RapidoidInitializer;
import org.rapidoid.util.Msc;

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
public class REST extends RapidoidInitializer {

	public static final RESTClient DEFAULT_CLIENT = new RESTClient();

	public static <T> Future<T> get(String uri, Class<T> resultType, Callback<T> callback) {
		return DEFAULT_CLIENT.get(uri, resultType, callback);
	}

	public static <T> T get(String uri, Class<T> resultType) {
		return DEFAULT_CLIENT.get(uri, resultType);
	}

	public static <T> Future<T> post(String uri, Class<T> resultType, Callback<T> callback) {
		return DEFAULT_CLIENT.post(uri, resultType, callback);
	}

	public static <T> T post(String uri, Class<T> resultType) {
		return DEFAULT_CLIENT.post(uri, resultType);
	}

	public static <T> Future<T> put(String uri, Class<T> resultType, Callback<T> callback) {
		return DEFAULT_CLIENT.put(uri, resultType, callback);
	}

	public static <T> T put(String uri, Class<T> resultType) {
		return DEFAULT_CLIENT.put(uri, resultType);
	}

	public static <T> Future<T> delete(String uri, Class<T> resultType, Callback<T> callback) {
		return DEFAULT_CLIENT.delete(uri, resultType, callback);
	}

	public static <T> T delete(String uri, Class<T> resultType) {
		return DEFAULT_CLIENT.delete(uri, resultType);
	}

	public static <T> Future<T> patch(String uri, Class<T> resultType, Callback<T> callback) {
		return DEFAULT_CLIENT.patch(uri, resultType, callback);
	}

	public static <T> T patch(String uri, Class<T> resultType) {
		return DEFAULT_CLIENT.patch(uri, resultType);
	}

	public static <T> Future<T> options(String uri, Class<T> resultType, Callback<T> callback) {
		return DEFAULT_CLIENT.options(uri, resultType, callback);
	}

	public static <T> T options(String uri, Class<T> resultType) {
		return DEFAULT_CLIENT.options(uri, resultType);
	}

	public static <T> Future<T> head(String uri, Class<T> resultType, Callback<T> callback) {
		return DEFAULT_CLIENT.head(uri, resultType, callback);
	}

	public static <T> T head(String uri, Class<T> resultType) {
		return DEFAULT_CLIENT.head(uri, resultType);
	}

	public static <T> Future<T> trace(String uri, Class<T> resultType, Callback<T> callback) {
		return DEFAULT_CLIENT.trace(uri, resultType, callback);
	}

	public static <T> T trace(String uri, Class<T> resultType) {
		return DEFAULT_CLIENT.trace(uri, resultType);
	}

	public static <T> T call(String verb, String uri, Class<T> resultType) {
		switch (HttpVerb.from(verb)) {
			case GET:
				return get(uri, resultType);

			case POST:
				return post(uri, resultType);

			case PUT:
				return put(uri, resultType);

			case DELETE:
				return delete(uri, resultType);

			case PATCH:
				return patch(uri, resultType);

			case OPTIONS:
				return options(uri, resultType);

			case HEAD:
				return head(uri, resultType);

			case TRACE:
				return trace(uri, resultType);

			default:
				throw Err.notExpected();
		}
	}

	public static <T> Future<T> call(String verb, String uri, Class<T> resultType, Callback<T> callback) {
		switch (HttpVerb.from(verb)) {
			case GET:
				return get(uri, resultType, callback);

			case POST:
				return post(uri, resultType, callback);

			case PUT:
				return put(uri, resultType, callback);

			case DELETE:
				return delete(uri, resultType, callback);

			case PATCH:
				return patch(uri, resultType, callback);

			case OPTIONS:
				return options(uri, resultType, callback);

			case HEAD:
				return head(uri, resultType, callback);

			case TRACE:
				return trace(uri, resultType, callback);

			default:
				throw Err.notExpected();
		}
	}

	public static <T> T client(Class<T> clientInterface) {
		return Msc.dynamic(clientInterface, new DynamicRESTClient(clientInterface));
	}

	public static <T> T client(Class<T> clientInterface, Config config) {
		return Msc.dynamic(clientInterface, new DynamicRESTClient(clientInterface, config));
	}

}
