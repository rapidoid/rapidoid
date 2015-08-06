package demo.taskplanner.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Web;
import org.rapidoid.app.GUI;
import org.rapidoid.util.U;
import org.rapidoid.widget.LayoutWidget;

/*
 * #%L
 * rapidoid-demo
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

@Web
@Authors("Nikolche Mihajlovski")
@Since("2.4.0")
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
		return arr(cmd("abc").warning(), lay, DEBUG);
	}

}
