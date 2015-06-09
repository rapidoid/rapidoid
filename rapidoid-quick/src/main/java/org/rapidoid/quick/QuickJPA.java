package org.rapidoid.quick;

/*
 * #%L
 * rapidoid-quick
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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.boot.internal.SettingsImpl;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.PersistorFactory;
import org.rapidoid.util.Scan;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class QuickJPA implements PersistorFactory {

	private final Object[] args;

	private EntityManager em;

	public QuickJPA(Object... args) {
		this.args = args;
	}

	@SuppressWarnings("deprecation")
	public static EntityManager createEM(Object[] args) {
		org.hibernate.cfg.AnnotationConfiguration cfg = new org.hibernate.cfg.AnnotationConfiguration();

		List<Class<?>> entityTypes = Scan.annotated(Entity.class);
		for (Class<?> entityType : entityTypes) {
			cfg.addAnnotatedClass(entityType);
		}

		for (Object arg : args) {
			if (arg instanceof Class<?>) {
				Class<?> entityType = (Class<?>) arg;
				if (!entityTypes.contains(entityType)) {
					cfg.addAnnotatedClass(entityType);
				}
			}
		}

		SessionFactory sf = cfg.buildSessionFactory();

		SessionFactoryImplementor sfi = (SessionFactoryImplementor) sf;

		SettingsImpl settings = new SettingsImpl();
		org.hibernate.ejb.HibernateEntityManagerFactory ff = new EntityManagerFactoryImpl("pu-main-h2", sfi, settings,
				U.map(), cfg);

		EntityManager em = ff.createEntityManager();
		return em;
	}

	@Override
	public synchronized EntityManager createPersistor() {
		if (em == null) {
			em = createEM(args);
		}
		return em;
	}

}
