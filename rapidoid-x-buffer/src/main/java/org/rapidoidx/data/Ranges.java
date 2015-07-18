package org.rapidoidx.data;

/*
 * #%L
 * rapidoid-x-buffer
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.bytes.Bytes;
import org.rapidoidx.bytes.BytesUtil;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Ranges {

	public final Range[] ranges;

	public int count;

	public Ranges(int capacity) {
		this.ranges = new Range[capacity];

		for (int i = 0; i < capacity; i++) {
			ranges[i] = new Range();
			ranges[i].reset();
		}
	}

	public Ranges reset() {
		for (int i = 0; i < count; i++) {
			ranges[i].reset();
		}
		count = 0;

		return this;
	}

	public Range getByPrefix(Bytes bytes, byte[] prefix, boolean caseSensitive) {
		return BytesUtil.getByPrefix(bytes, this, prefix, caseSensitive);
	}

	@Override
	public String toString() {
		return super.toString() + "[" + count + "]";
	}

	public long max() {
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

	public void add(long start, long length) {
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

	public Range get(int index) {
		assert index >= 0 && index < count;
		return ranges[index];
	}

	public Range first() {
		return get(0);
	}

	public Range last() {
		return get(count - 1);
	}

}
