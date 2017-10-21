package org.rapidoid.commons;

/*
 * #%L
 * rapidoid-commons
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
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;

import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.0.4")
public class JSTest extends AbstractCommonsTest {

	@Test
	public void testEvalJS() throws ScriptException {
		eq(((Number) JS.eval("1 + 2")).intValue(), 3);
		eq(JS.eval("1 + 'ab'"), "1ab");
		eq(JS.eval("(function (x) { return x.toUpperCase(); })('abc')"), "ABC");
		eq(JS.eval("x + y + y.length", U.map("x", "10", "y", "abcd")), "10abcd4");
	}

	@Test
	public void testCompileJS() throws ScriptException {
		eq(((Number) JS.compile("1 + 2").eval()).intValue(), 3);
		eq(JS.compile("1 + 'ab'").eval(), "1ab");

		Map<String, Object> map = U.cast(U.map("U", new U()));
		SimpleBindings bindings = new SimpleBindings(map);

		Object res1;
		try {
			// Rhino style
			res1 = JS.compile("(function (x) { return U.str(x); })('hey')").eval(bindings);
		} catch (Exception e) {
			// Nashorn style
			res1 = JS.compile("(function (x) { return U.class.static.str(x); })('hey')").eval(bindings);
		}

		eq(res1, "hey");
	}

}
