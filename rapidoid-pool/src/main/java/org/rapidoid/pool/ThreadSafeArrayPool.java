package org.rapidoid.pool;

/*
 * #%L
 * rapidoid-pool
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

import java.util.concurrent.Callable;

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class ThreadSafeArrayPool<T> extends ArrayPool<T> {

	private final Thread ownerThread = Thread.currentThread();

	private final Pool<T> synchronizedPool;

	public ThreadSafeArrayPool(Callable<T> factory, int capacity) {
		super(factory, capacity);
		this.synchronizedPool = new SynchronizedArrayPool<T>(factory, capacity);
	}

	@Override
	public T get() {
		if (Thread.currentThread() == ownerThread) {
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
	public int instances() {
		if (Thread.currentThread() == ownerThread) {
			return super.instances();
		} else {
			return synchronizedPool.instances();
		}
	}

}
