package org.rapidoid.cache.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.Cache;
import org.rapidoid.cache.Caching;
import org.rapidoid.commons.Rnd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.util.AbstractMapImpl;
import org.rapidoid.util.MapEntry;
import org.rapidoid.util.Msc;
import org.rapidoid.util.SimpleBucket;

import java.util.concurrent.ScheduledThreadPoolExecutor;
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
public class ConcurrentCache<K, V> extends AbstractMapImpl<K, ConcurrentCacheAtom<K, V>> implements Cache<K, V> {

	private static final int DESIRED_BUCKET_SIZE = 8;

	private final String name;

	private final int capacity;

	private final Mapper<K, V> loader;

	private final long ttlInMs;

	private final CacheStats stats = new CacheStats();

	private final boolean statistics;

	private final int l1Xor = Rnd.rnd();

	private static final int L1_SEGMENTS = 32;

	private static final int L1_SEGMENT_SIZE = 16;

	@SuppressWarnings("unchecked")
	private final L1CacheSegment<K, V>[] l1Cache = new L1CacheSegment[L1_SEGMENTS];

	private final int l1BitMask = Msc.bitMask(Msc.log2(L1_SEGMENTS));

	public static <K, V> ConcurrentCache<K, V> create(String name, int capacity, Mapper<K, V> loader, long ttlInMs,
	                                                  ScheduledThreadPoolExecutor scheduler, boolean statistics, boolean manageable) {

		boolean unbounded = capacity == 0;
		if (unbounded) capacity = 65536; // initial capacity

		return new ConcurrentCache<>(name, capacity, loader, ttlInMs, scheduler, statistics, manageable, unbounded);
	}

	private ConcurrentCache(String name, int capacity, Mapper<K, V> loader, long ttlInMs,
	                        ScheduledThreadPoolExecutor scheduler, boolean statistics, boolean manageable, boolean unbounded) {

		super(new SimpleCacheTable<K, V>(capacity, DESIRED_BUCKET_SIZE, unbounded));

		for (int i = 0; i < l1Cache.length; i++) {
			l1Cache[i] = new L1CacheSegment<>(L1_SEGMENT_SIZE);
		}

		this.name = name;
		this.loader = loader;
		this.ttlInMs = ttlInMs;
		this.statistics = statistics;

		scheduleCrawl(ttlInMs, scheduler);

		this.capacity = capacity;

		if (manageable) {
			new ManageableCache(this);
		}
	}

