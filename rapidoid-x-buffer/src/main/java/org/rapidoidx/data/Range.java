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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.bytes.Bytes;
import org.rapidoidx.bytes.BytesUtil;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Range {

	public static final Range NONE = new Range();

	public int start = -1;

	public int length = 0;

	public Range() {}

	public Range(int start, int length) {
		this.start = start;
		this.length = length;
	}

	public Range reset() {
		start = -1;
		length = 0;
		return this;
	}

	public int limit() {
		return start + length;
	}

	public int last() {
		return start + length - 1;
	}

	@Override
	public String toString() {
		return isEmpty() ? "[]" : "[" + start + ":" + length + "]";
	}

	public boolean isEmpty() {
		return start < 0 || length <= 0;
	}

	public void ends(int endPos) {
		length = endPos - start;
	}

	public void starts(int startPos) {
		start = startPos;
	}

	public void set(int start, int length) {
		this.start = start;
		this.length = length;
	}

	public void setInterval(int start, int limit) {
		assert start <= limit;
		this.start = start;
		this.length = limit - start;
	}

	public String get(String whole) {
		return start >= 0 ? whole.substring(start, start + length) : "";
	}

	public byte[] bytes(Buf src) {
		byte[] bytes = new byte[length];
		src.get(this, bytes, 0);
		return bytes;
	}

	public long backup() {
		long backup = start;

		backup -= Integer.MIN_VALUE;
		backup <<= 32;
		backup += length;

		return backup;
	}

	public void restore(long backup) {
		length = (int) backup;
		backup -= length;
		start = (int) ((backup >>> 32) + Integer.MIN_VALUE);
	}

	public boolean sameAs(long backup) {
		int len = (int) backup;

		if (len != length) {
			return false;
		}

		backup -= len;
		int st = (int) ((backup >>> 32) + Integer.MIN_VALUE);

		return st == start;
	}

	public static Range fromTo(int from, int to) {
		U.must(from <= to, "Invalid range!");

		return new Range(from, to - from);
	}

	public String str(Buf buf) {
		return BytesUtil.get(buf.bytes(), this);
	}

	public String str(Bytes bytes) {
		return BytesUtil.get(bytes, this);
	}

	public void assign(Range range) {
		set(range.start, range.length);
	}

	public void strip(int left, int right) {
		start += left;
		length -= left + right;
	}

}
