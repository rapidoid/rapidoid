package org.rapidoid.cache;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.AbstractMapImpl;
import org.rapidoid.util.MapEntry;
import org.rapidoid.util.SimpleList;

import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class CachedImpl<K, V> extends AbstractMapImpl<K, CachedCalc<V>> implements Cached<K, V> {

	private static final int BUCKET_SIZE = 10;

	private final Mapper<K, V> loader;

	private final long ttlInMs;

	public CachedImpl(int capacity, Mapper<K, V> loader, long ttlInMs) {
		this(capacity / BUCKET_SIZE, BUCKET_SIZE, loader, ttlInMs);
	}

	public CachedImpl(int buckets, int bucketSize, Mapper<K, V> loader, long ttlInMs) {
		super(buckets, bucketSize);
		this.loader = loader;
		this.ttlInMs = ttlInMs;
	}

	@Override
	public V get(final K key) {
		SimpleList<MapEntry<K, CachedCalc<V>>> bucket = entries.bucket(key.hashCode());
		MapEntry<K, CachedCalc<V>> entry = findEntry(key, bucket);

		if (entry != null) {
			return entry.value.get();
		}

		CachedCalc<V> cachedValue = new ConcurrentCached<V>(loaderFor(key), ttlInMs);

		synchronized (bucket) {
			bucket.addRotating(new MapEntry<K, CachedCalc<V>>(key, cachedValue));
		}

		return cachedValue.get();
	}

	private Callable<V> loaderFor(final K key) {
		return new Callable<V>() {
			@Override
			public V call() throws Exception {
				return loader.map(key);
			}
		};
	}

}
