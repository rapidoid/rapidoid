package org.rapidoid.app.example;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import static org.rapidoid.html.BasicUtils.*;
import static org.rapidoid.html.Bootstrap.*;
import static org.rapidoid.html.HTML.*;
import static org.rapidoid.pages.BootstrapWidgets.*;

import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.H1Tag;
import org.rapidoid.model.Items;
import org.rapidoid.model.Model;

public class TasksScreen {

	public String title = "My Tasks";

	public Object content() {

		Items items = Model.mockBeanItems(20, Task.class);

		H1Tag caption = h1("Manage tasks");

		Tag<?> table = grid(items, 10);

		return arr(caption, rowFull(table));
	}

}
