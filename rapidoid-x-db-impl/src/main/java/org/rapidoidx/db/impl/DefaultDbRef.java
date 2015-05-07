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

import java.util.HashSet;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoidx.db.Database;
import org.rapidoidx.db.DbRef;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DefaultDbRef<E> extends DbRelsCommons<E> implements DbRef<E> {

	private static final long serialVersionUID = -1239566356630772624L;

	public DefaultDbRef(Database db, Object holder, String relation) {
		this(db, holder, relation, -1);
	}

	public DefaultDbRef(Database db, Object holder, String relation, long id) {
		super(db, holder, relation, new HashSet<Long>());
		if (id > 0) {
			initId(id);
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

		long id = db.persistedIdOf(value);
		long oldId = getSingleId();

		if (id != oldId) {
			clear();
			addId(id);
		}
	}

	@Override
	public boolean eq(Object obj) {
		return U.eq(get(), obj);
	}

}
