package org.rapidoid.jpa;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.datamodel.Results;
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
public class JPA extends RapidoidThing {

	public static EntityManager em() {
		return JPAUtil.em();
	}

	public static EntityManager currentEM() {
		return JPAUtil.currentEntityManager();
	}

	public static JPATool with(EntityManager em) {
		return new JPATool(em, true);
	}

	private static JPATool tool() {
		Ctx ctx = Ctxs.get();

		if (ctx != null) {
			EntityManager em = ctx.persister();
			return new JPATool(em, true);

		} else {
			EntityManagerFactory emf = JPAUtil.emf();
			U.notNull(emf, "JPA.emf");
			EntityManager em = emf.createEntityManager();
			return new JPATool(em, false);
		}
	}

	public static <E> E reference(Class<E> clazz, Object id) {
		JPATool jpa = tool();
		try {
			return jpa.reference(clazz, id);
		} finally {
			jpa.done();
		}
	}

	public static <E> E get(Class<E> clazz, Object id) {
		JPATool jpa = tool();
		try {
			return jpa.get(clazz, id);
		} finally {
			jpa.done();
		}
	}

	public static <T> T getIfExists(Class<T> clazz, Object id) {
		JPATool jpa = tool();
		try {
			return jpa.getIfExists(clazz, id);
		} finally {
			jpa.done();
		}
	}

	public static <E> List<E> getAllEntities() {
		JPATool jpa = tool();
		try {
			return jpa.getAllEntities();
		} finally {
			jpa.done();
		}
	}

	public static <E> E save(E record) {
		JPATool jpa = tool();
		try {
			return jpa.save(record);
		} finally {
			jpa.done();
		}
	}

	public static <E> E insert(E entity) {
		JPATool jpa = tool();
		try {
			return jpa.insert(entity);
		} finally {
			jpa.done();
		}
	}

	public static <E> E update(E record) {
		JPATool jpa = tool();
		try {
			return jpa.update(record);
		} finally {
			jpa.done();
		}
	}

	public static void delete(Object record) {
		JPATool jpa = tool();
		try {
			jpa.delete(record);
		} finally {
			jpa.done();
		}
	}

	public static <E> void delete(Class<E> clazz, Object id) {
		JPATool jpa = tool();
		try {
			jpa.delete(clazz, id);
		} finally {
			jpa.done();
		}
	}

	public static void refresh(Object entity) {
		JPATool jpa = tool();
		try {
			jpa.refresh(entity);
		} finally {
			jpa.done();
		}
	}

	public static <E> E merge(E entity) {
		JPATool jpa = tool();
		try {
			return jpa.merge(entity);
		} finally {
			jpa.done();
		}
	}

	public static void detach(Object entity) {
		JPATool jpa = tool();
		try {
			jpa.detach(entity);
		} finally {
			jpa.done();
		}
	}

	public static void flush() {
		JPATool jpa = tool();
		try {
			jpa.flush();
		} finally {
			jpa.done();
		}
	}

	public static void bootstrap(String[] path, Class<?>... providedEntities) {
		JPAUtil.bootstrap(path, providedEntities);
	}

	public static void transaction(Runnable action) {
		transaction(action, false);
	}

	public static void transaction(Runnable action, boolean readOnly) {
		Ctx ctx = Ctxs.get();
		boolean newContext = ctx == null;

		if (newContext) {
			ctx = Ctxs.open("transaction");
		}

		try {
			EntityManager em = ctx.persister();
			JPA.with(em).transactional(action, readOnly);

		} finally {
			if (newContext) {
				Ctxs.close();
			}
		}
	}

	public static boolean isLoaded(Object entity) {
		JPATool jpa = tool();
		try {
			return jpa.isLoaded(entity);
		} finally {
			jpa.done();
		}
	}

	public static boolean isLoaded(Object entity, String attribute) {
		JPATool jpa = tool();
		try {
			return jpa.isLoaded(entity, attribute);
		} finally {
			jpa.done();
		}
	}

	public static Object getIdentifier(Object entity) {
		JPATool jpa = tool();
		try {
			return jpa.getIdentifier(entity);
		} finally {
			jpa.done();
		}
	}

	public static boolean isEntity(Object obj) {
		return JPAUtil.isEntity(obj);
	}

	public static List<EntityType<?>> getEntityTypes() {
		JPATool jpa = tool();
		try {
			return jpa.getEntityTypes();
		} finally {
			jpa.done();
		}
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

	public static <T> Results<T> of(Class<T> clazz) {
		JPATool jpa = tool();
		try {
			return jpa.of(clazz);
		} finally {
			jpa.done();
		}
	}

	public static long count(Class<?> clazz) {
		JPATool jpa = tool();
		try {
			return jpa.count(clazz);
		} finally {
			jpa.done();
		}
	}

	public static <T> Results<T> find(CriteriaQuery<T> criteria) {
		JPATool jpa = tool();
		try {
			return jpa.find(criteria);
		} finally {
			jpa.done();
		}
	}

	public static void bind(Query query, Map<String, ?> namedArgs, Object... args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}

		if (namedArgs != null) {
			for (Parameter<?> param : query.getParameters()) {
				String name = param.getName();
				if (U.notEmpty(name)) {
					U.must(namedArgs.containsKey(name), "A named argument wasn't specified for the named JPQL parameter: %s", name);
					query.setParameter(name, Cls.convert(namedArgs.get(name), param.getParameterType()));
				}
			}
		}
	}

	public static JPQL jpql(String jpql) {
		return new JPQL(jpql);
	}

	public static JPQL jpql(String jpql, Object... args) {
		return new JPQL(jpql).bind(args);
	}

}
