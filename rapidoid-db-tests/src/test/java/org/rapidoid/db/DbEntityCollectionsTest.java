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
import org.rapidoid.db.model.Profile;
import org.testng.annotations.Test;

public class DbEntityCollectionsTest extends DbTestCommons {

	@Test(enabled = false)
	public void testCollectionsPersistence() {

		IProfile profile = DB.create(IProfile.class);
		notNull(profile);

		IPost post1 = DB.create(IPost.class);
		post1.content().set("post 1");

		IPost post2 = DB.create(IPost.class);
		post2.content().set("post 2");

		IPost post3 = DB.create(IPost.class);
		post3.content().set("post 3");

		profile.posts().add(post1);
		DB.persist(profile);
		profile.posts().add(post2);
		profile.posts().add(post3);
		DB.persist(profile);

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
		eq(post1.content().get(), "post 1");
		eq(post1.likes().size(), 1);
		eq(post1.likes().iterator().next().name().get(), "person 1");

		Profile p = DB.get(2);
		eq(p.posts.size(), 3);

		post2 = DB.get(3);
		eq(post2.content().get(), "post 2");
		eq(post2.likes().size(), 1);
		eq(post1.likes().iterator().next().name().get(), "person 1");

		post3 = DB.get(4);
		eq(post3.content().get(), "post 3");
		eq(post3.likes().size(), 1);
		eq(post1.likes().iterator().next().name().get(), "person 1");

		DB.shutdown();
	}

}
