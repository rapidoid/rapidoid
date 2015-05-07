package org.rapidoidx.db;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-x-db
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public abstract class DAO<E> {

	private final Class<E> clazz;

	/**
	 * e.g. IF PersonService extends DAO&lt;Person&gt; THEN entity == Person
	 */
	private static Class<?> inferEntityType(Class<? extends DAO<?>> daoClass) {

		U.must(daoClass.getSuperclass() == DAO.class, "Expected DAO to be superclass of %s, but found: %s!", daoClass,
				daoClass.getSuperclass());

		ParameterizedType genDao = Cls.generic(daoClass.getGenericSuperclass());

		U.must(genDao != null && genDao.getActualTypeArguments().length > 0, "Cannot infer entity type for: %s",
				daoClass);

		Type arg = genDao.getActualTypeArguments()[0];

		return Cls.clazz(arg);
	}

	@SuppressWarnings("unchecked")
	public DAO() {
		this.clazz = (Class<E>) inferEntityType((Class<? extends DAO<?>>) getClass());
	}

	public DAO(Class<E> clazz) {
		this.clazz = clazz;
	}

	public Class<E> getEntityType() {
		return clazz;
	}

	public long insert(E record) {
		return DB.insert(record);
	}

	public void update(long id, E record) {
		DB.update(id, record);
	}

	public void delete(long id) {
		DB.delete(id);
	}

	public void delete(E record) {
		DB.delete(record);
	}

	public E get(long id) {
		return DB.get(id, clazz);
	}

	public List<E> all() {
		return DB.getAll(clazz);
	}

	public List<E> page(int page) {
		return U.page(DB.getAll(clazz), page, 20);
	}

	public <T> T read(long id, String column) {
		return DB.readColumn(id, column);
	}

}
