package org.rapidoid.u;

/*
 * #%L
 * rapidoid-essentials
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
import org.rapidoid.test.TestCommons;

import java.util.*;

/**
 * @author Nikolche Mihajlovski
 * @author Marina Mihajlovska
 * @since 2.0.0
 */
public class UTest extends TestCommons {

	@Test
	public void testTextCollectionOfObject() {
		eq(U.str(new ArrayList<Integer>()), "[]");

		List<String> lst = new ArrayList<String>();

		lst.add("java");
		lst.add("c");
		lst.add("c++");

		eq(U.str(lst), "[java, c, c++]");
	}

	@Test
	public void testTextObject() {
		eq(U.str((Object) null), null);

		eq(U.str(123), "123");
		eq(U.str(1.23), "1.23");

		eq(U.str(true), "true");
		eq(U.str(false), "false");

		eq(U.str(""), "");
		eq(U.str("abc"), "abc");

		eq(U.str(new byte[]{-50, 0, 9}), "[-50, 0, 9]");
		eq(U.str(new short[]{-500, 0, 9}), "[-500, 0, 9]");
		eq(U.str(new int[]{300000000, 70, 100}), "[300000000, 70, 100]");
		eq(U.str(new long[]{3000000000000000000L, 1, -8900000000000000000L}),
			"[3000000000000000000, 1, -8900000000000000000]");

		eq(U.str(new float[]{-30.40000000f, -1.587f, 89.3f}), "[-30.4, -1.587, 89.3]");
		eq(U.str(new double[]{-9987.1, -1.5, 8.3}), "[-9987.1, -1.5, 8.3]");

		eq(U.str(new boolean[]{true}), "[true]");

		eq(U.str(new char[]{'k', 'o', 'h'}), "[k, o, h]");
		eq(U.str(new char[]{'-', '.', '+'}), "[-, ., +]");
	}

	@Test
	public void testTextObjectArray() {
		eq(U.str(new Object[]{}), "[]");
		eq(U.str(new Object[]{1, new boolean[]{true, false}, 3}), "[1, [true, false], 3]");
		eq(U.str(new Object[]{new double[]{-9987.1}, new char[]{'a', '.'}, new int[]{300, 70, 100}}),
			"[[-9987.1], [a, .], [300, 70, 100]]");

		eq(U.str(new int[][]{{1, 2}, {3, 4, 5}}), "[[1, 2], [3, 4, 5]]");

		eq(U.str(new String[][][]{{{"a"}, {"r"}}, {{"m"}}}), "[[[a], [r]], [[m]]]");
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
	public void testIsEmptyString() {
		eq(U.isEmpty(""), true);
		eq(U.isEmpty("a"), false);
		eq(U.isEmpty((String) null), true);
	}

	@Test
	public void testIsEmptyCollection() {
		eq(U.isEmpty(U.set()), true);
		eq(U.isEmpty(U.list()), true);
		eq(U.isEmpty((Collection<?>) null), true);
		eq(U.isEmpty(U.set(1)), false);
		eq(U.isEmpty(U.list("2")), false);
	}

	@Test
	public void testSafe() {
		eq(U.safe(3), 3);

		eq(U.safe((Long) null), 0L);
	}

	@Test
	public void testArray() {
		String[] arr = U.array("x", "y");
		eq(arr.getClass(), String[].class);
		eq(arr.length, 2);
		eq(arr[0], "x");
		eq(arr[1], "y");
	}

	@Test
	public void testArrayOf() {
		Number[] arr = U.arrayOf(Number.class, 123);
		eq(arr.getClass(), Number[].class);
		eq(arr.length, 1);
		eq(arr[0].intValue(), 123);
	}

	@Test
	public void testArrayOfType() {
		Number[] arr = U.arrayOf(Number.class, U.list(1, 2, 3));
		eq(arr.getClass(), Number[].class);
		eq(arr, U.array(1, 2, 3));
	}

}
