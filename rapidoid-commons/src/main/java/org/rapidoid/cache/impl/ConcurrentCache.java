package org.rapidoid.cache.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.Cache;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;
import org.rapidoid.util.AbstractMapImpl;
import org.rapidoid.util.MapEntry;
import org.rapidoid.util.SimpleList;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/*
 * #%L
 * rapidoid-commons
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
@Since("5.3.0")
public class ConcurrentCache<K, V> extends AbstractMapImpl<K, ConcurrentCacheAtom<V>> implements Cache<K, V> {

	private static final int BUCKET_SIZE = 10;

	private final Mapper<K, V> loader;

	private final long ttlInMs;

	public ConcurrentCache(int capacity, Mapper<K, V> loader, long ttlInMs) {
		this(capacity / BUCKET_SIZE, BUCKET_SIZE, loader, ttlInMs);
	}

	public ConcurrentCache(int buckets, int bucketSize, Mapper<K, V> loader, long ttlInMs) {
		super(buckets, bucketSize);
		this.loader = loader;
		this.ttlInMs = ttlInMs;

		U.must(buckets > 0 && bucketSize > 0, "The capacity is too small!");

		Jobs.every(1, TimeUnit.SECONDS).run(new Runnable() {
			@Override
			public void run() {
				crawl();
			}
		});
	}

	private void crawl() {
		SimpleList<MapEntry<K, ConcurrentCacheAtom<V>>>[] buckets = entries.buckets;

		for (SimpleList<MapEntry<K, ConcurrentCacheAtom<V>>> bucket : buckets) {
			if (bucket != null) {
				for (int i = 0; i < bucket.size(); i++) {
					MapEntry<K, ConcurrentCacheAtom<V>> entry = bucket.get(i);

					if (entry != null) {
						ConcurrentCacheAtom<V> cachedCalc = entry.value;
						if (cachedCalc != null) {
							cachedCalc.checkTTL();
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V get(final K key) {
		SimpleList<MapEntry<K, ConcurrentCacheAtom<V>>> bucket = entries.bucket(key.hashCode());
		MapEntry<K, ConcurrentCacheAtom<V>> entry = findEntry(key, bucket);

		if (entry != null) {
			return entry.value.get();

		} else {
			ConcurrentCacheAtom<V> atom = new ConcurrentCacheAtom<>(loaderFor(key), ttlInMs);

			putAtom(key, bucket, atom);

			return atom.get();
		}
	}

	public void putAtom(K key, SimpleList<MapEntry<K, ConcurrentCacheAtom<V>>> bucket, ConcurrentCacheAtom<V> atom) {
		synchronized (bucket) {
			MapEntry<K, ConcurrentCacheAtom<V>> oldEntry = bucket.addRotating(new MapEntry<>(key, atom));

			if (oldEntry != null) {
				oldEntry.value.invalidate();
			}
		}
	}

	private Callable<V> loaderFor(final K key) {
		if (loader == null) {
			return null;
		}

		return new Callable<V>() {
			@Override
			public V call() throws Exception {
				return loader.map(key);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V getIfExists(K key) {
		MapEntry<K, ConcurrentCacheAtom<V>> entry = findEntry(key);
		return entry != null ? entry.value.get() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invalidate(K key) {
		MapEntry<K, ConcurrentCacheAtom<V>> entry = findEntry(key);
		if (entry != null) {
			entry.value.invalidate();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(K key, V value) {
		SimpleList<MapEntry<K, ConcurrentCacheAtom<V>>> bucket = entries.bucket(key.hashCode());
		MapEntry<K, ConcurrentCacheAtom<V>> entry = findEntry(key, bucket);

		if (entry != null) {
			entry.value.set(value);
		} else {
			ConcurrentCacheAtom<V> atom = new ConcurrentCacheAtom<>(loaderFor(key), ttlInMs);
			atom.set(value);
			putAtom(key, bucket, atom);
		}
	}

}
