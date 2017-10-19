package org.rapidoid.cache.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.group.Action;
import org.rapidoid.group.AutoManageable;
import org.rapidoid.group.ManageableBean;
import org.rapidoid.u.U;

import java.util.List;

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
@ManageableBean(kind = "cache")
public class ManageableCache extends AutoManageable<ManageableCache> {

	private final ConcurrentCache<?, ?> cache;

	public ManageableCache(ConcurrentCache<?, ?> cache) {
		super(cache.name());
		this.cache = cache;
	}

	@Override
	public List<String> getManageableProperties() {
		return U.list("id", "size", "capacity", "hitRate", "hits", "misses", "bypassed", "errors", "ttl");
	}

	@Action(name = "!purge")
	public void purge() {
		cache.clear();
	}

	public long ttl() {
		return cache.ttlInMs();
	}

	public long misses() {
		return cache.stats().misses.get();
	}

	public long hits() {
		return cache.stats().hits.get();
	}

	public long errors() {
		return cache.stats().errors.get();
	}

	public long bypassed() {
		return cache.stats().bypassed.get();
	}

	public String hitRate() {
		long total = cache.stats().total();
		return total != 0 ? Math.round(hits() * 10000.0 / total) / 100.0 + " %" : "";
	}

	public int size() {
		return cache.size();
	}

	public int capacity() {
		return cache.capacity();
	}
}
