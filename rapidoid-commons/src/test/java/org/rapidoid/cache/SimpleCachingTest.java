package org.rapidoid.cache;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.impl.CacheStats;
import org.rapidoid.cache.impl.ConcurrentCacheAtom;
import org.rapidoid.commons.Rnd;
import org.rapidoid.io.IO;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.concurrent.Callable;

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

	private static Callable<String> ABC = new Callable<String>() {
		@Override
		public String call() throws Exception {
			return IO.load("cached-file.txt");
		}
	};

	@Test
	public void testCachedValue() {
		final ConcurrentCacheAtom<String> cached = new ConcurrentCacheAtom<String>(ABC, 10, new CacheStats());

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

		System.out.println(cached);
	}

	@Test
	public void testCache() {
		final int capacity = 1000;

		final Cache<Integer, Integer> cache = Caching.of(NEXT).capacity(capacity).ttl(10).build();

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
	}

}
