package org.rapidoid.db;

import java.util.List;

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

public abstract class CRUD<E> {

	private final Class<E> clazz;

	/**
	 * e.g. com.example.PersonService -> com.example.Person
	 */
	private static Class<?> inferEntityClass(String className) {
		String type;
		if (className.endsWith("Service")) {
			type = className.substring(0, className.length() - 7);
		} else if (className.endsWith("Controller")) {
			type = className.substring(0, className.length() - 10);
		} else if (className.endsWith("CRUD")) {
			type = className.substring(0, className.length() - 4);
		} else {
			throw U.rte("Automatic entity detection requires class name suffix to be 'Service', 'Controller' or 'CRUD'");
		}

		Class<?> entityClass = U.getClassIfExists(type);
		U.must(entityClass != null, "Cannot infer entity class for the service: %s", className);
		return entityClass;
	}

	@SuppressWarnings("unchecked")
	public CRUD() {
		this.clazz = (Class<E>) inferEntityClass(getClass().getCanonicalName());
	}

	public CRUD(Class<E> clazz) {
		this.clazz = clazz;
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

	public E get(long id) {
		return DB.get(id, clazz);
	}

	public List<E> getAll() {
		return DB.getAll(clazz);
	}

	public <T> T read(long id, String column) {
		return DB.read(id, column);
	}

}
