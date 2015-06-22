package org.rapidoid.docs.eg005;

import static org.rapidoid.app.AppGUI.grid;

import javax.persistence.Entity;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.jpa.JPAEntity;
import org.rapidoid.quick.Quick;
import org.rapidoid.rql.RQL;

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

// Using the grid widget :: Grid widget automatically loading data from DB

@App
public class Main {
	String title = "Grid Widget";
	String theme = "4";

	public static void main(String[] args) {
		Quick.run(args);
	}

	public void init() {
		RQL.run("INSERT Movie title=Rambo, year=1985"); // here
		RQL.run("INSERT Movie title=Her, year=2013"); // here
		RQL.run("INSERT Movie title=Batman, year=1989"); // here
	}

	Object content() {
		return grid(Movie.class).orderBy("year"); // here
	}
}

@Scaffold
@Entity
class Movie extends JPAEntity {
	String title;
	int year;
}
