package org.rapidoid.docs.eg006;

import static org.rapidoid.widget.BootstrapWidgets.CANCEL;
import static org.rapidoid.widget.BootstrapWidgets.SAVE;
import static org.rapidoid.widget.BootstrapWidgets.create;

import javax.persistence.Entity;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Session;
import org.rapidoid.jpa.JPAEntity;
import org.rapidoid.plugins.DB;
import org.rapidoid.quick.Quick;
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

// Using the form widget ("create" mode) :: Creating form for new entity 

@App
public class Main {
	String title = "New movie demo";
	String theme = "1";

	public static void main(String[] args) {
		Quick.run(args);
	}
}

class HomeScreen {
	@Session
	Movie movie = new Movie();

	Object content() {
		FormWidget f = create(movie); // here
		f = f.buttons(SAVE, CANCEL); // here
		return f;
	}

	public void onSave() {
		DB.insert(movie);
		movie = new Movie();
	}

	public void onCancel() {
		movie = new Movie();
	}
}

@Entity
class Movie extends JPAEntity {
	String title;
	int year;
}
