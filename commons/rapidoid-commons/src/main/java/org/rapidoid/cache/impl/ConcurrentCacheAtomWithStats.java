package org.rapidoid.cache.impl;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Mapper;

import java.util.concurrent.atomic.AtomicLong;

@Authors("Nikolche Mihajlovski")
@Since("5.3.3")
public class ConcurrentCacheAtomWithStats<K, V> extends ConcurrentCacheAtom<K, V> {

	private final AtomicLong hits = new AtomicLong();

	private final AtomicLong misses = new AtomicLong();

	private final AtomicLong errors = new AtomicLong();

	private final CacheStats stats;

	public ConcurrentCacheAtomWithStats(K key, Mapper<K, V> loader, long ttlInMs, CacheStats stats) {
		super(key, loader, ttlInMs);
		this.stats = stats;
	}


	protected void updateStats(boolean missed, boolean hasError) {
		if (hasError) {
			errors.incrementAndGet();
			stats.errors.incrementAndGet();
		} else {
			if (missed) {
				misses.incrementAndGet();
				stats.misses.incrementAndGet();
			} else {
				hits.incrementAndGet();
				stats.hits.incrementAndGet();
			}
		}
	}

	public AtomicLong getHits() {
		return hits;
	}

	public AtomicLong getMisses() {
		return misses;
	}

	public AtomicLong getErrors() {
		return errors;
	}

	@Override
	public String toString() {
		return "ConcurrentCacheAtomWithStats{" +
			"hits=" + hits +
			", misses=" + misses +
			", errors=" + errors +
			", stats=" + stats +
			'}';
	}
}
