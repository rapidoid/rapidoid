package org.rapidoid.util;

/*
 * #%L
 * rapidoid-u
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.test.TestCommons;
import org.testng.annotations.Test;

public class UTest extends TestCommons {

	@Test
	public void testTextCollectionOfObject() {
		eq(U.text(new ArrayList<Integer>()), "[]");

		List<String> lst = new ArrayList<String>();

		lst.add("java");
		lst.add("c");
		lst.add("c++");

		eq(U.text(lst), "[java, c, c++]");
	}

	@Test
	public void testTextObject() {
		eq(U.text((Object) null), "null");

		eq(U.text(123), "123");
		eq(U.text(1.23), "1.23");

		eq(U.text(true), "true");
		eq(U.text(false), "false");

		eq(U.text(""), "");
		eq(U.text("abc"), "abc");

		eq(U.text(new byte[] { -50, 0, 9 }), "[-50, 0, 9]");
		eq(U.text(new short[] { -500, 0, 9 }), "[-500, 0, 9]");
		eq(U.text(new int[] { 300000000, 70, 100 }), "[300000000, 70, 100]");
		eq(U.text(new long[] { 3000000000000000000L, 1, -8900000000000000000L }),
				"[3000000000000000000, 1, -8900000000000000000]");

		eq(U.text(new float[] { -30.40000000f, -1.587f, 89.3f }), "[-30.4, -1.587, 89.3]");
		eq(U.text(new double[] { -9987.1, -1.5, 8.3 }), "[-9987.1, -1.5, 8.3]");

		eq(U.text(new boolean[] { true }), "[true]");

		eq(U.text(new char[] { 'k', 'o', 'h' }), "[k, o, h]");
		eq(U.text(new char[] { '-', '.', '+' }), "[-, ., +]");
	}

	@Test
	public void testTextObjectArray() {
		eq(U.text(new Object[] {}), "[]");
		eq(U.text(new Object[] { 1, new boolean[] { true, false }, 3 }), "[1, [true, false], 3]");
		eq(U.text(new Object[] { new double[] { -9987.1 }, new char[] { 'a', '.' }, new int[] { 300, 70, 100 } }),
				"[[-9987.1], [a, .], [300, 70, 100]]");

		eq(U.text(new int[][] { { 1, 2 }, { 3, 4, 5 } }), "[[1, 2], [3, 4, 5]]");

		eq(U.text(new String[][][] { { { "a" }, { "r" } }, { { "m" } } }), "[[[a], [r]], [[m]]]");
	}

	@Test
	public void testXor() {
		eq(U.xor(true, true), false);
		eq(U.xor(true, false), true);
		eq(U.xor(false, true), true);
		eq(U.xor(false, false), false);
	}

	@Test
	public void testEq() {
		isTrue(U.eq("2", "2"));
		isFalse(U.eq("2", "3"));
		isTrue(U.eq("2", "2"));
		isFalse(U.eq("a", "b"));
		isFalse(U.eq('a', 'b'));

		isFalse(U.eq(null, 'b'));
		isFalse(U.eq('a', null));
		isTrue(U.eq(null, null));
	}

	@Test
	public void testSubarray() {
		String[] arr = new String[] { "aa", "bb", "c", "ddd", "e" };

		String[] subarr = U.subarray(arr, 0, 2);
		eq(subarr, new String[] { "aa", "bb", "c" });

		subarr = U.subarray(arr, 2, 4);
		eq(subarr, new String[] { "c", "ddd", "e" });

		subarr = U.subarray(arr, 0, 4);
		eq(subarr, new String[] { "aa", "bb", "c", "ddd", "e" });

		subarr = U.subarray(arr, 3, 3);
		eq(subarr, new String[] { "ddd" });

		subarr = U.subarray(arr, 1, 3);
		eq(subarr, new String[] { "bb", "c", "ddd" });
	}

	@Test(expectedExceptions = { RuntimeException.class })
	public void testSubarrayException() {
		U.subarray(new String[] { "aa", "bb", "c" }, 2, 1);
	}

	@Test
	public void testSet() {
		Set<Integer> set = U.set(1, 3, 5, 8);

		eq((set.size()), 4);

		isTrue(set.contains(1));
		isTrue(set.contains(3));
		isTrue(set.contains(5));
		isTrue(set.contains(8));
	}

	@Test
	public void testList() {
		List<String> list = U.list("m", "k", "l");

		eq((list.size()), 3);

		eq((list.get(0)), "m");
		eq((list.get(1)), "k");
		eq((list.get(2)), "l");
	}

	@Test
	public void testMap() {
		Map<String, Integer> map = U.map();

		isTrue((map.isEmpty()));
	}

	@Test
	public void testMapKV() {
		Map<String, Integer> map = U.map("a", 1);

		eq((map.size()), 1);

		eq((map.get("a").intValue()), 1);
	}

	@Test
	public void testMapKVKV() {
		Map<String, Integer> map = U.map("a", 1, "b", 2);

		eq((map.size()), 2);

		eq((map.get("a").intValue()), 1);
		eq((map.get("b").intValue()), 2);
	}

	@Test
	public void testMapKVKVKV() {
		Map<String, Integer> map = U.map("a", 1, "b", 2, "c", 3);

		eq((map.size()), 3);

		eq((map.get("a").intValue()), 1);
		eq((map.get("b").intValue()), 2);
		eq((map.get("c").intValue()), 3);
	}

	@Test
	public void testMapKVKVKVKV() {
		Map<String, Integer> map = U.map("a", 1, "b", 2, "c", 3, "d", 4);

		eq((map.size()), 4);

		eq((map.get("a").intValue()), 1);
		eq((map.get("b").intValue()), 2);
		eq((map.get("c").intValue()), 3);
		eq((map.get("d").intValue()), 4);
	}

	@Test
	public void testIsEmpty() {
		eq(U.isEmpty(""), true);
		eq(U.isEmpty("a"), false);
		eq(U.isEmpty(null), true);
	}

	@Test
	public void testExclude() {
		String[] arr = { "a", "b", "c" };
		eq(U.exclude(arr, "a"), U.array("b", "c"));
		eq(U.exclude(arr, "b"), U.array("a", "c"));
		eq(U.exclude(arr, "c"), U.array("a", "b"));
	}

	@Test
	public void testInclude() {
		String[] arr = { "a", "b", "c" };
		eq(U.include(arr, "a"), U.array("a", "b", "c"));
		eq(U.include(arr, "d"), U.array("a", "b", "c", "d"));
	}

}
