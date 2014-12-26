package org.rapidoid.demo.taskplanner.model;

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

import java.util.Set;

import org.rapidoid.app.entity.Entity;
import org.rapidoid.db.DB;

public class User extends Entity {

	public String username;

	public String firstName;

	public String lastName;

	public final Set<Task> tasksOwned = DB.set();

	public final Set<Task> tasksLiked = DB.set();

	public final Set<Comment> commentsLiked = DB.set();

	public void doTransferTo(Task task, User newOwner) {
		tasksLiked.remove(task);
	}

}
