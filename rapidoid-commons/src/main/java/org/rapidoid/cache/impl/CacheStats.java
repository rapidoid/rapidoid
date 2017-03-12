package org.rapidoid.cache.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.concurrent.atomic.AtomicLong;

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
public class CacheStats extends RapidoidThing {

	public final AtomicLong hits = new AtomicLong();

	public final AtomicLong misses = new AtomicLong();

	public final AtomicLong errors = new AtomicLong();

	public final AtomicLong bypassed = new AtomicLong();

	public final AtomicLong crawls = new AtomicLong();

	public final AtomicLong l1Hits = new AtomicLong();

	public final AtomicLong l1Misses = new AtomicLong();

	public void reset() {
		// basic
		hits.set(0);
		misses.set(0);
		errors.set(0);
		bypassed.set(0);

		// additional
		crawls.set(0);
		l1Hits.set(0);
		l1Misses.set(0);
	}

	public long total() {
		// without the additional stats
		return hits.get() + misses.get() + errors.get() + bypassed.get();
	}

	@Override
	public String toString() {
		return "CacheStats{" +
			"hits=" + hits +
			", misses=" + misses +
			", errors=" + errors +
			", bypassed=" + bypassed +
			", crawls=" + crawls +
			", l1Hits=" + l1Hits +
			", l1Misses=" + l1Misses +
			'}';
	}
}
