package org.rapidoid.docs.eg020;

import static org.rapidoid.html.HTML.*;
import static org.rapidoid.widget.BootstrapWidgets.*;

import org.rapidoid.annotation.Session;
import org.rapidoid.app.Apps;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ButtonTag;

/*
 * #%L
 * rapidoid-docs
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

// Constructing GUI and handling events :: Constructing buttons, handling clicks 

public class App {
	String title = "Numbers";
	String theme = "3";

	public static void main(String[] args) {
		Apps.run(args);
	}

	@Session
	String op = "";

	Object content() {
		Tag btns = div();
		Tag row = div();
		for (int i = 1; i <= 9; i++) {
			ButtonTag b = cmd("" + i); // here
			row = row.append(b); // here
			if (i % 3 == 0) {
				btns = btns.append(row); // here
				row = div();
			}
		}
		return row(btns, h4("You pressed: ", op));
	}

	public void on(String cmd, Object... args) { // here
		op += cmd;
	}
}