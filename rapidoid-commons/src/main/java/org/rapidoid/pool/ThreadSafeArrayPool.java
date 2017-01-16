package org.rapidoid.pool;

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

import java.util.concurrent.Callable;

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class ThreadSafeArrayPool<T> extends ArrayPool<T> {

	private final Thread ownerThread = Thread.currentThread();

	private final Pool<T> synchronizedPool;

	public ThreadSafeArrayPool(String name, Callable<T> factory, int capacity) {
		super(name, factory, capacity);
		this.synchronizedPool = new SynchronizedArrayPool<T>(name, factory, capacity);
	}

	@Override
	public T get() {
		if (Thread.currentThread() == ownerThread && super.size() > 0) {
			return super.get();
		} else {
			return synchronizedPool.get();
		}
	}

	@Override
	public void release(T obj) {
		if (Thread.currentThread() == ownerThread) {
			super.release(obj);
		} else {
			synchronizedPool.release(obj);
		}
	}

	@Override
	public int objectsCreated() {
		return super.objectsCreated() + synchronizedPool.objectsCreated();
	}

	@Override
	public int size() {
		return super.size() + synchronizedPool.size();
	}

	@Override
	public void clear() {
		super.clear();
		synchronizedPool.clear();
	}

}
