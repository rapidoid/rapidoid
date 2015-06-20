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

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.BeanProperties;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.util.U;
import org.rapidoidx.db.impl.DbProxy;
import org.rapidoidx.db.model.IPost;
import org.rapidoidx.db.model.IProfile;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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

		eq(postProps, U.map("id", "123", "version", "456", "createdOn", null, "createdBy", null, "lastUpdatedOn", null,
				"lastUpdatedBy", null, "likes", likes, "postedOn", postedOn, "content", "abc"));

		profile.posts().add(post1);
		Map<String, Object> profileProps = Beany.read(profile);
		eq(profileProps, U.map("id", null, "version", null, "createdOn", null, "createdBy", null, "lastUpdatedOn",
				null, "lastUpdatedBy", null, "posts", U.map("relation", "posted", "ids", U.list(123L))));
		IProfile profile2 = XDB.entity(IProfile.class);
		Beany.update(profile2, profileProps, false);

		Map<String, Object> profileProps2 = Beany.read(profile2);
		eq(profileProps2, profileProps);
	}

	private void checkProfileProperties(Class<?> clazz) {
		BeanProperties props = Beany.propertiesOf(clazz);

		for (Prop prop : props) {
			isFalse(prop.isReadOnly());
		}

		eq(U.set(props.names),
				U.set("id", "version", "createdBy", "createdOn", "lastUpdatedBy", "lastUpdatedOn", "posts"));

		eq(props.get("posts").getType(), DbList.class);
		notNull(props.get("posts").getGenericType());
		eq(props.get("posts").getGenericType().getRawType(), DbList.class);
		eq(props.get("posts").getTypeKind(), TypeKind.OBJECT);
		eq(props.get("posts").getTypeArgsCount(), 1);
		eq(props.get("posts").getTypeArg(0), IPost.class);
	}

}
