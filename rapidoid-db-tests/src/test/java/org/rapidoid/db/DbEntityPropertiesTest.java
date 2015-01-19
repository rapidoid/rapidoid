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

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.beany.BeanProperties;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.db.impl.DbProxy;
import org.rapidoid.db.model.IPost;
import org.rapidoid.db.model.IProfile;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class DbEntityPropertiesTest extends DbTestCommons {

	@Test
	public void testEntityProperties() {
		checkProfileProperties(IProfile.class);

		IProfile profile = DbProxy.create(IProfile.class);
		checkProfileProperties(profile.getClass());

		ConcurrentMap<String, Object> map = U.concurrentMap("id", (Object) 123L, "version", (Object) 456);

		IPost post1 = DbProxy.create(IPost.class, map);
		post1.content().set("abc");

		Map<String, Object> postProps = Beany.read(post1);
		Map<String, Object> likes = U.map("relation", "likes", "ids", U.set());
		Map<String, Object> postedOn = U.map("relation", "^posted", "ids", U.set());
		eq(postProps, U.map("id", 123L, "version", 456L, "likes", likes, "postedOn", postedOn, "content", "abc"));

		profile.posts().add(post1);
		Map<String, Object> profileProps = Beany.read(profile);
		eq(profileProps, U.map("id", null, "version", null, "posts", U.map("relation", "posted", "ids", U.list(123L))));

		IProfile profile2 = DB.create(IProfile.class);
		Beany.update(profile2, profileProps, false);

		Map<String, Object> profileProps2 = Beany.read(profile2);
		eq(profileProps2, profileProps);
	}

	private void checkProfileProperties(Class<?> clazz) {
		BeanProperties props = Beany.propertiesOf(clazz);

		for (Prop prop : props) {
			isFalse(prop.isReadOnly());
		}

		eq(U.set(props.names), U.set("id", "version", "posts"));
		eq(props.get("posts").getType(), DbList.class);
		notNull(props.get("posts").getGenericType());
		eq(props.get("posts").getGenericType().getRawType(), DbList.class);
		eq(props.get("posts").getTypeKind(), TypeKind.OBJECT);
		eq(props.get("posts").getTypeArgsCount(), 1);
		eq(props.get("posts").getTypeArg(0), IPost.class);
	}

}
