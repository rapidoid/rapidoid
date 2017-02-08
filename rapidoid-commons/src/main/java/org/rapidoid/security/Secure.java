package org.rapidoid.security;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Metadata;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
import org.rapidoid.security.annotation.*;
import org.rapidoid.u.U;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
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
@Since("2.0.0")
public class Secure extends RapidoidThing {

	public static boolean hasRoleForClass(String username, Set<String> roles, String role, Class<?> clazz) {
		return hasRole(username, roles, role, Cls.unproxy(clazz), null);
	}

	public static boolean hasRoleForRecord(String username, Set<String> roles, String role, Object record) {
		return hasRole(username, roles, role, Cls.unproxy(record.getClass()), record);
	}

	public static boolean isOwnerOf(String username, Set<String> roles, Object record) {
		return isOwnerOf(username, record);
	}

	public static boolean isSharedWith(String username, Set<String> roles, Object record) {
		return isSharedWith(username, record);
	}

	public static boolean canAccessClass(String username, Set<String> roles, Class<?> clazz) {
		U.notNull(clazz, "class");
		clazz = Cls.unproxy(clazz);
		return hasRoleBasedClassAccess(username, roles, clazz) && canAccessClass(username, clazz);
	}

	public static boolean canAccessMethod(String username, Set<String> roles, Method method) {
		U.notNull(method, "method");
		Class<?> clazz = method.getDeclaringClass();
		return canAccessClass(username, roles, clazz) && hasRoleBasedMethodAccess(username, roles, method);
	}

	public static boolean hasRoleBasedClassAccess(String username, Set<String> roles, Class<?> clazz) {
		U.notNull(clazz, "class");
		clazz = Cls.unproxy(clazz);
		return hasRoleBasedAccess(username, roles, clazz, null);
	}

	public static boolean hasRoleBasedObjectAccess(String username, Set<String> roles, Object target) {
		U.notNull(target, "target");
		return hasRoleBasedAccess(username, roles, Cls.unproxy(target.getClass()), target);
	}

	private static boolean hasRoleBasedAccess(String username, Set<String> roles, Class<?> clazz, Object target) {
		clazz = Cls.unproxy(clazz);
		Set<String> rolesAllowed = getRolesAllowed(clazz);
		return U.isEmpty(rolesAllowed) || hasAnyRole(username, roles, rolesAllowed, clazz, target);
	}

	public static boolean hasRoleBasedMethodAccess(String username, Set<String> roles, Method method) {
		U.notNull(method, "method");
		Set<String> rolesAllowed = getRolesAllowed(method);
		return U.isEmpty(rolesAllowed) || hasAnyRole(username, roles, rolesAllowed);
	}

	public static boolean hasAnyRole(String username, Set<String> roles, Set<String> targetRoles, Class<?> clazz, Object target) {
		clazz = Cls.unproxy(clazz);
		for (String role : targetRoles) {
			if (hasRole(username, roles, role, clazz, target)) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasAnyRole(String username, Set<String> roles, Set<String> targetRoles) {
		for (String role : targetRoles) {
			if (hasRole(username, roles, role)) {
				return true;
			}
		}

		return false;
	}


	public static DataPermissions getPropertyPermissions(String username, Set<String> roles, Class<?> clazz, Object target,
	                                                     String propertyName) {
		U.notNull(clazz, "class");
		clazz = Cls.unproxy(clazz);

		if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)
			|| Object[].class.isAssignableFrom(clazz)) {
			return DataPermissions.ALL;
		}

		if (!hasRoleBasedAccess(username, roles, clazz, target)) {
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

		boolean read = canRead == null || hasAnyRole(username, roles, roles(canRead.value()), clazz, target);

		boolean insert = canInsert != null && hasAnyRole(username, roles, roles(canInsert.value()), clazz, target);
		boolean change = canChange != null && hasAnyRole(username, roles, roles(canChange.value()), clazz, target);
		boolean delete = canDelete != null && hasAnyRole(username, roles, roles(canDelete.value()), clazz, target);

		boolean manage = canManage != null && hasAnyRole(username, roles, roles(canManage.value()), clazz, target);
		insert |= manage;
		change |= manage;
		delete |= manage;

		return DataPermissions.from(read, insert, change, delete);
	}


	private static Set<String> roles(String[] roles) {
		return U.set(roles);
	}

	public static DataPermissions getClassPermissions(String username, Set<String> roles, Class<?> clazz) {
		U.notNull(clazz, "class");
		clazz = Cls.unproxy(clazz);

		if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)
			|| Object[].class.isAssignableFrom(clazz)) {
			return DataPermissions.ALL;
		}

