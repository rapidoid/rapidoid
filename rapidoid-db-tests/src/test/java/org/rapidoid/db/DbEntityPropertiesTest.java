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

import org.rapidoid.db.impl.DbProxy;
import org.rapidoid.db.model.IPost;
import org.rapidoid.db.model.IProfile;
import org.rapidoid.prop.BeanProperties;
import org.rapidoid.prop.Prop;
import org.rapidoid.util.Cls;
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

		Map<String, Object> postProps = Cls.read(post1);
		eq(postProps, U.map("id", 123L, "version", 456, "likes", U.map("relation", "likes", "ids", U.set()), "content",
				"abc"));

		profile.posts().add(post1);
		Map<String, Object> profileProps = Cls.read(profile);
		eq(profileProps, U.map("id", 0L, "version", 0L, "posts", U.map("relation", "posted", "ids", U.list(123L))));

		IProfile profile2 = DB.create(IProfile.class);
		Cls.update(profileProps, profile2);

		Map<String, Object> profileProps2 = Cls.read(profile2);
		eq(profileProps2, profileProps);
	}

	private void checkProfileProperties(Class<?> clazz) {
		BeanProperties props = Cls.propertiesOf(clazz);
		
		for (Prop prop : props) {
			System.out.println(prop);
			isFalse(prop.isReadOnly());
		}

		eq(props.names, U.list("id", "version", "posts"));
		eq(props.get("posts").getType(), DbList.class);
		notNull(props.get("posts").getGenericType());
		eq(props.get("posts").getGenericType().getRawType(), DbList.class);
		eq(props.get("posts").getTypeKind(), TypeKind.OBJECT);
		eq(props.get("posts").typeArgsCount(), 1);
		eq(props.get("posts").typeArg(0), IPost.class);
	}

}
