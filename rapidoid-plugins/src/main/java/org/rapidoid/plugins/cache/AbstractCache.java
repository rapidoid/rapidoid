package org.rapidoid.plugins.cache;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Future;
import org.rapidoid.concurrent.Promise;
import org.rapidoid.concurrent.Promises;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public abstract class AbstractCache<K, V> implements ICache<K, V> {

	protected final String name;

	protected final long timeToLiveMs;

	protected final boolean allowsDifferentTTLThanInitialized;

	public AbstractCache(String name, long timeToLiveMs, boolean allowsDifferentTTLThanInitialized) {
		this.name = name;
		this.timeToLiveMs = timeToLiveMs;
		this.allowsDifferentTTLThanInitialized = allowsDifferentTTLThanInitialized;
	}

	@Override
	public Future<Void> set(K key, V value, long timeToLiveMs) {
		validateTTL(timeToLiveMs);
		Promise<Void> promise = Promises.create();
		set(key, value, timeToLiveMs, promise);
		return promise;
	}

	@Override
	public Future<V> get(K key) {
		Promise<V> promise = Promises.create();
		get(key, promise);
		return promise;
	}

	@Override
	public void set(K key, V value, Callback<Void> callback) {
		set(key, value, timeToLiveMs, callback);
	}

	@Override
	public Future<Void> set(K key, V value) {
		return set(key, value, timeToLiveMs);
	}

	@Override
	public void set(K key, V value, long timeToLiveMs, Callback<Void> callback) {
		validateTTL(timeToLiveMs);
		doSet(key, value, timeToLiveMs, callback);
	}

	@Override
	public void get(K key, Callback<V> callback) {
		doGet(key, callback);
	}

	private void validateTTL(long ttl) {
		U.argMust(
				this.allowsDifferentTTLThanInitialized || ttl == this.timeToLiveMs,
				"The cache implementation doesn't support different timeToLiveMs value than the initially specified: %s",
				this.allowsDifferentTTLThanInitialized);
	}

	protected abstract void doSet(K key, V value, long timeToLiveMs, Callback<Void> callback);

	protected abstract void doGet(K key, Callback<V> callback);

}
