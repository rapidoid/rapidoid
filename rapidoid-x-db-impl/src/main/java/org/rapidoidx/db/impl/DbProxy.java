package org.rapidoidx.db.impl;

/*
 * #%L
 * rapidoid-x-db-impl
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.rapidoid.entity.IEntity;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;
import org.rapidoidx.db.AbstractRichEntity;
import org.rapidoidx.db.DbColumn;
import org.rapidoidx.db.DbList;
import org.rapidoidx.db.DbRef;
import org.rapidoidx.db.DbSet;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
				|| methodClass.equals(AbstractRichEntity.class) || methodClass.equals(IEntity.class)) {
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
