package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-gui
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

import org.junit.Before;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.reqinfo.MockReqInfo;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagWidget;
import org.rapidoid.test.TestCommons;
import org.rapidoid.var.Var;

import java.util.regex.Pattern;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class WidgetTestCommons extends TestCommons {

	@Before
	public void setup() {
		MockReqInfo.set("GET /");
	}

	protected void print(Object content) {
		System.out.println(content.toString());
	}

	protected void has(Object content, String... containingTexts) {
		String html = content.toString();
		notNull(html);

		for (String text : containingTexts) {
			isTrue(html.contains(text));
		}
	}

	protected void hasRegex(Object content, String... containingRegexes) {
		String html = content.toString();
		notNull(html);

		for (String regex : containingRegexes) {
			isTrue(Pattern.compile(regex).matcher(html).find());
		}
	}

	protected void eq(Var<?> var, Object value) {
		eq(var.get(), value);
	}

	protected void verifyGUI(String name, TagWidget widget) {
		verify(name, widget.toString());
	}

	protected void verifyGUI(String name, Tag tag) {
		verify(name, tag.toString());
	}

}
