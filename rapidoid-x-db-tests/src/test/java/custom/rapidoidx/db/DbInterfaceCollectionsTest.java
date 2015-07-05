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

import custom.rapidoidx.db.model.IPerson;
import custom.rapidoidx.db.model.IPost;
import custom.rapidoidx.db.model.IProfile;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbInterfaceCollectionsTest extends DbTestCommons {

	@Test
	public void testCollectionsPersistence() {

		IProfile profile = XDB.entity(IProfile.class);
		notNull(profile);
		eq(XDB.size(), 0);

		IPost post1 = XDB.entity(IPost.class, "content", "post 1");

		IPost post2 = XDB.entity(IPost.class);
		post2.content().set("post 2");

		IPost post3 = XDB.entity(IPost.class, "content", "post 3");

		profile.posts().add(post1);
		eq(XDB.size(), 1);

		XDB.persist(profile);
		eq(XDB.size(), 2);

		profile.posts().add(post2);
		eq(XDB.size(), 3);

		profile.posts().add(post3);
		eq(XDB.size(), 4);

		notNull(profile.id());
		XDB.persist(profile);
		eq(XDB.size(), 4);

		int pn = 1;
		for (IPost post : profile.posts()) {
			IPerson person = XDB.entity(IPerson.class);
			person.name().set("person " + pn);
			person.age().set(pn * 10);
			post.likes().add(person);
			XDB.persist(post);
			pn++;
		}

		XDB.shutdown();
		XDB.start();

		eq(XDB.size(), 7);

		post1 = XDB.get(1);
		notNull(post1);
		notNull(post1.likes());

		eq(post1.content().get(), "post 1");
		eq(post1.likes().size(), 1);
		eq(post1.likes().iterator().next().name().get(), "person 1");

		IProfile p = XDB.get(2);
		eq(p.posts().size(), 3);

		post2 = XDB.get(3);
		eq(post2.content().get(), "post 2");
		eq(post2.likes().size(), 1);
		eq(post2.likes().iterator().next().name().get(), "person 2");

		post3 = XDB.get(4);
		eq(post3.content().get(), "post 3");
		eq(post3.likes().size(), 1);
		eq(post3.likes().iterator().next().name().get(), "person 3");

		XDB.shutdown();
	}

}
