package org.rapidoid.data;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.u.U;

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
public class BufRanges extends RapidoidThing {

	public final BufRange[] ranges;

	public int count;

	public BufRanges(int capacity) {
		this.ranges = new BufRange[capacity];

		for (int i = 0; i < capacity; i++) {
			ranges[i] = new BufRange();
			ranges[i].reset();
		}
	}

	public BufRanges reset() {
		for (int i = 0; i < count; i++) {
			ranges[i].reset();
		}
		count = 0;

		return this;
	}

	public BufRange getByPrefix(Bytes bytes, byte[] prefix, boolean caseSensitive) {
		return BytesUtil.getByPrefix(bytes, this, prefix, caseSensitive);
	}

	@Override
	public String toString() {
		return super.toString() + "[" + count + "]";
	}

	public int max() {
		return ranges.length;
	}

	public String str(Bytes bytes) {
		StringBuilder sb = new StringBuilder();

		sb.append("[");
		for (int i = 0; i < count; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append("<");
			sb.append(ranges[i].str(bytes));
			sb.append(">");
		}
		sb.append("]");

		return sb.toString();
	}

	public String str(Buf buf) {
		return str(buf.bytes());
	}

	public int add() {
		if (count >= max()) {
			throw U.rte("too many key-values!");
		}

		return count++;
	}

	public void add(int start, int length) {
		if (count >= max()) {
			throw U.rte("too many key-values!");
		}

		ranges[count++].set(start, length);
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public List<String> toList(Bytes bytes, int from, int to) {
		List<String> list = U.list();

		for (int i = from; i <= to; i++) {
			String s = ranges[i].str(bytes);
			list.add(s);
		}

		return list;
	}

	public Map<String, String> toMap(Bytes bytes, int from, int to, String separator) {
		Map<String, String> map = U.map();

		for (int i = from; i <= to; i++) {
			String s = ranges[i].str(bytes);
			String[] kv = s.split(separator, 2);
			map.put(kv[0], kv.length > 1 ? kv[1] : "");
		}

		return map;
	}

	public String getConcatenated(Bytes bytes, int from, int to, String separator) {
		StringBuilder sb = new StringBuilder();

		for (int i = from; i <= to; i++) {
			if (i > from && !U.isEmpty(separator)) {
				sb.append(separator);
			}
			sb.append(ranges[i].str(bytes));
		}

		return sb.toString();
	}

	public BufRange get(int index) {
		assert index >= 0 && index < count;
		return ranges[index];
	}

	public BufRange first() {
		return get(0);
	}

	public BufRange last() {
		return get(count - 1);
	}

}
