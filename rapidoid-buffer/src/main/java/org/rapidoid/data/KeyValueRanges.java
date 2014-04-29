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
import org.rapidoid.util.U;

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
			if (buf.matches(keys[i], key, caseSensitive)) {
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
			sb.append(keys[i].str(src));
			sb.append(":=");
			sb.append(values[i].str(src));
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

}
