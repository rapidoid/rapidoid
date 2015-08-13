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
import org.rapidoid.concurrent.Future;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Services {

	public static final ServicesClient DEFAULT_CLIENT = new ServicesClient();

	public static <T> Future<T> get(String uri, Callback<T> callback) {
		return DEFAULT_CLIENT.get(uri, callback);
	}

	public static <T> T get(String uri) {
		return DEFAULT_CLIENT.get(uri);
	}

	public static <T> Future<T> post(String uri, Callback<T> callback) {
		return DEFAULT_CLIENT.post(uri, callback);
	}

	public static <T> T post(String uri) {
		return DEFAULT_CLIENT.post(uri);
	}

}
