package org.rapidoid.bytes;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.util.Msc;
import org.rapidoid.wrap.IntWrap;

import static org.rapidoid.util.Constants.*;

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
public class BytesUtil extends RapidoidThing {

	public static final byte[] CHARS_SWITCH_CASE = new byte[128];

	private static final String URI_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~:/?#[]@!$&()'*+,;=%";

	private static final boolean[] URI_ALLOWED_CHARACTER = new boolean[128];

	static {
		for (int ch = 0; ch < 128; ch++) {
			if (ch >= 'a' && ch <= 'z') {
				CHARS_SWITCH_CASE[ch] = (byte) (ch - 32);
			} else if (ch >= 'A' && ch <= 'Z') {
				CHARS_SWITCH_CASE[ch] = (byte) (ch + 32);
			} else {
				CHARS_SWITCH_CASE[ch] = (byte) ch;
			}

			URI_ALLOWED_CHARACTER[ch] = URI_CHARACTERS.indexOf(ch) >= 0;
		}
	}

	public static Bytes from(byte[] bytes) {
		return new ByteArrayBytes(bytes);
	}

	public static Bytes from(String s) {
		return new StringBytes(s);
	}

	public static int parseLines(Bytes bytes, BufRanges lines, IntWrap res, int start, int limit, byte end1, byte end2) {
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
					len = i - from;
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

	public static int parseLines(Bytes bytes, BufRanges lines, int start, int limit) {
		byte b0 = 0, b1 = 0;
		int ret = -1;

		int i;
		int from = start;
		for (i = start; i < limit; i++) {
			b0 = b1;
			b1 = bytes.get(i);

			if (b1 == LF) {
				int len;

				if (b0 == CR) {
					len = i - from - 1;
				} else {
					len = i - from;
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

	public static int parseLine(Bytes bytes, BufRange line, int start, int limit) {
		byte b0 = 0, b1 = 0;
		int ret = -1;

		int i;
		for (i = start; i < limit; i++) {
			b0 = b1;
			b1 = bytes.get(i);

			if (b1 == LF) {
				int len;

				if (b0 == CR) {
					len = i - start - 1;
				} else {
					len = i - start;
				}

				line.set(start, len);
				ret = i + 1;
				break;
			}
		}

		return ret;
	}

	public static BufRange getByPrefix(Bytes bytes, BufRanges ranges, byte[] prefix, boolean caseSensitive) {
		for (int i = 0; i < ranges.count; i++) {
			if (startsWith(bytes, ranges.ranges[i], prefix, caseSensitive)) {
				return ranges.ranges[i];
			}
		}
		return null;
	}

	public static String get(Bytes bytes, BufRange range) {
		return new String(getBytes(bytes, range));
	}

	public static byte[] getBytes(Bytes bytes, BufRange range) {
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

	public static int scanNoCase(Bytes bytes, int from, int to, byte value) {
		for (int i = from; i <= to; i++) {
			byte b = bytes.get(i);

			if (b == value || (b >= 'A' && CHARS_SWITCH_CASE[b] == value)) {
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
			return scanNoCase(bytes, start, limit - 1, match);
		}
	}

	public static int find(Bytes bytes, int start, int limit, byte[] match, boolean caseSensitive) {
		return find(bytes, start, limit, match, 0, match.length, caseSensitive);
	}

	public static int find(Bytes bytes, int start, int limit, byte[] match, int offset, int length,
	                       boolean caseSensitive) {

		assert start >= 0;
		assert limit >= 0;
		assert offset >= 0;
		assert length >= 0;

		int result;
		if (caseSensitive) {
			result = findSensitive(bytes, start, limit, match, offset, length);
		} else {
			result = findNoCase(bytes, start, limit, match, offset, length);
		}

		return result;
	}

	private static int findNoCase(Bytes bytes, int start, int limit, byte[] match, int offset, int length) {
		throw Err.notReady();
	}

	private static int findSensitive(Bytes bytes, int start, int limit, byte[] match, int offset, int length) {
		if (limit - start < length) {
			return -1;
		}

		int pos = start;
		int last = limit - length;

		while ((pos = scan(bytes, pos, last, match[0])) >= 0) {
			if (matchSensitive(bytes, pos, match, offset, length)) {
				return pos;
			}
			pos++;
		}

		return -1;
	}

	public static boolean matches(Bytes bytes, BufRange target, byte[] match, boolean caseSensitive) {

		if (target.length != match.length || target.start < 0 || target.last() >= bytes.limit()) {
			return false;
		}

		boolean result = match(bytes, target.start, match, caseSensitive);

		return result;
	}

	public static boolean startsWith(Bytes bytes, BufRange target, byte[] match, boolean caseSensitive) {

		if (target.length < match.length || target.start < 0 || target.last() >= bytes.limit()) {
			return false;
		}

		boolean result = match(bytes, target.start, match, caseSensitive);

		return result;
	}

	public static boolean containsAt(Bytes bytes, BufRange target, int offset, byte[] match, boolean caseSensitive) {

		if (offset < 0 || target.length < offset + match.length || target.start < 0 || target.last() >= bytes.limit()) {
			return false;
		}

		boolean result = match(bytes, target.start + offset, match, caseSensitive);

		return result;
	}

	public static void trim(Bytes bytes, BufRange target) {

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

	public static boolean split(Bytes bytes, BufRange target, byte sep, BufRange before, BufRange after, boolean trimParts) {

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

	/**
	 * Scans the buffer until the specified separator is found, and matches the 4-byte prefix of the scanned selection
	 * against the specified search prefix. Returns the position of the separator, or <code>-1</code> if the limit is
	 * reached and separator not found. If the prefix is matched, the negative of the position is returned, to mark the
	 * prefix match. Duplicated code for performance reasons.
	 */
	public static int scanUntilAndMatchPrefix(Bytes bytes, BufRange result, byte separator, int fromPos, int toPos,
	                                          int searchPrefix) {

		byte b0, b1, b2, b3;

		int p = fromPos;
		if (p <= toPos) {
			b0 = bytes.get(p);
			if (b0 == separator) {
				result.set(fromPos, 0);
				return p + 1;
			}
		} else {
			result.reset();
			return NOT_FOUND;
		}

		p++;
		if (p <= toPos) {
			b1 = bytes.get(p);
			if (b1 == separator) {
				result.set(fromPos, 1);
				return p + 1;
			}
		} else {
			result.reset();
			return NOT_FOUND;
		}

		p++;
		if (p <= toPos) {
			b2 = bytes.get(p);
			if (b2 == separator) {
				result.set(fromPos, 2);
				return p + 1;
			}
		} else {
			result.reset();
			return NOT_FOUND;
		}

		p++;
		if (p <= toPos) {
			b3 = bytes.get(p);
			if (b3 == separator) {
				result.set(fromPos, 3);
				return p + 1;
			}
		} else {
			result.reset();
			return NOT_FOUND;
		}

		int prefix = Msc.intFrom(b0, b1, b2, b3);

		boolean matchedPrefix = prefix == searchPrefix;

		for (int i = p; i <= toPos; i++) {
			if (bytes.get(i) == separator) {
				result.setInterval(fromPos, i);
				int nextPos = i + 1;
				return matchedPrefix ? -nextPos : nextPos;
			}
		}

		result.reset();
		return NOT_FOUND;
	}

	/**
	 * Scans the buffer until a line separator (CRLF or LF) is found, and matches the 4-byte prefix of the scanned
	 * selection against the specified search prefix. Returns the position of the separator, or <code>-1</code> if the
	 * limit is reached and separator not found. If the prefix is matched, the negative of the position is returned, to
	 * mark the prefix match. Duplicated code for performance reasons.
	 */
	public static int scanLnAndMatchPrefix(Bytes bytes, BufRange result, int fromPos, int toPos, int searchPrefix) {

		byte b0, b1, b2, b3;

		int p = fromPos;
		if (p <= toPos) {
			b0 = bytes.get(p);
			if (b0 == LF) {
				result.set(fromPos, 0);
				return p + 1;
			}
		} else {
			result.reset();
			return NOT_FOUND;
		}

		p++;
		if (p <= toPos) {
			b1 = bytes.get(p);
			if (b1 == LF) {
				if (b0 == CR) {
					result.set(fromPos, 0);
				} else {
					result.set(fromPos, 1);
				}
				return p + 1;
			}
		} else {
			result.reset();
			return NOT_FOUND;
		}

		p++;
		if (p <= toPos) {
			b2 = bytes.get(p);
			if (b2 == LF) {
				if (b1 == CR) {
					result.set(fromPos, 1);
				} else {
					result.set(fromPos, 2);
				}
				return p + 1;
			}
		} else {
			result.reset();
			return NOT_FOUND;
		}

		p++;
		if (p <= toPos) {
			b3 = bytes.get(p);
			if (b3 == LF) {
				if (b2 == CR) {
					result.set(fromPos, 2);
				} else {
					result.set(fromPos, 3);
				}
				return p + 1;
			}
		} else {
			result.reset();
			return NOT_FOUND;
		}

		int prefix = Msc.intFrom(b0, b1, b2, b3);

		boolean matchedPrefix = prefix == searchPrefix;

		for (int i = p; i <= toPos; i++) {
			if (bytes.get(i) == LF) {

				if (bytes.get(i - 1) == CR) {
					result.setInterval(fromPos, i - 1);
				} else {
					result.setInterval(fromPos, i);
				}

				int nextPos = i + 1;
				return matchedPrefix ? -nextPos : nextPos;
			}
		}

		result.reset();
		return NOT_FOUND;
	}

	public static boolean isValidURI(Bytes bytes, BufRange uri) {
		int start = uri.start;
		int len = uri.length;
		int last = uri.last();

		if (len == 0 || bytes.get(start) != '/') {
			return false;
		}

		boolean inPath = true;

		byte prev = '/';
		for (int p = start + 1; p <= last; p++) {
			byte b = bytes.get(p);

			if (b <= 0 || !URI_ALLOWED_CHARACTER[b]) {
				return false;
			}

			// disallow '..' OR '//' in the URI's PATH (before the '?')
			if (inPath) {
				if (b == '.' || b == '/') {
					if (prev == b) {
						return false;
					}
				} else if (b == '?') {
					inPath = false;
				}
			}

			prev = b;
		}

		return true;
	}

	public static int getIntPrefixOf(Bytes bytes, int position, int limit) {
		byte b0, b1, b2, b3;

		int p = position;
		if (p < limit) {
			b0 = bytes.get(p);
		} else {
			return 0;
		}

		p++;
		if (p < limit) {
			b1 = bytes.get(p);
		} else {
			return Msc.intFrom(b0, BYTE_0, BYTE_0, BYTE_0);
		}

		p++;
		if (p < limit) {
			b2 = bytes.get(p);
		} else {
			return Msc.intFrom(b0, b1, BYTE_0, BYTE_0);
		}

		p++;
		if (p < limit) {
			b3 = bytes.get(p);
		} else {
			return Msc.intFrom(b0, b1, b2, BYTE_0);
		}

		return Msc.intFrom(b0, b1, b2, b3);
	}

}
