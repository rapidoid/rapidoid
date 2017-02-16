package org.rapidoid.render;

/*
 * #%L
 * rapidoid-render
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
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RenderTest extends AbstractRenderTest {

	private final static Object[] VALUES = {"abc", 123, 3.14, true, false, U.list(), U.set(1, 2), U.map(1, "one", "two", 2)};

	private final static Object[] TRUE_VALUES = {"abc", 0, 123, 3.14, true, U.list("a", true, false), U.set(false, 2), U.map(1, "one", "two", 2)};

	private final static Object[] FALSE_VALUES = {"", null, false, U.set(), U.list(), U.map()};

	private static final String IF_TEMPLATE = "{{?x}}[${x}]{{/x}}";

	private static final String IF_NOT_TEMPLATE = "{{^x}}[${x}]{{/x}}";

	private static final String IF_NOT_TEMPLATE2 = "{{!x}}[${x}]{{/x}}";

	@Test
	public void testPrint() {
		for (Object x : VALUES) {
			eq(Render.template("${.}").model(x), "" + x);
		}
	}

	@Test
	public void testPrintUnescaped() {
		for (Object x : VALUES) {
			eq(Render.template("${.}").model(x), "" + x);
		}
	}

	@Test
	public void testIf() {
		for (Object x : TRUE_VALUES) {
			eq(Render.template(IF_TEMPLATE).model(U.map("x", x)), "[" + view(x) + "]");
		}

		for (Object x : FALSE_VALUES) {
			eq(Render.template(IF_TEMPLATE).model(U.map("x", x)), "");
		}
	}

	@Test
	public void testIfNot() {
		for (Object x : FALSE_VALUES) {
			eq(Render.template(IF_NOT_TEMPLATE).model(U.map("x", x)), "[" + view(x) + "]");
		}

		for (Object x : TRUE_VALUES) {
			eq(Render.template(IF_NOT_TEMPLATE).model(U.map("x", x)), "");
		}
	}

	@Test
	public void testIfNot2() {
		for (Object x : FALSE_VALUES) {
			eq(Render.template(IF_NOT_TEMPLATE2).model(U.map("x", x)), "[" + view(x) + "]");
		}

		for (Object x : TRUE_VALUES) {
			eq(Render.template(IF_NOT_TEMPLATE2).model(U.map("x", x)), "");
		}
	}

	@Test
	public void testIteration() {
		eq(Render.template("{{#.}}(${length}){{/.}}").model(U.list("aaa", "bb")), "(3)(2)");
	}

	@Test
	public void testScopes() {
		eq(Render.template("${x}").multiModel(U.map("x", 1), U.map("x", 2)), "2");

		eq(Render.template("${x}").multiModel(U.map("x", 1), U.map("y", 2)), "1");
		eq(Render.template("${y}").multiModel(U.map("x", 1), U.map("y", 2)), "2");
		eq(Render.template("${z}").multiModel(U.map("x", 1), U.map("y", 2)), view(null));
	}

	@Test
	public void testEscaping() {
		String s = "a\r\n\tb";
		String t = "<p id=\"foo\"><b ng-click=\"f(1, 'x', 2);\">abc</b></p>";
		String t2 = "&lt;p id=&quot;foo&quot;&gt;&lt;b ng-click=&quot;f(1, &apos;x&apos;, 2);&quot;&gt;abc&lt;/b&gt;&lt;/p&gt;";

		eq(Render.template("${.}").model(s), s);
		eq(Render.template("@{.}").model(s), s);

		eq(Render.template("${.}").model(t), t2);
		eq(Render.template("@{.}").model(t), t);
	}

	@Test
	public void testUTF8() {
		eq(Render.template("Штрк-X-123").model(U.map()), "Штрк-X-123");
		eq(Render.template("абвгд-${x}-foo").model(U.map("x", "чачача")), "абвгд-чачача-foo");

		// 2-byte UTF-8 encoding
		eq(Render.template("${x}:ш:${x}").model(U.map("x", "ч")), "ч:ш:ч");

		// 3-byte UTF-8 encoding
		eq(Render.template("${y}:€€:${y}").model(U.map("y", "€")), "€:€€:€");
	}

	@Test
	public void testUTF8Surrogates() {
		String a = "\uD852\uDF62";
		String b = "\uD852\uDF63";

		eq(a.length(), 2);
		eq(b.length(), 2);

		// 4-byte (surrogate) UTF-8 encoding
		eq(Render.template("${x}:" + a + ":${x}").model(U.map("x", b)), b + ":" + a + ":" + b);

		// mix of 2-byte, 3-byte and 4-byte (surrogate) UTF-8 encoding
		eq(Render.template("шш€f€€\uD852\uDF62:${x}").model(U.map("x", "4€шf3\uD852\uDF63")), "шш€f€€\uD852\uDF62:4€шf3\uD852\uDF63");
	}

	@Test
	public void testUTF8MalformedSurrogates() {
		String missing = "\uD852"; // [high]
		eq(Render.template("${x}:" + missing + ":${x}").model(U.map("x", missing)), "?:?:?");

		String wrongOrder = "\uDF62\uD852"; // [low, high]
		eq(Render.template("${x}:" + wrongOrder + ":${x}").model(U.map("x", wrongOrder)), "??:??:??");
	}

	private String view(Object x) {
		return U.or(x, "N/A").toString();
	}

}
