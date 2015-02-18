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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.db.model.Person;
import org.rapidoid.db.model.Post;
import org.rapidoid.db.model.Profile;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbClassCollectionsTest extends DbTestCommons {

	@Test
	public void testCollectionsPersistence() {

		Profile profile = new Profile();

		profile.posts.add(new Post("post 1"));
		DB.persist(profile);
		profile.posts.add(new Post("post 2"));
		profile.posts.add(new Post("post 3"));
		DB.persist(profile);

		int pn = 1;
		for (Post post : profile.posts) {
			post.likes.add(new Person("person " + pn, pn * 10));
			DB.persist(post);
			pn++;
		}

		DB.shutdown();
		DB.start();

		eq(DB.size(), 7);

		Post post1 = DB.get(1);
		eq(post1.content, "post 1");
		eq(post1.likes.size(), 1);
		eq(post1.likes.iterator().next().name, "person 1");

		Profile p = DB.get(2);
		eq(p.posts.size(), 3);

		Post post2 = DB.get(3);
		eq(post2.content, "post 2");
		eq(post2.likes.size(), 1);
		eq(post2.likes.iterator().next().name, "person 2");

		Post post3 = DB.get(4);
		eq(post3.content, "post 3");
		eq(post3.likes.size(), 1);
		eq(post3.likes.iterator().next().name, "person 3");

		DB.shutdown();
	}

}
