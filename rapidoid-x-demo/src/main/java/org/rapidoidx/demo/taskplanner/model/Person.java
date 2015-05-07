package org.rapidoidx.demo.taskplanner.model;

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

import java.util.Date;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Display;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoidx.db.DbSet;
import org.rapidoidx.db.XDB;
import org.rapidoidx.db.XEntity;

@Scaffold
@SuppressWarnings("serial")
@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Person extends XEntity {

	@Display
	public String username;

	public String email;

	public String name;

	public Date birthdate;

	public int age;

	public final DbSet<Task> tasksOwned = XDB.set(this, "owns");

	public final DbSet<Comment> commentsOwned = XDB.set(this, "owns");

	public final DbSet<Task> tasksLiked = XDB.set(this, "likes");

	public final DbSet<Task> sharedTasks = XDB.set(this, "^sharedWith");

	public final DbSet<Comment> commentsLiked = XDB.set(this, "likes");

}
