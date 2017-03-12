package org.rapidoid.cache.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.SimpleList;

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
@Since("5.3.3")
public class CacheBucket<T> extends SimpleList<T> {

	public CacheBucket(int capacity) {
		super(capacity);
	}

	public CacheBucket(int capacity, int growFactor) {
		super(capacity, growFactor);
	}

	@Override
	public T add(T obj) {
		if (size < array.length) {
			array[size++] = obj;
			return null;

		} else {
			T oldValue = array[position];
			array[position] = obj;
			position++;

			if (position >= array.length) {
				position = 0;
			}

			return oldValue;
		}
	}

}
