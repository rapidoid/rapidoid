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
import org.rapidoid.util.U;
import org.rapidoidx.db.XDB;
import org.testng.annotations.Test;

import custom.rapidoidx.db.model.Post;
import custom.rapidoidx.db.model.Profile;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbClassRelationsTest extends DbTestCommons {

	@Test
	public void testInverseRelations1() {

		Profile profile = new Profile();
		Post post1 = new Post("post 1");
		Post post2 = new Post("post 2");

		profile.posts.add(post1);
		profile.posts.add(post2);

		XDB.persist(profile);
		XDB.refresh(post1);
		XDB.refresh(post2);

		notNull(post1.id());
		notNull(post2.id());

		eq(profile.posts, U.list(post1, post2));
		eq(post1.postedOn.get(), profile);
		eq(post2.postedOn.get(), profile);

		profile.posts.remove(post1);

		XDB.persist(profile);
		XDB.refresh(post1);
		XDB.refresh(post2);

		eq(profile.posts, U.list(post2));
		isNull(post1.postedOn.get());
		eq(post2.postedOn.get(), profile);

		XDB.shutdown();
	}

	@Test
	public void testInverseRelations2() {

		Profile profile = new Profile();
		Post post1 = new Post("post 1");
		Post post2 = new Post("post 2");

		post1.postedOn.set(profile);
		post2.postedOn.set(profile);

		XDB.persist(post1);
		XDB.persist(post2);
		XDB.refresh(profile);

		eq(profile.posts, U.list(post1, post2));
		eq(post1.postedOn.get(), profile);
		eq(post2.postedOn.get(), profile);

		post1.postedOn.set(null);

		XDB.persist(post1);
		XDB.refresh(profile);
		XDB.refresh(post2);

		eq(profile.posts, U.list(post2));
		isNull(post1.postedOn.get());
		eq(post2.postedOn.get(), profile);

		XDB.shutdown();
	}

}
