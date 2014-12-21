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
import org.rapidoid.security.annotation.Owner;
import org.rapidoid.security.annotation.Role;
import org.rapidoid.security.annotation.Roles;
import org.rapidoid.security.annotation.SharedWith;
import org.rapidoid.util.Arr;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;

public class AppSecurity implements Constants {

	public Set<RoleBasedAccess> rolesAllowedForClass(Class<?> clazz, Object record, String propertyName) {
		Set<RoleBasedAccess> roles = U.set();

		for (Annotation ann : clazz.getAnnotations()) {
			Class<? extends Annotation> type = ann.annotationType();
			String roleName = type.getSimpleName().toUpperCase();

			if (type.equals(Admin.class)) {
				Admin r = (Admin) ann;
				roles.add(RoleBasedAccess.from(roleName, r.fullAccess(), r.insert(), r.read(), r.update(), r.delete()));
			} else if (type.equals(Manager.class)) {
				Manager r = (Manager) ann;
				roles.add(RoleBasedAccess.from(roleName, r.fullAccess(), r.insert(), r.read(), r.update(), r.delete()));
			} else if (type.equals(Moderator.class)) {
				Moderator r = (Moderator) ann;
				roles.add(RoleBasedAccess.from(roleName, r.fullAccess(), r.insert(), r.read(), r.update(), r.delete()));
			} else if (type.equals(LoggedIn.class)) {
				LoggedIn r = (LoggedIn) ann;
				roles.add(RoleBasedAccess.from(roleName, r.fullAccess(), r.insert(), r.read(), r.update(), r.delete()));
			} else if (type.equals(Owner.class)) {
				Owner r = (Owner) ann;
				roles.add(RoleBasedAccess.from(roleName, r.fullAccess(), r.insert(), r.read(), r.update(), r.delete()));
			} else if (type.equals(SharedWith.class)) {
				SharedWith r = (SharedWith) ann;
				roles.add(RoleBasedAccess.from(roleName, r.fullAccess(), r.insert(), r.read(), r.update(), r.delete()));
			} else if (type.equals(Roles.class)) {
				Role[] values = ((Roles) ann).value();
				U.must(values.length > 0, "At least one role must be specified in @Roles annotation!");
				for (Role r : values) {
					roles.add(RoleBasedAccess.from(roleName, r.fullAccess(), r.insert(), r.read(), r.update(),
							r.delete()));
				}
			}
		}

		return roles;
	}

	public boolean canAccessClass(String username, Class<?> clazz) {
		return true;
	}

	public boolean hasRole(String username, String role, Class<?> clazz, Object record) {
		if (U.isEmpty(username)) {
			return false;
		}

		if (role.equalsIgnoreCase(LoggedIn.class.getSimpleName())) {
			return !U.isEmpty(username);
		}

		String roleConfig = "role-" + role.toLowerCase();
		String[] usernames = U.option(roleConfig, EMPTY_STRING_ARRAY);
		return !U.isEmpty(username) && Arr.indexOf(usernames, username) >= 0;
	}

	public boolean isAdmin(String username) {
		return hasRole(username, "ADMIN", null, null);
	}

	public boolean isManager(String username) {
		return hasRole(username, "MANAGER", null, null);
	}

	public boolean isModerator(String username) {
		return hasRole(username, "MODERATOR", null, null);
	}

	public DataPermissions classPermissions(String username, Class<?> clazz) {
		return DataPermissions.ALL;
	}

	public DataPermissions recordPermissions(String username, Object record) {
		return DataPermissions.ALL;
	}

	public DataPermissions propertyPermissions(String username, Object record, String propertyName) {
		return DataPermissions.ALL;
	}

}
