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

import java.util.HashSet;

import org.rapidoid.db.Db;
import org.rapidoid.db.DbRef;

public class DefaultDbRef<E> extends DbRelsCommons<E> implements DbRef<E> {

	private static final long serialVersionUID = -1239566356630772624L;

	public DefaultDbRef(Db db, String relation) {
		this(db, relation, -1);
	}

	public DefaultDbRef(Db db, String relation, long id) {
		super(db, relation, new HashSet<Long>());
		if (id > 0) {
			addId(id);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get() {
		long id = getSingleId();
		return (E) (id > 0 ? db.get(id) : null);
	}

	@Override
	public void set(E value) {
		if (value == null) {
			clear();
			return;
		}

		long id = db.persist(value);
		long oldId = getSingleId();

		if (id != oldId) {
			clear();
			addId(id);
		}
	}

}
