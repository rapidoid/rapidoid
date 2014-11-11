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

import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.html.Var;
import org.rapidoid.pages.HtmlWidget;
import org.rapidoid.pages.PagesTestCommons;
import org.testng.annotations.Test;

public class PagerWidgetTest extends PagesTestCommons {

	@Test
	public void testPagerWidget() {

		TagContext ctx = Tags.context();
		Var<Integer> pageN = HtmlWidget.var(3);

		PagerWidget pager = new PagerWidget(1, 7, pageN);
		print(ctx, pager);

		eq(pageN.get().intValue(), 3);
		has(ctx, pager, "(page 3)");

		ctx.emit("_2", "click"); // first

		eq(pageN, 1);
		has(ctx, pager, "(page 1)");

		ctx.emit("_6", "click"); // last

		eq(pageN, 7);
		has(ctx, pager, "(page 7)");

		ctx.emit("_3", "click"); // prev
		ctx.emit("_3", "click"); // prev

		eq(pageN, 5);
		has(ctx, pager, "(page 5)");

		ctx.emit("_5", "click"); // next

		eq(pageN, 6);
		has(ctx, pager, "(page 6)");
	}

}
