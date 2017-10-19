package org.rapidoid.cache.impl;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.MapEntry;
import org.rapidoid.util.SimpleBucket;
import org.rapidoid.util.SimpleHashTable;
import org.rapidoid.util.SimpleList;

@Authors("Nikolche Mihajlovski")
@Since("5.3.3")
public class SimpleCacheTable<K, V> extends SimpleHashTable<MapEntry<K, ConcurrentCacheAtom<K, V>>> {

	SimpleCacheTable(int capacity, int desiredBucketSize, boolean unbounded) {
		super(capacity, desiredBucketSize, unbounded);
	}

	@Override
	protected SimpleBucket<MapEntry<K, ConcurrentCacheAtom<K, V>>> createBucket(int bucketSize, boolean unbounded) {
		if (unbounded) {
			return new SimpleList<>(bucketSize);
		} else {
			return new CacheBucket<>(bucketSize);
		}
	}

}
