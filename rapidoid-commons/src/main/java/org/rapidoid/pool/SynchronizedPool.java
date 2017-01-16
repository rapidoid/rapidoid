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

import org.rapidoid.RapidoidThing;

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class SynchronizedPool<T> extends RapidoidThing implements Pool<T> {

	private final Pool<T> pool;

	public SynchronizedPool(Pool<T> pool) {
		this.pool = pool;
	}

	public synchronized T get() {
		return pool.get();
	}

	public synchronized void release(T obj) {
		pool.release(obj);
	}

	public synchronized int objectsCreated() {
		return pool.objectsCreated();
	}

	@Override
	public synchronized int size() {
		return pool.size();
	}

	@Override
	public void clear() {
		pool.clear();
	}

}
