package org.rapidoid.plugins.cache.memcached;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.cache.AbstractCachePlugin;
import org.rapidoid.plugins.cache.ICache;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-cache-memcached
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
@Since("4.2.0")
public class MemcachedCachePlugin extends AbstractCachePlugin {

	private volatile MemcachedClient client;

	public MemcachedCachePlugin() {
		super("memcached");
	}

	@Override
	protected void doRestart() throws Exception {
		if (this.client != null) {
			this.client.shutdown(5, TimeUnit.SECONDS);
			this.client = null;
		}

		List<String> servers = option("servers", U.list("localhost:11211"));

		this.client = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(servers));
	}

	public synchronized MemcachedClient client() {
		return client;
	}

	@Override
	public <K, V> ICache<K, V> create(String cacheName, long timeToLiveMs, boolean resetTimeToLiveWhenAccessed) {
		return new MemcachedCache<K, V>(client, cacheName, timeToLiveMs, resetTimeToLiveWhenAccessed);
	}

}
