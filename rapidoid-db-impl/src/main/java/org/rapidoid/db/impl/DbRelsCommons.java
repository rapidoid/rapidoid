package org.rapidoid.db.impl;

/*
 * #%L
 * rapidoid-db-impl
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.beany.SerializableBean;
import org.rapidoid.db.Database;
import org.rapidoid.util.U;

public abstract class DbRelsCommons<E> implements DbRelationInternals, SerializableBean<Map<String, Object>>,
		Comparable<DbRelsCommons<E>> {

	protected final Database db;

	protected Object holder;

	protected String name;

	private final Collection<Long> ids;

	protected final DbRelChangesTracker tracker = new DbRelChangesTracker();

	private final Collection<Long> idsView;

	public DbRelsCommons(Database db, Object holder, String relation, Collection<Long> ids) {
		U.notNull(db, "db");
		U.notNull(relation, "relation");
		U.notNull(ids, "ids");

		this.db = db;
		this.holder = holder;
		this.name = relation;
		this.ids = ids;
		this.idsView = Collections.unmodifiableCollection(ids);
	}

	protected void initIds(Collection<? extends Number> initIds) {
		this.ids.clear();
		for (Number id : initIds) {
			addIdWithoutTracking(id.longValue()); // no tracking on initialization
		}
	}

	protected void initId(long id) {
		this.ids.clear();
		addIdWithoutTracking(id); // no tracking on initialization
	}

	@Override
	public void setHolder(Object holder) {
		this.holder = holder;
	}

	public Object getHolder() {
		return holder;
	}

	public String getName() {
		return name;
	}

	@Override
	public Set<Long> getAddedRelations() {
		return tracker.getAddedRelations();
	}

	@Override
	public Set<Long> getRemovedRelations() {
		return tracker.getRemovedRelations();
	}

	public void addIdWithoutTracking(long id) {
		ids.add(id); // just add the ID, no tracking
	}

	public void removeIdWithoutTracking(long id) {
		ids.remove(id); // just remove the ID, no tracking
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((db == null) ? 0 : db.hashCode());
		result = prime * result + ((ids == null) ? 0 : ids.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	protected long getSingleId() {
		U.must(ids.size() <= 1);
		return !ids.isEmpty() ? ids.iterator().next() : -1;
	}

	public boolean isEmpty() {
		return ids.isEmpty();
	}

	public int size() {
		return ids.size();
	}

	@Override
	public boolean hasId(long id) {
		return ids.contains(id);
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

	protected int indexOfId(long id) {
		return getIdsAsList().indexOf(id);
	}

	protected int lastIndexOfId(long id) {
		return getIdsAsList().lastIndexOf(id);
	}

	protected List<Long> getIdSublist(int fromIndex, int toIndex) {
		// TODO make it modifiable (solve the problem of tracking changes through the sublist)
		return Collections.unmodifiableList(getIdsAsList().subList(fromIndex, toIndex));
	}

	/*
	 * THE FOLLOWING METHODS CHANGE THE IDs (IT IS IMPORTANT TO TRACK THE CHANGES):
	 */

	public void clear() {
		for (long id : ids) {
			tracker.removedRelTo(id);
		}
		ids.clear();
	}

	@Override
	public boolean addId(long id) {
		if (!hasId(id)) {
			boolean changed = ids.add(id);

			if (changed) {
				tracker.addedRelTo(id);
			}

			return changed;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeId(long id) {
		boolean changed = ids.remove(id);

		if (changed) {
			tracker.removedRelTo(id);
		}

		return changed;
	}

	protected boolean retainIds(Collection<Long> retainIds) {
		for (Long id : ids) {
			if (!retainIds.contains(id)) {
				tracker.removedRelTo(id);
			}
		}

		return retainIds.retainAll(retainIds);
	}

	protected boolean addIdAt(int index, long id) {
		if (!hasId(id)) {
			getIdsAsList().add(index, id);
			tracker.addedRelTo(id);
			return true;
		} else {
			return false;
		}
	}

	protected boolean addIdsAt(int index, Collection<Long> idsToAdd) {
		boolean changed = false;

		for (long id : idsToAdd) {
			boolean added = addIdAt(index, id);
			if (added) {
				index++;
			}
			changed |= added;
		}

		return changed;
	}

	protected long removeIdAt(int index) {
		long removedId = getIdsAsList().remove(index);
		tracker.removedRelTo(removedId);
		return removedId;
	}

	protected long setIdAt(int index, long id) {
		long removedId = getIdsAsList().set(index, id);

		// FIXME handle possible duplications

		if (id != removedId) {
			tracker.addedRelTo(id);
			tracker.removedRelTo(removedId);
		}

		return removedId;
	}

	public Collection<Long> getIdsView() {
		return idsView;
	}

	@Override
	public String toString() {
		return "#" + ids.toString();
	}

	@Override
	public Map<String, Object> serializeBean() {
		return U.map("relation", name, "ids", ids);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserializeBean(Map<String, Object> serialized) {
		if (serialized != null) {
			name = (String) serialized.get("relation");
			U.notNull(name, "relation");

			Collection<? extends Number> initialIds = (Collection<? extends Number>) serialized.get("ids");
			U.notNull(initialIds, "ids");
			initIds(initialIds);
		}
	}

	@Override
	public int compareTo(DbRelsCommons<E> rel) {
		return ids.size() - rel.ids.size();
	}

}
