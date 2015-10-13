package org.rapidoid.security;

/*
 * #%L
 * rapidoid-security
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import org.junit.Before;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.D;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class SecurityTestCommons extends TestCommons {

	@Before
	public void init() {
		Conf.setRootPath(getClass().getSimpleName());
	}

	protected void checkPermissions(String username, Class<?> clazz, Object target, String propertyName,
			boolean canRead, boolean canChange) {

		DataPermissions perms = Secure.getPropertyPermissions(username, clazz, target, propertyName);

		eq(perms.read, canRead);
		eq(perms.change, canChange);
	}

	protected void checkPermissions(String username, Class<?> clazz, String propertyName, boolean canRead,
			boolean canUpdate) {
		D.print("CHECKING", username, clazz, propertyName, canRead, canUpdate);
		checkPermissions(username, clazz, null, propertyName, canRead, canUpdate);
	}

}
