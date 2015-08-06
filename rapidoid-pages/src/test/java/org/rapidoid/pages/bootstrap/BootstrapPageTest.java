package org.rapidoid.pages.bootstrap;

/*
 * #%L
 * rapidoid-pages
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.html.Tag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.pages.PagesTestCommons;
import org.rapidoid.webapp.AppCtx;
import org.rapidoid.widget.BootstrapWidgets;

@SuppressWarnings("unused")
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class BootstrapPageTest extends PagesTestCommons {

	public void confSetup() {
		Conf.set("title", "Some title");
	}

	@Test
	public void testPojoPage() {
		AppCtx.app().setTitle("Some title");

		Object page = new Object() {

			public Tag content(HttpExchange x) {
				return BootstrapWidgets.div("abc");
			}
		};

		print(page);

		has(page, "<title>Some title</title>");
		hasRegex(page, "<div[^>]*?>abc</div>");
	}

	@Test
	public void testPojoPage2() {
		AppCtx.app().setTitle("Some title");

		Object page = new Object() {

			public Tag content = BootstrapWidgets.div("abc");

		};

		print(page);

		has(page, "<title>Some title</title>");
		hasRegex(page, "<div[^>]*?>abc</div>");
	}

}
