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

import org.rapidoid.pages.Var;
import org.rapidoid.pages.PagesTestCommons;
import org.rapidoid.pages.bootstrap.PagerWidget;
import org.rapidoid.pages.html.Tags;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class PagerWidgetTest extends PagesTestCommons {

	@Test
	public void testPagerWidget() {
		Tags tags = new Tags();

		Var<Integer> pageN = tags.var(3);
		PagerWidget pager = new PagerWidget(1, 7, pageN);
		U.print(pager);

		eq(pageN.get().intValue(), 3);
		has(pager, "(page 3)");

		pager.emit("click", "_1");

		eq(pageN, 1);
		has(pager, "(page 1)");

		pager.emit("click", "_4");

		eq(pageN, 7);
		has(pager, "(page 7)");

		pager.emit("click", "_2");
		pager.emit("click", "_2");

		eq(pageN, 5);
		has(pager, "(page 5)");

		pager.emit("click", "_3");

		eq(pageN, 6);
		has(pager, "(page 6)");
	}

}
