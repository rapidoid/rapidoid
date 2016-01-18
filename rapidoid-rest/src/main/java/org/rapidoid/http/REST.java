package org.rapidoid.http;

/*
 * #%L
 * rapidoid-rest
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
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class REST {

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

	public static <T> T call(String verb, String uri, Class<T> resultType) {
		if ("GET".equalsIgnoreCase(verb)) {
			return get(uri, resultType);
		} else if ("POST".equalsIgnoreCase(verb)) {
			return post(uri, resultType);
		} else {
			// FIXME support PUT and DELETE
			throw U.rte("Unsupported REST verb: '%s'", verb);
		}
	}

	public static <T> Future<T> call(String verb, String uri, Class<T> resultType, Callback<T> callback) {
		if ("GET".equalsIgnoreCase(verb)) {
			return get(uri, resultType, callback);
		} else if ("POST".equalsIgnoreCase(verb)) {
			return post(uri, resultType, callback);
		} else {
			// FIXME support PUT and DELETE
			throw U.rte("Unsupported REST verb: '%s'", verb);
		}
	}

	public static <T> T client(Class<T> clientInterface) {
		return U.dynamic(clientInterface, new DynamicRESTClient(clientInterface));
	}

}
