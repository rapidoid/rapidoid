package org.rapidoid.plugins.impl;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.beany.Beany;
import org.rapidoid.entity.IEntity;
import org.rapidoid.plugins.spec.EntitiesPlugin;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Scan;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-app
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

/**
 * @author Nikolche Mihajlovski
 * @since 3.0.0
 */
public class DefaultEntitiesPlugin implements EntitiesPlugin {

	private final ConcurrentMap<String, Class<?>> entityTypes = U.concurrentMap();

	public DefaultEntitiesPlugin() {
		for (Class<?> entityType : Scan.annotated(DbEntity.class)) {
			putEntityType(entityType);
		}

		for (Class<?> entityType : Scan.annotated(Scaffold.class)) {
			putEntityType(entityType);
		}
	}

	private <E> void putEntityType(Class<E> entityType) {
		String type = entityType.getSimpleName().toLowerCase();

		entityTypes.putIfAbsent(type, entityType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Class<E> getEntityType(String simpleTypeName) {
		return (Class<E>) entityTypes.get(simpleTypeName.toLowerCase());
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
	public <E> E create(Class<E> clazz) {
		return create(clazz, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E create(Class<E> clazz, Map<String, ?> properties) {
		U.notNull(clazz, "entity class");
		Class<E> entityType = getEntityTypeFor(clazz);
		U.notNull(entityType, "entity type");

		E entity = Cls.newInstance(entityType);

		if (properties != null) {
			Beany.update(entity, (Map<String, Object>) properties, true);
		}

		return entity;
	}

}
