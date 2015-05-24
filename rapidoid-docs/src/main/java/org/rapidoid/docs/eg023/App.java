package org.rapidoid.docs.eg023;

import org.rapidoid.annotation.Scaffold;
import org.rapidoid.app.Apps;
import org.rapidoid.app.Screen;
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

// Basic database CRUD operations :: Database CRUD is easy with the DB API: 

public class App {
	String title = "DB API";
	String theme = "4";

	public static void main(String[] args) {
		Apps.run(args);
	}
}

class HomeScreen extends Screen {
	Object[] content = { grid(Todo.class), cmd("Add") };

	public void onAdd() {
		Todo todo = new Todo();
		todo.content = "Learn Rapidoid!";
		long id = XDB.insert(todo); // here
		Todo todo2 = XDB.get(id); // here
		todo2.content += " :)";
		XDB.update(todo2); // here
	}
}

@Scaffold
class Todo {
	String content;
}
