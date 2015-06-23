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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.SerializableBean;
import org.rapidoid.cls.Cls;
import org.rapidoid.util.U;
import org.rapidoidx.db.DbColumn;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbColumnImpl<E> implements DbColumn<E>, SerializableBean<Object> {

	private static final long serialVersionUID = 5047929644817533060L;

	private final Map<String, Object> map;

	private final String name;

	private Class<E> type;

	public DbColumnImpl(Map<String, Object> map, String name, Class<E> type) {
		U.notNull(map, "map");
		U.notNull(name, "name");
		U.notNull(type, "type");

		this.map = map;
		this.name = name;
		this.type = type;
	}

	@Override
	public E get() {
		return Cls.convert(map.get(name), type);
	}

	@Override
	public void set(E value) {
		if (value != null) {
			map.put(name, Cls.convert(value, type));
		} else {
			map.remove(name);
		}
	}

	@Override
	public Object serializeBean() {
		return get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deserializeBean(Object serialized) {
		set((E) serialized);
	}

	@Override
	public int compareTo(DbColumn<E> col) {
		return U.compare(get(), col.get());
	}

	@Override
	public String name() {
		return name;
	}

}
