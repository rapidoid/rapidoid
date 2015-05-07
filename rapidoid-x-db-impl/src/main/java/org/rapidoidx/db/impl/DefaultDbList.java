package org.rapidoidx.db.impl;

/*
 * #%L
 * rapidoid-x-db-impl
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.db.Database;
import org.rapidoidx.db.DbList;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DefaultDbList<E> extends DefaultDbCollection<E> implements DbList<E> {

	public DefaultDbList(Database db, Object holder, String relation) {
		super(db, holder, relation, new ArrayList<Long>());
	}

	public DefaultDbList(Database db, Object holder, String relation, List<? extends Number> ids) {
		this(db, holder, relation);
		initIds(ids);
	}

	public void add(int index, E e) {
		long id = db.persistedIdOf(e);
		addIdAt(index, id);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		return addIdsAt(index, idsOf(c, true));
	}

	public E get(int index) {
		return db.get(getIdAt(index));
	}

	public int indexOf(Object e) {
		long id = db.getIdOf(e);
		return indexOfId(id);
	}

	public int lastIndexOf(Object e) {
		long id = db.getIdOf(e);
		return lastIndexOfId(id);
	}

	public ListIterator<E> listIterator() {
		// TODO add support for element removal
		return Collections.unmodifiableList(records()).listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		// TODO add support for element removal
		return Collections.unmodifiableList(records()).listIterator(index);
	}

	public E remove(int index) {
		return db.get(removeIdAt(index));
	}

	public E set(int index, E e) {
		long id = db.persistedIdOf(e);
		return db.get(setIdAt(index, id));
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return new DefaultDbList<E>(db, holder, name, getIdSublist(fromIndex, toIndex));
	}

}
