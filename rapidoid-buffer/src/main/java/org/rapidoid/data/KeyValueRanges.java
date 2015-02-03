package org.rapidoid.data;

/*
 * #%L
 * rapidoid-buffer
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class KeyValueRanges {

	public final Range[] keys;

	public final Range[] values;

	public int count;

	public KeyValueRanges(int capacity) {
		this.keys = new Range[capacity];
		this.values = new Range[capacity];

		for (int i = 0; i < capacity; i++) {
			keys[i] = new Range();
			values[i] = new Range();
			keys[i].reset();
			values[i].reset();
		}
	}

	public void reset() {
		for (int i = 0; i < count; i++) {
			keys[i].reset();
			values[i].reset();
		}
		count = 0;
	}

	public Range get(Buf buf, byte[] key, boolean caseSensitive) {
		for (int i = 0; i < count; i++) {
			if (BytesUtil.matches(buf.bytes(), keys[i], key, caseSensitive)) {
				return values[i];
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return super.toString() + "[" + count + "]";
	}

	public int max() {
		return keys.length;
	}

	public String str(Buf src) {
		StringBuilder sb = new StringBuilder();

		sb.append("[");
		for (int i = 0; i < count; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append("<");
			sb.append(keys[i].str(src.bytes()));
			sb.append(":=");
			sb.append(values[i].str(src.bytes()));
			sb.append(">");
		}
		sb.append("]");

		return sb.toString();
	}

	public int add() {
		if (count >= max()) {
			throw U.rte("too many key-values!");
		}

		return count++;
	}

	public Map<String, String> toMap(String data) {
		Map<String, String> map = U.map();

		for (int i = 0; i < count; i++) {
			map.put(keys[i].get(data), values[i].get(data));
		}

		return map;
	}

	public Map<String, String> toMap(Buf src, boolean urlDecodeKeys, boolean urlDecodeVals) {
		Map<String, String> map = U.map();

		for (int i = 0; i < count; i++) {
			String key = keys[i].str(src.bytes());
			String val = values[i].str(src.bytes());

			if (urlDecodeKeys) {
				key = UTILS.urlDecode(key);
			}
			if (urlDecodeVals) {
				val = UTILS.urlDecode(val);
			}

			map.put(key, val);
		}

		return map;
	}

	public Map<String, byte[]> toBinaryMap(Buf src, boolean urlDecodeKeys) {
		Map<String, byte[]> map = U.map();

		for (int i = 0; i < count; i++) {
			String key = keys[i].str(src.bytes());
			byte[] val = values[i].bytes(src);

			if (urlDecodeKeys) {
				key = UTILS.urlDecode(key);
			}

			map.put(key, val);
		}

		return map;
	}

}
