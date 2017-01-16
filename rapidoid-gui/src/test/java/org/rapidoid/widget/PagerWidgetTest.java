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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Pager;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class PagerWidgetTest extends WidgetTestCommons {

	@Test
	public void testPager() {
		Pager p1 = GUI.pager("x").min(5).max(10);
		verify("5-to-10", p1.toString());

		Pager p2 = GUI.pager("x").min(5).max(10).initial(7);
		verify("5-to-10-init-7", p2.toString());

		Pager p3 = GUI.pager("x").min(1).max(3).right(true);
		verify("1-to-3-right", p3.toString());
	}

}
