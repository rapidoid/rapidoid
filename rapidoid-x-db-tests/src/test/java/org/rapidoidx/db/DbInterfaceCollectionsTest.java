package org.rapidoidx.db;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.db.DB;
import org.rapidoidx.db.model.IPerson;
import org.rapidoidx.db.model.IPost;
import org.rapidoidx.db.model.IProfile;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbInterfaceCollectionsTest extends DbTestCommons {

	@Test
	public void testCollectionsPersistence() {

		IProfile profile = DB.entity(IProfile.class);
		notNull(profile);
		eq(DB.size(), 0);

		IPost post1 = DB.entity(IPost.class, "content", "post 1");

		IPost post2 = DB.entity(IPost.class);
		post2.content().set("post 2");

		IPost post3 = DB.entity(IPost.class, "content", "post 3");

		profile.posts().add(post1);
		eq(DB.size(), 1);

		DB.persist(profile);
		eq(DB.size(), 2);

		profile.posts().add(post2);
		eq(DB.size(), 3);

		profile.posts().add(post3);
		eq(DB.size(), 4);

		notNull(profile.id());
		DB.persist(profile);
		eq(DB.size(), 4);

		int pn = 1;
		for (IPost post : profile.posts()) {
			IPerson person = DB.entity(IPerson.class);
			person.name().set("person " + pn);
			person.age().set(pn * 10);
			post.likes().add(person);
			DB.persist(post);
			pn++;
		}

		DB.shutdown();
		DB.start();

		eq(DB.size(), 7);

		post1 = DB.get(1);
		notNull(post1);
		notNull(post1.likes());

		eq(post1.content().get(), "post 1");
		eq(post1.likes().size(), 1);
		eq(post1.likes().iterator().next().name().get(), "person 1");

		IProfile p = DB.get(2);
		eq(p.posts().size(), 3);

		post2 = DB.get(3);
		eq(post2.content().get(), "post 2");
		eq(post2.likes().size(), 1);
		eq(post2.likes().iterator().next().name().get(), "person 2");

		post3 = DB.get(4);
		eq(post3.content().get(), "post 3");
		eq(post3.likes().size(), 1);
		eq(post3.likes().iterator().next().name().get(), "person 3");

		DB.shutdown();
	}

}
