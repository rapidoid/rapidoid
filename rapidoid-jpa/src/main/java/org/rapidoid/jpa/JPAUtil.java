package org.rapidoid.jpa;

import org.hibernate.proxy.HibernateProxy;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.jpa.impl.CustomHibernatePersistenceProvider;
import org.rapidoid.jpa.impl.JPAInternals;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;
import java.net.ConnectException;
import java.util.List;
import java.util.Properties;

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
public class JPAUtil extends RapidoidThing {

	static volatile EntityManagerFactory emf;

	static final List<String> entities = U.list();

	static final List<Class<?>> entityJavaTypes = U.list();

	public static void reset() {
		emf = null;
		entities.clear();
		entityJavaTypes.clear();
	}

	public static EntityManager em() {
		Ctx ctx = Ctxs.get();

		if (ctx != null) {
			return JPAInternals.wrapEM((EntityManager) ctx.persister());

		} else {
			EntityManagerFactory emf = JPAUtil.emf;
			U.notNull(emf, "JPA.emf");
			return JPAInternals.wrapEM(emf.createEntityManager());
		}
	}

	public static EntityManager currentEntityManager() {
		return Ctxs.required().persister();
	}

	public static void bootstrap(String[] path, Class<?>... providedEntities) {
		if (MscOpts.hasHibernate()) {

			if (emf() == null) {
				bootstrapJPA(path, providedEntities);
			} else {
				Log.warn("A custom EMF was already assigned, won't bootstrap JPA!");
			}

		} else {
			Log.warn("Couldn't find Hibernate, cannot bootstrap JPA!");
		}
	}

	private static void bootstrapJPA(String[] path, Class<?>[] providedEntities) {
		Msc.logSection("Bootstrapping JPA (Hibernate)...");

		List<String> entityTypes = EMFUtil.createEMF(path, providedEntities);

		if (entityTypes.isEmpty()) {
			Log.info("Didn't find JPA entities, canceling JPA/Hibernate setup!");
			return;
		}

		Properties props = EMFUtil.hibernateProperties();
		Msc.logSection("Hibernate properties:");
		Msc.logProperties(props);

		Msc.logSection("Starting Hibernate:");

		CustomHibernatePersistenceProvider provider = new CustomHibernatePersistenceProvider();
		provider.names().addAll(entityTypes);

		EntityManagerFactory emf = createEMF(props, provider);

		emf(emf);

		Msc.logSection("JPA (Hibernate) is ready.");
	}

	private static EntityManagerFactory createEMF(Properties props, CustomHibernatePersistenceProvider provider) {
		while (true) {
			try {
				return provider.createEntityManagerFactory("rapidoid", props);

			} catch (Exception e) {
				if (Msc.rootCause(e) instanceof ConnectException) {
					Log.warn("Couldn't connect, will retry again in 3 seconds...");
					U.sleep(3000); // FIXME improve back-off
				} else {
					throw U.rte("Failed to create EMF!", e);
				}
			}
		}
	}

	public static boolean isEntity(Object obj) {
		if (obj == null) {
			return false;
		}

		if (entities.contains(obj.getClass().getName())) {
			return true;
		}

		for (Class<?> type : entityJavaTypes) {
			if (type.isAssignableFrom(obj.getClass())) {
				return true;
			}
		}

		return false;
	}

	public static <T> T unproxy(T entity) {
		return Cls.exists("org.hibernate.proxy.HibernateProxy") ? _unproxy(entity) : entity;
	}

	private static <T> T _unproxy(T entity) {
		if (Cls.exists("org.hibernate.proxy.HibernateProxy") && entity instanceof HibernateProxy) {
			entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
		}

		return entity;
	}

	public static void emf(EntityManagerFactory emf) {
		U.notNull(emf, "emf");

		reset();
		JPAUtil.emf = emf;

		for (EntityType<?> entityType : emf.getMetamodel().getEntities()) {
			Class<?> type = entityType.getJavaType();
			entityJavaTypes.add(type);
			entities.add(type.getName());
		}
	}

	public static EntityManagerFactory emf() {
		return emf;
	}

	public static <T> List<T> getPage(Query q, long skip, long limit) {

		U.must(skip < Integer.MAX_VALUE && skip >= 0);
		U.must(limit >= -1); // -1 means no limit
		limit = Math.min(limit, Integer.MAX_VALUE);

		q.setFirstResult((int) skip);
		q.setMaxResults(limit >= 0 ? (int) limit : Integer.MAX_VALUE);

		return q.getResultList();
	}

}
