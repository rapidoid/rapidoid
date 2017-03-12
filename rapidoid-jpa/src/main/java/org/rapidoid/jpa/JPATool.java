package org.rapidoid.jpa;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.datamodel.Results;
import org.rapidoid.datamodel.impl.ResultsImpl;
import org.rapidoid.jpa.impl.JPACriteriaQueryEntities;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.u.U;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-jpa
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
@Named
public class JPATool extends RapidoidThing {

	@PersistenceContext
	private volatile EntityManager em;

	private final boolean managed;

	/**
	 * This constructor should be used only implicitly by the dependency injection libraries.
	 */
	public JPATool() {
		this(null, true);
	}

	public JPATool(EntityManager em, boolean managed) {
		this.em = em;
		this.managed = managed;
	}

	public <E> E save(E entity) {
		Object id = getIdentifier(entity);

		if (id == null) {
			return insert(entity);

		} else {
			return update(entity);
		}
	}

	public <E> E insert(final E entity) {
		return transactional(new Callable<E>() {

			@Override
			public E call() throws Exception {
				em.persist(entity);
				return entity;
			}

		});
	}

	public <E> E update(final E entity) {
		U.notNull(getIdentifier(entity), "entity identifier");

		return transactional(new Callable<E>() {

			@Override
			public E call() throws Exception {
				if (em.contains(entity)) {
					em.persist(entity);
					return entity;
				} else {
					return em.merge(entity);
				}
			}

		});
	}

	public <E> E merge(final E entity) {
		return transactional(new Callable<E>() {

			@Override
			public E call() throws Exception {
				return em.merge(entity);
			}

		});
	}

	public <E> void delete(final Class<E> clazz, final Object id) {
		transactional(new Callable<E>() {

			@Override
			public E call() throws Exception {
				em.remove(get(clazz, id));
				return null;
			}

		});
	}

	public void delete(final Object entity) {
		transactional(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				em.remove(entity);
				return null;
			}

		});
	}

	public void transactional(Runnable action) {
		transactional(action, false);
	}

	public void transactional(Runnable action, boolean readOnly) {
		transactional(Lmbd.callable(action), readOnly);
	}

	public <E> E transactional(Callable<E> action) {
		return transactional(action, false);
	}

	public <E> E transactional(Callable<E> action, boolean readOnly) {
		ensureNotInRollbackOnlyTransation();

		EntityTransaction tx = em.getTransaction();
		U.notNull(tx, "transaction");

		boolean newTx = !tx.isActive();

		if (newTx) {
			tx.begin();
		}

		if (readOnly) {
			tx.setRollbackOnly();
		}

		try {
			E result = action.call();

			if (newTx) {
				if (tx.getRollbackOnly()) {
					tx.rollback();
				} else {
					tx.commit();
				}
			}

			return result;

		} catch (Throwable e) {

			if (newTx) {
				if (tx.isActive()) {
					tx.rollback();
				}
			}

			throw U.rte("Transaction execution error, rolled back!", e);
		}
	}

	private void ensureNotInRollbackOnlyTransation() {
		EntityTransaction tx = em.getTransaction();
		U.must(!tx.isActive() || !tx.getRollbackOnly(), "Cannot perform writes inside read-only transaction!");
	}

	public <E> E get(Class<E> clazz, Object id) {
		E entity = getIfExists(clazz, id);
		U.must(entity != null, "Cannot find %s with ID=%s", clazz.getSimpleName(), id);
		return entity;
	}

	public <E> E reference(Class<E> clazz, Object id) {
		return em.getReference(clazz, id);
	}

	public <T> T getIfExists(Class<T> clazz, Object id) {
		return em.find(clazz, id);
	}

	public List<EntityType<?>> getEntityTypes() {
		return U.list(em.getMetamodel().getEntities());
	}

	@SuppressWarnings("unchecked")
	public <E> List<E> getAllEntities() {
		List<E> all = U.list();

		for (EntityType<?> entityType : getEntityTypes()) {
			all.addAll((List<E>) of(entityType.getJavaType()).all());
		}

		return all;
	}

	public long count(Class<?> clazz) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(clazz)));

		return em.createQuery(cq).getSingleResult();
	}

	public void flush() {
		transactional(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				em.flush();
				return null;
			}

		});
	}

	public void refresh(final Object entity) {
		em.refresh(entity);
	}

	public void detach(final Object entity) {
		em.detach(entity);
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

	public <T> Results<T> find(CriteriaQuery<T> query) {
		return new ResultsImpl<>(new JPACriteriaQueryEntities<T>(query));
	}

	public <T> Results<T> of(Class<T> clazz) {
		CriteriaQuery<T> query = cb().createQuery(clazz);
		query.from(clazz);
		return find(query);
	}

	private CriteriaBuilder cb() {
		return JPA.provideEmf().getCriteriaBuilder();
	}

	public void close() {
		em.close();
	}

	public void done() {
		if (!managed) {
			close();
		}
	}

	public EntityManager em() {
		return em;
	}

}
