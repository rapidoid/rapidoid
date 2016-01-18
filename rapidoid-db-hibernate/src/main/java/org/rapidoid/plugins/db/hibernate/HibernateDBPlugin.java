package org.rapidoid.plugins.db.hibernate;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.job.Jobs;
import org.rapidoid.plugins.db.DBPluginBase;
import org.rapidoid.u.U;

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
@Since("3.0.0")
public class HibernateDBPlugin extends DBPluginBase {

	public HibernateDBPlugin() {
		super("hibernate");
	}

	@Override
	public String insert(Object entity) {
		ensureNotInReadOnlyTransation();

		EntityTransaction tx = em().getTransaction();

		boolean txWasActive = tx.isActive();

		if (!txWasActive) {
			tx.begin();
		}

		try {
			em().persist(entity);
			em().flush();

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

	@Override
	public void update(String id, Object entity) {
		ensureNotInReadOnlyTransation();
		Beany.setId(entity, id);
		em().persist(entity);
	}

	@Override
	public <T> T getIfExists(Class<T> clazz, String id) {
		return em().find(clazz, castId(clazz, id));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> List<E> getAll() {
		List<E> all = U.list();

		Metamodel metamodel = em().getMetamodel();
		Set<EntityType<?>> entityTypes = metamodel.getEntities();

		for (EntityType<?> entityType : entityTypes) {
			List<E> entities = (List<E>) getAll(entityType.getJavaType());
			all.addAll(entities);
		}

		return all;
	}

	@Override
	public <T> List<T> getAll(Class<T> clazz) {
		CriteriaBuilder cb = em().getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(clazz);
		CriteriaQuery<T> all = query.select(query.from(clazz));
		return em().createQuery(all).getResultList();
	}

	@Override
	public void refresh(Object entity) {
		em().refresh(entity);
	}

	@Override
	public <E> void delete(Class<E> clazz, String id) {
		ensureNotInReadOnlyTransation();
		em().remove(get(clazz, id));
	}

	@Override
	public void delete(Object record) {
		ensureNotInReadOnlyTransation();
		em().remove(record);
	}

	@Override
	public void transaction(final Runnable action, boolean readonly) {
		final EntityTransaction tx = em().getTransaction();

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

	private void ensureNotInReadOnlyTransation() {
		EntityTransaction tx = em().getTransaction();
		U.must(!tx.isActive() || !tx.getRollbackOnly(), "Cannot perform writes inside read-only transaction!");
	}

	protected EntityManager em() {
		return Ctxs.ctx().persister();
	}

}
