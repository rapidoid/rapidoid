package org.rapidoid.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.rapidoid.plugins.Plugins;
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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public abstract class DAO<E> {

	private final Class<E> clazz;

	/**
	 * e.g. IF PersonService extends DAO&lt;Person&gt; THEN entity == Person
	 */
	private static Class<?> inferEntityType(Class<? extends DAO<?>> daoClass) {

		U.must(daoClass.getSuperclass() == DAO.class, "Expected DAO to be superclass of %s, but found: %s!", daoClass,
				daoClass.getSuperclass());

		Type type = daoClass.getGenericSuperclass();
		ParameterizedType genericDao = (type instanceof ParameterizedType) ? ((ParameterizedType) type) : null;

		U.must(genericDao != null && genericDao.getActualTypeArguments().length > 0,
				"Cannot infer entity type for: %s", daoClass);

		Type arg = genericDao.getActualTypeArguments()[0];

		return arg instanceof Class ? (Class<?>) arg : Object.class;
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
		return Plugins.db().insert(record);
	}

	public void update(long id, E record) {
		Plugins.db().update(id, record);
	}

	public void delete(long id) {
		Plugins.db().delete(id);
	}

	public void delete(E record) {
		Plugins.db().delete(record);
	}

	public E get(long id) {
		return Plugins.db().get(id, clazz);
	}

	public List<E> all() {
		return Plugins.db().getAll(clazz);
	}

	public List<E> page(int page) {
		return U.page(Plugins.db().getAll(clazz), page, 20);
	}

}
