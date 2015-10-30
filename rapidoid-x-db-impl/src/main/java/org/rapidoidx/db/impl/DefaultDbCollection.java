package org.rapidoidx.db.impl;

/*
 * #%L
 * rapidoid-x-db-impl
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoidx.db.Database;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
