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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.Transaction;
import org.rapidoid.annotation.Controller;
import org.rapidoid.gui.Btn;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.u.U;

import demo.taskplanner.model.Task;
import demo.taskplanner.model.User;

@CanInsert("logged_in")
class Book {
	public String title;
	public User author;
}

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
@Controller
public class About extends GUI {

	public Object content() {
		Tag mix = h2(a("Enter the mix").href("/mix"));
		Btn tx = btn("Transactional").command("tx");
		Btn dlg = cmd("Dialog");

		return arr(mix, tx, dlg, ADD);
	}

	@Transaction
	public void onTx() {
		String id = DB.insert(task());
		DB.update(id, task());
		DB.update("1", task());
		DB.delete(Task.class, "1");
		throw U.rte("some failure!");
	}

	private Task task() {
		Task task = new Task();
		task.title = "DON'T GO TO THE DATABASE!";
		return task;
	}

}
