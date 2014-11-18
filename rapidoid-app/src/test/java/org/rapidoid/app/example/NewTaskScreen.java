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

import static org.rapidoid.html.Bootstrap.*;
import static org.rapidoid.html.HTML.*;
import static org.rapidoid.pages.BootstrapWidgets.*;

import org.rapidoid.annotation.Session;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.DivTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.H1Tag;
import org.rapidoid.model.Item;
import org.rapidoid.model.Model;

public class NewTaskScreen {

	@Session
	private User user = new User();

	@Session
	private Task task = new Task("aa", Priority.MEDIUM, user);

	public Object content() {

		H1Tag caption = h1("Create a new task");

		DivTag caption2 = rowFull(h4("Edit user:"));

		Tag<?>[] buttons = { cmd("Save"), cmd("Cancel") };

		Item item = Model.item(task);
		FormTag frm1 = form_(item, buttons);

		Item item2 = Model.item(user);
		FormTag frm2 = form_(item2, buttons);

		return row(col2(), col8(caption, frm1, caption2, frm2), col2());
	}

}
