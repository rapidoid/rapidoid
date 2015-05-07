package org.rapidoidx.demo.taskplanner.model;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Display;
import org.rapidoid.annotation.Optional;
import org.rapidoid.annotation.Programmatic;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoid.security.annotation.CanChange;
import org.rapidoid.security.annotation.CanRead;
import org.rapidoid.util.CommonRoles;
import org.rapidoidx.db.XDB;
import org.rapidoidx.db.DbList;
import org.rapidoidx.db.DbRef;
import org.rapidoidx.db.DbSet;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

@Scaffold
@SuppressWarnings("serial")
// @CanRead(CommonRoles.LOGGED_IN)
// @CanChange({ CommonRoles.OWNER })
// @CanInsert(CommonRoles.LOGGED_IN)
// @CanDelete({ CommonRoles.OWNER, CommonRoles.ADMIN })
@DbEntity
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Task extends Entity {

	@Display
	@CanChange({ MODERATOR, OWNER })
	public String title;

	@Display
	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	public Priority priority = Priority.MEDIUM;

	@Optional
	@CanChange({ MODERATOR, OWNER, SHARED_WITH })
	public String description;

	public int rating;

	@Programmatic
	public final DbRef<User> owner = XDB.ref(this, "^owns");

	@CanRead({ CommonRoles.OWNER })
	public final DbSet<User> sharedWith = XDB.set(this, "sharedWith");

	@Programmatic
	@CanRead({ CommonRoles.OWNER, CommonRoles.SHARED_WITH })
	public final DbList<Comment> comments = XDB.list(this, "has");

	@Programmatic
	public final DbSet<User> likedBy = XDB.set(this, "^likes");

}
