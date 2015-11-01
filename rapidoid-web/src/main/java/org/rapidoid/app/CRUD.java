package org.rapidoid.app;

/*
 * #%L
 * rapidoid-web
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.DELETE;
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.PUT;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class CRUD<E> {

	private final Class<E> clazz;

	/**
	 * e.g. IF PersonService extends CRUD&lt;Person&gt; THEN entity == Person
	 */
	private static Class<?> inferEntityType(Class<? extends CRUD<?>> CRUDClass) {

		U.must(CRUDClass.getSuperclass() == CRUD.class, "Expected CRUD to be superclass of %s, but found: %s!",
				CRUDClass, CRUDClass.getSuperclass());

		Type type = CRUDClass.getGenericSuperclass();
		ParameterizedType genericCRUD = (type instanceof ParameterizedType) ? ((ParameterizedType) type) : null;

		U.must(genericCRUD != null && genericCRUD.getActualTypeArguments().length > 0,
				"Cannot infer entity type for: %s", CRUDClass);

		Type arg = genericCRUD.getActualTypeArguments()[0];

		return arg instanceof Class ? (Class<?>) arg : Object.class;
	}

	@SuppressWarnings("unchecked")
	public CRUD() {
		this.clazz = (Class<E>) inferEntityType((Class<? extends CRUD<?>>) getClass());
	}

	public CRUD(Class<E> clazz) {
		this.clazz = clazz;
	}

	public Class<E> getEntityType() {
		return clazz;
	}

	@GET(uri = "/get")
	public E get(String id) {
		return DB.get(clazz, id);
	}

	@POST(uri = "/insert")
	public String insert(E record) {
		return DB.insert(record);
	}

	@POST(uri = "/update")
	@PUT
	public void update(String id, E record) {
		DB.update(id, record);
	}

	@POST(uri = "/delete")
	@DELETE
	public void delete(String id) {
		DB.delete(id);
	}

	@GET(uri = "/all")
	public Iterable<E> all() {
		return DB.getAll(clazz);
	}

	@GET(uri = "/page")
	public Iterable<E> page(int page) {
		return DB.getAll(clazz, page, 20);
	}

}
