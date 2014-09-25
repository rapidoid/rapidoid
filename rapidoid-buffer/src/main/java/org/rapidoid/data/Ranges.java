package org.rapidoid.data;

/*
 * #%L
 * rapidoid-buffer
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.buffer.Buf;
import org.rapidoid.bytes.BYTES;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.util.U;

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
		return BYTES.getByPrefix(bytes, this, prefix, caseSensitive);
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

}
