package org.rapidoid.pool;

import org.rapidoid.insight.AbstractInsightful;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.concurrent.Callable;

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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class ArrayPool<T> extends AbstractInsightful implements Pool<T> {

	private final Callable<T> factory;

	private T[] free;

	private int freeN = 0;

	private int instancesN = 0;

	@SuppressWarnings("unchecked")
	public ArrayPool(String name, Callable<T> factory, int capacity) {
		super("pool", name);
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
			Log.info("The pool wasn't big enough, expanding...", "name", getName(), "old size", free.length,
				"new size", newSize);
			free = Msc.expand(free, expandFactor);
		}

		free[freeN++] = obj;
	}

	@Override
	public int objectsCreated() {
		return instancesN;
	}

	@Override
	public int size() {
		return freeN;
	}

	@Override
	public String toString() {
		return getName() + "#" + size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		freeN = 0;
		instancesN = 0;
		free = (T[]) new Object[100];
	}

}
