package org.rapidoid.cache;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ConcurrentCached<V> extends RapidoidThing implements CachedCalc<V>, Callable<V> {

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private final Callable<V> loader;

	private final long ttlInMs;

	private final AtomicLong hits = new AtomicLong();

	private final AtomicLong misses = new AtomicLong();

	private final AtomicLong errors = new AtomicLong();

	private volatile V value;

	private volatile boolean cacheValid = false;

	private volatile long expiresAt;

	public ConcurrentCached(Callable<V> loader, long ttlInMs) {
		this.loader = loader;
		this.ttlInMs = ttlInMs;
	}

	/**
	 * Gets the cached value, or recalculates/reloads it if expired.
	 * <p>
	 * The synchronization is based on the {@link ReentrantReadWriteLock} documentation.
	 */
	@Override
	public V get() {
		V result;
		Throwable error = null;
		boolean missed = false;

		if (U.time() > expiresAt) {
			cacheValid = false;
		}

		lock.readLock().lock();

		if (!cacheValid) {

			lock.readLock().unlock();
			lock.writeLock().lock();

			// another thread might have acquired write lock and changed state already
			if (!cacheValid) {
				try {
					value = loader.call();
				} catch (Throwable e) {
					error = e;
				}
				expiresAt = ttlInMs > 0 ? U.time() + ttlInMs : Long.MAX_VALUE;
				cacheValid = true;
				missed = true;
			}

			// downgrade by acquiring read lock before releasing write lock
			lock.readLock().lock();
			lock.writeLock().unlock();
		}

		result = value;
		lock.readLock().unlock();

		if (missed) {
			misses.incrementAndGet();
		} else {
			hits.incrementAndGet();
		}

		if (error != null) {
			errors.incrementAndGet();
			throw U.rte("Couldn't recalculate the cache value!", error);
		}

		return result;
	}

	/**
	 * Invalidates the cache.
	 */
	public void invalidate() {
		cacheValid = false;
	}

	@Override
	public V call() throws Exception {
		return get();
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
		return "ConcurrentCached [ttlInMs=" + ttlInMs + ", hits=" + hits + ", misses=" + misses + ", errors=" + errors
			+ ", value=" + value + ", cacheValid=" + cacheValid + ", expiresAt=" + expiresAt + "]";
	}

}
