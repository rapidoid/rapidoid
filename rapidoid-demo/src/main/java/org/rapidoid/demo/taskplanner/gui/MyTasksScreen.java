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

import java.util.Comparator;

import org.rapidoid.app.Screen;
import org.rapidoid.demo.taskplanner.model.Task;
import org.rapidoid.demo.taskplanner.model.User;
import org.rapidoid.html.Tag;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.util.Cls;
import org.rapidoid.widget.GridWidget;

@LoggedIn
public class MyTasksScreen extends Screen {

	public Object content() {
		Tag c1 = titleBox("Tasks owned by me");

		GridWidget grid1 = grid(Task.class, new Predicate<Task>() {
			@Override
			public boolean eval(Task t) throws Exception {
				User user = t.owner.get();
				return user != null && user.username.equals(ctx().username());
			}
		}, "-priority", 10, "id", "title", "priority");

		Tag c2 = titleBox("Tasks shared with me");

		GridWidget grid2 = grid(Task.class, new Predicate<Task>() {
			@Override
			public boolean eval(Task t) throws Exception {
				return Cls.projection(t.sharedWith, "username").contains(ctx().username());
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
