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

import java.util.List;

import org.rapidoid.annotation.Session;
import org.rapidoid.app.GUI;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.util.U;

public class NewTaskScreen extends GUI {

	@Session
	private Task task = new Task();

	@Session
	private List<Task> tasks = U.list();

	public Object content() {

		FormTag frm = edit(task, cmds("Save", "Cancel"), "title", "priority");

		return row(col2(), col8(h1("Create a new task"), frm, grid(Task.class, tasks, 5)), col2());
	}

	public void onSave() {
		tasks.add(task);
		task = null;
	}

	public void onCancel() {
		task = new Task();
	}

}
