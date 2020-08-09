/*-
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2020 Nikolche Mihajlovski and contributors
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

package org.rapidoid.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.rapidoid.RapidoidThing;
import org.rapidoid.cache.impl.CacheStats;
import org.rapidoid.cache.impl.ManageableCache;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CaffeineCache<K, V> extends RapidoidThing implements Cache<K, V> {

    private final String name;
    private final int capacity;
    private final long ttl;
    private final boolean loading;

    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;
    private final LoadingCache<K, V> loadingCache;

    private final AtomicLong bypassed = new AtomicLong();

    public CaffeineCache(String name, int capacity, Mapper<K, V> loader, long ttl, boolean statistics, boolean manageable) {
        this.name = name;
        this.capacity = capacity;
        this.ttl = ttl;
        this.loading = loader != null;

        Caffeine<Object, Object> builder = Caffeine.newBuilder();

        if (capacity > 0) {
            builder.maximumSize(capacity);
        }

        if (ttl > 0) {
            builder.expireAfterWrite(ttl, TimeUnit.MILLISECONDS);
        }

        if (statistics) {
            builder.recordStats();
        }

        if (manageable) {
            new ManageableCache(this);
        }

        if (loading) {
            this.loadingCache = builder.build(loader::map);
            this.cache = loadingCache;

        } else {
            this.loadingCache = null;
            this.cache = builder.build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(K key) {
        U.must(loading, "No loader was specified for this cache. Please specify one or use getIfExists()!");
        return loadingCache.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getIfExists(K key) {
        return cache.getIfPresent(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidate(K key) {
        cache.invalidate(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(K key, V value) {
        cache.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        cache.invalidateAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return cache.estimatedSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bypass() {
        bypassed.incrementAndGet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int capacity() {
        return capacity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long ttl() {
        return ttl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheStats stats() {
        return new CacheStats(cache.stats(), bypassed);
    }
}
