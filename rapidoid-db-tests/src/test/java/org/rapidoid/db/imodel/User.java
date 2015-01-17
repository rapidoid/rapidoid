package org.rapidoid.db.imodel;

/*
 * #%L
 * rapidoid-db-tests
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

import java.util.Date;

import org.rapidoid.annotation.Relation;
import org.rapidoid.db.DB;
import org.rapidoid.db.DbColumn;
import org.rapidoid.db.DbDsl;
import org.rapidoid.db.DbSet;
import org.rapidoid.db.Entity;

public interface User extends Entity {

	DbDsl<User> DSL = DB.dsl(User.class);

	DbColumn<String> email();

	DbColumn<String> name();

	DbColumn<Date> birthdate();

	@Relation("owns")
	DbSet<Task> tasksOwned();

	@Relation("owns")
	DbSet<Comment> commentsOwned();

	@Relation("likes")
	DbSet<Task> tasksLiked();

	@Relation("^sharedWith")
	DbSet<Task> sharedTasks();

	@Relation("likes")
	DbSet<Comment> commentsLiked();

}
