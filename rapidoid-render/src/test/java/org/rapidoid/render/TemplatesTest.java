package org.rapidoid.render;

/*
 * #%L
 * rapidoid-render
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class TemplatesTest extends AbstractRenderTest {

	@Test
	public void testPatterns() {
		isTrue(Pattern.matches(TemplateParser.TAG, "<x-i-fd href=\"aa>dd\" b=\"dd\">"));
		isTrue(Pattern.matches(TemplateParser.TAG, "<x-i-fd href=\"aa>dd\" \n  b=\"dd\">"));
		isFalse(Pattern.matches(TemplateParser.TAG, "<x-i- href=\"aa>dd\" \n  b=\"dd\">"));
		isFalse(Pattern.matches(TemplateParser.TAG, "<-ifd href=\"aa>dd\" \n  b=\"dd\">"));

		eq(TemplateToCode.literal("a\"b"), "\"a\\\"b\"");
	}

	@Test
	public void testFileTemplatesAPI() {
		verify("tmpl", Templates.fromFile("tmpl.html").render(tmplModel()));
	}

	private Map<String, List<String>> tmplModel() {
		return U.map("items", U.list("a", "b", "c"), "categories", U.list("comedy", "drama"));
	}

	@Test
	public void testStringTemplatesAPI() {
		eq(Templates.fromString("${x}-${y}").render(U.map("x", 1), U.map("y", "2")), "1-2");
	}

	@Test
	public void testRender() {
		eq(Render.template("${.}").model(123), "123");
	}

}
