package org.rapidoid.widget;

/*
 * #%L
 * rapidoid-gui
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.Pager;
import org.rapidoid.gui.base.BootstrapWidgets;
import org.rapidoid.var.Var;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class PagerWidgetTest extends WidgetTestCommons {

	// @Test
	public void testPagerWidget() {

		// FIXME: find the event numbers to be able to emit events

		Var<Integer> pageN = BootstrapWidgets.var("page", 3);

		Pager pager = BootstrapWidgets.pager(1, 7, pageN);
		print(pager);

		eq(pageN.get().intValue(), 3);
		has(pager, "Page 3 of 7");

		// ctx.getEventCmd(6); // first

		eq(pageN, 1);
		has(pager, "Page 1 of 7");

		// ctx.getEventCmd(20); // last

		eq(pageN, 7);
		has(pager, "Page 7 of 7");

		// ctx.getEventCmd(10); // prev
		// ctx.getEventCmd(10); // prev

		eq(pageN, 5);
		has(pager, "Page 5 of 7");

		// ctx.getEventCmd(16); // next

		eq(pageN, 6);
		has(pager, "Page 6 of 7");
	}

}
