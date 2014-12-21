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

import java.util.Set;

import org.rapidoid.util.Constants;
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

	public static boolean canAccessClass(String username, Class<?> clazz) {
		U.notNull(clazz, "class");
		return hasRoleBasedAccess(username, clazz) && security.canAccessClass(username, clazz);
	}

	public static boolean hasRoleBasedAccess(String username, Class<?> clazz) {
		return getRoleBasedDataPermissions(username, clazz, null, null).read;
	}

	public static DataPermissions getRoleBasedDataPermissions(String username, Class<?> clazz, Object record,
			String propertyName) {

		Set<RoleBasedAccess> rolesAllowed = getClassRolesAllowed(clazz, record, propertyName);

		if (!rolesAllowed.isEmpty() && U.isEmpty(username)) {
			return DataPermissions.NONE;
		}

		DataPermissions perm = rolesAllowed.isEmpty() ? DataPermissions.ALL : DataPermissions.NONE;

		for (RoleBasedAccess roleAccess : rolesAllowed) {
			if (security.hasRole(username, roleAccess.role, clazz, record)) {
				// disjunction of all permissions: e.g. @Moderator(edit=true) || @Manager(delete=true)
				perm = perm.or(roleAccess.dataPermissions());
			}
		}

		return perm;
	}

	private static Set<RoleBasedAccess> getClassRolesAllowed(Class<?> clazz, Object record, String propertyName) {
		// TODO use caching
		return security.rolesAllowedForClass(clazz, record, propertyName);
	}

	public static DataPermissions classPermissions(String username, Class<?> clazz) {
		U.notNull(clazz, "class");

		DataPermissions dataAccess = getRoleBasedDataPermissions(username, clazz, null, null);

		return security.classPermissions(username, clazz).and(dataAccess);
	}

	public static DataPermissions recordPermissions(String username, Object record) {
		U.notNull(record, "record");

		DataPermissions classPerm = classPermissions(username, record.getClass());

		DataPermissions recordAccess = getRoleBasedDataPermissions(username, record.getClass(), record, null);

		return security.recordPermissions(username, record).and(classPerm).and(recordAccess);
	}

	public static DataPermissions propertyPermissions(String username, Object record, String propertyName) {
		U.notNull(record, "record");
		U.notNull(propertyName, "property name");

		DataPermissions recordPerm = recordPermissions(username, record);

		DataPermissions fieldAccess = getRoleBasedDataPermissions(username, record.getClass(), record, propertyName);

		return security.propertyPermissions(username, record, propertyName).and(recordPerm).and(fieldAccess);
	}

}
