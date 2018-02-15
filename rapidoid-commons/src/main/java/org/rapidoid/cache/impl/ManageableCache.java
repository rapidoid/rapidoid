/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid.cache.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.Cache;
import org.rapidoid.commons.Nums;
import org.rapidoid.group.Action;
import org.rapidoid.group.AutoManageable;
import org.rapidoid.group.ManageableBean;
import org.rapidoid.u.U;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
@ManageableBean(kind = "cache")
public class ManageableCache extends AutoManageable<ManageableCache> {

	private final Cache<?, ?> cache;

	public ManageableCache(Cache<?, ?> cache) {
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
		return cache.ttl();
	}

	public long loads() {
		return cache.stats().loads();
	}

	public long misses() {
		return cache.stats().misses();
	}

	public long hits() {
		return cache.stats().hits();
	}

	public long errors() {
		return cache.stats().errors();
	}

	public long bypassed() {
		return cache.stats().bypassed();
	}

	public String hitRate() {
		return Nums.percent(cache.stats().hitRate()) + " %";
	}

	public long size() {
		return cache.size();
	}

	public int capacity() {
		return cache.capacity();
	}
}
