package org.rapidoid.jpa;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Coll;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/*
 * #%L
 * rapidoid-commons
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
public class JPA {

	private static final List<String> entities = U.list();

	public static EntityManager em() {
		Ctx ctx = Ctxs.get();
		U.must(ctx != null, "Must be inside JPA transaction!");
		return ctx.persister();
	}

	public static JPAUtil with(EntityManager em) {
		return JPAUtil.with(em);
	}

	public static <E> E ref(Class<E> clazz, Object id) {
		return with(em()).ref(clazz, id);
	}

	public static <E> E get(Class<E> clazz, Object id) {
		return with(em()).get(clazz, id);
	}

	public static <T> T find(Class<T> clazz, Object id) {
		return with(em()).find(clazz, id);
	}

	public static <E> List<E> getAllEntities() {
		return with(em()).getAll();
	}

	public static List<EntityType<?>> getEntityTypes() {
		return with(em()).getEntityTypes();
	}

	public static <E> List<E> getAll(Class<E> clazz, List<String> ids) {
		return with(em()).getAll(clazz, ids);
	}

	public static <T> List<T> getAll(Class<T> clazz) {
		return with(em()).getAll(clazz);
	}

	public static Object save(Object record) {
		return with(em()).save(record);
	}

	public static Object insert(Object entity) {
		return with(em()).insert(entity);
	}

	public static void update(Object record) {
		with(em()).update(record);
	}

	public static void delete(Object record) {
		with(em()).delete(record);
	}

	public static <E> void delete(Class<E> clazz, Object id) {
		with(em()).delete(clazz, id);
	}

	public static void refresh(Object entity) {
		with(em()).refresh(entity);
	}

	public static void merge(Object entity) {
		with(em()).merge(entity);
	}

	public static void transaction(Runnable action) {
		tx(action, false);
	}

	public static void transactionReadOnly(Runnable action) {
		tx(action, true);
	}

	private static void tx(Runnable action, boolean readOnly) {
		Ctx ctx = Ctxs.get();
		boolean newContext = ctx == null;

		if (newContext) {
			ctx = Ctxs.open("transaction");
		}

		try {
			EntityManager em = ctx.persister();
			with(em).transaction(action, readOnly);

		} finally {
			if (newContext) {
				Ctxs.close();
			}
		}
	}

	public static void flush() {
		em().flush();
	}

	public static QuickJPA bootstrap(String[] path, Class<?>... providedEntities) {
		if (Cls.exists("org.hibernate.cfg.Configuration") && entities.isEmpty()) {
			UTILS.logSection("Bootstrapping JPA (Hibernate)...");

			List<String> entityTypes = EMFUtil.createEMF(path, providedEntities);

			if (entityTypes.isEmpty()) {
				UTILS.logSection("Didn't find JPA providedEntities, canceling JPA/Hibernate setup!");
				return null;
			}

			UTILS.logSection("Hibernate properties:");
			Properties props = EMFUtil.hibernateProperties();
			UTILS.logProperties(props);

			UTILS.logSection("Starting Hibernate:");

			CustomHibernatePersistenceProvider provider = new CustomHibernatePersistenceProvider();
			provider.names().addAll(entityTypes);

			EntityManagerFactory emf = provider.createEntityManagerFactory("pu", props);

			QuickJPA jpa = new QuickJPA(emf, entityTypes);
			Ctxs.setPersisterProvider(jpa);

			UTILS.logSection("JPA (Hibernate) is ready.");

			Coll.assign(entities, entityTypes);
			return jpa;
		} else {
			return null;
		}
	}

	public static List<String> entities() {
		return entities;
	}

	public static boolean isLoaded(Object entity) {
		return with(em()).isLoaded(entity);
	}

	public static boolean isLoaded(Object entity, String attribute) {
		return with(em()).isLoaded(entity, attribute);
	}

	public static Object getIdentifier(Object entity) {
		return with(em()).getIdentifier(entity);
	}

	public static <T> List<T> jpql(String jpql, Object... args) {
		return with(em()).jpql(jpql, args);
	}

	public static <T> List<T> jpql(String jpql, Map<String, ?> namedArgs, Object... args) {
		return with(em()).jpql(jpql, namedArgs, args);
	}

	public static boolean isEntity(Object obj) {
		return obj != null && entities().contains(obj.getClass().getName());
	}

}
