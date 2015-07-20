package org.rapidoid.plugins.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.beany.PropertyFilter;
import org.rapidoid.cls.Cls;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Promise;
import org.rapidoid.concurrent.Promises;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.plugins.entities.Entities;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-plugins
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public abstract class DBPluginBase extends AbstractDBPlugin {

	private static final Pattern P_WORD = Pattern.compile("\\w+");

	@SuppressWarnings("serial")
	protected static final PropertyFilter SEARCHABLE_PROPS = new PropertyFilter() {
		@Override
		public boolean eval(Prop prop) throws Exception {
			return Cls
					.isAssignableTo(prop.getType(), Number.class, String.class, Boolean.class, Enum.class, Date.class);
		}
	};

	@Override
	public String persist(Object record) {
		String id = Beany.getIdIfExists(record);
		if (id == null) {
			return insert(record);
		} else {
			update(id, record);
			return id;
		}
	}

	@Override
	public void delete(Object record) {
		delete(record.getClass(), Beany.getId(record));
	}

	@Override
	public void update(Object record) {
		update(Beany.getId(record), record);
	}

	@Override
	public String insertOrGetId(Object record) {
		String id = Beany.getIdIfExists(record);
		if (id == null) {
			return insert(record);
		} else {
			return id;
		}
	}

	@Override
	public <E> List<E> getAll(Class<E> clazz, Iterable<String> ids) {
		List<E> results = new ArrayList<E>();

		for (String id : ids) {
			results.add(this.<E> get(clazz, id));
		}

		return results;
	}

	@Override
	public <E> List<E> getAll(Class<E> clazz, int pageNumber, int pageSize) {
		return U.page(getAll(clazz), pageNumber, pageSize);
	}

	@Override
	public <E> E get(Class<E> clazz, String id) {
		E entity = getIfExists(clazz, id);

		if (entity == null) {
			throw U.rte("Cannot find entity with ID=%s", id);
		}

		return entity;
	}

	@Override
	public <E> E entity(Class<E> entityType, Map<String, ?> properties) {
		return Entities.create(entityType, properties);
	}

	@Override
	public <E> List<E> find(final Class<E> clazz, final Predicate<E> match, final Comparator<E> orderBy) {

		Predicate<E> match2 = new Predicate<E>() {
			@Override
			public boolean eval(E record) throws Exception {
				return (clazz == null || clazz.isAssignableFrom(record.getClass()))
						&& (match == null || match.eval(record));
			}
		};

		return sorted(find(match2), orderBy);
	}

	protected <E> List<E> sorted(List<E> records, Comparator<E> orderBy) {
		if (orderBy != null) {
			Collections.sort(records, orderBy);
		}
		return records;
	}

	@Override
	public <E> List<E> fullTextSearch(String searchPhrase) {
		final String search = searchPhrase.toLowerCase();

		Predicate<E> match = new Predicate<E>() {
			@Override
			public boolean eval(E record) throws Exception {

				if (record.getClass().getSimpleName().toLowerCase().contains(search)) {
					return true;
				}

				for (Prop prop : Beany.propertiesOf(record).select(SEARCHABLE_PROPS)) {
					String s = String.valueOf(prop.get(record)).toLowerCase();
					if (s.contains(search)) {
						return true;
					}
				}
				return false;
			}
		};

		return find(match);
	}

	@Override
	public <E> List<E> query(final Class<E> clazz, final String query, final Object... args) {

		Predicate<E> match = new Predicate<E>() {
			@Override
			public boolean eval(E record) throws Exception {
				return clazz.isAssignableFrom(record.getClass()) && matches(record, query, args);
			}
		};

		return find(match);
	}

	protected boolean matches(Object record, String query, Object... args) {

		if (query == null || query.isEmpty()) {
			return true;
		}

		if (P_WORD.matcher(query).matches() && args.length == 1) {
			Object val = Beany.getPropValue(record, query, null);
			Object arg = args[0];
			return val == arg || (val != null && val.equals(arg));
		}

		throw new RuntimeException("Query not supported: " + query);
	}

	@Override
	public void transaction(final Runnable tx, final boolean readonly, final Callback<Void> callback) {
		Jobs.execute(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				transaction(tx, readonly);
				return null;
			}

		}, callback);
	}

	@Override
	public void transaction(Runnable transaction, boolean readOnly) {
		Promise<Void> promise = Promises.create();
		transaction(transaction, readOnly, promise);
		promise.get();
	}

	@Override
	public void deleteAllData() {
		List<Object> all = getAll();
		for (Object entity : all) {
			delete(entity);
		}
	}

	@Override
	public <E> List<E> find(final Predicate<E> match) {
		final List<E> results = new ArrayList<E>();

		each(new Operation<E>() {
			@Override
			public void execute(E record) throws Exception {
				if (match.eval(record)) {
					results.add(record);
				}
			}

		});

		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> void each(final Operation<E> lambda) {
		for (Object record : getAll()) {

			try {
				lambda.execute((E) record);
			} catch (ClassCastException e) {
				// ignore, cast exceptions are expected
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public <RESULT> RESULT sql(String sql, Object... args) {
		throw U.notSupported();
	}

	@Override
	public long size() {
		return getAll().size();
	}

}
