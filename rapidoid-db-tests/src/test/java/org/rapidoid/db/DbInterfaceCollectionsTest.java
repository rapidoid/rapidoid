package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db-tests
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

import org.rapidoid.db.model.IPerson;
import org.rapidoid.db.model.IPost;
import org.rapidoid.db.model.IProfile;
import org.testng.annotations.Test;

public class DbInterfaceCollectionsTest extends DbTestCommons {

	@Test
	public void testCollectionsPersistence() {

		IProfile profile = DB.create(IProfile.class);
		notNull(profile);
		eq(DB.size(), 0);

		IPost post1 = DB.create(IPost.class, "content", "post 1");

		IPost post2 = DB.create(IPost.class);
		post2.content().set("post 2");

		IPost post3 = DB.create(IPost.class, "content", "post 3");

		profile.posts().add(post1);
		eq(DB.size(), 1);

		DB.persist(profile);
		eq(DB.size(), 2);

		profile.posts().add(post2);
		eq(DB.size(), 3);

		profile.posts().add(post3);
		eq(DB.size(), 4);

		notNull(profile.id());
		notNull(profile.id().get());
		DB.persist(profile);
		eq(DB.size(), 4);

		int pn = 1;
		for (IPost post : profile.posts()) {
			IPerson person = DB.create(IPerson.class);
			person.name().set("person " + pn);
			person.age().set(pn * 10);
			post.likes().add(person);
			DB.persist(post);
			pn++;
		}

		DB.shutdown();
		DB.init();

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
