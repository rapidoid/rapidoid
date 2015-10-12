package org.rapidoid.ctx;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-ctx
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
	public static Set<String> getRolesFor(String username) {
		Set<String> roles = new HashSet<String>();
		// FIXME implement this
		return roles;
	}

}
