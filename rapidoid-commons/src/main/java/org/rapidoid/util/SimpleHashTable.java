package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Rnd;
import org.rapidoid.u.U;

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

	private static final int DEFAULT_BUCKET_SIZE = 8;

	public final SimpleBucket<T>[] buckets;

	public final int factor;

	private final int hashMask;

	private final int xor = Rnd.rnd();

	public SimpleHashTable(int capacity, int bucketSize) {
		this(capacity, bucketSize, true);
	}

	@SuppressWarnings("unchecked")
	public SimpleHashTable(int capacity, int bucketSize, boolean unbounded) {
		U.must(capacity >= 2, "The capacity is too small!");

		if (bucketSize <= 0) bucketSize = DEFAULT_BUCKET_SIZE;

		int factor = Msc.log2(Math.max(capacity / bucketSize, 1));

		int width = (int) Math.pow(2, factor);
		int realCapacity = width * bucketSize;
		U.must(capacity <= realCapacity);

		this.buckets = new SimpleList[width];
		this.factor = factor;
		this.hashMask = Msc.bitMask(factor);

		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = createBucket(bucketSize, unbounded);
		}
	}

	protected SimpleBucket<T> createBucket(int bucketSize, boolean unbounded) {
		U.must(unbounded, "Only unbounded buckets are supported!");
		return new SimpleList<>(bucketSize);
	}

	public void put(long key, T value) {
		bucket(key).add(value);
	}

	public SimpleBucket<T> bucket(long key) {
		int index = index(key);
		return getBucketAt(index);
	}

	public SimpleBucket<T> getBucketAt(int index) {
		SimpleBucket<T> list;

		// after construction, other threads might need some time to see the new references
		while ((list = buckets[index]) == null) {
		}

		return list;
	}

	public int index(long key) {
		return (int) ((key ^ xor) & hashMask);
	}

	public void clear() {
		for (int i = 0; i < buckets.length; i++) {
			clearBucket(i);
		}
	}

	protected void clearBucket(int index) {
		getBucketAt(index).clear();
	}

	public int bucketCount() {
		return buckets.length;
	}
}
