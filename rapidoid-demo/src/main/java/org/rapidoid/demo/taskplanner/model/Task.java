package org.rapidoid.demo.taskplanner.model;

import org.rapidoid.annotation.Optional;
import org.rapidoid.annotation.Programmatic;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.app.entity.AbstractEntity;
import org.rapidoid.db.DB;
import org.rapidoid.db.DbList;
import org.rapidoid.db.DbRef;
import org.rapidoid.db.DbSet;
import org.rapidoid.security.annotation.CanChange;
import org.rapidoid.security.annotation.CanDelete;
import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.security.annotation.CanRead;
import org.rapidoid.util.CommonRoles;

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

@Scaffold
@SuppressWarnings("serial")
@CanRead(CommonRoles.LOGGED_IN)
@CanChange({ CommonRoles.OWNER })
@CanInsert(CommonRoles.LOGGED_IN)
@CanDelete({ CommonRoles.OWNER, CommonRoles.ADMIN })
public class Task extends AbstractEntity {

	public static final Task DSL = DB.dsl(Task.class);

	@CanChange({ MODERATOR, OWNER })
	public String title;

	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	public Priority priority = Priority.MEDIUM;

	@Optional
	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	public String description;

	public int rating;

	@Programmatic
	public final DbRef<User> owner = DB.ref(this, "^owns");

	@CanRead({ CommonRoles.OWNER })
	public final DbSet<User> sharedWith = DB.set(this, "sharedWith");

	@Programmatic
	@CanRead({ CommonRoles.OWNER, CommonRoles.SHARED_WITH })
	public final DbList<Comment> comments = DB.list(this, "has");

	@Programmatic
	public final DbSet<User> likedBy = DB.set(this, "^likes");

}
