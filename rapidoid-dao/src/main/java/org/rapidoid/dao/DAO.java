package org.rapidoid.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.rapidoid.plugins.db.DB;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-dao
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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public abstract class DAO<E> {

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

	public String insert(E record) {
		return DB.insert(record);
	}

	public void update(String id, E record) {
		DB.update(id, record);
	}

	public void delete(String id) {
		DB.delete(clazz, id);
	}

	public void delete(E record) {
		DB.delete(record);
	}

	public E get(String id) {
		return DB.get(clazz, id);
	}

	public Iterable<E> all() {
		return DB.getAll(clazz);
	}

	public Iterable<E> page(int page) {
		return DB.getAll(clazz, page, 20);
	}

}
