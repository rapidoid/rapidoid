package org.rapidoid.html;

/*
 * #%L
 * rapidoid-html
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
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.0.10")
public class HtmlTagsTest extends TestCommons {

	@Test
	public void shouldRenderToHTML() {
		Tag div = HTML.div("a");
		eq(div.toString(), "<div>a</div>");
	}

	@Test
	public void shouldBehaveLikeNormalObject() {
		Tag div = HTML.div("a");
		isFalse(div.equals("asd"));
		isTrue(div.equals(div));
		isFalse(div.equals(null));
		eq(div.hashCode(), div.hashCode());

		eq(U.list(div), U.list(div));
		eq(U.map("x", div), U.map("x", div));
		eq(U.map(div, "y"), U.map(div, "y"));
	}

	@Test
	public void testHTMLEscape() {
		String esc = HTML.escape("<aa> b=\"123\" c->d; a & b && c &nbsp;");
		eq(esc, "&lt;aa&gt; b=&quot;123&quot; c-&gt;d; a &amp; b &amp;&amp; c &amp;nbsp;");
	}

}
