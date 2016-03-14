package org.rapidoid.jpa;

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

import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.boot.internal.SettingsImpl;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.PersisterProvider;
import org.rapidoid.log.Log;
import org.rapidoid.scan.Scan;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Properties;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
@SuppressWarnings("deprecation")
public class QuickJPA implements PersisterProvider {

	private final org.hibernate.ejb.HibernateEntityManagerFactory emf;

	public QuickJPA(HibernateEntityManagerFactory emf) {
		this.emf = emf;
	}

	public static synchronized org.hibernate.ejb.HibernateEntityManagerFactory emf(String path[], Class<?>... entities) {

		org.hibernate.cfg.AnnotationConfiguration cfg = new org.hibernate.cfg.AnnotationConfiguration();

		List<Class<?>> entityTypes = Scan.annotated(Entity.class).in(path).loadAll();

		for (Class<?> entityType : entityTypes) {
			cfg.addAnnotatedClass(entityType);
		}

		for (Class<?> entityType : entities) {
			if (!entityTypes.contains(entityType)) {
				cfg.addAnnotatedClass(entityType);
			}
		}

		if (entityTypes.isEmpty()) {
			UTILS.logSection("Didn't find JPA entities, canceling JPA/Hibernate setup!");
			return null;
		}

		UTILS.logSection("Total " + entityTypes.size() + " JPA Entities:");
		for (Class<?> entityType : entityTypes) {
			Log.info("Entity", "package", entityType.getPackage().getName(), "name", entityType.getSimpleName());
		}

		UTILS.logSection("Hibernate properties:");
		Properties props = hibernateProperties();
		cfg.addProperties(props);
		UTILS.logProperties(props);

		UTILS.logSection("Starting Hibernate:");
		SessionFactory sf = cfg.buildSessionFactory();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) sf;
		SettingsImpl settings = new SettingsImpl();

		return new EntityManagerFactoryImpl("pu", sfi, settings, U.map(), cfg);
	}

	public static Properties hibernateProperties() {
		return Conf.HIBERNATE.toProperties();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> P openPersister() {
		return (P) emf.createEntityManager();
	}

	@Override
	public void closePersister(Object persister) {
		EntityManager em = (EntityManager) persister;
		em.close();
	}

}
