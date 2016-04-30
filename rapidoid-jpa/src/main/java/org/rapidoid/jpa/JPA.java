package org.rapidoid.jpa;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.u.U;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.EntityType;
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
public class JPA extends RapidoidThing {

	public static EntityManager em() {
		return JPAUtil.em();
	}

	public static EntityManager currentEM() {
		return JPAUtil.currentEntityManager();
	}

	public static EM with(EntityManager em) {
		return new EM(em);
	}

	private static EM em_() {
		return with(em());
	}

	public static <E> E reference(Class<E> clazz, Object id) {
		return em_().reference(clazz, id);
	}

	public static <E> E get(Class<E> clazz, Object id) {
		return em_().get(clazz, id);
	}

	public static <T> T getIfExists(Class<T> clazz, Object id) {
		return em_().getIfExists(clazz, id);
	}

	public static <E> List<E> getAllEntities() {
		return em_().getAllEntities();
	}

	public static <E> E save(E record) {
		return em_().save(record);
	}

	public static <E> E insert(E entity) {
		return em_().insert(entity);
	}

	public static <E> E update(E record) {
		return em_().update(record);
	}

	public static void delete(Object record) {
		em_().delete(record);
	}

	public static <E> void delete(Class<E> clazz, Object id) {
		em_().delete(clazz, id);
	}

	public static void refresh(Object entity) {
		em_().refresh(entity);
	}

	public static void merge(Object entity) {
		em_().merge(entity);
	}

	public static void detach(Object entity) {
		em_().detach(entity);
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

	public static void bootstrap(String[] path, Class<?>... providedEntities) {
		JPAUtil.bootstrap(path, providedEntities);
	}

	public static boolean isLoaded(Object entity) {
		return em_().isLoaded(entity);
	}

	public static boolean isLoaded(Object entity, String attribute) {
		return em_().isLoaded(entity, attribute);
	}

	public static Object getIdentifier(Object entity) {
		return em_().getIdentifier(entity);
	}

	public static boolean isEntity(Object obj) {
		return JPAUtil.isEntity(obj);
	}

	public static List<EntityType<?>> getEntityTypes() {
		return em_().getEntityTypes();
	}

	public static List<String> entities() {
		return JPAUtil.entities;
	}

	public static List<Class<?>> getEntityJavaTypes() {
		return JPAUtil.entityJavaTypes;
	}

	public static EntityManagerFactory provideEmf() {
		EntityManagerFactory emf = JPAUtil.emf;
		U.notNull(emf, "JPA.emf");
		return emf;
	}

	public static EntityManagerFactory getEmf() {
		return JPAUtil.emf;
	}

	public static void setEmf(EntityManagerFactory emf) {
		JPAUtil.emf(emf);
	}

	public static <T> T unproxy(T entity) {
		return JPAUtil.unproxy(entity);
	}

	public static boolean isActive() {
		return JPAUtil.emf() != null;
	}

	public static <T> Entities<T> of(Class<T> clazz) {
		return em_().of(clazz);
	}

	public static long count(Class<?> clazz) {
		return em_().count(clazz);
	}

	public static long count(String jpql, Object... args) {
		return em_().count(jpql, args);
	}

	public static long count(String jpql, Map<String, ?> namedArgs, Object... args) {
		return em_().count(jpql, namedArgs, args);
	}

	public static <T> Entities<T> find(CriteriaQuery<T> criteria) {
		return em_().find(criteria);
	}

	public static <T> Entities<T> find(String jpql, Object... args) {
		return em_().find(jpql, args);
	}

	public static <T> Entities<T> find(String jpql, Map<String, ?> namedArgs, Object... args) {
		return em_().find(jpql, namedArgs, args);
	}

	public static int execute(String jpql, Object... args) {
		return em_().execute(jpql, args);
	}

	public static int execute(String jpql, Map<String, ?> namedArgs, Object... args) {
		return em_().execute(jpql, namedArgs, args);
	}

	public static void bind(Query query, Map<String, ?> namedArgs, Object... args) {
		for (int i = 0; i < args.length; i++) {
			query.setParameter(i + 1, args[i]);
		}

		for (Parameter<?> param : query.getParameters()) {
			String name = param.getName();
			if (U.notEmpty(name)) {
				U.must(namedArgs != null && namedArgs.containsKey(name), "A named argument wasn't specified for the named JPQL parameter: %s", name);
				query.setParameter(name, Cls.convert(namedArgs.get(name), param.getParameterType()));
			}
		}
	}

}
