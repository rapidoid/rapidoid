package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;

import java.util.Arrays;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class SimpleList<T> extends RapidoidThing implements SimpleBucket<T> {

	public T[] array;

	protected int size = 0;

	protected int position = 0;

	protected final int growFactor;

	public SimpleList(int capacity) {
		this(capacity, 2);
	}

	@SuppressWarnings("unchecked")
	public SimpleList(int capacity, int growFactor) {
		this.growFactor = growFactor;
		this.array = (T[]) new Object[capacity];
	}

	@Override
	public void clear() {
		size = 0;
		position = 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public T get(int index) {
		return array[index];
	}

	@Override
	public T add(T obj) {
		if (size == array.length) {
			array = Arrays.copyOf(array, array.length * growFactor);
		}
		array[size++] = obj;
		return null;
	}

	@Override
	public void delete(int index) {
		Err.bounds(index, 0, size - 1);

		System.arraycopy(array, index + 1, array, index, size - index - 1);

		size--;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < size; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(get(i));
		}

		return "[" + sb.toString() + "]";
	}

}
