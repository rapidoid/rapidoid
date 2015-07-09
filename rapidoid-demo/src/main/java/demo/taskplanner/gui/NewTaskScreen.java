package demo.taskplanner.gui;

/*
 * #%L
 * rapidoid-demo
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

import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Local;
import org.rapidoid.annotation.Order;
import org.rapidoid.annotation.Screen;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Transaction;
import org.rapidoid.app.GUI;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.Tag;
import org.rapidoid.plugins.DB;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;
import org.rapidoid.widget.FormWidget;
import org.rapidoid.widget.GridWidget;

import demo.taskplanner.model.Task;

@Screen
@Order(1)
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class NewTaskScreen extends GUI {

	private Task task = new Task();

	@Local
	private List<String> comments = U.list();

	private Var<String> v = var("abc");

	private Var<List<String>> v2 = var("v2", U.list("b", "AA"));

	public Object content() {
		Tag caption1 = titleBox("Add new task");
		FormWidget frm = create(task).buttons(ADD, CANCEL);
		frm.field("description").setType(FieldType.TEXTAREA);

		frm.add(field(null, null, null, "abcd", null, FieldType.CHECKBOXES, U.list("AA", "b", "cDeF"), true, v2, null));

		frm.field("description").setLabel(h3("my custom field"));

		// frm.field("comments").setInput(div((Object[]) radios(U.list("a", "bb", "ccc"), v)));

		Tag caption2 = titleBox("Most recent tasks");
		GridWidget grid = grid(Task.class, "-id", 7, "id", "priority", "title");

		return row(col4(caption1, frm), col8(caption2, grid));
	}

	@Transaction
	public void onAdd() {
		DB.transaction(new Runnable() {
			@Override
			public void run() {
				task.description = v.get();
				DB.insert(task);
				task = new Task();
			}
		}, false);

		// User user = UsersTool.current(User.class);
		// if (user != null) {
		// // task.owner = user;
		// }
	}

	public void onCancel() {
		task = new Task();
	}

}
