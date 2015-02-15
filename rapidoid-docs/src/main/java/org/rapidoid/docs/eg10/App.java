package org.rapidoid.docs.eg10;

import org.rapidoid.annotation.Scaffold;
import org.rapidoid.app.Apps;
import org.rapidoid.app.Screen;
import org.rapidoid.db.DB;

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
		if (DB.size() == 0) {
			DB.insert(new Movie("Rambo", 1985));
			DB.insert(new Movie("Her", 2013));
			DB.insert(new Movie("Batman", 1989));
		}
		Apps.run(args);
	}
}

class HomeScreen extends Screen {
	Object content() {
		return grid(Movie.class, "year", 3);
	}
}

@Scaffold
class Movie {
	long id;
	String title;
	int year;

	public Movie() {}

	public Movie(String title, int year) {
		this.title = title;
		this.year = year;
	}
}
