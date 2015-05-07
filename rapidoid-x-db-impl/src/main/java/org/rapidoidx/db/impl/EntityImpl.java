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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Rel;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Metadata;
import org.rapidoid.entity.AbstractEntity;
import org.rapidoid.entity.IEntity;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;
import org.rapidoidx.db.DbColumn;
import org.rapidoidx.db.DbList;
import org.rapidoidx.db.DbRef;
import org.rapidoidx.db.DbSet;
import org.rapidoidx.db.XDB;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class EntityImpl extends AbstractEntity implements IEntity, Serializable {

	private static final long serialVersionUID = -5556123216690345146L;

	@SuppressWarnings("unused")
	private final Class<?> type;

	private final ConcurrentMap<String, DbColumn<?>> columns = U.concurrentMap();

	private final ConcurrentMap<String, DbSet<?>> sets = U.concurrentMap();

	private final ConcurrentMap<String, DbList<?>> lists = U.concurrentMap();

	private final ConcurrentMap<String, DbRef<?>> refs = U.concurrentMap();

	private final ConcurrentMap<String, Object> values = U.concurrentMap();

	private IEntity proxy;

	public EntityImpl(Class<?> type) {
		this.type = type;
	}

	public void setProxy(IEntity proxy) {
		this.proxy = proxy;
	}

	@Override
	public String toString() {
		return Beany.beanToStr(proxy, false);
	}

	public DbColumn<?> column(Method method) {
		U.must(DbColumn.class.isAssignableFrom(method.getReturnType()));

		String name = method.getName();
		DbColumn<?> res = columns.get(name);

		if (res == null) {
			Class<Object> colType = Cls.clazz(Cls.generic(method.getGenericReturnType()).getActualTypeArguments()[0]);
			DbColumn<Object> value = XDB.column(values, method.getName(), colType);
			DbColumn<?> old = columns.putIfAbsent(name, value);
			return U.or(old, value);
		}

		return res;
	}

	public DbSet<?> set(Method method) {
		String name = method.getName();
		DbSet<?> res = sets.get(name);

		if (res == null) {
			DbSet<Object> value = XDB.set(proxy, rel(method).value());
			DbSet<?> old = sets.putIfAbsent(name, value);
			return old != null ? old : value;
		}

		return res;
	}

	public DbList<?> list(Method method) {
		String name = method.getName();
		DbList<?> res = lists.get(name);

		if (res == null) {
			DbList<Object> value = XDB.list(proxy, rel(method).value());
			DbList<?> old = lists.putIfAbsent(name, value);
			return old != null ? old : value;
		}

		return res;
	}

	public DbRef<?> ref(Method method) {
		String name = method.getName();
		DbRef<?> res = refs.get(name);

		if (res == null) {
			DbRef<Object> value = XDB.ref(proxy, rel(method).value());
			DbRef<?> old = refs.putIfAbsent(name, value);
			return old != null ? old : value;
		}

		return res;
	}

	private static Rel rel(Method method) {
		Rel rel = Metadata.get(method.getAnnotations(), Rel.class);
		U.must(rel != null, "@Relation is required for method: %s", method);
		return rel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id() ^ (id() >>> 32));
		return result;
	}

}
