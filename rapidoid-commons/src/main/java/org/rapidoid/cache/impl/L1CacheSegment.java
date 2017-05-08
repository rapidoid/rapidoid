package org.rapidoid.cache.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Rnd;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

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
public class L1CacheSegment<K, V> extends RapidoidThing {

	private final int bitMask;
	private final int xor = Rnd.rnd();

	private final Object[] keys;
	private final ConcurrentCacheAtom<K, V>[] atoms;

	int indexCounter;

	private volatile ConcurrentCacheAtom<K, V> latest;

	@SuppressWarnings("unchecked")
	public L1CacheSegment(int size) {
		this.bitMask = Msc.bitMask(Msc.log2(size));
		this.keys = new Object[size];
		this.atoms = new ConcurrentCacheAtom[size];
		this.latest = null;
	}

	public ConcurrentCacheAtom<K, V> find(K key) {

		ConcurrentCacheAtom<K, V> latestAtom = latest;
		if (latestAtom != null && U.eq(latestAtom.key, key)) {
			return latestAtom;
		}

		for (int i = 0; i < keys.length; i++) {
			if (U.eq(keys[i], key)) {
				ConcurrentCacheAtom<K, V> atom = atoms[i];
				if (atom != null && U.eq(atom.key, key)) {
					return atom;
				}
			}
		}

		return null;
	}

	public void add(int hash, ConcurrentCacheAtom<K, V> atom) {
		int index = (hash ^ xor ^ indexCounter++) & bitMask;

		ConcurrentCacheAtom<K, V> oldAtom = this.atoms[index];

		if (oldAtom == null || atom.approxAccessCounter > oldAtom.approxAccessCounter) {
			this.keys[index] = atom.key;
			this.atoms[index] = atom;
			this.latest = atom;
		}
	}

	public void invalidate(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (U.eq(keys[i], key)) {
				ConcurrentCacheAtom<K, V> atom = atoms[i];
				if (U.eq(atom.key, key)) {
					atom.invalidate();
					this.latest = atom;
				}
			}
		}
	}

	public void set(K key, V value) {
		for (int i = 0; i < keys.length; i++) {
			if (U.eq(keys[i], key)) {
				ConcurrentCacheAtom<K, V> atom = atoms[i];
				if (U.eq(atom.key, key)) {
					atom.set(value);
					this.latest = atom;
				}
			}
		}
	}

	public void clear() {
		for (int i = 0; i < keys.length; i++) {
			keys[i] = null;
			atoms[i] = null;
		}
		this.latest = null;
	}
}
