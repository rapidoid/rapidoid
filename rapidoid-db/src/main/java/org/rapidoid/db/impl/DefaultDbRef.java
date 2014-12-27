package org.rapidoid.db.impl;

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

import org.rapidoid.db.Db;
import org.rapidoid.db.Ref;

import com.fasterxml.jackson.annotation.JsonValue;

public class DefaultDbRef<E> implements Ref<E> {

	private final Db db;

	private long id;

	public DefaultDbRef(Db db) {
		this(db, -1);
	}

	public DefaultDbRef(Db db, long id) {
		this.db = db;
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get() {
		return (E) (id > 0 ? db.get(id) : null);
	}

	@Override
	public void set(E value) {
		this.id = db.persist(value);
	}

	@JsonValue
	public long id() {
		return id;
	}

}
