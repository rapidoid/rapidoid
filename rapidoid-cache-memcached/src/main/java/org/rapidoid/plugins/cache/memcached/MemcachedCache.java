package org.rapidoid.plugins.cache.memcached;

/*
 * #%L
 * rapidoid-cache-memcached
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

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.GetCompletionListener;
import net.spy.memcached.internal.GetFuture;
import net.spy.memcached.internal.OperationCompletionListener;
import net.spy.memcached.internal.OperationFuture;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Callbacks;
import org.rapidoid.plugins.cache.AbstractCache;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.2.0")
public class MemcachedCache<K, V> extends AbstractCache<K, V> {

	private final MemcachedClient client;

	public MemcachedCache(MemcachedClient client, String cacheName, long timeToLiveMs,
			boolean resetTimeToLiveWhenAccessed) {
		super(cacheName, timeToLiveMs, false);
		this.client = client;
	}

	@Override
	protected void doSet(K key, V value, long timeToLiveMs, final Callback<Void> callback) {
		try {

			// TTL specified in seconds can be max 1 month (2,592,000 seconds)
			int timeToLiveSec = (int) Math.min(timeToLiveMs / 1000, 2592000);

			OperationFuture<Boolean> future = client.set(key(key), timeToLiveSec, value);

			future.addListener(new OperationCompletionListener() {

				@Override
				public void onComplete(OperationFuture<?> future) throws Exception {
					boolean success = (Boolean) future.get();
					if (success) {
						Callbacks.success(callback, null);
					} else {
						Callbacks.error(callback, U.rte("Couldn't write the value to the cache!"));
					}
				}

			});

		} catch (Exception e) {
			Callbacks.error(callback, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(K key, final Callback<V> callback) {
		try {

			GetFuture<Object> future = client.asyncGet(key(key));

			future.addListener(new GetCompletionListener() {
				@Override
				public void onComplete(GetFuture<?> future) throws Exception {
					V value = (V) future.get();
					Callbacks.success(callback, value);
				}
			});

		} catch (Exception e) {
			Callbacks.error(callback, e);
		}
	}

	private String key(K key) {
		String skey = Cls.str(key);
		return !U.isEmpty(name) ? name + ":::" + skey : skey;
	}

}
