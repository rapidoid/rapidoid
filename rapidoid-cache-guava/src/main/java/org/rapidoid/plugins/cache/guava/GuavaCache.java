package org.rapidoid.plugins.cache.guava;

/*
 * #%L
 * rapidoid-cache-guava
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

import java.util.concurrent.TimeUnit;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Callbacks;
import org.rapidoid.plugins.cache.AbstractCache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class GuavaCache<K, V> extends AbstractCache<K, V> {

	private final Cache<K, V> cache;

	public GuavaCache(String cacheName, long timeToLiveMs, boolean resetTimeToLiveWhenAccessed) {
		super(cacheName, timeToLiveMs, false);
		this.cache = builder(timeToLiveMs, resetTimeToLiveWhenAccessed).build();
	}

	@SuppressWarnings("unchecked")
	private static <K, V> CacheBuilder<K, V> builder(long timeToLiveMs, boolean resetTimeToLiveWhenAccessed) {
		CacheBuilder<K, V> builder = (CacheBuilder<K, V>) CacheBuilder.newBuilder();

		if (resetTimeToLiveWhenAccessed) {
			return builder.expireAfterAccess(timeToLiveMs, TimeUnit.MILLISECONDS);
		} else {
			return builder.expireAfterWrite(timeToLiveMs, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	protected void doSet(K key, V value, long timeToLiveMs, Callback<Void> callback) {
		cache.put(key, value);
		Callbacks.success(callback, null);
	}

	@Override
	protected void doGet(K key, Callback<V> callback) {
		V value = cache.getIfPresent(key);
		Callbacks.success(callback, value);
	}

}
