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
import org.rapidoid.log.Log;
import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.util.U;
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

	public Tag addBook() {
		return modal("Add new book", create(new Book()), div(SAVE, CANCEL));
	}

	public Tag yesNo() {
		return modal("Confirm deletion", h1("Are you sure?"), div(YES, NO));
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

	public void onDialog() {
		showModal("yesNo");
	}

	public void onAdd() {
		showModal("addBook");
	}

	public void onYes() {
		Log.info("yes");
		hideModal();
	}

	public void onNo() {
		Log.info("no");
		hideModal();
	}

}
