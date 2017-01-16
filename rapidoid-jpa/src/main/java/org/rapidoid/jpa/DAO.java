package org.rapidoid.jpa;

import org.rapidoid.RapidoidThing;
import org.rapidoid.u.U;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/*
 * #%L
 * rapidoid-jpa
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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public abstract class DAO<E> extends RapidoidThing {

	private final Class<E> clazz;

	/**
	 * e.g. IF PersonService extends DAO&lt;Person&gt; THEN entity == Person
	 */
	private static Class<?> inferEntityType(Class<? extends DAO<?>> daoClass) {

		U.must(daoClass.getSuperclass() == DAO.class, "Expected DAO to be superclass of %s, but found: %s!", daoClass,
			daoClass.getSuperclass());

		Type type = daoClass.getGenericSuperclass();
		ParameterizedType genericDao = (type instanceof ParameterizedType) ? ((ParameterizedType) type) : null;

		U.must(genericDao != null && genericDao.getActualTypeArguments().length > 0,
			"Cannot infer entity type for: %s", daoClass);

		Type arg = genericDao.getActualTypeArguments()[0];

		return arg instanceof Class ? (Class<?>) arg : Object.class;
	}

	@SuppressWarnings("unchecked")
	public DAO() {
		this.clazz = (Class<E>) inferEntityType((Class<? extends DAO<?>>) getClass());
	}

	public DAO(Class<E> clazz) {
		this.clazz = clazz;
	}

	public Class<E> getEntityType() {
		return clazz;
	}

	public Object insert(E record) {
		return JPA.insert(record);
	}

	public void update(E record) {
		JPA.update(record);
	}

	public void deleteById(Object id) {
		JPA.delete(clazz, id);
	}

	public void delete(E record) {
		JPA.delete(record);
	}

	public E get(Object id) {
		return JPA.get(clazz, id);
	}

	public List<E> all() {
		return JPA.of(clazz).all();
	}

}
