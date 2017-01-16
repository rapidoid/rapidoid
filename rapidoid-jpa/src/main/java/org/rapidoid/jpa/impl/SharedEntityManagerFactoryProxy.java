package org.rapidoid.jpa.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.jpa.JPA;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
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
public class SharedEntityManagerFactoryProxy extends RapidoidThing implements EntityManagerFactory {

	public static final SharedEntityManagerFactoryProxy INSTANCE = new SharedEntityManagerFactoryProxy();

	private SharedEntityManagerFactoryProxy() {
	}

	private EntityManagerFactory emf() {
		return JPA.provideEmf();
	}

	@Override
	public EntityManager createEntityManager() {
		return emf().createEntityManager();
	}

	@Override
	public EntityManager createEntityManager(Map map) {
		return emf().createEntityManager(map);
	}

	@Override
	public EntityManager createEntityManager(SynchronizationType synchronizationType) {
		return emf().createEntityManager(synchronizationType);
	}

	@Override
	public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
		return emf().createEntityManager(synchronizationType, map);
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return emf().getCriteriaBuilder();
	}

	@Override
	public Metamodel getMetamodel() {
		return emf().getMetamodel();
	}

	@Override
	public boolean isOpen() {
		return emf().isOpen();
	}

	@Override
	public void close() {
		emf().close();
	}

	@Override
	public Map<String, Object> getProperties() {
		return emf().getProperties();
	}

	@Override
	public Cache getCache() {
		return emf().getCache();
	}

	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		return emf().getPersistenceUnitUtil();
	}

	@Override
	public void addNamedQuery(String name, Query query) {
		emf().addNamedQuery(name, query);
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		return emf().unwrap(cls);
	}

	@Override
	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
		emf().addNamedEntityGraph(graphName, entityGraph);
	}
}
