package org.rapidoid.docs.eg008;

import static org.rapidoid.app.AppGUI.*;
import static org.rapidoid.widget.BootstrapWidgets.*;

import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Session;
import org.rapidoid.app.Apps;
import org.rapidoid.db.DB;
import org.rapidoid.db.Entity;
import org.rapidoid.widget.FormWidget;

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

// Using the form widget ("view" mode) :: Creating form for displaying entity

public class App {
	String title = "Show movie demo";
	String theme = "3";

	public static void main(String[] args) {
		Apps.run(args);
	}

	public void init() {
		DB.init("movie title=Rambo, year=1985"); // here
	}
}

class HomeScreen {
	@Session
	Movie movie;

	Object content() {
		movie = DB.get(1); // here
		FormWidget f = show(movie); // here
		f = f.buttons(BACK); // here
		return f;
	}
}

@Scaffold
class Movie extends Entity {
	String title;
	int year;
}
