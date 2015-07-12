package org.rapidoid.plugins.cache;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.concurrent.Promise;
import org.rapidoid.concurrent.Promises;
import org.rapidoid.plugins.Plugins;

/*
 * #%L
 * rapidoid-plugins
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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class Cache {

	public static void set(Object key, Object value, long timeToLiveMs, Callback<Void> callback) {
		Plugins.remoteCache().set(key, value, timeToLiveMs, callback);
	}

	public static <T> void get(Object key, Callback<T> callback) {
		Plugins.remoteCache().get(key, callback);
	}

	public static Future<Void> set(Object key, Object value, long timeToLiveMs) {
		Promise<Void> promise = Promises.create();
		set(key, value, timeToLiveMs, promise);
		return promise;
	}

	public static <T> Future<T> get(Object key) {
		Promise<T> promise = Promises.create();
		get(key, promise);
		return promise;
	}

}
