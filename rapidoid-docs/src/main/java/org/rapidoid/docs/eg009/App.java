package org.rapidoid.docs.eg009;

import static org.rapidoid.widget.BootstrapWidgets.btn;
import static org.rapidoid.widget.BootstrapWidgets.create;

import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Session;
import org.rapidoid.app.Apps;
import org.rapidoid.widget.ButtonWidget;
import org.rapidoid.widget.FormWidget;
import org.rapidoidx.db.XDB;

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

// Customizing form fields and buttons :: Customizing a form 

public class App {
	String title = "Custom form";
	String theme = "5";

	public static void main(String[] args) {
		Apps.run(args);
	}

	public void init() {
		XDB.init("movie title=Rambo, year=1985"); // here
	}
}

class HomeScreen {

	@Session
	Movie movie;

	Object content() {
		movie = XDB.get(1);
		FormWidget f = create(movie, "year"); // here
		ButtonWidget ab = btn("Ab"); // here
		ButtonWidget cd = btn("Change year").command("ch").primary(); // here
		ButtonWidget efg = btn("!Efg").danger(); // here
		f = f.buttons(ab, cd, efg); // here
		return f;
	}

	public void onCh() {
		XDB.update(movie);
	}
}

@Scaffold
class Movie {
	String title;
	int year;
}
