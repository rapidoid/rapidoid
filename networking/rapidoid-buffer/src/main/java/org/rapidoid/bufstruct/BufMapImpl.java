package org.rapidoid.bufstruct;

/*
 * #%L
 * rapidoid-buffer
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
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.data.BufRange;
import org.rapidoid.util.AbstractMapImpl;
import org.rapidoid.util.MapEntry;
import org.rapidoid.util.SimpleBucket;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class BufMapImpl<T> extends AbstractMapImpl<byte[], T> implements BufMap<T> {

	public BufMapImpl() {
		this(100);
	}

	public BufMapImpl(int capacity) {
		super(capacity, -1);
	}

	private long hash(String key) {
		Bytes bytes = BytesUtil.from(key);
		return hash(bytes, BufRange.fromTo(0, bytes.limit()));
	}

	private long hash(Bytes bytes, BufRange key) {
		int prefix = BytesUtil.getIntPrefixOf(bytes, key.start, key.limit());
		return prefix * 17 + key.length * 19 + bytes.get(key.last());
	}

	public void put(String key, T value) {
		assert key.length() >= 1;

		MapEntry<byte[], T> route = new MapEntry<>(key.getBytes(), value);

		long hash = hash(key);

		entries.put(hash, route);
	}

	@Override
	public T get(Buf buf, BufRange key) {
		long hash = hash(buf.bytes(), key);

		SimpleBucket<MapEntry<byte[], T>> candidates = entries.bucket(hash);

		if (candidates != null) {
			for (int i = 0; i < candidates.size(); i++) {
				MapEntry<byte[], T> route = candidates.get(i);

				if (BytesUtil.matches(buf.bytes(), key, route.key, true)) {
					return route.value;
				}
			}
		}

		return defaultValue;
	}

	@Override
	public boolean remove(String key) {
		assert key.length() >= 1;

		long hash = hash(key);

		SimpleBucket<MapEntry<byte[], T>> bucket = this.entries.bucket(hash);

		if (bucket == null) {
			return false;
		}

		for (int i = 0; i < bucket.size(); i++) {
			MapEntry<byte[], T> route = bucket.get(i);

			if (new String(route.key).equals(key)) {
				bucket.delete(i);
				return true;
			}
		}

		return false;
	}

}
