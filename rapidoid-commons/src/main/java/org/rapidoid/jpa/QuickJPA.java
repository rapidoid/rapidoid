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
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.boot.internal.SettingsImpl;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.ctx.PersisterProvider;
import org.rapidoid.io.IO;
import org.rapidoid.scan.Scan;
import org.rapidoid.u.U;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class QuickJPA implements PersisterProvider {

	private final String path;

	private final Class<?>[] entities;

	public QuickJPA(String path, Class<?>... entities) {
		this.path = path;
		this.entities = entities;
	}

	@SuppressWarnings("deprecation")
	private org.hibernate.ejb.HibernateEntityManagerFactory emFactory;

	@SuppressWarnings("deprecation")
	private synchronized org.hibernate.ejb.HibernateEntityManagerFactory emFactory() {

		if (emFactory == null) {

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

			cfg.addProperties(hibernateProperties());
			SessionFactory sf = cfg.buildSessionFactory();

			SessionFactoryImplementor sfi = (SessionFactoryImplementor) sf;

			SettingsImpl settings = new SettingsImpl();

			emFactory = new EntityManagerFactoryImpl("pu", sfi, settings, U.map(), cfg);
		}

		return emFactory;
	}

	public static Properties hibernateProperties() {

		Properties properties = new Properties();
		Map<String, String> props;

		if (Conf.production()) {
			props = IO.loadMap("hibernate-prod.properties");
			if (props == null) {
				props = IO.loadMap("hibernate-prod.default.properties");
			}
		} else {
			props = IO.loadMap("hibernate-dev.properties");
			if (props == null) {
				props = IO.loadMap("hibernate-dev.default.properties");
			}
		}

		if (props != null) {
			properties.putAll(props);
		}

		return properties;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> P openPersister() {
		return (P) emFactory().createEntityManager();
	}

	@Override
	public void closePersister(Object persister) {
		EntityManager em = (EntityManager) persister;
		em.close();
	}

	public static void bootstrap(String path, Class<?>... entities) {
		Ctx ctx = Ctxs.open("jpa");
		Ctxs.setPersisterProvider(new QuickJPA(path, entities));
	}

}
