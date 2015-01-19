package org.rapidoid.demo.taskplanner.imodel;

import org.rapidoid.annotation.Optional;
import org.rapidoid.db.DB;
import org.rapidoid.db.DbColumn;
import org.rapidoid.db.DbDsl;
import org.rapidoid.db.Entity;
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

@CanRead(CommonRoles.LOGGED_IN)
@CanChange({ CommonRoles.OWNER })
@CanInsert(CommonRoles.LOGGED_IN)
@CanDelete({ CommonRoles.OWNER, CommonRoles.ADMIN })
interface Task extends Entity, Commentable, Likeable, Shareable, Owned {

	DbDsl<Task> DSL = DB.dsl(Task.class);

	@CanChange({ MODERATOR, OWNER })
	DbColumn<String> title();

	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	DbColumn<Priority> priority();

	@Optional
	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	DbColumn<String> description();

	DbColumn<Integer> rating();

}
