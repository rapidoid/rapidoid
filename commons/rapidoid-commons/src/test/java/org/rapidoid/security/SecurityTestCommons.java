package org.rapidoid.security;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;

import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class SecurityTestCommons extends AbstractCommonsTest {

	protected void checkPermissions(String username, Class<?> clazz, Object target, String propertyName,
	                                boolean canRead, boolean canChange) {

		Set<String> roles = roles(username);

		DataPermissions perms = Secure.getPropertyPermissions(username, roles, clazz, target, propertyName);

		eq(perms.read, canRead);
		eq(perms.change, canChange);
	}

	protected Set<String> roles(String username) {
		return Auth.getRolesFor(username);
	}

	protected void checkPermissions(String username, Class<?> clazz, String propertyName, boolean canRead,
	                                boolean canUpdate) {
		U.print("CHECKING PERMISSION", username, clazz, propertyName, canRead, canUpdate);
		checkPermissions(username, clazz, null, propertyName, canRead, canUpdate);
	}

}
