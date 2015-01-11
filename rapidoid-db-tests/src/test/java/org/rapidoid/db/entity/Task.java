package org.rapidoid.db.entity;

import org.rapidoid.annotation.Optional;
import org.rapidoid.annotation.Relation;
import org.rapidoid.db.DB;
import org.rapidoid.db.DbColumn;
import org.rapidoid.db.DbDsl;
import org.rapidoid.db.DbRef;
import org.rapidoid.db.DbSet;
import org.rapidoid.db.Entity;

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

interface Task extends Entity, Commentable {

	DbDsl<Task> DSL = DB.dsl(Task.class);

	DbColumn<String> title();

	DbColumn<Priority> priority();

	@Optional
	DbColumn<String> description();

	DbColumn<Integer> rating();

	@Relation("^owns")
	DbRef<User> owner();

	@Relation("^likes")
	DbSet<User> likedBy();

}
