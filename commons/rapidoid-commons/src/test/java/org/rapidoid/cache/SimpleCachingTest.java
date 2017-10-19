package org.rapidoid.cache;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.impl.CacheStats;
import org.rapidoid.cache.impl.ConcurrentCacheAtomWithStats;
import org.rapidoid.commons.Rnd;
import org.rapidoid.io.IO;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.BenchmarkOperation;
import org.rapidoid.util.Msc;

import java.util.concurrent.atomic.AtomicInteger;

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
public class SimpleCachingTest extends TestCommons {

	private static final Mapper<Integer, Integer> N_TO_N = new Mapper<Integer, Integer>() {
		@Override
		public Integer map(Integer key) throws Exception {
			return key;
		}
	};

	private static Mapper<String, Integer> LENGTH = new Mapper<String, Integer>() {
		@Override
		public Integer map(String src) throws Exception {
			return src.length();
		}
	};

	private static Mapper<Integer, Integer> NEXT = new Mapper<Integer, Integer>() {
		@Override
		public Integer map(Integer x) throws Exception {
			return x + 1;
		}
	};

	private static Mapper<String, String> ABC = new Mapper<String, String>() {
		@Override
		public String map(String key) throws Exception {
			return IO.load(key);
		}
	};

	@Test
	public void testCachedValue() {
		final ConcurrentCacheAtomWithStats<String, String> cached = new ConcurrentCacheAtomWithStats<>("cached-file.txt", ABC, 10, new CacheStats());

		Msc.benchmarkMT(100, "reads", 10000000, new Runnable() {
			@Override
			public void run() {
				try {
					eq(cached.get(), "ABC");

				} catch (Exception e) {
					throw U.rte(e);
				}
			}
		});

		U.print(cached);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCache() {
		final int capacity = 1000;

		final Cache<Integer, Integer> cache = Caching.of(NEXT).capacity(capacity).ttl(10).statistics(true).build();

		Msc.benchmarkMT(100, "ops", 10000000, new Runnable() {
			@Override
			public void run() {
				int n = Rnd.rnd(capacity * 100);

				if (Rnd.rnd(3) == 0) cache.invalidate(n);

				Integer maybe = cache.getIfExists(n);
				isTrue(maybe == null || maybe == n + 1);

				eq(cache.get(n).intValue(), n + 1);

				if (Rnd.rnd(5) == 0) cache.set(n, n + 1);
			}
		});

		CacheStats stats = cache.stats();
		U.print(stats);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPreloadedCache() {
		int count = 100_000;

		final Cache<Integer, Integer> cache = Caching.of(N_TO_N).statistics(true).build();

		loadCacheValues(cache, count);

		CacheStats stats = cache.stats();
		stats.reset();

		final int mask = Msc.bitMask(10); // 1024 hot keys

		int total = 200_000_000;
		Msc.benchmarkMT(8, "ops", total, new BenchmarkOperation() {
			@Override
			public void run(int i) {
				int key = i & mask;
				int n = cache.get(key);
				eq(n, key);
			}
		});

		U.print(stats);

		eq(stats.hits.get(), total);
		eq(stats.total(), total);
		eq(cache.size(), count);

		// 1024 hot keys, with L1 cache size == 512 -> expecting 50% L1 hit rate
		double l1HitRate = stats.l1Hits.get() * 1.0 / stats.l1Misses.get();
		Log.info("L1 hit rate", "rate", l1HitRate);

		isTrue(0.7 < l1HitRate);
		isTrue(l1HitRate < 1.3);
	}

	private void loadCacheValues(Cache<Integer, ?> cache, int countTo) {
		for (int i = 0; i < countTo; i++) {
			cache.get(Rnd.rnd(countTo));
		}

		U.print(cache.stats());

		for (int i = 0; i < countTo; i++) {
			cache.get(i);
		}

		U.print(cache.stats());

		for (int i = 0; i < countTo; i++) {
			cache.get(i);
		}

		U.print(cache.stats());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCacheCrawling() {
		Cache<Integer, Integer> cache = Caching.of(N_TO_N).capacity(100).statistics(true).build();
		eq(cache.get(1000).intValue(), 1000);

		U.sleep(1500);

		eq(Caching.scheduler().getCompletedTaskCount(), 0);
		eq(cache.stats().crawls.get(), 0);

		Cache<Integer, Integer> cacheWithTTL = Caching.of(N_TO_N).capacity(100).statistics(true).ttl(100).build();
		eq(cacheWithTTL.get(22).intValue(), 22);

		U.sleep(1500);
		eq(Caching.scheduler().getCompletedTaskCount(), 1);
		isTrue(cacheWithTTL.stats().crawls.get() > 0);

		isFalse(Caching.scheduler().isShutdown());
		isFalse(Caching.scheduler().isTerminated());
		isFalse(Caching.scheduler().isTerminating());

		Caching.shutdown();

		isTrue(Caching.scheduler().isShutdown());
		isTrue(Caching.scheduler().isTerminated());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCacheInvalidation() {

		final AtomicInteger value = new AtomicInteger();

		final Cache<Integer, Integer> cache = Caching.of(new Mapper<Integer, Integer>() {
			@Override
			public Integer map(Integer key) throws Exception {
				return value.get();
			}
		}).capacity(64).build();

		for (int i = 0; i < 100; i++) {
			iteration(value, cache);
		}
	}

	private void iteration(AtomicInteger value, final Cache<Integer, Integer> cache) {

		final int max = 2000;
		final int n = value.incrementAndGet();

		for (int i = 0; i < max; i++) {
			cache.set(i, -n);
		}

		checkValues("changed", cache, max, -n, true);

		for (int i = 0; i < max; i++) {
			cache.invalidate(i);
		}

		checkValues("invalidated", cache, max, n, false);
	}

	private void checkValues(String op, final Cache<Integer, Integer> cache, final int max, final int expect, final boolean optional) {
		Msc.benchmarkMT(64, op, 640_000, new BenchmarkOperation() {
			@Override
			public void run(int i) {
				int key = i % max;

				Integer maybeVal = cache.getIfExists(key);
				if (maybeVal != null) {
					eq(maybeVal.intValue(), expect);
				}

				if (!optional) {
					eq(cache.get(key).intValue(), expect);
				}
			}
		});
	}

}
