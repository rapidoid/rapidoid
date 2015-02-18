package org.rapidoid.docs.eg10;

import static org.rapidoid.app.AppGUI.*;

import org.rapidoid.annotation.Scaffold;
import org.rapidoid.app.Apps;
import org.rapidoid.db.DB;
import org.rapidoid.db.Entity;

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

// Grid widget automatically loading data from DB

public class App {
	String title = "Grid Widget";
	String theme = "4";

	public static void main(String[] args) {
		Apps.run(args);
	}

	public void init() {
		DB.prefill("movie title=Rambo, year=1985"); // here
		DB.prefill("movie title=Her, year=2013"); // here
		DB.prefill("movie title=Batman, year=1989"); // here
	}

	Object content() {
		return grid(Movie.class); // here
	}
}

@Scaffold
class Movie extends Entity {
	String title;
	int year;
}
