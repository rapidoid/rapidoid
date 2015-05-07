package org.rapidoidx.demo.taskplanner.gui;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Order;
import org.rapidoid.annotation.Session;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.Screen;
import org.rapidoid.app.UsersTool;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.Tag;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;
import org.rapidoid.widget.FormWidget;
import org.rapidoid.widget.GridWidget;
import org.rapidoidx.db.XDB;
import org.rapidoidx.demo.taskplanner.model.Task;
import org.rapidoidx.demo.taskplanner.model.User;

@Order(1)
@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class NewTaskScreen extends Screen {

	@Session
	private Task task = new Task();

	@Session
	private List<String> comments = U.list();

	@Session
	private Var<String> v = var("abc");

	@Session
	private Var<List<String>> v2 = var(U.list("b", "AA"));

	public Object content() {
		Tag caption1 = titleBox("Add new task");
		FormWidget frm = create(task).buttons(ADD, CANCEL);
		frm.field("description").setType(FieldType.TEXTAREA);

		frm.add(field(null, null, null, "abcd", null, FieldType.CHECKBOXES, U.list("AA", "b", "cDeF"), true, v2, null));

		frm.field("description").setLabel(h3("my custom field"));

		frm.field("comments").setInput(div((Object[]) radios(U.list("a", "bb", "ccc"), v)));

		Tag caption2 = titleBox("Most recent tasks");
		GridWidget grid = grid(Task.class, "-id", 7, "id", "priority", "title");

		return row(col4(caption1, frm), col8(caption2, grid));
	}

	public void onAdd() {
		User user = UsersTool.current(User.class);
		if (user != null) {
			task.owner.set(user);
			task.description = v.get();
			XDB.insert(task);
			task = new Task();
		}
	}

	public void onCancel() {
		task = new Task();
	}

}
