package org.rapidoid.cache;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
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
public class SimpleCacheTest extends TestCommons {

	private static Mapper<String, Integer> LENGTH = new Mapper<String, Integer>() {
		@Override
		public Integer map(String src) throws Exception {
			return src.length();
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
		final ConcurrentCached<String> cached = new ConcurrentCached<String>(ABC, 10);

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

		Cached<String, Integer> cache = Cache.of(LENGTH).capacity(10).ttl(1000).build();

		eq(cache.get("x").intValue(), 1);
		eq(cache.get("x").intValue(), 1);
		eq(cache.get("aaa").intValue(), 3);
		eq(cache.get("bb").intValue(), 2);
		eq(cache.get("ccc").intValue(), 3);
	}

}
