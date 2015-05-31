package org.rapidoidx.db.model;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.db.DbRef;
import org.rapidoidx.db.DbSet;
import org.rapidoidx.db.XDB;
import org.rapidoidx.db.XEntity;

/*
 * #%L
 * rapidoid-x-db-tests
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class Post extends XEntity {

	private static final long serialVersionUID = 3776106825040831514L;

	public String content;

	public final DbRef<Profile> postedOn = XDB.ref(this, "^posted");

	public final DbSet<Person> likes = XDB.set(this, "likes");

	public Post() {}

	public Post(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Post [content=" + content + "]";
	}

}
