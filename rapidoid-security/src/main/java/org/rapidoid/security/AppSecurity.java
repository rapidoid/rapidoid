package org.rapidoid.security;

/*
 * #%L
 * rapidoid-security
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.lang.annotation.Annotation;
import java.util.Set;

import org.rapidoid.security.annotation.Admin;
import org.rapidoid.security.annotation.LoggedIn;
import org.rapidoid.security.annotation.Manager;
import org.rapidoid.security.annotation.Moderator;
import org.rapidoid.security.annotation.Roles;
import org.rapidoid.util.Arr;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;

public class AppSecurity implements Constants {

	public Set<String> rolesAllowedForClass(Class<?> clazz) {
		Set<String> roles = U.set();

		for (Annotation ann : clazz.getAnnotations()) {
			Class<? extends Annotation> type = ann.annotationType();
			if (type.equals(Admin.class)) {
				roles.add(Admin.class.getSimpleName().toUpperCase());
			} else if (type.equals(Manager.class)) {
				roles.add(Manager.class.getSimpleName().toUpperCase());
			} else if (type.equals(Moderator.class)) {
				roles.add(Moderator.class.getSimpleName().toUpperCase());
			} else if (type.equals(LoggedIn.class)) {
				roles.add(LoggedIn.class.getSimpleName().toUpperCase());
			} else if (type.equals(Roles.class)) {
				String[] values = ((Roles) ann).value();
				U.must(values.length > 0, "At least one role must be specified in @Roles annotation!");
				for (String role : values) {
					roles.add(role.toUpperCase());
				}
			}
		}

		return roles;
	}

	public boolean canAccessClass(String username, Class<?> clazz) {
		return true;
	}

	public boolean hasRole(String username, String role) {
		if (role.equalsIgnoreCase(LoggedIn.class.getSimpleName())) {
			return !U.isEmpty(username);
		}

		String roleConfig = "role-" + role.toLowerCase();
		String[] users = U.option(roleConfig, EMPTY_STRING_ARRAY);
		return !U.isEmpty(username) && Arr.indexOf(users, username) >= 0;
	}

	public boolean isAdmin(String username) {
		return hasRole(username, "ADMIN");
	}

	public boolean isManager(String username) {
		return hasRole(username, "MANAGER");
	}

	public boolean isModerator(String username) {
		return hasRole(username, "MODERATOR");
	}

	public DataPermissions classPermissions(String username, Class<?> clazz) {
		return DataPermissions.ALL;
	}

	public DataPermissions recordPermissions(String username, Object record) {
		return DataPermissions.ALL;
	}

	public DataPermissions fieldPermissions(String username, Object record, String fieldName) {
		return DataPermissions.ALL;
	}

}
