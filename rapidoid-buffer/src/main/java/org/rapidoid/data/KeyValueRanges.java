package org.rapidoid.data;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.commons.Str;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class KeyValueRanges extends RapidoidThing {

	public final BufRange[] keys;

	public final BufRange[] values;

	public int count;

	public KeyValueRanges(int capacity) {
		this.keys = new BufRange[capacity];
		this.values = new BufRange[capacity];

		for (int i = 0; i < capacity; i++) {
			keys[i] = new BufRange();
			values[i] = new BufRange();
			keys[i].reset();
			values[i].reset();
		}
	}

	public KeyValueRanges reset() {
		for (int i = 0; i < count; i++) {
			keys[i].reset();
			values[i].reset();
		}
		count = 0;

		return this;
	}

	public BufRange get(Buf buf, byte[] key, boolean caseSensitive) {
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

	public void toMap(Buf src, boolean urlDecodeKeys, boolean urlDecodeVals, boolean lowerCaseKeys, Map<String, ? super String> dest) {
		for (int i = 0; i < count; i++) {
			String key = keys[i].str(src.bytes());
			String val = values[i].str(src.bytes());

			if (urlDecodeKeys) {
				key = Msc.urlDecodeOrKeepOriginal(key);
			}

			if (urlDecodeVals) {
				val = Msc.urlDecodeOrKeepOriginal(val);
			}

			if (lowerCaseKeys) {
				key = key.toLowerCase();
			}

			dest.put(key, val);
		}
	}

	public Map<String, String> toMap(Buf src, boolean urlDecodeKeys, boolean urlDecodeVals, boolean lowerCaseKeys) {
		Map<String, String> map = U.map();
		toMap(src, urlDecodeKeys, urlDecodeVals, lowerCaseKeys, map);
		return map;
	}

	@SuppressWarnings("unchecked")
	public void toUrlEncodedParams(Buf src, Map<String, Object> params) {
		for (int i = 0; i < count; i++) {
			String key = keys[i].str(src.bytes());
			String val = values[i].str(src.bytes());

			key = Msc.urlDecodeOrKeepOriginal(key);
			val = Msc.urlDecodeOrKeepOriginal(val);

			if (key.endsWith("[]")) {
				key = Str.sub(key, 0, -2);
				List<String> list = (List<String>) params.get(key);

				if (list == null) {
					list = U.list();
					params.put(key, list);
				}

				list.add(val);

			} else {
				params.put(key, val);
			}
		}
	}

	public Map<String, byte[]> toBinaryMap(Buf src, boolean urlDecodeKeys) {
		Map<String, byte[]> map = U.map();

		for (int i = 0; i < count; i++) {
			String key = keys[i].str(src.bytes());
			byte[] val = values[i].bytes(src);

			if (urlDecodeKeys) {
				key = Msc.urlDecodeOrKeepOriginal(key);
			}

			map.put(key, val);
		}

		return map;
	}

}
