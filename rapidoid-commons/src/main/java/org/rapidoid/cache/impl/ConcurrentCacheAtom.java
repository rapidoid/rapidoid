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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.CacheAtom;
import org.rapidoid.u.U;
import org.rapidoid.util.Resetable;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ConcurrentCacheAtom<V> extends RapidoidThing implements CacheAtom<V>, Callable<V> {

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	private final Callable<V> loader;

	private final long ttlInMs;

	private final AtomicLong hits = new AtomicLong();

	private final AtomicLong misses = new AtomicLong();

	private final AtomicLong errors = new AtomicLong();

	private volatile V value;

	private volatile boolean cacheValid = false;

	private volatile long expiresAt;

	public ConcurrentCacheAtom(Callable<V> loader, long ttlInMs) {
		this.loader = loader;
		this.ttlInMs = ttlInMs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V get() {
		return retrieveCachedValue(true, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V getIfExists() {
		return retrieveCachedValue(false, true);
	}

	/**
	 * Retrieves the cached value, and maybe recalculates/reloads it if expired.
	 * <p>
	 * The synchronization is based on the {@link ReentrantReadWriteLock} documentation.
	 */
	private V retrieveCachedValue(boolean loadIfExpired, boolean updateStats) {
		V result;
		V oldValue = null;
		Throwable error = null;
		boolean missed = false;
		long now = U.time();

		lock.readLock().lock(); // ----------------- START LOCKS -----------------

		if (!cacheValid || now > expiresAt) {

			lock.readLock().unlock();
			lock.writeLock().lock();

			// double-check: another thread might have acquired write lock and changed state already
			if (!cacheValid || now > expiresAt) {

				V newValue;

				if (loadIfExpired) {
					try {
						newValue = loader != null ? loader.call() : null;

					} catch (Throwable e) {
						error = e;
						newValue = null;
					}

				} else {
					newValue = null;
				}

				oldValue = setValueInsideWriteLock(newValue);

				missed = true;
			}

			// downgrade by acquiring read lock before releasing write lock
			lock.readLock().lock();
			lock.writeLock().unlock();
		}

		result = value; // read the cached value

		lock.readLock().unlock(); // ----------------- END LOCKS -----------------

		releaseOldValue(oldValue); // release the old value - outside of lock

		if (updateStats) {
			updateStats(missed, error != null);
		}

		if (error != null) {
			throw U.rte("Couldn't recalculate the cache value!", error);
		}

		return result;
	}

	private void updateStats(boolean missed, boolean hasError) {
		if (hasError) {
			errors.incrementAndGet();
		} else {
			if (missed) {
				misses.incrementAndGet();
			} else {
				hits.incrementAndGet();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(V value) {
		lock.writeLock().lock();
		V oldValue = setValueInsideWriteLock(value);
		lock.writeLock().unlock();

		releaseOldValue(oldValue); // release the old value - outside of lock
	}

	/**
	 * Sets new cached value, executes inside already acquired write lock.
	 */
	private V setValueInsideWriteLock(V newValue) {
		V oldValue = this.value;

		boolean isRealValue = newValue != null;

		this.value = newValue;
		this.cacheValid = isRealValue;

		if (isRealValue) {
			this.expiresAt = ttlInMs > 0 ? U.time() + ttlInMs : Long.MAX_VALUE;
		} else {
			this.expiresAt = 0;
		}

		return oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invalidate() {
		lock.writeLock().lock();
		V oldValue = setValueInsideWriteLock(null);
		lock.writeLock().unlock();

		releaseOldValue(oldValue); // release the old value - outside of lock
	}

	/**
	 * Clean-up of an old, previously cached value.
	 *
	 * @param oldValue can be null
	 */
	private void releaseOldValue(V oldValue) {
		if (oldValue instanceof Resetable) {
			((Resetable) oldValue).reset();
		}
	}

	void checkTTL() {
		retrieveCachedValue(false, false);
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
