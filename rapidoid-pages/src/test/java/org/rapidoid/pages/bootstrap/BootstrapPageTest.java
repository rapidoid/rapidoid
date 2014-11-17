package org.rapidoid.pages.bootstrap;

/*
 * #%L
 * rapidoid-pages
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

import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pages.BootstrapWidgets;
import org.rapidoid.pages.PagesTestCommons;
import org.testng.annotations.Test;

@SuppressWarnings("unused")
public class BootstrapPageTest extends PagesTestCommons {

	@Test
	public void testPojoPage() {
		Object page = new Object() {

			public Tag<?> content(HttpExchange x) {
				return BootstrapWidgets.body(BootstrapWidgets.div("abc"));
			}

			public String title() {
				return "Some title";
			}

		};

		TagContext ctx = Tags.context();
		print(ctx, page);

		has(ctx, page, "<title>Some title</title>");
		hasRegex(ctx, page, "<div[^>]*?>abc</div>");
	}

	@Test
	public void testPojoPage2() {
		Object page = new Object() {

			public Tag<?> content = BootstrapWidgets.body(BootstrapWidgets.div("abc"));

			public String title = "Some title";

		};

		TagContext ctx = Tags.context();
		print(ctx, page);

		has(ctx, page, "<title>Some title</title>");
		hasRegex(ctx, page, "<div[^>]*?>abc</div>");
	}

}
