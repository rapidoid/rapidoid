package org.rapidoid.docs.eg023;

import javax.persistence.Entity;

import org.rapidoid.annotation.App;
import org.rapidoid.annotation.Screen;
import org.rapidoid.app.GUI;
import org.rapidoid.jpa.JPAEntity;
import org.rapidoid.plugins.DB;
import org.rapidoid.quick.Quick;

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

// Basic database CRUD operations :: Database CRUD is easy with the DB API: 

@App
public class Main {
	String title = "DB API";
	String theme = "4";

	public static void main(String[] args) {
		Quick.run(args);
	}
}

@Screen
class HomeScreen extends GUI {
	Object[] content = { grid(Todo.class), cmd("Add") };

	public void onAdd() {
		Todo todo = new Todo();
		todo.content = "Learn Rapidoid!";
		String id = DB.insert(todo); // here
		Todo todo2 = DB.get(Todo.class, id); // here
		todo2.content += " :)";
		DB.update(todo2); // here
	}
}

@Entity
class Todo extends JPAEntity {
	String content;
}
