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

import java.util.Comparator;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Order;
import org.rapidoid.annotation.Since;
import org.rapidoid.app.GUI;
import org.rapidoid.beany.Beany;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.html.Tag;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.u.U;
import org.rapidoid.widget.GridWidget;
import org.rapidoidx.demo.taskplanner.model.Task;
import org.rapidoidx.demo.taskplanner.model.User;

@LoggedIn
@Order(3)
@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class MyTasksScreen extends GUI {

	public Object content() {
		Tag c1 = titleBox("Tasks owned by me");

		GridWidget grid1 = grid(Task.class, new Predicate<Task>() {
			@Override
			public boolean eval(Task t) throws Exception {
				User user = t.owner.get();
				return user != null && U.eq(user.username, Ctxs.ctx().username());
			}
		}, "-priority", 10, "id", "title", "priority");

		Tag c2 = titleBox("Tasks shared with me");

		GridWidget grid2 = grid(Task.class, new Predicate<Task>() {
			@Override
			public boolean eval(Task t) throws Exception {
				return Beany.projection(t.sharedWith, "username").contains(Ctxs.ctx().username());
			}
		}, new Comparator<Task>() {
			@Override
			public int compare(Task t1, Task t2) {
				return t1.sharedWith.size() - t2.sharedWith.size();
			}
		}, 10, "id", "title", "priority");

		return row(col6(c1, grid1), col6(c2, grid2));
	}
}
