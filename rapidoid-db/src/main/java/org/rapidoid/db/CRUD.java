package org.rapidoid.db;

import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-db
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

	private static Class<?> detectEntity() {
		throw U.notReady(); // FIXME detect and load entity class from this class' name
	}

	@SuppressWarnings("unchecked")
	public CRUD() {
		this.clazz = (Class<E>) detectEntity();
	}

	public CRUD(Class<E> clazz) {
		this.clazz = clazz;
	}

	public long insert(E record) {
		return DB.insert(record);
	}

	public void delete(long id) {
		DB.delete(id);
	}

	public E get(long id) {
		return DB.get(id, clazz);
	}

	public <T> T read(long id, String column) {
		return DB.read(id, column);
	}

	public void update(long id, E record) {
		DB.update(id, record);
	}

}
