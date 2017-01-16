package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

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
public class SimpleHashTable<T> extends RapidoidThing {

	protected static final int DEFAULT_BUCKET_SIZE = 5;

	public final SimpleList<T>[] buckets;

	public SimpleHashTable(int width) {
		this(width, DEFAULT_BUCKET_SIZE);
	}

	@SuppressWarnings("unchecked")
	public SimpleHashTable(int width, int initialBucketSize) {
		this.buckets = new SimpleList[width];
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = newList(initialBucketSize);
		}
	}

	protected SimpleList<T> newList(int initialBucketSize) {
		return new SimpleList<T>(initialBucketSize);
	}

	public void put(long key, T value) {
		bucket(key).add(value);
	}

	public SimpleList<T> bucket(long key) {
		int index = index(key);
		return getBucket(index);
	}

	protected SimpleList<T> getBucket(int index) {
		SimpleList<T> list;

		// after construction, other threads might need some time to see the new references
		while ((list = buckets[index]) == null) {
		}

		return list;
	}

	public int index(long key) {
		return (int) (Math.abs(key) % buckets.length);
	}

	public void clear() {
		for (int i = 0; i < buckets.length; i++) {
			clearBucket(i);
		}
	}

	protected void clearBucket(int index) {
		getBucket(index).clear();
	}

}
