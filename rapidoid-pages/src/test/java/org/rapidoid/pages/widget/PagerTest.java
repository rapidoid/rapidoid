package org.rapidoid.pages.widget;

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
import org.rapidoid.pages.html.Tags;
import org.rapidoid.pages.widget.Pager;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class PagerTest extends TestCommons {

	@Test
	public void testPagerButtons() {
		Tags tags = new Tags();

		Var<Integer> pageN = tags.var(3);
		Pager pager = new Pager(1, 7, pageN);
		U.print(pager);

		eq(pageN.get().intValue(), 3);
		isTrue(pager.toString().contains("(page 3)"));

		pager.emit("click", "_1");
		eq(pageN.get().intValue(), 1);
		isTrue(pager.toString().contains("(page 1)"));

		pager.emit("click", "_4");
		eq(pageN.get().intValue(), 7);
		isTrue(pager.toString().contains("(page 7)"));

		pager.emit("click", "_2");
		pager.emit("click", "_2");
		eq(pageN.get().intValue(), 5);
		isTrue(pager.toString().contains("(page 5)"));

		pager.emit("click", "_3");
		eq(pageN.get().intValue(), 6);
		isTrue(pager.toString().contains("(page 6)"));
	}

}
