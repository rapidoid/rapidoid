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

import org.rapidoid.db.model.IPost;
import org.rapidoid.db.model.IProfile;
import org.rapidoid.prop.BeanProperties;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class DbEntityPropertiesTest extends DbTestCommons {

	@Test
	public void testEntityProperties() {
		BeanProperties props = Cls.propertiesOf(IProfile.class);
		eq(props.names, U.list("id", "version", "posts"));
		eq(props.get("posts").typeArgsCount(), 1);
		eq(props.get("posts").typeArg(0), IPost.class);
	}

}
