package org.rapidoid.security;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Metadata;
import org.rapidoid.security.annotation.*;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppSecurity implements Constants {

	public String[] getRolesAllowed(Map<Class<?>, Annotation> annotations) {

		Set<String> roles = U.set();

		for (Entry<Class<?>, Annotation> e : annotations.entrySet()) {
			Annotation ann = e.getValue();
			Class<? extends Annotation> type = ann.annotationType();

			if (type.equals(Administrator.class)) {
				roles.add(Roles.ADMIN);
			} else if (type.equals(Manager.class)) {
				roles.add(Roles.MANAGER);
			} else if (type.equals(Moderator.class)) {
				roles.add(Roles.MODERATOR);
			} else if (type.equals(LoggedIn.class)) {
				roles.add(Roles.LOGGED_IN);
			} else if (type.equals(HasRole.class)) {
				Role[] values = ((HasRole) ann).value();
				U.must(values.length > 0, "At least one role must be specified in @Roles annotation!");
				for (Role r : values) {
					roles.add(r.value().toLowerCase());
				}
			}
		}

		return roles.toArray(new String[roles.size()]);
	}

	public String[] getRolesAllowed(Class<?> clazz) {
		Map<Class<?>, Annotation> annotations = Metadata.classAnnotations(clazz);
		return getRolesAllowed(annotations);
	}

	public String[] getRolesAllowed(Method method) {
		Map<Class<?>, Annotation> annotations = Metadata.methodAnnotations(method);
		return getRolesAllowed(annotations);
	}

	public boolean canAccessClass(String username, Class<?> clazz) {
		return true;
	}

	public boolean hasRole(String username, String role, Class<?> clazz, Object record) {

		if (Roles.ANYBODY.equalsIgnoreCase(role)) {
			return true;
		}

		if (U.isEmpty(username) || U.isEmpty(role)) {
			return false;
		}

		if (record != null) {

			if (role.equalsIgnoreCase(Roles.OWNER)) {
				return isOwnerOf(username, record);
			}

			if (role.equalsIgnoreCase(Roles.SHARED_WITH)) {
				return isSharedWith(username, record);
			}
		}

		return hasRole(username, role);
	}

	protected boolean hasSpecialRoleInDevMode(String username, String role) {
		return false;
	}

	protected boolean hasRole(String username, String role) {
		if (hasSpecialRoleInDevMode(username, role)) {
			return true;
		}

		if (role.equalsIgnoreCase(Roles.LOGGED_IN)) {
			return !U.isEmpty(username);
		}

		return Roles.getRolesFor(username).contains(role.toLowerCase());
	}

	public boolean isAdmin(String username) {
		return hasRole(username, Roles.ADMIN, null, null);
	}

	public boolean isManager(String username) {
		return hasRole(username, Roles.MANAGER, null, null);
	}

	public boolean isModerator(String username) {
		return hasRole(username, Roles.MODERATOR, null, null);
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

	public boolean isOwnerOf(String username, Object record) {
		if (U.isEmpty(username) || record == null) {
			return false;
		}

		Object owner = Beany.getPropValue(record, "createdBy", null);

		return owner instanceof String && username.equalsIgnoreCase((String) owner);
	}

	public boolean isSharedWith(String username, Object record) {
		if (U.isEmpty(username) || record == null) {
			return false;
		}

		Object sharedWith = Beany.getPropValue(record, "sharedWith", null);

		if (sharedWith != null && sharedWith instanceof Collection<?>) {
			for (Object user : (Collection<?>) sharedWith) {
				if (username.equalsIgnoreCase(Beany.getPropValue(user, "username", ""))) {
					return true;
				}
			}
		}

		return false;
	}

}
