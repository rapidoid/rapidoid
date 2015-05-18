package org.rapidoid.jpa.dbplugin;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.lambda.Callback;
import org.rapidoid.plugins.impl.DefaultDBPlugin;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-jpa
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
public class JPADBPlugin extends DefaultDBPlugin {

	private final EntityManager em;

	public JPADBPlugin(EntityManager em) {
		this.em = em;
	}

	@Override
	public long insert(Object entity) {
		em.persist(entity);
		return Beany.getId(entity);
	}

	@Override
	public void update(long id, Object entity) {
		Beany.setId(entity, id);
		em.persist(entity);
	}

	@Override
	public <T> T getIfExists(Class<T> clazz, long id) {
		return em.find(clazz, id);
	}

	@SuppressWarnings("unchecked")
	@Override
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

	@Override
	public <T> List<T> getAll(Class<T> clazz) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> query = cb.createQuery(clazz);
		CriteriaQuery<T> all = query.select(query.from(clazz));
		return em.createQuery(all).getResultList();
	}

	@Override
	public void refresh(Object entity) {
		em.refresh(entity);
	}

	@Override
	public <E> void delete(Class<E> clazz, long id) {
		em.remove(get(clazz, id));
	}

	@Override
	public void delete(Object record) {
		em.remove(record);
	}

	@Override
	public void transaction(Runnable tx, boolean readonly) {
		em.getTransaction().begin();

		try {
			tx.run();
		} catch (Throwable e) {
			em.getTransaction().rollback();
			throw U.rte("Transaction execution error, rolled back!", e);
		}

		em.getTransaction().commit();
	}

	@Override
	public void transaction(Runnable tx, boolean readonly, Callback<Void> callback) {
		// FIXME make async

		try {
			transaction(tx, readonly);
		} catch (Throwable e) {
			callback.onDone(null, e);
			return;
		}

		callback.onDone(null, null);
	}

}
