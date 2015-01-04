package org.rapidoid.demo.taskplanner.gui;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.app.Screen;
import org.rapidoid.db.DB;
import org.rapidoid.demo.taskplanner.model.Task;
import org.rapidoid.demo.taskplanner.model.User;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.util.U;

@CanInsert("logged_in")
class Book {
	public String title;
	public User author;
}

public class AboutScreen extends Screen {

	public Object content() {
		Tag mix = h2(a("Enter the mix").href("/mix"));
		ButtonTag tx = btn("Transactional").cmd("tx");
		ButtonTag dlg = cmd("Dialog");

		return arr(mix, tx, dlg, ADD);
	}

	public Tag addBook() {
		return modal("Add new book", create(new Book()), div(SAVE, CANCEL));
	}

	public Tag yesNo() {
		return modal("Confirm deletion", h1("Are you sure?"), div(YES, NO));
	}

	public void onTx() {
		long id = DB.insert(task());
		DB.update(id, task());
		DB.update(1, task());
		DB.delete(1);
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
		U.info("yes");
		hideModal();
	}

	public void onNo() {
		U.info("no");
		hideModal();
	}

}
