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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.rapidoid.db.Db;
import org.rapidoid.db.DbRelationTo;
import org.rapidoid.util.U;

import com.fasterxml.jackson.annotation.JsonValue;

public class DefaultDbCollection<E> implements Collection<E>, DbRelationTo {

	protected final Db db;

	protected String relation;

	protected final Collection<Long> ids;

	public DefaultDbCollection(Db db, String relation, Collection<Long> ids) {
		this.db = db;
		this.relation = relation;
		this.ids = ids;
	}

	public boolean add(E e) {
		long id = db.persist(e);
		return ids.add(id);
	}

	public boolean addAll(Collection<? extends E> c) {
		return ids.addAll(idsOf(c, true));
	}

	public void clear() {
		ids.clear();
	}

	public boolean contains(Object e) {
		long id = db.getIdOf(e);
		if (id > 0) {
			return ids.contains(id);
		} else {
			return false;
		}
	}

	public boolean containsAll(Collection<?> c) {
		for (Object record : c) {
			if (!contains(record)) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return ids.isEmpty();
	}

	public Iterator<E> iterator() {
		// TODO add support for element removal
		return Collections.unmodifiableList(records()).iterator();
	}

	public boolean remove(Object e) {
		return ids.remove(db.getIdOf(e));
	}

	public boolean removeAll(Collection<?> c) {
		return ids.removeAll(idsOf(c, false));
	}

	public boolean retainAll(Collection<?> c) {
		return ids.retainAll(idsOf(c, false));
	}

	public int size() {
		return ids.size();
	}

	public Object[] toArray() {
		return db.getAll(ids).toArray();
	}

	public <T> T[] toArray(T[] arr) {
		return db.getAll(ids).toArray(arr);
	}

	protected List<E> records() {
		return db.<E> getAll(ids);
	}

	protected Collection<Long> idsOf(Collection<?> c, boolean persist) {
		Collection<Long> recordIds = U.list();

		for (Object record : c) {
			long id = persist ? db.persist(record) : db.getIdOf(record);
			recordIds.add(id);
		}

		return recordIds;
	}

	public Collection<Long> ids() {
		return ids;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultDbCollection<?> other = (DefaultDbCollection<?>) obj;
		if (db == null) {
			if (other.db != null)
				return false;
		} else if (!db.equals(other.db))
			return false;
		if (ids == null) {
			if (other.ids != null)
				return false;
		} else if (!ids.equals(other.ids))
			return false;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((db == null) ? 0 : db.hashCode());
		result = prime * result + ((ids == null) ? 0 : ids.hashCode());
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		return result;
	}

	@JsonValue
	public Object serialized() {
		return U.map("relation", relation, "ids", ids);
	}

	@Override
	public void addLinkTo(long id) {
		if (!hasLinkTo(id)) {
			ids.add(id);
		}
	}

	@Override
	public void removeLinkTo(long id) {
		ids.remove(id);
	}

	@Override
	public boolean hasLinkTo(long id) {
		return ids.contains(id);
	}

}
