package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Callbacks;
import org.rapidoid.concurrent.Future;
import org.rapidoid.concurrent.Futures;
import org.rapidoid.jackson.JSON;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Services {

	private static final Mapper<byte[], Object> JSON_BYTES_TO_OBJ = new Mapper<byte[], Object>() {
		@Override
		public Object map(byte[] src) throws Exception {
			return src != null ? JSON.parse(src, Object.class) : null;
		}
	};

	public static <T> Future<T> get(final String uri, final Callback<T> callback) {
		Mapper<byte[], T> mapper = U.cast(JSON_BYTES_TO_OBJ);
		Callback<byte[]> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(HTTP.get(uri, cb), mapper);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(final String uri) {
		return (T) get(uri, null).get();
	}

	public static <T> Future<T> post(final String uri, final Callback<T> callback) {
		Mapper<byte[], T> mapper = U.cast(JSON_BYTES_TO_OBJ);
		Callback<byte[]> cb = Callbacks.mapping(callback, mapper);
		return Futures.mapping(HTTP.post(uri, null, null, null, cb), mapper);
	}

	@SuppressWarnings("unchecked")
	public static <T> T post(final String uri) {
		return (T) post(uri, null).get();
	}

}
