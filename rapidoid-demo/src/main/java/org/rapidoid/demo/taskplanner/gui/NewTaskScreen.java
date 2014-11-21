package org.rapidoid.demo.taskplanner.gui;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.annotation.Session;
import org.rapidoid.db.DB;
import org.rapidoid.demo.taskplanner.model.Priority;
import org.rapidoid.demo.taskplanner.model.Task;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.H1Tag;
import org.rapidoid.model.Item;
import org.rapidoid.model.Model;

public class NewTaskScreen extends GUI {

	@Session
	private Item task = Model.item(new Task("Buy milk!", Priority.MEDIUM));

	public Object content() {

		H1Tag caption = h1("Add new task");

		FormTag frm = edit(task, cmds("Save", "Cancel"), "title", "priority");

		Tag<?> grid = grid(all(Task.class), 3, "id", "priority", "title");

		return rowFull(caption, frm, grid);
	}

	public void onSave() {
		DB.insert(task.value());
		task = null;
	}

	public void onCancel() {
		task = Model.item(new Task("Buy milk!", Priority.MEDIUM));
	}

}
