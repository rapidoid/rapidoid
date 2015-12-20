package org.rapidoid.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("4.4.0")
public class Roles {

	public static final String ANYBODY = "anybody";

	public static final String ADMIN = "admin";

	public static final String MANAGER = "manager";

	public static final String MODERATOR = "moderator";

	public static final String LOGGED_IN = "logged_in";

	public static final String OWNER = "owner";

	public static final String AUTHOR = "author";

	public static final String SHARED_WITH = "shared_with";

	public static final String RESTARTER = "restarter";

	public static final String ANONYMOUS = "anonymous";

	public static final List<String> COMMON_ROLES = Collections.unmodifiableList(Arrays.asList(ADMIN, MANAGER,
			MODERATOR, LOGGED_IN, OWNER, RESTARTER));

	public static final Set<String> ROLES_ANONYMOUS = U.set(ANONYMOUS);

	public static final Set<String> ROLES_LOGGED_IN = U.set(LOGGED_IN);

	private static Config USERS;

	@SuppressWarnings("unchecked")
	public static Set<String> getRolesFor(String username) {
		if (U.isEmpty(username)) {
			return ROLES_ANONYMOUS;
		}

		Config users = usersConfig();

		Map<String, Object> user = users.get(username);
		if (user == null) {
			return ROLES_LOGGED_IN;
		}

		Object roles = U.cast(user.get("roles"));

		if (U.isCollection(roles)) {
			Set<String> roleSet = U.set();
			for (String role : (Collection<String>) roles) {
				roleSet.add(role.toLowerCase());
			}
			return roleSet;

		} else if (roles instanceof String) {
			String role = (String) roles;
			return U.set(role.toLowerCase());

		} else {
			return ROLES_LOGGED_IN;
		}
	}

	public static synchronized Config usersConfig() {
		if (USERS == null) {
			USERS = Conf.refreshing("", "users.yaml");
		}

		return USERS;
	}

	public static synchronized void resetConfig() {
		USERS = null;
	}

}
