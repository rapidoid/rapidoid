package org.rapidoidx.db.impl;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.entity.IEntity;
import org.rapidoid.rql.RQL;
import org.rapidoid.u.U;
import org.rapidoid.util.English;
import org.rapidoid.webapp.FindClasses;
import org.rapidoidx.db.DbDsl;
import org.rapidoidx.db.DbSchema;

/*
 * #%L
 * rapidoid-x-db-impl
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbSchemaImpl implements DbSchema {

	private final ConcurrentMap<String, Class<?>> entityTypes = U.concurrentMap();

	private final ConcurrentMap<String, Class<?>> entityTypesPlural = U.concurrentMap();

	public DbSchemaImpl() {
		for (Class<?> entityType : FindClasses.annotated(DbEntity.class)) {
			putEntityType(entityType);
		}
		for (Class<?> entityType : FindClasses.annotated(Scaffold.class)) {
			putEntityType(entityType);
		}
	}

	@Override
	public <E> DbDsl<E> dsl(Class<E> entityType) {
		putEntityType(entityType);

		return null; // FIXME implement this
	}

	private <E> void putEntityType(Class<E> entityType) {
		String type = entityType.getSimpleName().toLowerCase();

		entityTypes.putIfAbsent(type, entityType);
		entityTypesPlural.putIfAbsent(English.plural(type), entityType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Class<E> getEntityType(String typeName) {
		return (Class<E>) entityTypes.get(typeName.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Class<E> getEntityTypeFromPlural(String typeNamePlural) {
		return (Class<E>) entityTypesPlural.get(typeNamePlural.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E entity(Class<E> clazz, Map<String, ?> properties) {
		U.notNull(clazz, "entity class");
		Class<E> entityType = getEntityTypeFor(clazz);
		U.notNull(entityType, "entity type");
		if (entityType.isInterface() && IEntity.class.isAssignableFrom(entityType)) {
			Class<? extends IEntity> cls = (Class<? extends IEntity>) entityType;
			return (E) DbProxy.create(cls, properties);
		} else {
			E entity = Cls.newInstance(entityType);
			Beany.update(entity, (Map<String, Object>) properties, true);
			return entity;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Class<E> getEntityTypeFor(Class<E> clazz) {
		if (IEntity.class.isAssignableFrom(clazz)) {
			if (Proxy.class.isAssignableFrom(clazz)) {
				for (Class<?> interf : clazz.getInterfaces()) {
					if (IEntity.class.isAssignableFrom(interf)) {
						return (Class<E>) interf;
					}
				}
				throw U.rte("Cannot find entity type for: %s!", clazz);
			}
		}
		return clazz;
	}

	@Override
	public Object entity(String rql, Object... args) {
		return RQL.entity(rql, args);
	}

	@Override
	public String toString() {
		return "DbSchemaImpl [entityTypes=" + entityTypes + ", entityTypesPlural=" + entityTypesPlural + "]";
	}

}
