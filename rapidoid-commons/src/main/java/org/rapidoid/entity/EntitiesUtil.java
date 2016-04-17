package org.rapidoid.entity;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DbEntity;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.scan.Scan;
import org.rapidoid.u.U;

import java.lang.reflect.Proxy;
import java.util.Map;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
@Since("3.0.0")
public class EntitiesUtil extends RapidoidThing {

	@SuppressWarnings("unchecked")
	public static synchronized <E> Class<E> getEntityType(String simpleTypeName) {

		for (Class<?> type : Scan.annotated(DbEntity.class).loadAll()) {
			if (type.getSimpleName().equalsIgnoreCase(simpleTypeName)) {
				return (Class<E>) type;
			}
		}

		for (Class<?> type : Scan.annotated(Scaffold.class).loadAll()) {
			if (type.getSimpleName().equalsIgnoreCase(simpleTypeName)) {
				return (Class<E>) type;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <E> Class<E> getEntityTypeFor(Class<E> clazz) {
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

	public static <E> E create(Class<E> clazz) {
		return create(clazz, null);
	}

	@SuppressWarnings("unchecked")
	public static <E> E create(Class<E> clazz, Map<String, ?> properties) {
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
