package org.rapidoid.security;

/*
 * #%L
 * rapidoid-security
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

import org.rapidoid.security.annotation.Change;
import org.rapidoid.security.annotation.Read;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Metadata;
import org.rapidoid.util.U;

public class Secure implements Constants {

	private static AppSecurity security = U.customizable(AppSecurity.class);

	public static boolean hasRole(String username, String role) {
		return security.hasRole(username, role, null, null);
	}

	public static boolean hasRoleForClass(String username, String role, Class<?> clazz) {
		return security.hasRole(username, role, clazz, null);
	}

	public static boolean hasRoleForRecord(String username, String role, Object record) {
		return security.hasRole(username, role, record.getClass(), record);
	}

	public static boolean isAdmin(String username) {
		return security.isAdmin(username);
	}

	public static boolean isManager(String username) {
		return security.isManager(username);
	}

	public static boolean isModerator(String username) {
		return security.isModerator(username);
	}

	public static boolean isOwnerOf(String username, Object record) {
		return security.isOwnerOf(username, record);
	}

	public static boolean isSharedWith(String username, Object record) {
		return security.isSharedWith(username, record);
	}

	public static boolean canAccessClass(String username, Class<?> clazz) {
		U.notNull(clazz, "class");
		return hasRoleBasedClassAccess(username, clazz) && security.canAccessClass(username, clazz);
	}

	public static boolean hasRoleBasedClassAccess(String username, Class<?> clazz) {
		U.notNull(clazz, "class");
		return hasRoleBasedAccess(username, clazz, null);
	}

	public static boolean hasRoleBasedObjectAccess(String username, Object target) {
		U.notNull(target, "target");
		return hasRoleBasedAccess(username, target.getClass(), target);
	}

	private static boolean hasRoleBasedAccess(String username, Class<?> clazz, Object target) {
		String[] roles = security.getRolesAllowed(clazz);
		return roles.length == 0 || hasAnyRole(username, roles, clazz, target);
	}

	public static boolean hasAnyRole(String username, String[] roles, Class<?> clazz, Object target) {
		for (String role : roles) {
			if (security.hasRole(username, role, clazz, target)) {
				return true;
			}
		}

		return false;
	}

	public static DataPermissions getDataPermissions(String username, Class<?> clazz, Object target, String propertyName) {
		U.notNull(clazz, "class");

		if (!hasRoleBasedAccess(username, clazz, target)) {
			return DataPermissions.NONE;
		}

		Read read = Metadata.fieldAnnotation(clazz, propertyName, Read.class);
		Change change = Metadata.fieldAnnotation(clazz, propertyName, Change.class);

		if (read == null && change == null) {
			return DataPermissions.ALL;
		}

		boolean canRead = read != null && hasAnyRole(username, read.value(), clazz, target);
		boolean canChange = change != null && hasAnyRole(username, change.value(), clazz, target);

		return DataPermissions.from(canRead, canChange);
	}

}
