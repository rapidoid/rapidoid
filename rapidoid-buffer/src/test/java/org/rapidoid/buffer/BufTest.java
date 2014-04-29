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
import org.rapidoid.util.Constants;
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
	public void shouldScanData() {
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

	private void checkMatch(Buf buf, int start, int limit, String match, int... positions) {
		for (int pos : positions) {
			int p = buf.find(start, limit, match.getBytes(), true);
			eq(p, pos);
			start = p + 1;
		}
	}

}
