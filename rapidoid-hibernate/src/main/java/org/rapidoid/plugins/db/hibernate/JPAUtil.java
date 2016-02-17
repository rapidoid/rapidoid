package org.rapidoid.plugins.db.hibernate;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.beany.PropertyFilter;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Err;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.entity.EntitiesUtil;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.u.U;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

/*
 * #%L
 * rapidoid-db-hibernate
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
@Since("5.1.0")
public class JPAUtil {

	private static final Pattern P_WORD = Pattern.compile("\\w+");

	@SuppressWarnings("serial")
	public static final PropertyFilter SEARCHABLE_PROPS = new PropertyFilter() {

		public boolean eval(Prop prop) throws Exception {
			return Cls.isAssignableTo(prop.getType(), Number.class, String.class, Boolean.class, Enum.class, Date.class);
		}
	};

	private final EntityManager em;

	public JPAUtil(EntityManager em) {
		this.em = em;
	}

	public String persist(Object record) {
		String id = Beany.getIdIfExists(record);
		if (id == null) {
			return insert(record);
		} else {
			update(id, record);
			return id;
		}
	}

	public void update(Object record) {
		update(Beany.getId(record), record);
	}


	public String insertOrGetId(Object record) {
		String id = Beany.getIdIfExists(record);
		if (id == null) {
			return insert(record);
		} else {
			return id;
		}
	}

	public <E> List<E> getAll(Class<E> clazz, List<String> ids) {
		List<E> results = new ArrayList<E>();

		for (String id : ids) {
			results.add(this.<E>get(clazz, id));
		}

		return results;
	}


	public <E> List<E> getAll(Class<E> clazz, int pageNumber, int pageSize) {
		return U.page(getAll(clazz), pageNumber, pageSize);
	}


	public <E> E get(Class<E> clazz, String id) {
		E entity = getIfExists(clazz, id);

		if (entity == null) {
			throw U.rte("Cannot find entity with ID=%s", id);
		}

		return entity;
	}


	public <E> E entity(Class<E> entityType, Map<String, ?> properties) {
		return EntitiesUtil.create(entityType, properties);
	}


	public <E> List<E> find(final Class<E> clazz, final Predicate<E> match, final Comparator<E> orderBy) {

		Predicate<E> match2 = new Predicate<E>() {

			public boolean eval(E record) throws Exception {
				return (clazz == null || clazz.isAssignableFrom(record.getClass()))
						&& (match == null || match.eval(record));
			}
		};

		return sorted(find(match2), orderBy);
	}

	public <E> List<E> sorted(List<E> records, Comparator<E> orderBy) {
		if (orderBy != null) {
			Collections.sort(U.list(records), orderBy);
		}
		return records;
	}


	public <E> List<E> fullTextSearch(String searchPhrase) {
		final String search = searchPhrase.toLowerCase();

		Predicate<E> match = new Predicate<E>() {

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


	public <E> List<E> query(final Class<E> clazz, final String query, final Object... args) {

		Predicate<E> match = new Predicate<E>() {

			public boolean eval(E record) throws Exception {
				return clazz.isAssignableFrom(record.getClass()) && matches(record, query, args);
			}
		};

		return find(match);
	}

	public boolean matches(Object record, String query, Object... args) {

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

	public void deleteAllData() {
		List<Object> all = getAll();
		for (Object entity : all) {
			delete(entity);
		}
	}


	public <E> List<E> find(final Predicate<E> match) {
		final List<E> results = new ArrayList<E>();

		each(new Operation<E>() {

			public void execute(E record) throws Exception {
				if (match.eval(record)) {
					results.add(record);
				}
			}

		});

		return results;
	}

	@SuppressWarnings("unchecked")

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


	public <RESULT> RESULT sql(String sql, Object... args) {
		throw Err.notSupported();
	}


	public long size() {
		return U.list(getAll()).size();
	}

	public Object castId(Class<?> clazz, String id) {
		return Cls.convert(id, Beany.property(clazz, "id", true).getType());
	}


	public String insert(Object entity) {
		ensureNotInReadOnlyTransation();

		EntityTransaction tx = em.getTransaction();

		boolean txWasActive = tx.isActive();

		if (!txWasActive) {
			tx.begin();
		}

		try {
			em.persist(entity);
			em.flush();

			String id = Beany.getId(entity);

			if (!txWasActive) {
				tx.commit();
			}

			return id;

		} catch (Throwable e) {
			if (!txWasActive) {
				if (tx.isActive()) {
					tx.rollback();
				}
			}
			throw U.rte("Transaction execution error, rolled back!", e);
		}
	}


	public void update(String id, Object entity) {
		ensureNotInReadOnlyTransation();
		Beany.setId(entity, id);
		em.persist(entity);
	}


	public <T> T getIfExists(Class<T> clazz, String id) {
		return em.find(clazz, castId(clazz, id));
	}

	@SuppressWarnings("unchecked")

	public <E> List<E> getAll() {
		List<E> all = U.list();

		Metamodel metamodel = em.getMetamodel();
		Set<EntityType<?>> entityTypes = metamodel.getEntities();

		for (EntityType<?> entityType : entityTypes) {
			List<E> entities = (List<E>) getAll(entityType.getJavaType());
			all.addAll(entities);
		}

		return all;
	}


	public <T> List<T> getAll(Class<T> clazz) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(clazz);
		CriteriaQuery<T> all = query.select(query.from(clazz));
		return em.createQuery(all).getResultList();
	}


	public void refresh(Object entity) {
		em.refresh(entity);
	}


	public <E> void delete(Class<E> clazz, String id) {
		ensureNotInReadOnlyTransation();
		em.remove(get(clazz, id));
	}


	public void delete(Object record) {
		ensureNotInReadOnlyTransation();
		em.remove(record);
	}


	public void transaction(final Runnable action, boolean readonly) {
		final EntityTransaction tx = em.getTransaction();

		if (readonly) {
			runTxReadOnly(action, tx);
		} else {
			runTxRW(action, tx);
		}
	}

	private void runTxReadOnly(Runnable action, EntityTransaction tx) {
		boolean txWasActive = tx.isActive();

		if (!txWasActive) {
			tx.begin();
		}

		tx.setRollbackOnly();

		try {
			action.run();
		} catch (Throwable e) {
			tx.rollback();
			throw U.rte("Transaction execution error, rolled back!", e);
		}

		if (!txWasActive) {
			tx.rollback();
		}
	}

	private void runTxRW(Runnable action, EntityTransaction tx) {
		boolean txWasActive = tx.isActive();

		if (!txWasActive) {
			tx.begin();
		}

		try {
			action.run();
		} catch (Throwable e) {
			tx.rollback();
			throw U.rte("Transaction execution error, rolled back!", e);
		}

		if (!txWasActive) {
			tx.commit();
		}
	}


	public void transaction(final Runnable tx, final boolean readonly, final Callback<Void> callback) {
		Jobs.execute(new Callable<Void>() {

			public Void call() throws Exception {
				transaction(tx, readonly);
				return null;
			}

		}, callback);
	}

	private void ensureNotInReadOnlyTransation() {
		EntityTransaction tx = em.getTransaction();
		U.must(!tx.isActive() || !tx.getRollbackOnly(), "Cannot perform writes inside read-only transaction!");
	}

	public static JPAUtil with(EntityManager em) {
		return new JPAUtil(em);
	}

}
