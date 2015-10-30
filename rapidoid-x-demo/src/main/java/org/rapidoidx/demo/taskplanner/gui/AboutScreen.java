package org.rapidoidx.demo.taskplanner.gui;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.GUI;
import org.rapidoid.html.Tag;
import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.u.U;
import org.rapidoid.widget.ButtonWidget;
import org.rapidoidx.db.XDB;
import org.rapidoidx.demo.taskplanner.model.Task;
import org.rapidoidx.demo.taskplanner.model.User;

@CanInsert("logged_in")
class Book {
	public String title;
	public User author;
}

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class AboutScreen extends GUI {

	public Object content() {
		Tag mix = h2(a("Enter the mix").href("/mix"));
		ButtonWidget tx = btn("Transactional").command("tx");
		ButtonWidget dlg = cmd("Dialog");

		return arr(mix, tx, dlg, ADD);
	}

	public void onTx() {
		long id = XDB.insert(task());
		XDB.update(id, task());
		XDB.update(1, task());
		XDB.delete(1);
		throw U.rte("some failure!");
	}

	private Task task() {
		Task task = new Task();
		task.title = "DON'T GO TO THE DATABASE!";
		return task;
	}

}
