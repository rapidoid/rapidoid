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
import java.util.Iterator;

import org.rapidoid.annotation.Authors;
import org.rapidoid.db.Database;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
public class DefaultDbCollection<E> extends DbRelsCommons<E> implements Collection<E> {

	public DefaultDbCollection(Database db, Object holder, String relation, Collection<Long> ids) {
		super(db, holder, relation, ids);
	}

	public boolean add(E e) {
		long id = db.persistedIdOf(e);
		return addId(id);
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean added = false;

		for (Long id : idsOf(c, true)) {
			added |= addId(id);
		}

		return added;
	}

	public boolean contains(Object e) {
		long id = db.getIdOf(e);
		if (id > 0) {
			return hasId(id);
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

	public Iterator<E> iterator() {
		// TODO add support for element removal
		return Collections.unmodifiableList(records()).iterator();
	}

	public boolean remove(Object e) {
		return removeId(db.getIdOf(e));
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;

		for (long id : idsOf(c, false)) {
			changed |= removeId(id);
		}

		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		return retainIds(idsOf(c, false));
	}

	protected Collection<Long> idsOf(Collection<?> c, boolean persist) {
		Collection<Long> recordIds = U.list();

		for (Object record : c) {
			long id = persist ? db.persistedIdOf(record) : db.getIdOf(record);
			recordIds.add(id);
		}

		return recordIds;
	}

}
