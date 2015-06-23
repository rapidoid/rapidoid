package org.rapidoid.app;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.DB;
import org.rapidoid.plugins.Entities;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public abstract class AbstractEntityScreenGeneric extends Screen {

	protected final Class<?> entityType;

	protected final Object entity;

	public AbstractEntityScreenGeneric(Class<?> entityType) {
		this.entityType = entityType;
		this.entity = Entities.create(entityType);
	}

	@SuppressWarnings("unchecked")
	protected <T> T entity() {
		long id = Long.parseLong(ctx().pathSegment(1));
		Object entity = DB.getIfExists(entityType, id);

		if (entity == null) {
			throw ctx().notFound();
		}

		return (T) entity;
	}

	@Override
	public String toString() {
		return "AbstractEntityScreenGeneric [entityType=" + entityType + ", entity=" + entity + "]";
	}

}
