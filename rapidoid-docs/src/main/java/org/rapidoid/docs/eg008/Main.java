package org.rapidoid.docs.eg008;

import static org.rapidoid.widget.BootstrapWidgets.BACK;
import static org.rapidoid.widget.BootstrapWidgets.show;

import javax.persistence.Entity;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Session;
import org.rapidoid.jpa.JPAEntity;
import org.rapidoid.plugins.DB;
import org.rapidoid.quick.Quick;
import org.rapidoid.rql.RQL;
import org.rapidoid.widget.FormWidget;

/*
 * #%L
 * rapidoid-docs
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

// Using the form widget ("view" mode) :: Creating form for displaying entity

@App
public class Main {
	String title = "Show movie demo";
	String theme = "3";

	public static void main(String[] args) {
		Quick.run(args);
	}

	public void init() {
		RQL.run("INSERT Movie title=Rambo, year=1985"); // here
	}
}

class HomeScreen {
	@Session
	Movie movie;

	Object content() {
		movie = DB.get(Movie.class, "1"); // here
		FormWidget f = show(movie); // here
		f = f.buttons(BACK); // here
		return f;
	}
}

@Entity
class Movie extends JPAEntity {
	String title;
	int year;
}
