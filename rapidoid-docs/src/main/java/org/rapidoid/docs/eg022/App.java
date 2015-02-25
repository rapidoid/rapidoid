package org.rapidoid.docs.eg022;

import org.rapidoid.annotation.Session;
import org.rapidoid.app.Apps;
import org.rapidoid.app.Screen;
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

// Basic event handling and session storage :: Increase n when the "+" button is clicked: 

public class App {
	String title = "Clicky";
	String theme = "3";

	public static void main(String[] args) {
		Apps.run(args);
	}
}

class HomeScreen extends Screen {
	@Session // here
	public int n = 0; // here

	public Object content() {
		Tag caption = h3(n, " clicks");
		ButtonTag hi = btn("+").cmd("Inc"); // here
		return row(caption, hi);
	}

	public void onInc() { // here
		n++;
	}
}
