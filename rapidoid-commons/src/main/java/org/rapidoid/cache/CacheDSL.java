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
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.impl.CacheFactory;
import org.rapidoid.lambda.Mapper;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class CacheDSL<K, V> extends RapidoidThing {

	private volatile String name;

	private volatile Mapper<K, V> loader;

	private volatile int capacity;

	private volatile long ttl = 0;

	private volatile ScheduledThreadPoolExecutor crawler;

	private volatile boolean statistics;

	private volatile boolean manageable;

	/**
	 * Please use loader(...) instead.
	 */
	@Deprecated
	public CacheDSL<K, V> of(Mapper<K, V> of) {
		this.loader = of;
		return this;
	}

	/**
	 * Please use loader() instead.
	 */
	@Deprecated
	public Mapper<K, V> of() {
		return this.loader;
	}

	public Mapper<K, V> loader() {
		return loader;
	}

	public CacheDSL<K, V> loader(Mapper<K, V> loader) {
		this.loader = loader;
		return this;
	}

	public String name() {
		return name;
	}

	public CacheDSL name(String name) {
		this.name = name;
		return this;
	}

	public CacheDSL<K, V> capacity(int capacity) {
		this.capacity = capacity;
		return this;
	}

	public int capacity() {
		return this.capacity;
	}

	public CacheDSL<K, V> ttl(long ttl) {
		this.ttl = ttl;
		return this;
	}

	public long ttl() {
		return this.ttl;
	}

	public ScheduledThreadPoolExecutor crawler() {
		return crawler;
	}

	public CacheDSL crawler(ScheduledThreadPoolExecutor crawler) {
		this.crawler = crawler;
		return this;
	}

	public boolean statistics() {
		return statistics;
	}

	public CacheDSL statistics(boolean statistics) {
		this.statistics = statistics;
		return this;
	}

	public boolean manageable() {
		return manageable;
	}

	public CacheDSL manageable(boolean manageable) {
		this.manageable = manageable;
		return this;
	}

	public Cache<K, V> build() {
		return CacheFactory.create(this);
	}

}
