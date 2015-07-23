package org.rapidoid.security;

/*
 * #%L
 * rapidoid-security
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Metadata;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
import org.rapidoid.security.annotation.CanChange;
import org.rapidoid.security.annotation.CanDelete;
import org.rapidoid.security.annotation.CanInsert;
import org.rapidoid.security.annotation.CanManage;
import org.rapidoid.security.annotation.CanRead;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Secure implements Constants {

	private static AppSecurity security = Cls.customizable(AppSecurity.class);

	public static boolean hasRole(String username, String role) {
		return security.hasRole(username, role, null, null);
	}

	public static boolean hasRoleForClass(String username, String role, Class<?> clazz) {
		return security.hasRole(username, role, Cls.unproxy(clazz), null);
	}

	public static boolean hasRoleForRecord(String username, String role, Object record) {
		return security.hasRole(username, role, Cls.unproxy(record.getClass()), record);
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
		clazz = Cls.unproxy(clazz);
		return hasRoleBasedClassAccess(username, clazz) && security.canAccessClass(username, clazz);
	}

	public static boolean hasRoleBasedClassAccess(String username, Class<?> clazz) {
		U.notNull(clazz, "class");
		clazz = Cls.unproxy(clazz);
		return hasRoleBasedAccess(username, clazz, null);
	}

	public static boolean hasRoleBasedObjectAccess(String username, Object target) {
		U.notNull(target, "target");
		return hasRoleBasedAccess(username, Cls.unproxy(target.getClass()), target);
	}

	private static boolean hasRoleBasedAccess(String username, Class<?> clazz, Object target) {
		clazz = Cls.unproxy(clazz);
		String[] roles = security.getRolesAllowed(clazz);
		return roles.length == 0 || hasAnyRole(username, roles, clazz, target);
	}

	public static boolean hasAnyRole(String username, String[] roles, Class<?> clazz, Object target) {
		clazz = Cls.unproxy(clazz);
		for (String role : roles) {
			if (security.hasRole(username, role, clazz, target)) {
				return true;
			}
		}

		return false;
	}

	public static DataPermissions getPropertyPermissions(String username, Class<?> clazz, Object target,
			String propertyName) {
		U.notNull(clazz, "class");
		clazz = Cls.unproxy(clazz);

		if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)
				|| Object[].class.isAssignableFrom(clazz)) {
			return DataPermissions.ALL;
		}

		if (!hasRoleBasedAccess(username, clazz, target)) {
			return DataPermissions.NONE;
		}

		CanRead canRead = Metadata.propAnnotation(clazz, propertyName, CanRead.class);
		CanInsert canInsert = Metadata.propAnnotation(clazz, propertyName, CanInsert.class);
		CanChange canChange = Metadata.propAnnotation(clazz, propertyName, CanChange.class);
		CanDelete canDelete = Metadata.propAnnotation(clazz, propertyName, CanDelete.class);
		CanManage canManage = Metadata.propAnnotation(clazz, propertyName, CanManage.class);

		if (canRead == null && canInsert == null && canChange == null && canDelete == null && canManage == null) {
			return DataPermissions.ALL;
		}

		boolean read = canRead == null || hasAnyRole(username, canRead.value(), clazz, target);

		boolean insert = canInsert != null && hasAnyRole(username, canInsert.value(), clazz, target);
		boolean change = canChange != null && hasAnyRole(username, canChange.value(), clazz, target);
		boolean delete = canDelete != null && hasAnyRole(username, canDelete.value(), clazz, target);

		boolean manage = canManage != null && hasAnyRole(username, canManage.value(), clazz, target);
		insert |= manage;
		change |= manage;
		delete |= manage;

		return DataPermissions.from(read, insert, change, delete);
	}

	public static DataPermissions getClassPermissions(String username, Class<?> clazz) {
		U.notNull(clazz, "class");
		clazz = Cls.unproxy(clazz);

		if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)
				|| Object[].class.isAssignableFrom(clazz)) {
			return DataPermissions.ALL;
		}

		if (!hasRoleBasedAccess(username, clazz, null)) {
			return DataPermissions.NONE;
		}

		CanRead canRead = Metadata.classAnnotation(clazz, CanRead.class);
		CanInsert canInsert = Metadata.classAnnotation(clazz, CanInsert.class);
		CanChange canChange = Metadata.classAnnotation(clazz, CanChange.class);
		CanDelete canDelete = Metadata.classAnnotation(clazz, CanDelete.class);
		CanManage canManage = Metadata.classAnnotation(clazz, CanManage.class);

		if (canRead == null && canInsert == null && canChange == null && canDelete == null && canManage == null) {
			return DataPermissions.ALL;
		}

		boolean read = canRead == null || hasAnyRole(username, canRead.value(), clazz, null);

		boolean insert = canInsert != null && hasAnyRole(username, canInsert.value(), clazz, null);
		boolean change = canChange != null && hasAnyRole(username, canChange.value(), clazz, null);
		boolean delete = canDelete != null && hasAnyRole(username, canDelete.value(), clazz, null);

		boolean manage = canManage != null && hasAnyRole(username, canManage.value(), clazz, null);
		insert |= manage;
		change |= manage;
		delete |= manage;

		return DataPermissions.from(read, insert, change, delete);
	}

	public static DataPermissions getObjectPermissions(String username, Object target) {
		U.notNull(target, "target");
		Class<?> clazz = target.getClass();
		clazz = Cls.unproxy(clazz);

		if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)
				|| Object[].class.isAssignableFrom(clazz)) {
			return DataPermissions.ALL;
		}

		if (!hasRoleBasedAccess(username, clazz, null)) {
			return DataPermissions.NONE;
		}

		CanRead canRead = Metadata.classAnnotation(clazz, CanRead.class);
		CanInsert canInsert = Metadata.classAnnotation(clazz, CanInsert.class);
		CanChange canChange = Metadata.classAnnotation(clazz, CanChange.class);
		CanDelete canDelete = Metadata.classAnnotation(clazz, CanDelete.class);
		CanManage canManage = Metadata.classAnnotation(clazz, CanManage.class);

		if (canRead == null && canInsert == null && canChange == null && canDelete == null && canManage == null) {
			return DataPermissions.ALL;
		}

		boolean read = canRead == null || hasAnyRole(username, canRead.value(), clazz, target);

		boolean insert = canInsert != null && hasAnyRole(username, canInsert.value(), clazz, target);
		boolean change = canChange != null && hasAnyRole(username, canChange.value(), clazz, target);
		boolean delete = canDelete != null && hasAnyRole(username, canDelete.value(), clazz, target);

		boolean manage = canManage != null && hasAnyRole(username, canManage.value(), clazz, target);
		insert |= manage;
		change |= manage;
		delete |= manage;

		return DataPermissions.from(read, insert, change, delete);
	}

	public static List<String> getUserRoles(String username) {
		return security.getUserRoles(username);
	}

	public static boolean canRead(String username, Object record) {
		return hasRoleBasedObjectAccess(username, record) && getObjectPermissions(username, record).read;
	}

	public static boolean canInsert(String username, Object record) {
		return hasRoleBasedObjectAccess(username, record) && getObjectPermissions(username, record).insert;
	}

	public static boolean canUpdate(String username, Object record) {
		return hasRoleBasedObjectAccess(username, record) && getObjectPermissions(username, record).change;
	}

	public static boolean canDelete(String username, Object record) {
		return hasRoleBasedObjectAccess(username, record) && getObjectPermissions(username, record).delete;
	}

	public static boolean canReadProperty(String username, Object record, String property) {
		return hasRoleBasedObjectAccess(username, record) && getObjectPermissions(username, record).read
				&& getPropertyPermissions(username, record.getClass(), record, property).read;
	}

	public static boolean canUpdateProperty(String username, Object record, String property) {
		return hasRoleBasedObjectAccess(username, record) && getObjectPermissions(username, record).change
				&& getPropertyPermissions(username, record.getClass(), record, property).change;
	}

	public static void resetInvisibleProperties(String username, Object record) {
		for (Prop prop : Beany.propertiesOf(record)) {
			if (!getPropertyPermissions(username, record.getClass(), record, prop.getName()).read) {
				prop.reset(record);
			}
		}
	}

}
