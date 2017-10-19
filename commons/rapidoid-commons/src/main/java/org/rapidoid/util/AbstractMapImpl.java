package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
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
@Since("5.0.2")
public abstract class AbstractMapImpl<K, V> extends RapidoidThing implements SimpleMap<K, V> {

	protected final SimpleHashTable<MapEntry<K, V>> entries;

	protected V defaultValue;

	public AbstractMapImpl(SimpleHashTable<MapEntry<K, V>> entries) {
		this.entries = entries;
	}

	public AbstractMapImpl(int capacity, int bucketSize) {
		this(new SimpleHashTable<MapEntry<K, V>>(capacity, bucketSize));
	}

	@Override
	public void clear() {
		entries.clear();
	}

	@Override
	public void setDefaultValue(V defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public V getDefaultValue() {
		return defaultValue;
	}

	protected MapEntry<K, V> findEntry(K key) {
		int hash = key.hashCode();
		return findEntry(key, entries.bucket(hash));
	}

	protected MapEntry<K, V> findEntry(K key, SimpleBucket<MapEntry<K, V>> bucket) {
		for (int i = 0; i < bucket.size(); i++) {
			MapEntry<K, V> entry = bucket.get(i);

			if (entry != null && U.eq(entry.key, key)) {
				return entry;
			}
		}

		return null;
	}

}
