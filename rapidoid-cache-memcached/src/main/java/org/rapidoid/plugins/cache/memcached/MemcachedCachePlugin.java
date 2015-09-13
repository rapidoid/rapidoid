package org.rapidoid.plugins.cache.memcached;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.cache.AbstractCachePlugin;
import org.rapidoid.plugins.cache.ICache;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-cache-memcached
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void start() {
		try {
			List<String> servers = (List<String>) config().get("servers");

			if (servers == null) {
				Log.warn("Memcached servers 'memcached.servers' were not configured, using localhost:11211 as default!");
				servers = U.list("localhost:11211");
			}

			this.client = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(servers));
		} catch (IOException e) {
			throw U.rte("Cannot initialize the Memcached client!");
		}
	}

	@Override
	protected void stop() {
		if (this.client != null) {
			this.client.shutdown(5, TimeUnit.SECONDS);
			this.client = null;
		}
	}

	public MemcachedClient client() {
		return client;
	}

	@Override
	public <K, V> ICache<K, V> create(String cacheName, long timeToLiveMs, boolean resetTimeToLiveWhenAccessed) {
		return new MemcachedCache<K, V>(client, cacheName, timeToLiveMs, resetTimeToLiveWhenAccessed);
	}

}
