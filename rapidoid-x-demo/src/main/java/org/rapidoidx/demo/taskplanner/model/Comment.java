package org.rapidoidx.demo.taskplanner.model;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Since;
import org.rapidoidx.db.XDB;
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

@SuppressWarnings("serial")
@DbEntity
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Comment extends Entity {

	public String content;

	public DbRef<User> owner = XDB.ref(this, "^owns");

	public DbRef<Task> task = XDB.ref(this, "^has");

	public DbSet<User> likedBy = XDB.set(this, "^likes");

}
