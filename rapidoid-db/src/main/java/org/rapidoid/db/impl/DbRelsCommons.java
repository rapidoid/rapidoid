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
import java.util.List;
import java.util.Set;

import org.rapidoid.db.Db;
import org.rapidoid.util.U;

import com.fasterxml.jackson.annotation.JsonValue;

public abstract class DbRelsCommons<E> implements DbRelationInternals {

	protected final Db db;

	protected final String relation;

	private final Collection<Long> ids;

	protected final Set<Long> addedRelations = U.set();

	protected final Set<Long> removedRelations = U.set();

	public DbRelsCommons(Db db, String relation, Collection<Long> ids) {
		this.db = db;
		this.relation = relation;
		this.ids = ids;
	}

	protected void addedRelTo(long id) {
		addedRelations.add(id);
		removedRelations.remove(id);
	}

	protected void removedRelTo(long id) {
		removedRelations.add(id);
		addedRelations.remove(id);
	}

	@Override
	public Set<Long> getAddedRelations() {
		return addedRelations;
	}

	@Override
	public Set<Long> getRemovedRelations() {
		return removedRelations;
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

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DbRelsCommons<E> other = (DbRelsCommons<E>) obj;
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

	protected Collection<Long> getIds() {
		return ids;
	}

	protected long getSingleId() {
		U.must(ids.size() <= 1);
		return !ids.isEmpty() ? ids.iterator().next() : -1;
	}

	@JsonValue
	public Object serialized() {
		return U.map("relation", relation, "ids", ids);
	}

	public void clear() {
		ids.clear();
	}

	public boolean isEmpty() {
		return ids.isEmpty();
	}

	public int size() {
		return ids.size();
	}

	@Override
	public boolean addId(long id) {
		return ids.add(id);
	}

	@Override
	public boolean removeId(long id) {
		return ids.remove(id);
	}

	@Override
	public boolean hasId(long id) {
		return ids.contains(id);
	}

	protected boolean retainIds(Collection<Long> ids) {
		return ids.retainAll(ids);
	}

	private List<Long> getIdsAsList() {
		return (List<Long>) ids;
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

	protected long getIdAt(int index) {
		return getIdsAsList().get(index);
	}

	protected void addIdAt(int index, long id) {
		getIdsAsList().add(index, id);
	}

	protected boolean addIdsAt(int index, Collection<Long> items) {
		return getIdsAsList().addAll(index, items);
	}

	protected long removeIdAt(int index) {
		return getIdsAsList().remove(index);
	}

	protected long setIdAt(int index, long id) {
		return getIdsAsList().set(index, id);
	}

	protected List<Long> getIdSublist(int fromIndex, int toIndex) {
		return getIdsAsList().subList(fromIndex, toIndex);
	}

	protected int indexOfId(long id) {
		return getIdsAsList().indexOf(id);
	}

	protected int lastIndexOfId(long id) {
		return getIdsAsList().lastIndexOf(id);
	}

}