		if (!hasRoleBasedAccess(username, roles, clazz, null)) {
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

		boolean read = canRead == null || hasAnyRole(username, roles, roles(canRead.value()), clazz, null);

		boolean insert = canInsert != null && hasAnyRole(username, roles, roles(canInsert.value()), clazz, null);
		boolean change = canChange != null && hasAnyRole(username, roles, roles(canChange.value()), clazz, null);
		boolean delete = canDelete != null && hasAnyRole(username, roles, roles(canDelete.value()), clazz, null);

		boolean manage = canManage != null && hasAnyRole(username, roles, roles(canManage.value()), clazz, null);
		insert |= manage;
		change |= manage;
		delete |= manage;

		return DataPermissions.from(read, insert, change, delete);
	}

	public static DataPermissions getObjectPermissions(String username, Set<String> roles, Object target) {
		U.notNull(target, "target");
		Class<?> clazz = target.getClass();
		clazz = Cls.unproxy(clazz);

		if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)
			|| Object[].class.isAssignableFrom(clazz)) {
			return DataPermissions.ALL;
		}

		if (!hasRoleBasedAccess(username, roles, clazz, null)) {
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

		boolean read = canRead == null || hasAnyRole(username, roles, roles(canRead.value()), clazz, target);

		boolean insert = canInsert != null && hasAnyRole(username, roles, roles(canInsert.value()), clazz, target);
		boolean change = canChange != null && hasAnyRole(username, roles, roles(canChange.value()), clazz, target);
		boolean delete = canDelete != null && hasAnyRole(username, roles, roles(canDelete.value()), clazz, target);

		boolean manage = canManage != null && hasAnyRole(username, roles, roles(canManage.value()), clazz, target);
		insert |= manage;
		change |= manage;
		delete |= manage;

		return DataPermissions.from(read, insert, change, delete);
	}

	public static boolean canRead(String username, Set<String> roles, Object record) {
		return hasRoleBasedObjectAccess(username, roles, record) && getObjectPermissions(username, roles, record).read;
	}

	public static boolean canInsert(String username, Set<String> roles, Object record) {
		return hasRoleBasedObjectAccess(username, roles, record) && getObjectPermissions(username, roles, record).insert;
	}

	public static boolean canUpdate(String username, Set<String> roles, Object record) {
		return hasRoleBasedObjectAccess(username, roles, record) && getObjectPermissions(username, roles, record).change;
	}

	public static boolean canDelete(String username, Set<String> roles, Object record) {
		return hasRoleBasedObjectAccess(username, roles, record) && getObjectPermissions(username, roles, record).delete;
	}

	public static boolean canReadProperty(String username, Set<String> roles, Object record, String property) {
		return hasRoleBasedObjectAccess(username, roles, record) && getObjectPermissions(username, roles, record).read
			&& getPropertyPermissions(username, roles, record.getClass(), record, property).read;
	}

	public static boolean canUpdateProperty(String username, Set<String> roles, Object record, String property) {
		return hasRoleBasedObjectAccess(username, roles, record) && getObjectPermissions(username, roles, record).change
			&& getPropertyPermissions(username, roles, record.getClass(), record, property).change;
	}

	public static void resetInvisibleProperties(String username, Set<String> roles, Object record) {
		for (Prop prop : Beany.propertiesOf(record)) {
			if (!getPropertyPermissions(username, roles, record.getClass(), record, prop.getName()).read) {
				prop.reset(record);
			}
		}
	}

	public static Set<String> getRolesAllowed(Map<Class<?>, Annotation> annotations) {

		Set<String> roles = U.set();

		for (Map.Entry<Class<?>, Annotation> e : annotations.entrySet()) {
			Annotation ann = e.getValue();
			Class<? extends Annotation> type = ann.annotationType();

			if (type.equals(Administrator.class)) {
				roles.add(Role.ADMINISTRATOR);
			} else if (type.equals(Manager.class)) {
				roles.add(Role.MANAGER);
			} else if (type.equals(Moderator.class)) {
				roles.add(Role.MODERATOR);
			} else if (type.equals(LoggedIn.class)) {
				roles.add(Role.LOGGED_IN);
			} else if (type.equals(Roles.class)) {
				String[] values = ((Roles) ann).value();
				U.must(values.length > 0, "At least one role must be specified in @Roles annotation!");
				for (String r : values) {
					roles.add(r.toLowerCase());
				}
			}
		}

		return roles;
	}

	public static Set<String> getRolesAllowed(Class<?> clazz) {
		Map<Class<?>, Annotation> annotations = Metadata.classAnnotations(clazz);
		return getRolesAllowed(annotations);
	}

	public static Set<String> getRolesAllowed(Method method) {
		Map<Class<?>, Annotation> annotations = Metadata.methodAnnotations(method);
		return getRolesAllowed(annotations);
	}

	public static boolean canAccessClass(String username, Class<?> clazz) {
		return true;
	}

	public static boolean hasRole(String username, Set<String> roles, String role, Class<?> clazz, Object record) {

		if (Role.ANYBODY.equalsIgnoreCase(role)) {
			return true;
		}

		if (U.isEmpty(username) || U.isEmpty(role)) {
			return false;
		}

		if (record != null) {

			if (role.equalsIgnoreCase(Role.OWNER)) {
				return isOwnerOf(username, record);
			}

			if (role.equalsIgnoreCase(Role.SHARED_WITH)) {
				return isSharedWith(username, record);
			}
		}

		return hasRole(username, roles, role);
	}

	protected static boolean hasSpecialRoleInDevMode(String username, String role) {
		return false;
	}

	protected static boolean hasRole(String username, Set<String> roles, String role) {
		if (hasSpecialRoleInDevMode(username, role)) {
			return true;
		}

		if (role.equalsIgnoreCase(Role.LOGGED_IN)) {
			return !U.isEmpty(username);
		}

		for (String r : roles) {
			if (r.equalsIgnoreCase(role)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAdministrator(String username, Set<String> roles) {
		return hasRole(username, roles, Role.ADMINISTRATOR, null, null);
	}

	public static boolean isManager(String username, Set<String> roles) {
		return hasRole(username, roles, Role.MANAGER, null, null);
	}

	public static boolean isModerator(String username, Set<String> roles) {
		return hasRole(username, roles, Role.MODERATOR, null, null);
	}

	public static DataPermissions classPermissions(String username, Class<?> clazz) {
		return DataPermissions.ALL;
	}

	public static DataPermissions recordPermissions(String username, Object record) {
		return DataPermissions.ALL;
	}

	public static DataPermissions propertyPermissions(String username, Object record, String propertyName) {
		return DataPermissions.ALL;
	}

	public static boolean isOwnerOf(String username, Object record) {
		if (U.isEmpty(username) || record == null) {
			return false;
		}

		Object owner = Beany.getPropValue(record, "createdBy", null);

		return owner instanceof String && username.equalsIgnoreCase((String) owner);
	}

	public static boolean isSharedWith(String username, Object record) {
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
