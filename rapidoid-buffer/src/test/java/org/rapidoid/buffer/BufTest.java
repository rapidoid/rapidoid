package org.rapidoid.buffer;

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
import org.testng.annotations.Test;

public class BufTest extends BufferTestCommons implements Constants {

	@Test
	public void shouldAppendData() {
		BufGroup bufs = new BufGroup(2);
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
		BufGroup bufs = new BufGroup(2);
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
		BufGroup bufs = new BufGroup(2);
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
		BufGroup bufs = new BufGroup(2);
		Buf buf = bufs.newBuf();
		buf.append("5a1234567890fg-3450fg0x45g-3");

		eq(buf.getN(new Range(0, 1)), 5);
		eq(buf.getN(new Range(2, 10)), 1234567890);
		eq(buf.getN(new Range(14, 5)), -3450);
		eq(buf.getN(new Range(21, 1)), 0);
		eq(buf.getN(new Range(23, 2)), 45);
		eq(buf.getN(new Range(26, 2)), -3);
	}

	@Test
	public void shouldFindSubsequences() {
		BufGroup bufs = new BufGroup(2);
		Buf buf = bufs.newBuf();

		/************* 0123456789012345678901234567890 */
		// buf.append("-abc-xAaw-54-bAr--The-End-");
		buf.append("-abc-xaaw-54-bar--the-end-");

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
		BufGroup bufs = new BufGroup(2);
		Buf buf = bufs.newBuf();

		buf.append("first second  third\r\na b c\r\n");

		buf.position(0);
		buf.limit(buf.size());

		Range range = new Range();

		buf.scanUntil(SPACE, range, true);
		eq(buf.get(range), "first");

		buf.scanUntil(SPACE, range, true);
		eq(buf.get(range), "second");

		buf.scanUntil(SPACE, range, true);
		eq(buf.get(range), "");

		buf.scanLn(range, true);
		eq(buf.get(range), "third");

		buf.scanUntil(SPACE, range, true);
		eq(buf.get(range), "a");

		buf.scanUntil(SPACE, range, true);
		eq(buf.get(range), "b");

		buf.scanLn(range, true);
		eq(buf.get(range), "c");

		isFalse(buf.hasRemaining());
	}

	@Test
	public void testScanWhile() {
		BufGroup bufs = new BufGroup(2);
		Buf buf = bufs.newBuf();

		buf.append("abc:  xy:");

		buf.position(0);
		buf.limit(buf.size());

		Range range = new Range();

		buf.scanUntil(COL, range, true);
		eq(buf.get(range), "abc");

		eq(buf.position(), 4);

		buf.scanWhile(SPACE, range, true);
		eq(range, 4, 2);

		eq(buf.position(), 6);

		buf.scanUntil(COL, range, true);
		eq(buf.get(range), "xy");

		eq(buf.position(), 9);
	}

	@Test
	public void testScanUntilAndMatchPrefix() {
		final int NO_PREFIX = 0;
		Range range = new Range();

		eq(MultiBuf.scanUntilAndMatchPrefix(U.buf("\n"), range, LF, 0, 0, NO_PREFIX), 1);
		eq(range, 0, 0);

		eq(MultiBuf.scanUntilAndMatchPrefix(U.buf("a\n"), range, COL, 0, 0, NO_PREFIX), NOT_FOUND);
		eq(range, -1, 0);

		eq(MultiBuf.scanUntilAndMatchPrefix(U.buf("a\n"), range, LF, 0, 1, NO_PREFIX), 2);
		eq(range, 0, 1);

		eq(MultiBuf.scanUntilAndMatchPrefix(U.buf("ab:c"), range, COL, 0, 3, NO_PREFIX), 3);
		eq(range, 0, 2);

		for (int i = 0; i < 10; i++) {
			String s = U.copyNtimes("a", i);

			eq(MultiBuf.scanUntilAndMatchPrefix(U.buf(s + ":"), range, COL, 0, i, NO_PREFIX), i + 1);
			eq(range, 0, i);

			eq(MultiBuf.scanLnAndMatchPrefix(U.buf(s + "\n"), range, 0, i, NO_PREFIX), i + 1);
			eq(range, 0, i);

			eq(MultiBuf.scanLnAndMatchPrefix(U.buf(s + "\r\n"), range, 0, i + 1, NO_PREFIX), i + 2);
			eq(range, 0, i);
		}

		eq(MultiBuf.scanLnAndMatchPrefix(U.buf("x\n"), range, 0, 0, NO_PREFIX), NOT_FOUND);
		eq(range, -1, 0);
		eq(MultiBuf.scanLnAndMatchPrefix(U.buf("x\r\n"), range, 0, 1, NO_PREFIX), NOT_FOUND);
		eq(range, -1, 0);

		eq(MultiBuf.scanLnAndMatchPrefix(U.buf("x\n"), range, 1, 1, NO_PREFIX), 2);
		eq(range, 1, 0);
		eq(MultiBuf.scanLnAndMatchPrefix(U.buf("x\r\n"), range, 1, 2, NO_PREFIX), 3);
		eq(range, 1, 0);
	}

	@Test
	public void testScanLnLn() {
		for (int factor = 1; factor <= 10; factor++) {
			BufGroup bufs = new BufGroup(factor);
			Buf buf = bufs.newBuf();

			buf.append("GET /hi H\naa: bb\nxyz\n\n");

			buf.position(0);
			buf.limit(buf.size());

			Range verb = new Range();
			Range uri = new Range();
			Range protocol = new Range();

			buf.scanUntil(SPACE, verb, true);
			buf.scanUntil(SPACE, uri, true);
			buf.scanLn(protocol, true);

			Int result = new Int();
			Ranges headers = new Ranges(10);
			buf.scanLnLn(headers, 0, result);

			eq(headers.count, 2);
			eq(headers.ranges[0], 10, 6);
			eq(headers.ranges[1], 17, 3);
		}
	}

	@Test
	public void testPutNumAsText() {
		BufGroup bufs = new BufGroup(1);

		String num = "1234567890";

		for (int dig = 1; dig <= 10; dig++) {
			int n = U.num(num.substring(0, dig));

			Buf buf = bufs.newBuf();
			buf.append(U.mul(" ", dig + 2));
			buf.putNumAsText(1, n, true);
			eq(buf.asText(), " " + n + " ");

			Buf buf2 = bufs.newBuf();
			buf2.append(U.mul(" ", dig + 3));
			buf2.putNumAsText(1, -n, true);
			eq(buf2.asText(), " " + (-n) + " ");

			Buf buf3 = bufs.newBuf();
			buf3.append(U.mul(" ", dig + 2));
			buf3.putNumAsText(dig, n, false);
			eq(buf3.asText(), " " + n + " ");

			Buf buf4 = bufs.newBuf();
			buf4.append(U.mul(" ", dig + 3));
			buf4.putNumAsText(dig + 1, -n, false);
			eq(buf4.asText(), " " + (-n) + " ");
		}

	}

	private void checkMatch(Buf buf, int start, int limit, String match, int... positions) {
		for (int pos : positions) {
			int p = buf.find(start, limit, match.getBytes(), true);
			eq(p, pos);
			start = p + 1;
		}
	}

}
