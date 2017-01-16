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
import org.rapidoid.gui.Grid;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class GridWidgetTest extends WidgetTestCommons {

	@Test
	public void testTableWidget() {
		Person john = new Person("John", 20);
		john.id = 1;

		Person rambo = new Person("Rambo", 50);
		rambo.id = 2;

		Grid table = GUI.grid(U.list(john, rambo)).pageSize(10);
		verifyGUI("persons-grid", table);

		verifyGUI("map-grid", GUI.grid(U.map("name", "Foo", "year", "2016")));
	}

}
