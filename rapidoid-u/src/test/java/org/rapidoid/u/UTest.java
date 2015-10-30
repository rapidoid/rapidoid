package org.rapidoid.u;

/*
 * #%L
 * rapidoid-u
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.junit.Test;
import org.rapidoid.lambda.Dynamic;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

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
		eq(U.str((Object) null), "null");

		eq(U.str(123), "123");
		eq(U.str(1.23), "1.23");

		eq(U.str(true), "true");
		eq(U.str(false), "false");

		eq(U.str(""), "");
		eq(U.str("abc"), "abc");

		eq(U.str(new byte[] { -50, 0, 9 }), "[-50, 0, 9]");
		eq(U.str(new short[] { -500, 0, 9 }), "[-500, 0, 9]");
		eq(U.str(new int[] { 300000000, 70, 100 }), "[300000000, 70, 100]");
		eq(U.str(new long[] { 3000000000000000000L, 1, -8900000000000000000L }),
				"[3000000000000000000, 1, -8900000000000000000]");

		eq(U.str(new float[] { -30.40000000f, -1.587f, 89.3f }), "[-30.4, -1.587, 89.3]");
		eq(U.str(new double[] { -9987.1, -1.5, 8.3 }), "[-9987.1, -1.5, 8.3]");

		eq(U.str(new boolean[] { true }), "[true]");

		eq(U.str(new char[] { 'k', 'o', 'h' }), "[k, o, h]");
		eq(U.str(new char[] { '-', '.', '+' }), "[-, ., +]");
	}

	@Test
	public void testTextObjectArray() {
		eq(U.str(new Object[] {}), "[]");
		eq(U.str(new Object[] { 1, new boolean[] { true, false }, 3 }), "[1, [true, false], 3]");
		eq(U.str(new Object[] { new double[] { -9987.1 }, new char[] { 'a', '.' }, new int[] { 300, 70, 100 } }),
				"[[-9987.1], [a, .], [300, 70, 100]]");

		eq(U.str(new int[][] { { 1, 2 }, { 3, 4, 5 } }), "[[1, 2], [3, 4, 5]]");

		eq(U.str(new String[][][] { { { "a" }, { "r" } }, { { "m" } } }), "[[[a], [r]], [[m]]]");
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
	public void testInsert() {
		eq(U.insert("", 0, "ab"), "ab");
		eq(U.insert("a", 0, "b"), "ba");
		eq(U.insert("a", 1, "b"), "ab");
		eq(U.insert("abc", 2, "123"), "ab123c");
	}

	@Test
	public void testTriml() {
		eq(U.triml("/abc/", "/"), "abc/");
		eq(U.triml("/abc/", "/a"), "bc/");
		eq(U.triml("/abc/", "/abc/"), "");
		eq(U.triml(".abc.", '.'), "abc.");
		eq(U.triml("/abc/", '.'), "/abc/");
	}

	@Test
	public void testTrimr() {
		eq(U.trimr("/abc/", "/"), "/abc");
		eq(U.trimr("/abc/", "c/"), "/ab");
		eq(U.trimr("/abc/", "/abc/"), "");
		eq(U.trimr(".abc.", '.'), ".abc");
		eq(U.trimr("/abc/", '.'), "/abc/");
	}

	@Test
	public void testDynamic() {
		Dynamic dynamic = new Dynamic() {
			@Override
			public Object call(Method m, Object[] args) {
				return m.getName() + ":" + U.join(",", args);
			}
		};

		EgInterface dyn = U.dynamic(EgInterface.class, dynamic);
		EgInterface dyn2 = U.dynamic(EgInterface.class, dynamic);

		isTrue(dyn != dyn2);

		isTrue(dyn instanceof Proxy);
		isTrue(dyn2 instanceof Proxy);

		isTrue(dyn instanceof EgInterface);
		isTrue(dyn2 instanceof EgInterface);

		isTrue(dyn.toString().startsWith("EgInterface@"));
		isTrue(dyn2.toString().startsWith("EgInterface@"));

		neq(dyn.toString(), dyn2.toString());

		eq(dyn.hey(), "hey:");
		eq(dyn.abc(123, true), "abc:123,true");
		eq(dyn2.abc(4, false), "abc:4,false");
	}

	@Test
	public void testEvalJS() throws ScriptException {
		eq(U.evalJS("1 + 2"), 3);
		eq(U.evalJS("1 + 'ab'"), "1ab");
		eq(U.evalJS("(function (x) { return x.toUpperCase(); })('abc')"), "ABC");
		eq(U.evalJS("x + y + y.length", U.map("x", "10", "y", "abcd")), "10abcd4");
	}

	@Test
	public void testCompileJS() throws ScriptException {
		eq(U.compileJS("1 + 2").eval(), 3);
		eq(U.compileJS("1 + 'ab'").eval(), "1ab");

		Map<String, Object> map = U.cast(U.map("U", new U()));
		SimpleBindings bindings = new SimpleBindings(map);

		Object res1;
		try {
			// Rhino style
			res1 = U.compileJS("(function (x) { return U.capitalized(x); })('hey')").eval(bindings);
		} catch (Exception e) {
			// Nashorn style
			res1 = U.compileJS("(function (x) { return U.class.static.capitalized(x); })('hey')").eval(bindings);
		}

		eq(res1, "Hey");
	}

	@Test
	public void testIsIn() {
		isTrue(U.is(null).in(null, ""));
		isTrue(U.is(null).in("x", null));
		isFalse(U.is(null).in("x", 123));

		isFalse(U.is("a").in("x", "sd"));
		isTrue(U.is("a").in("a", "b"));
		isTrue(U.is("a").in("x", "a"));
	}

	@Test
	public void testSafe() {
		eq(U.safe(3), 3);

		eq(U.safe((Long) null), 0L);
	}

	@Test
	public void testExists() {
		isFalse(U.exists(null));

		isFalse(U.exists(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return null;
			}
		}));

		isFalse(U.exists(new Callable<Object>() {
			@SuppressWarnings("null")
			@Override
			public Object call() throws Exception {
				String s = null;
				return s.length(); // throws NPE!
			}
		}));

		isTrue(U.exists(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				String s = "abc";
				return s.length();
			}
		}));
	}

	@Test
	public void testUri() {
		eq(U.uri(""), "/");
		eq(U.uri("", "a"), "/a");
		eq(U.uri("b", ""), "/b");
		eq(U.uri("/", "x"), "/x");
		eq(U.uri("/", "/x"), "/x");
		eq(U.uri("/ab\\", "cd\\"), "/ab/cd");
		eq(U.uri("/ab", "/cd/"), "/ab/cd");
		eq(U.uri("/ab/", "/cd/"), "/ab/cd");
		eq(U.uri("x", "123", "w"), "/x/123/w");
	}

	@Test
	public void testPath() {
		eq(U.path(""), "");
		eq(U.path("", "a"), "a");
		eq(U.path("b", ""), "b");
		eq(U.path("/", "x"), "/x");

		String abcd = "/ab" + File.separator + "cd";
		eq(U.path("/ab\\", "cd\\"), abcd);
		eq(U.path("/ab/", "cd"), abcd);
	}

}
