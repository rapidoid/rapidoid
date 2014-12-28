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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.rapidoid.db.Db;
import org.rapidoid.db.DbList;

public class DefaultDbList<E> extends DefaultDbCollection<E> implements DbList<E> {

	public DefaultDbList(Db db, String relation) {
		super(db, relation, new ArrayList<Long>());
	}

	public DefaultDbList(Db db, String relation, List<? extends Number> ids) {
		this(db, relation);
		for (Number id : ids) {
			ids().add(id.longValue());
		}
	}

	public void add(int index, E e) {
		long id = db.persist(e);
		ids().add(index, id);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		return ids().addAll(index, idsOf(c, true));
	}

	public E get(int index) {
		return db.get(ids().get(index));
	}

	public int indexOf(Object e) {
		return ids().indexOf(db.getIdOf(e));
	}

	public int lastIndexOf(Object e) {
		return ids().lastIndexOf(db.getIdOf(e));
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
		return db.get(ids().remove(index));
	}

	public E set(int index, E e) {
		long id = db.persist(e);
		return db.get(ids().set(index, id));
	}

	public List<E> subList(int fromIndex, int toIndex) {
		return new DefaultDbList<E>(db, relation, ids().subList(fromIndex, toIndex));
	}

	public List<Long> ids() {
		return (List<Long>) ids;
	}

}
