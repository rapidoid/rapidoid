package custom.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.db.XDB;
import org.junit.Test;

import custom.rapidoidx.db.model.IPost;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbInterfaceEqualityTest extends DbTestCommons {

	@Test
	public void testEntityEquality() {
		eq(XDB.entity(IPost.class, "id", 12), XDB.entity(IPost.class, "id", "12"));
		eq(XDB.entity(IPost.class, "id", 123L), XDB.entity(IPost.class, "id", 123));
		neq(XDB.entity(IPost.class, "id", 12345L), XDB.entity(IPost.class));
		neq(XDB.entity(IPost.class), XDB.entity(IPost.class, "id", 5432));
		neq(XDB.entity(IPost.class), XDB.entity(IPost.class));
		neq(XDB.entity(IPost.class, "id", null), XDB.entity(IPost.class, "id", null));
		neq(XDB.entity(IPost.class, "id", null), XDB.entity(IPost.class, "id", 12));
		neq(XDB.entity(IPost.class, "id", 123), XDB.entity(IPost.class, "id", null));
	}

}
