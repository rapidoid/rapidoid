package org.rapidoid.security;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.u.U;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Auth extends RapidoidThing {

	@SuppressWarnings("unchecked")
	public static Set<String> getRolesFor(String username) {
		if (U.isEmpty(username)) {
			return U.set();
		}

		Config user = Conf.USERS.sub(username);

		if (user.isEmpty()) {
			return U.set();
		}

		Object roles = user.entry("roles").getOrNull();

		if (Coll.isCollection(roles)) {
			Set<String> roleSet = U.set();

			for (String role : (Collection<String>) roles) {
				roleSet.add(role.toLowerCase());
			}

			return roleSet;

		} else if (roles instanceof String) {
			Set<String> roleSet = U.set();

			for (String role : ((String) roles).toLowerCase().split("\\s*\\,\\s*")) {
				role = role.trim();
				if (U.notEmpty(role)) {
					roleSet.add(role);
				}
			}

			return roleSet;

		} else {
			return Collections.emptySet();
		}
	}

	public static boolean login(String username, String password) {
		if (U.isEmpty(username) || password == null) {
			return false;
		}

		if (!Conf.USERS.has(username)) {
			return false;
		}

		Config user = Conf.USERS.sub(username);

		if (user.isEmpty()) {
			return false;
		}

		String pass = user.entry("password").str().getOrNull();
		String hash = user.entry("hash").str().getOrNull();

		return (pass != null && U.eq(password, pass)) || (hash != null && Crypto.passwordMatches(password, hash));
	}

}
