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
		return security.hasRole(username, role);
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
		Set<String> rolesAllowed = getClassRolesAllowed(clazz);

		if (!rolesAllowed.isEmpty() && U.isEmpty(username)) {
			return false;
		}

		for (String role : rolesAllowed) {
			if (hasRole(username, role)) {
				return true;
			}
		}

		return rolesAllowed.isEmpty();
	}

	private static Set<String> getClassRolesAllowed(Class<?> clazz) {
		// TODO use caching
		return security.rolesAllowedForClass(clazz);
	}

	public static DataPermissions classPermissions(String username, Class<?> clazz) {
		U.notNull(clazz, "class");

		if (!canAccessClass(username, clazz)) {
			return DataPermissions.NONE;
		}

		return security.classPermissions(username, clazz);
	}

	public static DataPermissions recordPermissions(String username, Object record) {
		U.notNull(record, "record");

		DataPermissions classPerm = classPermissions(username, record.getClass());

		if (classPerm == DataPermissions.NONE) {
			return DataPermissions.NONE;
		}

		return security.recordPermissions(username, record).and(classPerm);
	}

	public static DataPermissions fieldPermissions(String username, Object record, String fieldName) {
		U.notNull(record, "record");
		U.notNull(fieldName, "field name");

		DataPermissions recordPerm = recordPermissions(username, record);

		if (recordPerm == DataPermissions.NONE) {
			return DataPermissions.NONE;
		}

		return security.fieldPermissions(username, record, fieldName).and(recordPerm);
	}

}
