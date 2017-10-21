package org.rapidoid.data;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.u.U;

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
public class BufRange extends RapidoidThing {

	public static final BufRange NONE = new BufRange();

	public int start = -1;

	public int length = 0;

	public BufRange() {
	}

	public BufRange(int start, int length) {
		this.start = start;
		this.length = length;
	}

	public BufRange reset() {
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

	public static BufRange fromTo(int from, int to) {
		U.must(from <= to, "Invalid range!");

		return new BufRange(from, to - from);
	}

	public String str(Buf buf) {
		return BytesUtil.get(buf.bytes(), this);
	}

	public String str(Bytes bytes) {
		return BytesUtil.get(bytes, this);
	}

	public void assign(BufRange range) {
		set(range.start, range.length);
	}

	public void strip(int left, int right) {
		start += left;
		length -= left + right;
	}

}
