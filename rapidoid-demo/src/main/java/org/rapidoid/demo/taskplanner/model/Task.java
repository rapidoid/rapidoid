package org.rapidoid.demo.taskplanner.model;

import java.util.List;
import java.util.Set;

import org.rapidoid.db.DB;
import org.rapidoid.db.Entity;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.util.U;

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

@LoggedIn
public class Task extends Entity {

	public String title;

	public Priority priority = Priority.MEDIUM;

	public String description;

	public User owner;

	public User createdBy;

	public final List<Comment> comments = DB.list();

	public final Set<User> likedBy = DB.set();

	public Task() {
	}

	public Task(String title, Priority priority) {
		this.title = title;
		this.priority = priority;
	}

	public void like(User currentUser) {
		likedBy.add(currentUser);
	}

	public void unlike(User currentUser) {
		likedBy.remove(currentUser);
	}

	public void transferTo(User currentUser, User newOwner) {
		U.must(currentUser.equals(owner));

		owner = newOwner;
	}

}
