package org.rapidoid.db.imodel;

import org.rapidoid.annotation.Relation;
import org.rapidoid.db.DbColumn;
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

public interface Comment extends Entity {

	DbColumn<String> content();

	@Relation("^owns")
	DbRef<User> owner();

	@Relation("^has")
	DbRef<Task> task();

	@Relation("^likes")
	DbSet<User> likedBy();

}
