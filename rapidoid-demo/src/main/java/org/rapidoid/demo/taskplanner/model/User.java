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

import java.util.Date;

import org.rapidoid.app.entity.Entity;
import org.rapidoid.db.DB;
import org.rapidoid.db.DbSet;

@SuppressWarnings("serial")
public class User extends Entity {

	public String username;

	public String email;

	public String name;

	public Date birthdate;

	public final DbSet<Task> tasksOwned = DB.set(this, "owns");

	public final DbSet<Comment> commentsOwned = DB.set(this, "owns");

	public final DbSet<Task> tasksLiked = DB.set(this, "likes");

	public final DbSet<Task> sharedTasks = DB.set(this, "shared");

	public final DbSet<Comment> commentsLiked = DB.set(this, "likes");

	public void doTransferTo(Task task, User newOwner) {
		tasksLiked.remove(task);
	}

}
