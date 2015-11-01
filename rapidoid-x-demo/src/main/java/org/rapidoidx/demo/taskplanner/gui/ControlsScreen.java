package org.rapidoidx.demo.taskplanner.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.GUI;
import org.rapidoid.gui.LayoutWidget;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class ControlsScreen extends GUI {

	public Object content() {

		Object a1 = email("em1");
		Object a2 = password("pass1");
		Object a3 = txt("name1", "Joe");
		Object a4 = txtbig("desc1", "Some text");
		Object a5 = checkbox("ch1", true);

		Object a6 = dropdown(U.list("A", "B"), "v0", "B");
		Object a7 = multiSelect(U.list("A", "B", "C"), "v1", U.list("A", "C"));
		Object a8 = radios(U.list("A", "B"), "v2", "A");
		Object a9 = checkboxes(U.list("A", "B", "C"), "v3", U.list("A", "C"));

		LayoutWidget lay = layout(a1, a2, a3, a4, a5, a6, a7, a8, a9).cols(3);
		return arr(cmd("abc").warning(), lay, debug());
	}

}
