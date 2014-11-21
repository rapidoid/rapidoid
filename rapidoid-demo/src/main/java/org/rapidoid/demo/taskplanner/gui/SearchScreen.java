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

import static org.rapidoid.html.HTML.*;

import java.util.List;

import org.rapidoid.db.DB;
import org.rapidoid.demo.taskplanner.model.Task;
import org.rapidoid.html.tag.DivTag;
import org.rapidoid.html.tag.H2Tag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.lambda.Predicate;

public class SearchScreen {

	public Object content(HttpExchange x) {

		final String query = x.param("q");
		H2Tag title = h2("Search results for ", b(query), ":");

		DivTag res = div(title);

		List<Task> found = DB.find(new Predicate<Task>() {
			@Override
			public boolean eval(Task task) throws Exception {
				return task.title.contains(query);
			}
		});

		for (Task task : found) {
			res = res.append(div(task.title, " with ", b(task.priority), " priority"));
		}

		return res;
	}
}
