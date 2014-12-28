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
import org.rapidoid.db.DbRef;
import org.rapidoid.db.DbRelationTo;
import org.rapidoid.util.U;

import com.fasterxml.jackson.annotation.JsonValue;

public class DefaultDbRef<E> implements DbRef<E>, DbRelationTo {

	private static final long serialVersionUID = -1239566356630772624L;

	private final Db db;

	private final String relation;

	private long id;

	public DefaultDbRef(Db db, String relation) {
		this(db, relation, -1);
	}

	public DefaultDbRef(Db db, String relation, long id) {
		this.db = db;
		this.relation = relation;
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get() {
		return (E) (id > 0 ? db.get(id) : null);
	}

	@Override
	public void set(E value) {
		this.id = value != null ? db.persist(value) : -1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((db == null) ? 0 : db.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultDbRef<?> other = (DefaultDbRef<?>) obj;
		if (db == null) {
			if (other.db != null)
				return false;
		} else if (!db.equals(other.db))
			return false;
		if (id != other.id)
			return false;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		return true;
	}

	@JsonValue
	public Object serialized() {
		return U.map("relation", relation, "id", id);
	}

	@Override
	public void addLinkTo(long id) {
		this.id = id;
	}

	@Override
	public void removeLinkTo(long id) {
		if (hasLinkTo(id)) {
			this.id = -1;
		}
	}

	@Override
	public boolean hasLinkTo(long id) {
		return this.id == id;
	}

}
