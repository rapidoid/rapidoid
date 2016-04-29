package org.rapidoid.jpa;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.IRange;
import org.rapidoid.commons.Range;
import org.rapidoid.u.U;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-jpa
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
public class EM extends RapidoidThing {

	private final EntityManager em;

	public EM(EntityManager em) {
		this.em = em;
	}

	public <E> E save(E entity) {
		Object id = getIdentifier(entity);

		if (id == null) {
			return insert(entity);

		} else {
			return update(entity);
		}
	}

	public <E> E insert(E entity) {
		ensureNotInReadOnlyTransation();

		EntityTransaction tx = em.getTransaction();

		boolean txWasActive = tx.isActive();

		if (!txWasActive) {
			tx.begin();
		}

		try {
			em.persist(entity);
			em.flush();

			if (!txWasActive) {
				tx.commit();
			}

			return entity;

		} catch (Throwable e) {
			if (!txWasActive) {
				if (tx.isActive()) {
					tx.rollback();
				}
			}
			throw U.rte("Transaction execution error, rolled back!", e);
		}
	}

	public <E> E update(E entity) {
		ensureNotInReadOnlyTransation();

		if (em.contains(entity)) {
			em.persist(entity);
			return entity;
		} else {
			return em.merge(entity);
		}
	}

	public <E> List<E> getAll(Class<E> clazz, List<String> ids) {
		List<E> results = new ArrayList<E>();

		for (Object id : ids) {
			results.add(this.<E>get(clazz, id));
		}

		return results;
	}

	public <E> E get(Class<E> clazz, Object id) {
		E entity = find(clazz, id);
		U.must(entity != null, "Cannot find %s with ID=%s", clazz.getSimpleName(), id);
		return entity;
	}

	public <E> E ref(Class<E> clazz, Object id) {
		return em.getReference(clazz, id);
	}

	public <T> T find(Class<T> clazz, Object id) {
		return em.find(clazz, id);
	}

	public List<EntityType<?>> getEntityTypes() {
		return U.list(em.getMetamodel().getEntities());
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> getAll() {
		List<E> all = U.list();

		for (EntityType<?> entityType : getEntityTypes()) {
			List<E> entities = (List<E>) getAll(entityType.getJavaType(), Range.UNLIMITED);
			all.addAll(entities);
		}

		return all;
	}

	public <T> List<T> getAll(Class<T> clazz, IRange range) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<T> query = cb.createQuery(clazz);
		CriteriaQuery<T> all = query.select(query.from(clazz));

		return find(all, range);
	}

	public <T> List<T> find(CriteriaQuery<T> criteria, IRange range) {
		U.notNull(criteria, "criteria");
		U.notNull(range, "range");

		TypedQuery<T> q = em.createQuery(criteria);
		return find(q, range);
	}

	public <T> List<T> find(Query query, IRange range) {
		U.notNull(query, "query");
		U.notNull(range, "range");

		if (range != Range.UNLIMITED) {
			query.setFirstResult(range.start());
			query.setMaxResults(range.length());
		}

		return query.getResultList();
	}

	public long count(Class<?> clazz) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(clazz)));

		return em.createQuery(cq).getSingleResult();
	}

	public void refresh(Object entity) {
		em.refresh(entity);
	}

	public void merge(Object entity) {
		em.merge(entity);
	}

	public void detach(Object entity) {
		em.detach(entity);
	}

	public <E> void delete(Class<E> clazz, Object id) {
		ensureNotInReadOnlyTransation();
		em.remove(get(clazz, id));
	}

	public void delete(Object entity) {
		ensureNotInReadOnlyTransation();
		em.remove(entity);
	}

	public void transaction(Runnable action, boolean readOnly) {
		final EntityTransaction tx = em.getTransaction();
		U.notNull(tx, "transaction");

		if (readOnly) {
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

	private void ensureNotInReadOnlyTransation() {
		EntityTransaction tx = em.getTransaction();
		U.must(!tx.isActive() || !tx.getRollbackOnly(), "Cannot perform writes inside read-only transaction!");
	}

	public boolean isLoaded(Object entity) {
		return em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(entity);
	}

	public boolean isLoaded(Object entity, String attribute) {
		return em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(entity, attribute);
	}

	public Object getIdentifier(Object entity) {
		return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
	}

	public <T> List<T> jpql(String jpql, IRange range, Object... args) {
		return jpql(jpql, range, null, args);
	}

	public <T> List<T> jpql(String jpql, IRange range, Map<String, ?> namedArgs, Object... args) {
		Query q = JPA.em().createQuery(jpql);

		for (int i = 0; i < args.length; i++) {
			q.setParameter(i + 1, args[i]);
		}

		for (Parameter<?> param : q.getParameters()) {
			String name = param.getName();
			if (U.notEmpty(name)) {
				q.setParameter(name, Cls.convert(namedArgs.get(name), param.getParameterType()));
			}
		}

		return find(q, range);
	}

}
