package org.rapidoid.jpa;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;

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

	public static EntityManager em() {
		return JPAUtil.em();
	}

	public static EntityManager currentEM() {
		return JPAUtil.currentEntityManager();
	}

	public static EM with(EntityManager em) {
		return new EM(em);
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

	public static <E> List<E> getAll(Class<E> clazz, List<String> ids) {
		return with(em()).getAll(clazz, ids);
	}

	public static <T> List<T> getAll(Class<T> clazz) {
		return with(em()).getAll(clazz);
	}

	public static long count(Class<?> clazz) {
		return with(em()).count(clazz);
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

	public static void bootstrap(String[] path, Class<?>... providedEntities) {
		JPAUtil.bootstrap(path, providedEntities);
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
		return JPAUtil.isEntity(obj);
	}

	public static List<EntityType<?>> getEntityTypes() {
		return with(em()).getEntityTypes();
	}

	public static List<String> entities() {
		return JPAUtil.entities;
	}

	public static List<Class<?>> getEntityJavaTypes() {
		return JPAUtil.entityJavaTypes;
	}

	public static EntityManagerFactory emf() {
		return JPAUtil.emf;
	}

	public static void emf(EntityManagerFactory emf) {
		JPAUtil.emf(emf);
	}

	public static <T> T unproxy(T entity) {
		return JPAUtil.unproxy(entity);
	}

}
