package org.rapidoid.buffer;

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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.commons.Rnd;
import org.rapidoid.commons.Str;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.u.U;

import static org.rapidoid.util.Constants.*;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class BufTest extends BufferTestCommons {

	@Test
	public void shouldAppendData() {
		BufGroup bufs = new BufGroup(4);
		Buf buf = bufs.newBuf();

		eq(buf, "");

		buf.append("");
		eq(buf, "");

		buf.append("Foo");
		eq(buf, "Foo");

		buf.append("Bar");
		eq(buf, "FooBar");

		buf.append("Bazinga");
		eq(buf, "FooBarBazinga");

		buf.append("");
		eq(buf, "FooBarBazinga");

		buf.append("X");
		eq(buf, "FooBarBazingaX");

		buf.append("Y");
		eq(buf, "FooBarBazingaXY");

		buf.append("Z");
		eq(buf, "FooBarBazingaXYZ");

		buf.append("W");
		eq(buf, "FooBarBazingaXYZW");
	}

	@Test
	public void shouldShrinkOnLeft() {
		BufGroup bufs = new BufGroup(4);
		Buf buf = bufs.newBuf();

		buf.append("abcdefgh-foo-bar-123456789-the-end");
		eq(buf, "abcdefgh-foo-bar-123456789-the-end");

		buf.deleteBefore(1);
		eq(buf, "bcdefgh-foo-bar-123456789-the-end");

		buf.deleteBefore(2);
		eq(buf, "defgh-foo-bar-123456789-the-end");

		buf.deleteBefore(1);
		eq(buf, "efgh-foo-bar-123456789-the-end");

		buf.deleteBefore(1);
		eq(buf, "fgh-foo-bar-123456789-the-end");

		buf.deleteBefore(4);
		eq(buf, "foo-bar-123456789-the-end");

		buf.deleteBefore(10);
		eq(buf, "3456789-the-end");

		buf.deleteBefore(8);
		eq(buf, "the-end");

		buf.deleteBefore(4);
		eq(buf, "end");

		buf.deleteBefore(2);
		eq(buf, "d");

		buf.deleteBefore(1);
		eq(buf, "");

		buf.deleteBefore(0);
		eq(buf, "");
	}

	@Test
	public void shouldShrinkOnRight() {
		BufGroup bufs = new BufGroup(4);
		Buf buf = bufs.newBuf();

		buf.append("abcdefgh-foo-bar-123456789-the-end");
		eq(buf, "abcdefgh-foo-bar-123456789-the-end");

		buf.deleteLast(1);
		eq(buf, "abcdefgh-foo-bar-123456789-the-en");

		buf.deleteLast(2);
		eq(buf, "abcdefgh-foo-bar-123456789-the-");

		buf.deleteLast(1);
		eq(buf, "abcdefgh-foo-bar-123456789-the");

		buf.deleteLast(1);
		eq(buf, "abcdefgh-foo-bar-123456789-th");

		buf.deleteLast(4);
		eq(buf, "abcdefgh-foo-bar-12345678");

		buf.deleteLast(10);
		eq(buf, "abcdefgh-foo-ba");

		buf.deleteLast(8);
		eq(buf, "abcdefg");

		buf.deleteLast(4);
		eq(buf, "abc");

		buf.deleteLast(2);
		eq(buf, "a");

		buf.deleteLast(1);
		eq(buf, "");

		buf.deleteLast(0);
		eq(buf, "");
	}

	@Test
	public void shouldParseNumbers() {
		BufGroup bufs = new BufGroup(4);
		Buf buf = bufs.newBuf();

		buf.append("5a1234567890fg-3450fg0x45g-3");
		buf.setReadOnly(true);

		eq(buf.getN(new BufRange(0, 1)), 5);
		eq(buf.getN(new BufRange(2, 10)), 1234567890);
		eq(buf.getN(new BufRange(14, 5)), -3450);
		eq(buf.getN(new BufRange(21, 1)), 0);
		eq(buf.getN(new BufRange(23, 2)), 45);
		eq(buf.getN(new BufRange(26, 2)), -3);
	}

	@Test
	public void shouldFindSubsequences() {
		BufGroup bufs = new BufGroup(4);
		Buf buf = bufs.newBuf();

		/************* 0123456789012345678901234567890 */
		// buf.append("-abc-xAaw-54-bAr--The-End-");
		buf.append("-abc-xaaw-54-bar--the-end-");
		buf.setReadOnly(true);

		int max = buf.size();

		checkMatch(buf, 0, max, "a", 1, 6, 7, 14, -1);
		checkMatch(buf, 2, max, "a", 6, 7, 14, -1);
		checkMatch(buf, 5, max, "a", 6, 7, 14, -1);
		checkMatch(buf, 7, max, "a", 7, 14, -1);

		checkMatch(buf, 0, max, "abc", 1, -1);
		checkMatch(buf, 0, max, "-abc", 0, -1);

		checkMatch(buf, 0, max, "the", 18, -1);
		checkMatch(buf, 0, max, "end", 22, -1);

		checkMatch(buf, 0, max, "+", -1);

		checkMatch(buf, 0, max, "-", 0, 4, 9, 12, 16, 17, 21, 25, -1);
	}

	@Test
	public void testScanUntil() {
		BufGroup bufs = new BufGroup(4);
		Buf buf = bufs.newBuf();

		buf.append("first second  third\r\na b c\r\n");
		buf.setReadOnly(true);

		buf.position(0);
		buf.limit(buf.size());

		BufRange range = new BufRange();

		buf.scanUntil(SPACE, range);
		eq(buf.get(range), "first");

		buf.scanUntil(SPACE, range);
		eq(buf.get(range), "second");

		buf.scanUntil(SPACE, range);
		eq(buf.get(range), "");

		buf.scanLn(range);
		eq(buf.get(range), "third");

		buf.scanUntil(SPACE, range);
		eq(buf.get(range), "a");

		buf.scanUntil(SPACE, range);
		eq(buf.get(range), "b");

		buf.scanLn(range);
		eq(buf.get(range), "c");

		isFalse(buf.hasRemaining());
	}

	@Test
	public void testScanWhile() {
		BufGroup bufs = new BufGroup(4);
		Buf buf = bufs.newBuf();

		buf.append("abc:  xy:");
		buf.setReadOnly(true);

		buf.position(0);
		buf.limit(buf.size());

		BufRange range = new BufRange();

		buf.scanUntil(COL, range);
		eq(buf.get(range), "abc");

		eq(buf.position(), 4);

		buf.scanWhile(SPACE, range);
		eq(range, 4, 2);

		eq(buf.position(), 6);

		buf.scanUntil(COL, range);
		eq(buf.get(range), "xy");

		eq(buf.position(), 9);
	}

	@Test
	public void testScanUntilAndMatchPrefix() {
		final int NO_PREFIX = 0;
		BufRange range = new BufRange();

		eq(BytesUtil.scanUntilAndMatchPrefix(BytesUtil.from("\n"), range, LF, 0, 0, NO_PREFIX), 1);
		eq(range, 0, 0);

		eq(BytesUtil.scanUntilAndMatchPrefix(BytesUtil.from("a\n"), range, COL, 0, 0, NO_PREFIX), NOT_FOUND);
		eq(range, -1, 0);

		eq(BytesUtil.scanUntilAndMatchPrefix(BytesUtil.from("a\n"), range, LF, 0, 1, NO_PREFIX), 2);
		eq(range, 0, 1);

		eq(BytesUtil.scanUntilAndMatchPrefix(BytesUtil.from("ab:c"), range, COL, 0, 3, NO_PREFIX), 3);
		eq(range, 0, 2);

		for (int i = 0; i < 10; i++) {
			String s = Str.mul("a", i);

			eq(BytesUtil.scanUntilAndMatchPrefix(BytesUtil.from(s + ":"), range, COL, 0, i, NO_PREFIX), i + 1);
			eq(range, 0, i);

			eq(BytesUtil.scanLnAndMatchPrefix(BytesUtil.from(s + "\n"), range, 0, i, NO_PREFIX), i + 1);
			eq(range, 0, i);

			eq(BytesUtil.scanLnAndMatchPrefix(BytesUtil.from(s + "\r\n"), range, 0, i + 1, NO_PREFIX), i + 2);
			eq(range, 0, i);
		}

		eq(BytesUtil.scanLnAndMatchPrefix(BytesUtil.from("x\n"), range, 0, 0, NO_PREFIX), NOT_FOUND);
		eq(range, -1, 0);
		eq(BytesUtil.scanLnAndMatchPrefix(BytesUtil.from("x\r\n"), range, 0, 1, NO_PREFIX), NOT_FOUND);
		eq(range, -1, 0);

		eq(BytesUtil.scanLnAndMatchPrefix(BytesUtil.from("x\n"), range, 1, 1, NO_PREFIX), 2);
		eq(range, 1, 0);
		eq(BytesUtil.scanLnAndMatchPrefix(BytesUtil.from("x\r\n"), range, 1, 2, NO_PREFIX), 3);
		eq(range, 1, 0);
	}

	@Test
	public void testScanLnLn() {
		for (int factor = 1; factor <= 10; factor++) {
			BufGroup bufs = new BufGroup((int) Math.pow(2, factor));
			Buf buf = bufs.newBuf();

			String s = "GET /hi H\naa: bb\nxyz\r\n\r\n";
			buf.append(s);
			buf.setReadOnly(true);

			buf.position(0);
			buf.limit(buf.size());

			BufRange verb = new BufRange();
			BufRange uri = new BufRange();
			BufRange protocol = new BufRange();

			buf.scanUntil(SPACE, verb);
			eq(s, verb, "GET");

			buf.scanUntil(SPACE, uri);
			eq(s, uri, "/hi");

			buf.scanLn(protocol);
			eq(s, protocol, "H");

			BufRanges headers = new BufRanges(10);
			buf.scanLnLn(headers.reset());

			eq(headers.count, 2);

			eq(s, headers.ranges[0], "aa: bb");
			eq(s, headers.ranges[1], "xyz");
		}
	}

	@Test
	public void testPutNumAsText() {
		BufGroup bufs = new BufGroup(2);

		String num = "1234567890";

		for (int dig = 1; dig <= 10; dig++) {
			int n = U.num(num.substring(0, dig));

			Buf buf = bufs.newBuf();
			buf.append(Str.mul(" ", dig + 2));
			buf.putNumAsText(1, n, true);
			eq(buf.asText(), " " + n + " ");

			Buf buf2 = bufs.newBuf();
			buf2.append(Str.mul(" ", dig + 3));
			buf2.putNumAsText(1, -n, true);
			eq(buf2.asText(), " " + (-n) + " ");

			Buf buf3 = bufs.newBuf();
			buf3.append(Str.mul(" ", dig + 2));
			buf3.putNumAsText(dig, n, false);
			eq(buf3.asText(), " " + n + " ");

			Buf buf4 = bufs.newBuf();
			buf4.append(Str.mul(" ", dig + 3));
			buf4.putNumAsText(dig + 1, -n, false);
			eq(buf4.asText(), " " + (-n) + " ");

			Buf buf5 = bufs.newBuf();
			buf5.append(" ");
			buf5.putNumAsText(1, n, true);
			eq(buf5.asText(), " " + n);

			Buf buf6 = bufs.newBuf();
			buf6.append(Str.mul(" ", 20));
			buf6.putNumAsText(15, n, false);
			eq(buf6.asText(), Str.mul(" ", 16 - dig) + n + Str.mul(" ", 4));

			Buf buf7 = bufs.newBuf();
			buf7.append(Str.mul(" ", 20));
			buf7.putNumAsText(5, n, true);
			eq(buf7.asText(), Str.mul(" ", 5) + n + Str.mul(" ", 15 - dig));
		}
	}

	@Test
	public void testDeleteAfter() {
		BufGroup bufs = new BufGroup(16);
		Buf buf = bufs.newBuf();

		int size = 0;
		for (int i = 0; i < 1000000; i++) {
			int add = Rnd.rnd(100);

			for (int j = 0; j < 5; j++) {
				size += add;
				buf.append(Str.mul(" ", add));
				eq(buf.size(), size);
			}

			for (int j = 0; j < 5; j++) {
				if (buf.size() > 0) {
					if (Rnd.rnd(2) == 0) {
						int delFrom = Rnd.rnd(size);
						int delN = size - delFrom;

						size -= delN;
						buf.deleteAfter(delFrom);
					} else {
						int delN = Rnd.rnd(size);

						size -= delN;
						buf.deleteBefore(delN);
					}
				}
			}
		}
	}

	private void checkMatch(Buf buf, int start, int limit, String match, int... positions) {
		for (int pos : positions) {
			int p = BytesUtil.find(buf.bytes(), start, limit, match.getBytes(), true);
			eq(p, pos);
			start = p + 1;
		}
	}

}
