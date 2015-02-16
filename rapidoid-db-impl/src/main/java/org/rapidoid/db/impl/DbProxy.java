package org.rapidoid.db.impl;

/*
 * #%L
 * rapidoid-db-impl
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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.db.DbColumn;
import org.rapidoid.db.DbList;
import org.rapidoid.db.DbRef;
import org.rapidoid.db.DbSet;
import org.rapidoid.db.EntityCommons;
import org.rapidoid.db.IEntity;
import org.rapidoid.db.IEntityCommons;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbProxy implements InvocationHandler, Serializable {

	private static final long serialVersionUID = 8876053750757191711L;

	@SuppressWarnings("unchecked")
	public static <E extends IEntity> E create(Class<E> type, Map<String, ?> values) {
		EntityImpl entity = new EntityImpl(type);

		E proxy = Cls.createProxy(new DbProxy(entity, type), type);
		entity.setProxy(proxy);

		Beany.update(proxy, (Map<String, Object>) values, true);

		return proxy;
	}

	@SuppressWarnings("unchecked")
	public static <E extends IEntity> E create(Class<E> type) {
		return (E) create(type, Collections.EMPTY_MAP);
	}

	private final EntityImpl entity;

	public DbProxy(EntityImpl entity, Class<?> entityType) {
		this.entity = entity;
	}

	public Object invoke(Object target, Method method, Object[] args) throws Throwable {

		Class<?> methodClass = method.getDeclaringClass();

		String name = method.getName();
		Class<?> ret = method.getReturnType();
		Class<?>[] paramTypes = method.getParameterTypes();

		if (methodClass.equals(Object.class) || methodClass.equals(EntityImpl.class)
				|| methodClass.equals(EntityCommons.class) || methodClass.equals(IEntityCommons.class)
				|| methodClass.equals(IEntity.class)) {
			return method.invoke(entity, args);
		}

		boolean returnsCol = ret.equals(DbColumn.class);
		boolean returnsSet = ret.equals(DbSet.class);
		boolean returnsList = ret.equals(DbList.class);
		boolean returnsRef = ret.equals(DbRef.class);
		boolean has0arg = paramTypes.length == 0;

		if (has0arg) {
			if (returnsCol) {
				return entity.column(method);
			} else if (returnsSet) {
				return entity.set(method);
			} else if (returnsList) {
				return entity.list(method);
			} else if (returnsRef) {
				return entity.ref(method);
			}
		}

		// in case something is missed
		throw U.rte("Not implemented: " + name);
	}

}
