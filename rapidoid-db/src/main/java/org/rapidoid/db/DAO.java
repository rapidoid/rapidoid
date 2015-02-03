package org.rapidoid.db;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-db
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

@Authors("Nikolche Mihajlovski")
public abstract class DAO<E> {

	private final Class<E> clazz;

	/**
	 * e.g. IF PersonService extends DAO&lt;Person&gt; THEN entity == Person
	 */
	private static Class<?> inferEntityType(Class<? extends DAO<?>> daoClass) {

		U.must(daoClass.getSuperclass() == DAO.class, "Expected DAO to be superclass of %s, but found: %s!", daoClass,
				daoClass.getSuperclass());

		ParameterizedType genDao = Cls.generic(daoClass.getGenericSuperclass());

		U.must(genDao != null && genDao.getActualTypeArguments().length > 0, "Cannot infer entity type for: %s",
				daoClass);

		Type arg = genDao.getActualTypeArguments()[0];

		return Cls.clazz(arg);
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

	public long insert(E record) {
		return DB.insert(record);
	}

	public void update(long id, E record) {
		DB.update(id, record);
	}

	public void delete(long id) {
		DB.delete(id);
	}

	public void delete(E record) {
		DB.delete(record);
	}

	public E get(long id) {
		return DB.get(id, clazz);
	}

	public List<E> getAll() {
		return DB.getAll(clazz);
	}

	public <T> T read(long id, String column) {
		return DB.readColumn(id, column);
	}

}
