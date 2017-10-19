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

	private final int bucketSize;

	public final int factor;

	private final int hashMask;

	private final int xor = Rnd.rnd();

	public SimpleHashTable(int capacity, int bucketSize) {
		this(capacity, bucketSize, true);
	}

	@SuppressWarnings("unchecked")
	public SimpleHashTable(int capacity, int bucketSize, boolean unbounded) {

		U.must(capacity > 0, "The capacity must be a positive number!");

		if (bucketSize <= 0) bucketSize = DEFAULT_BUCKET_SIZE;

		int factor = calcSizeFactor(capacity, bucketSize);
		int width = calcWidth(factor);

		this.bucketSize = bucketSize;
		this.buckets = new SimpleList[width];
		this.factor = factor;
		this.hashMask = Msc.bitMask(factor);

		U.must(capacity <= capacity(), "capacity=%s, realCapacity=%s, bucketSize=%s", capacity, capacity(), bucketSize);

		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = createBucket(bucketSize, unbounded);
		}
	}

	private static int calcSizeFactor(int capacity, int bucketSize) {
		int requiredWidth = (int) Math.ceil(capacity * 1.0f / bucketSize);
		return Msc.log2(requiredWidth);
	}

	private static int calcWidth(int factor) {
		return (int) Math.pow(2, factor);
	}

	public int bucketSize() {
		return bucketSize;
	}

	public int capacity() {
		return buckets.length * bucketSize;
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
