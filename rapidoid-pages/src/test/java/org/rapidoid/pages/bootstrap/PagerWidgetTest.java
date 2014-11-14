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
import org.rapidoid.pages.HtmlWidget;
import org.rapidoid.pages.PagesTestCommons;
import org.rapidoid.reactive.Var;
import org.testng.annotations.Test;

public class PagerWidgetTest extends PagesTestCommons {

	@Test
	public void testPagerWidget() {

		TagContext ctx = Tags.context();
		Var<Integer> pageN = HtmlWidget.var(3);

		PagerWidget pager = new PagerWidget(1, 7, pageN);
		print(ctx, pager);

		eq(pageN.get().intValue(), 3);
		has(ctx, pager, "Page 3 of 7");

		ctx.emit(pager.content(), NO_CHANGES, 6, "click"); // first

		eq(pageN, 1);
		has(ctx, pager, "Page 1 of 7");

		ctx.emit(pager.content(), NO_CHANGES, 20, "click"); // last

		eq(pageN, 7);
		has(ctx, pager, "Page 7 of 7");

		ctx.emit(pager.content(), NO_CHANGES, 10, "click"); // prev
		ctx.emit(pager.content(), NO_CHANGES, 10, "click"); // prev

		eq(pageN, 5);
		has(ctx, pager, "Page 5 of 7");

		ctx.emit(pager.content(), NO_CHANGES, 16, "click"); // next

		eq(pageN, 6);
		has(ctx, pager, "Page 6 of 7");
	}

}
