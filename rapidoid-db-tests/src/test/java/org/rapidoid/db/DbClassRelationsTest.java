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
import org.rapidoid.db.model.Post;
import org.rapidoid.db.model.Profile;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbClassRelationsTest extends DbTestCommons {

	@Test
	public void testInverseRelations1() {

		Profile profile = new Profile();
		Post post1 = new Post("post 1");
		Post post2 = new Post("post 2");

		profile.posts.add(post1);
		profile.posts.add(post2);

		DB.persist(profile);
		DB.refresh(post1);
		DB.refresh(post2);

		notNull(post1.id);
		notNull(post2.id);

		eq(profile.posts, U.list(post1, post2));
		eq(post1.postedOn.get(), profile);
		eq(post2.postedOn.get(), profile);

		profile.posts.remove(post1);

		DB.persist(profile);
		DB.refresh(post1);
		DB.refresh(post2);

		eq(profile.posts, U.list(post2));
		isNull(post1.postedOn.get());
		eq(post2.postedOn.get(), profile);

		DB.shutdown();
	}

	@Test
	public void testInverseRelations2() {

		Profile profile = new Profile();
		Post post1 = new Post("post 1");
		Post post2 = new Post("post 2");

		post1.postedOn.set(profile);
		post2.postedOn.set(profile);

		DB.persist(post1);
		DB.persist(post2);
		DB.refresh(profile);

		eq(profile.posts, U.list(post1, post2));
		eq(post1.postedOn.get(), profile);
		eq(post2.postedOn.get(), profile);

		post1.postedOn.set(null);

		DB.persist(post1);
		DB.refresh(profile);
		DB.refresh(post2);

		eq(profile.posts, U.list(post2));
		isNull(post1.postedOn.get());
		eq(post2.postedOn.get(), profile);

		DB.shutdown();
	}

}
