package org.rapidoid.bufstruct;

/*
 * #%L
 * rapidoid-buffer
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoid.data.Range;
import org.rapidoid.util.SimpleHashTable;
import org.rapidoid.util.SimpleList;

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public class BufMapImpl<T> implements BufMap<T> {

	private class Entry {
		byte[] key;
		T value;

		@Override
		public String toString() {
			return new String(key) + ":" + value;
		}
	}

	private final SimpleHashTable<Entry> entries = new SimpleHashTable<Entry>(10000);

	private T defaultValue;

	private long hash(String key) {
		Bytes bytes = BytesUtil.from(key);
		return hash(bytes, Range.fromTo(0, bytes.limit()));
	}

	private long hash(Bytes bytes, Range key) {
		int prefix = BytesUtil.getIntPrefixOf(bytes, key.start, key.limit());
		return prefix * 17 + key.length * 19 + bytes.get(key.last());
	}

	public void put(String key, T value) {
		assert key.length() >= 1;

		Entry route = new Entry();
		route.key = key.getBytes();
		route.value = value;

		long hash = hash(key);

		entries.put(hash, route);
	}

	@Override
	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public T get(Buf buf, Range key) {
		long hash = hash(buf.bytes(), key);

		SimpleList<Entry> candidates = entries.get(hash);

		if (candidates != null) {
			for (int i = 0; i < candidates.size(); i++) {
				Entry route = candidates.get(i);

				if (BytesUtil.matches(buf.bytes(), key, route.key, true)) {
					return route.value;
				}
			}
		}

		return defaultValue;
	}

}