	private void scheduleCrawl(long ttlInMs, ScheduledThreadPoolExecutor scheduler) {
		if (ttlInMs > 0) {

			if (scheduler == null) {
				scheduler = Caching.scheduler();
			}

			scheduler.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try {
						crawl();
					} catch (Exception e) {
						Log.error("Error occurred while crawling the cache!", e);
					}
				}
			}, 1, 1, TimeUnit.SECONDS);
		}
	}

	private void crawl() {
		SimpleBucket<MapEntry<K, ConcurrentCacheAtom<K, V>>>[] buckets = entries.buckets;

		for (SimpleBucket<MapEntry<K, ConcurrentCacheAtom<K, V>>> bucket : buckets) {
			if (bucket != null) {
				for (int i = 0; i < bucket.size(); i++) {
					MapEntry<K, ConcurrentCacheAtom<K, V>> entry = bucket.get(i);

					if (entry != null) {
						ConcurrentCacheAtom<K, V> cachedCalc = entry.value;
						if (cachedCalc != null) {
							cachedCalc.checkTTL();
						}
					}
				}
			}
		}

		if (statistics) stats.crawls.incrementAndGet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V get(final K key) {
		int hash = key.hashCode();
		L1CacheSegment<K, V> l1 = l1Segment(hash);

		ConcurrentCacheAtom<K, V> l1atom = l1.find(key);

		if (l1atom != null) {
			if (statistics) stats.l1Hits.incrementAndGet();
			return l1atom.get();
		} else {
			if (statistics) stats.l1Misses.incrementAndGet();
		}

		SimpleBucket<MapEntry<K, ConcurrentCacheAtom<K, V>>> bucket = l2segment(hash);
		MapEntry<K, ConcurrentCacheAtom<K, V>> entry = findEntry(key, bucket);

		if (entry != null) {
			l1.add(hash, entry.value);

			ConcurrentCacheAtom<K, V> atom = entry.value;
			return atom.get();

		} else {
			ConcurrentCacheAtom<K, V> atom = createAtom(key);

			// this new atom might be ignored if other atom with the same key was inserted concurrently
			entry = putAtom(key, bucket, atom);

			l1.add(hash, entry.value);

			return atom.get();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V getIfExists(K key) {
		int hash = key.hashCode();
		L1CacheSegment<K, V> l1 = l1Segment(hash);

		ConcurrentCacheAtom<K, V> l1atom = l1.find(key);

		if (l1atom != null) {
			if (statistics) stats.l1Hits.incrementAndGet();
			return l1atom.getIfExists();
		} else {
			if (statistics) stats.l1Misses.incrementAndGet();
		}

		MapEntry<K, ConcurrentCacheAtom<K, V>> entry = findEntry(key);
		if (entry != null) {
			l1.add(hash, entry.value);
			return entry.value.getIfExists();

		} else {
			return null;
		}
	}

	private ConcurrentCacheAtom<K, V> createAtom(K key) {
		return statistics
			? new ConcurrentCacheAtomWithStats<>(key, loader, ttlInMs, stats)
			: new ConcurrentCacheAtom<>(key, loader, ttlInMs);
	}

	/**
	 * Searches again in synchronized way then putting the atom.
	 *
	 * @return new or existing entry for the specified key
	 */
	private MapEntry<K, ConcurrentCacheAtom<K, V>> putAtom(K key, SimpleBucket<MapEntry<K, ConcurrentCacheAtom<K, V>>> bucket,
	                                                       ConcurrentCacheAtom<K, V> atom) {
		synchronized (bucket) {

			// search again inside lock, maybe it was added in meantime
			MapEntry<K, ConcurrentCacheAtom<K, V>> entry = findEntry(key, bucket);

			if (entry == null) {
				entry = new MapEntry<>(key, atom);
				bucket.add(entry);
			}

			return entry;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invalidate(K key) {
		int hash = key.hashCode();

		l1Segment(hash).invalidate(key);

		SimpleBucket<MapEntry<K, ConcurrentCacheAtom<K, V>>> bucket = l2segment(hash);

		synchronized (bucket) {
			MapEntry<K, ConcurrentCacheAtom<K, V>> entry = findEntry(key, bucket);

			if (entry != null) {
				entry.value.invalidate();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(K key, V value) {
		int hash = key.hashCode();

		l1Segment(hash).set(key, value);

		SimpleBucket<MapEntry<K, ConcurrentCacheAtom<K, V>>> bucket = l2segment(hash);

		synchronized (bucket) {
			MapEntry<K, ConcurrentCacheAtom<K, V>> entry = findEntry(key, bucket);

			if (entry != null) {
				entry.value.set(value);
			} else {
				ConcurrentCacheAtom<K, V> atom = createAtom(key);
				atom.set(value);
				putAtom(key, bucket, atom);
			}
		}
	}

	public long ttlInMs() {
		return ttlInMs;
	}

	@Override
	public CacheStats stats() {
		return stats;
	}

	public String name() {
		return name;
	}

	@Override
	public int size() {
		int size = 0;

		for (int index = 0; index < entries.bucketCount(); index++) {
			SimpleBucket<MapEntry<K, ConcurrentCacheAtom<K, V>>> bucket = entries.getBucketAt(index);

			synchronized (bucket) {
				size += bucket.size();
			}
		}

		return size;
	}

	private L1CacheSegment<K, V> l1Segment(int hash) {
		return l1Cache[(hash ^ l1Xor) & l1BitMask];
	}

	private SimpleBucket<MapEntry<K, ConcurrentCacheAtom<K, V>>> l2segment(int hash) {
		return entries.bucket(hash);
	}

	public int capacity() {
		return capacity;
	}

	@Override
	public void bypass() {
		stats.bypassed.incrementAndGet();
	}

	@Override
	public synchronized void clear() {
		super.clear();

		for (L1CacheSegment<K, V> l1 : l1Cache) {
			l1.clear();
		}
	}

}
