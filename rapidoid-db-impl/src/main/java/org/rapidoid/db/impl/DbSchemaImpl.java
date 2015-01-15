package org.rapidoid.db.impl;

import java.util.concurrent.ConcurrentMap;

import org.rapidoid.db.DbDsl;
import org.rapidoid.db.DbSchema;
import org.rapidoid.db.Entity;
import org.rapidoid.util.U;

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

public class DbSchemaImpl implements DbSchema {

	private final ConcurrentMap<String, Class<?>> entityTypes = U.concurrentMap();

	private final ConcurrentMap<String, Class<?>> entityTypesPlural = U.concurrentMap();

	@Override
	public <E> DbDsl<E> dsl(Class<E> entityType) {
		String type = entityType.getSimpleName().toLowerCase();

		entityTypes.putIfAbsent(type, entityType);
		entityTypesPlural.putIfAbsent(U.plural(type), entityType);

		return null; // FIXME implement this
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

	@Override
	public <E extends Entity> E create(Class<E> entityType) {
		ConcurrentMap<String, Object> map = U.concurrentMap();
		return DbProxy.create(entityType, map);
	}

}
