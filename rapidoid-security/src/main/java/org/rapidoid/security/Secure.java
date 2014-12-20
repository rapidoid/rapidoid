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

import java.util.Collection;
import java.util.Set;

import org.rapidoid.util.Constants;
import org.rapidoid.util.U;

public class Secure implements Constants {

	private static AppSecurity security = U.customizable(AppSecurity.class);

	public static Set<String> rolesAllowedForClass(Class<?> clazz) {
		return security.rolesAllowedForClass(clazz);
	}

	public static boolean canAccessClass(Class<?> clazz, Collection<String> roles) {
		return security.canAccessClass(clazz, roles);
	}

	public static boolean canAccessClass(Class<?> clazz, String username) {
		return security.canAccessClass(clazz, username);
	}

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

	public static DataPermissions typePermissions(String username, String type) {
		return security.typePermissions(username, type);
	}

	public static DataPermissions recordPermissions(String username, Object record) {
		return security.recordPermissions(username, record);
	}

	public static DataPermissions fieldPermissions(String username, Object record, String fieldName) {
		return security.fieldPermissions(username, record, fieldName);
	}

}
