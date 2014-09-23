package org.rapidoid.bytes;

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

import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;
import org.rapidoid.wrap.Int;

public class BYTES implements Constants {

	public static final byte[] CHARS_SWITCH_CASE = new byte[128];

	static {
		for (int ch = 0; ch < 128; ch++) {
			if (ch >= 'a' && ch <= 'z') {
				CHARS_SWITCH_CASE[ch] = (byte) (ch - 32);
			} else if (ch >= 'A' && ch <= 'Z') {
				CHARS_SWITCH_CASE[ch] = (byte) (ch + 32);
			} else {
				CHARS_SWITCH_CASE[ch] = (byte) ch;
			}
		}
	}

	public static Bytes from(byte[] bytes) {
		return new ByteArrayBytes(bytes);
	}

	public static Bytes from(String s) {
		return new StringBytes(s);
	}

	public static int parseLines(Bytes bytes, Ranges lines, Int res, int start, int limit, byte end1, byte end2) {
		byte b0 = 0, b1 = 0, b2 = 0, b3 = 0;
		int ret = -1;
		res.value = NOT_FOUND;

		int i;
		int from = start;
		for (i = start; i < limit; i++) {
			b0 = b1;
			b1 = b2;
			b2 = b3;
			b3 = bytes.get(i);

			if (b3 == LF) {
				int len;

				if (b2 == CR) {
					len = i - from - 1;
					if (b0 == end1 && b1 == end2 && len > 0) {
						res.value = lines.count;
					}
				} else {
					len = i - 2 - from;
					if (b1 == end1 && b2 == end2 && len > 0) {
						res.value = lines.count;
					}
				}

				if (len == 0) {
					ret = i + 1;
					break;
				}

				lines.add(from, len);
				from = i + 1;
			}
		}

		return ret;
	}

	public static Range getByPrefix(Bytes bytes, Ranges lines, byte[] prefix, boolean caseSensitive) {
		for (int i = 0; i < lines.count; i++) {
			if (startsWith(bytes, lines.ranges[i], prefix, caseSensitive)) {
				return lines.ranges[i];
			}
		}
		return null;
	}

	public static String get(Bytes bytes, Range range) {
		return new String(getBytes(bytes, range));
	}

	public static byte[] getBytes(Bytes bytes, Range range) {
		byte[] byteArr = new byte[range.length];
		for (int i = 0; i < byteArr.length; i++) {
			byteArr[i] = bytes.get(range.start + i);
		}
		return byteArr;
	}

	public static int scan(Bytes bytes, int from, int to, byte value) {
		for (int i = from; i <= to; i++) {
			if (bytes.get(i) == value) {
				return i;
			}
		}

		return -1;
	}

	public static boolean match(Bytes bytes, int start, byte[] match, int offset, int length, boolean caseSensitive) {

		boolean result;
		if (caseSensitive) {
			result = matchSensitive(bytes, start, match, offset, length);
		} else {
			result = matchNoCase(bytes, start, match, offset, length);
		}

		return result;
	}

	public static boolean matchNoCase(Bytes bytes, int start, byte[] match, int offset, int length) {
		for (int i = 0; i < length; i++) {
			byte b = bytes.get(start + i);
			if (b != match[offset + i] && (b < 'A' || CHARS_SWITCH_CASE[b] != match[offset + i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean matchSensitive(Bytes bytes, int start, byte[] match, int offset, int length) {
		for (int i = 0; i < length; i++) {
			if (bytes.get(start + i) != match[offset + i]) {
				return false;
			}
		}

		return true;
	}

	public static boolean match(Bytes bytes, int start, byte[] match, boolean caseSensitive) {
		return match(bytes, start, match, 0, match.length, caseSensitive);
	}

	public static int find(Bytes bytes, int start, int limit, byte match, boolean caseSensitive) {

		assert start >= 0;
		assert limit >= 0;

		if (limit - start < 1) {
			return -1;
		}

		if (caseSensitive) {
			return scan(bytes, start, limit - 1, match);
		} else {
			throw U.notReady(); // FIXME
		}
	}

	public static boolean matches(Bytes bytes, Range target, byte[] match, boolean caseSensitive) {

		if (target.length != match.length || target.start < 0 || target.last() >= bytes.limit()) {
			return false;
		}

		boolean result = match(bytes, target.start, match, caseSensitive);

		return result;
	}

	public static boolean startsWith(Bytes bytes, Range target, byte[] match, boolean caseSensitive) {

		if (target.length < match.length || target.start < 0 || target.last() >= bytes.limit()) {
			return false;
		}

		boolean result = match(bytes, target.start, match, caseSensitive);

		return result;
	}

	public static boolean containsAt(Bytes bytes, Range target, int offset, byte[] match, boolean caseSensitive) {

		if (offset < 0 || target.length < offset + match.length || target.start < 0 || target.last() >= bytes.limit()) {
			return false;
		}

		boolean result = match(bytes, target.start + offset, match, caseSensitive);

		return result;
	}

	public static void trim(Bytes bytes, Range target) {

		int start = target.start;
		int len = target.length;
		int finish = start + len - 1;

		if (start < 0 || len == 0) {

			return;
		}

		while (start < finish && bytes.get(start) == ' ') {
			start++;
		}

		while (start < finish && bytes.get(finish) == ' ') {
			finish--;
		}

		target.start = start;
		target.length = finish - start + 1;

	}

	public static boolean split(Bytes bytes, Range target, byte sep, Range before, Range after, boolean trimParts) {

		int pos = find(bytes, target.start, target.limit(), sep, true);

		if (pos >= 0) {
			before.setInterval(target.start, pos);
			after.setInterval(pos + 1, target.limit());

			if (trimParts) {
				trim(bytes, before);
				trim(bytes, after);
			}

			return true;
		} else {
			before.assign(target);
			after.reset();

			if (trimParts) {
				trim(bytes, before);
			}

			return false;
		}
	}

}
