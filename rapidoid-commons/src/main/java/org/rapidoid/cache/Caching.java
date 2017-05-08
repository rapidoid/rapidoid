package org.rapidoid.cache;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.RapidoidThreadFactory;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.impl.CacheStats;
import org.rapidoid.cache.impl.ConcurrentCacheAtomWithStats;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;
import org.rapidoid.util.LazyInit;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class Caching extends RapidoidThing {

	private static final LazyInit<ScheduledThreadPoolExecutor> scheduler = new LazyInit<>(new Callable<ScheduledThreadPoolExecutor>() {
		@Override
		public ScheduledThreadPoolExecutor call() throws Exception {
			return new ScheduledThreadPoolExecutor(1, new RapidoidThreadFactory("cache-scheduler", true));
		}
	});

	public static <K, V> CacheDSL<K, V> of(Mapper<K, V> of) {
		return new CacheDSL<K, V>().loader(of);
	}

	@SuppressWarnings("unused")
	public static <K, V> CacheDSL<K, V> of(Class<K> keyClass, Class<V> valueClass) {
		return new CacheDSL<>();
	}

	public static <K, V> CacheAtom<V> atom(K key, Mapper<K, V> loader, long ttlInMs) {
		return new ConcurrentCacheAtomWithStats<>(key, loader, ttlInMs, new CacheStats());
	}

	public static ScheduledThreadPoolExecutor scheduler() {
		return scheduler.get();
	}

	public static void reset() {
		scheduler.reset();
	}

	public static void shutdown() {
		ScheduledThreadPoolExecutor scheduler = scheduler();
		scheduler.shutdownNow();
		while (!scheduler.isTerminated()) U.sleep(1);
	}
}
