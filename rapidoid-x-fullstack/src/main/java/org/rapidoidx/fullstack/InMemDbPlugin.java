package org.rapidoidx.fullstack;

/*
 * #%L
 * rapidoid-x-fullstack
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.plugins.spec.DBPlugin;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class InMemDbPlugin implements DBPlugin {

	@Override
	public long insert(Object entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(Object entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(long id, Object entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public long persist(Object record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long insertOrGetId(Object record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> T get(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(long id, Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getIfExists(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> getAll(Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> List<E> getAll(long... ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> List<E> getAll(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long refresh(Object entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Object entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> List<T> find(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> find(Class<T> clazz, Predicate<T> match, Comparator<T> orderBy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> List<E> find(Predicate<E> match) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <E> E entity(Class<E> entityType, Map<String, ?> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> List<E> query(Class<E> clazz, String query, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <RESULT> RESULT sql(String sql, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E> void each(Operation<E> lambda) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transaction(Runnable transaction, boolean readOnly) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transaction(Runnable tx, boolean readonly, Callback<Void> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAllData() {
		// TODO Auto-generated method stub

	}

}
