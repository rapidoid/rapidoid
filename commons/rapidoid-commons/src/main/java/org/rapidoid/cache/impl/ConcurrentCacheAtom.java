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
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;
import org.rapidoid.util.Resetable;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ConcurrentCacheAtom<K, V> extends RapidoidThing implements CacheAtom<V>, Callable<V> {

	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	protected final K key;

	protected final Mapper<K, V> loader;

	protected final long ttlInMs;

	protected volatile CachedValue<V> cachedValue;

	long approxAccessCounter;

	public ConcurrentCacheAtom(K key, Mapper<K, V> loader, long ttlInMs) {
		this.key = key;
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

		// race conditions are allowed
		this.approxAccessCounter++;

		long now;

		CachedValue<V> cached = cachedValue; // read the cached value

		if (cached != null) {

			long expiresAt = cached.expiresAt;
			if (expiresAt == Long.MAX_VALUE) {
				updateStats(false, false);
				return cached.value;
			}

			now = U.time();
			if (now <= expiresAt) {
				updateStats(false, false);
				return cached.value;
			}

		} else {
			now = U.time();
		}

		// ----------------- START LOCKS -----------------

		readLock();

		cached = cachedValue; // read the cached value again, inside the read lock

		if (cached == null || now > cached.expiresAt) {

			readUnlock();
			writeLock();

			// double-check: another thread might have acquired write lock and changed state already
			if (cached == null || now > cached.expiresAt) {

				V newValue;

				if (loadIfExpired) {
					try {
						newValue = loader != null ? loader.map(key) : null;

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
			readLock();
			writeUnlock();
		}

		cached = cachedValue;
		result = cached != null ? cached.value : null; // read the cached value

		readUnlock();

		releaseOldValue(oldValue); // release the old value - outside of lock

		if (updateStats) {
			updateStats(missed, error != null);
		}

		if (error != null) {
			throw U.rte("Couldn't recalculate the cache value!", error);
		}

		return result;
	}

	private void readLock() {
		try {
			if (!lock.readLock().tryLock(10, TimeUnit.SECONDS)) {
				throw new RuntimeException("Couldn't acquire READ lock!");
			}
		} catch (InterruptedException e) {
			throw new CancellationException();
		}
	}

	private void readUnlock() {
		lock.readLock().unlock();
	}

	private void writeLock() {
		try {
			if (!lock.writeLock().tryLock(10, TimeUnit.SECONDS)) {
				throw new RuntimeException("Couldn't acquire WRITE lock!");
			}
		} catch (InterruptedException e) {
			throw new CancellationException();
		}
	}

	private void writeUnlock() {
		lock.writeLock().unlock();
	}

	protected void updateStats(boolean missed, boolean hasError) {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(V value) {
		writeLock();
		V oldValue = setValueInsideWriteLock(value);
		writeUnlock();

		releaseOldValue(oldValue); // release the old value - outside of lock
	}

	/**
	 * Sets new cached value, executes inside already acquired write lock.
	 */
	private V setValueInsideWriteLock(V newValue) {
		CachedValue<V> cached = cachedValue; // read the cached value
		V oldValue = cached != null ? cached.value : null;

		if (newValue != null) {
			long expiresAt = ttlInMs > 0 ? U.time() + ttlInMs : Long.MAX_VALUE;
			cachedValue = new CachedValue<>(newValue, expiresAt);

		} else {
			cachedValue = null;
		}

		return oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invalidate() {
		writeLock();
		V oldValue = setValueInsideWriteLock(null);
		writeUnlock();

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

	@Override
	public String toString() {
		return "ConcurrentCacheAtom{" +
			"lock=" + lock +
			", loader=" + loader +
			", ttlInMs=" + ttlInMs +
			", cachedValue=" + cachedValue +
			'}';
	}
}
