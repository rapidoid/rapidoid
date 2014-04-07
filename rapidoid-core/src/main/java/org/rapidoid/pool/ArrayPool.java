package org.rapidoid.pool;

/*
 * #%L
 * rapidoid-core
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.util.U;

public class ArrayPool<T> implements Pool<T> {

	private final Callable<T> factory;

	private T[] free;

	private int freeN = 0;

	private int instancesN = 0;

	@SuppressWarnings("unchecked")
	public ArrayPool(Callable<T> factory, int capacity) {
		this.factory = factory;
		this.free = (T[]) new Object[capacity];
	}

	@Override
	public T get() {
		if (freeN == 0) {
			try {
				instancesN++;
				return factory.call();
			} catch (Exception e) {
				throw U.rte(e);
			}
		} else {
			T obj = free[--freeN];
			assert obj != null;
			return obj;
		}
	}

	@Override
	public void release(T obj) {
		assert obj != null;

		if (freeN >= free.length) {
			int expandFactor = free.length < 1000000 ? 10 : 2;
			int newSize = free.length * expandFactor;
			U.warn("Pool wasn't big enough, expanding...", "old size", free.length, "new size", newSize);
			free = U.expand(free, expandFactor);
		}

		free[freeN++] = obj;
	}

	@Override
	public int instances() {
		return instancesN;
	}

}
