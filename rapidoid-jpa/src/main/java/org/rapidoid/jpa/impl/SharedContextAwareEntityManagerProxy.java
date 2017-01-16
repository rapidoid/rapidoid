package org.rapidoid.jpa.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.jpa.JPA;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;
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
public class SharedContextAwareEntityManagerProxy extends RapidoidThing implements EntityManager {

	public static final SharedContextAwareEntityManagerProxy INSTANCE = new SharedContextAwareEntityManagerProxy();

	private SharedContextAwareEntityManagerProxy() {
	}

	private EntityManager em() {
		return JPA.currentEM();
	}

	@Override
	public void persist(Object entity) {
		em().persist(entity);
	}

	@Override
	public <T> T merge(T entity) {
		return em().merge(entity);
	}

	@Override
	public void remove(Object entity) {
		em().remove(entity);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return em().find(entityClass, primaryKey);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
		return em().find(entityClass, primaryKey, properties);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
		return em().find(entityClass, primaryKey, lockMode);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
		return em().find(entityClass, primaryKey, lockMode, properties);
	}

	@Override
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		return em().getReference(entityClass, primaryKey);
	}

	@Override
	public void flush() {
		em().flush();
	}

	@Override
	public void setFlushMode(FlushModeType flushMode) {
		em().setFlushMode(flushMode);
	}

	@Override
	public FlushModeType getFlushMode() {
		return em().getFlushMode();
	}

	@Override
	public void lock(Object entity, LockModeType lockMode) {
		em().lock(entity, lockMode);
	}

	@Override
	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		em().lock(entity, lockMode, properties);
	}

	@Override
	public void refresh(Object entity) {
		em().refresh(entity);
	}

	@Override
	public void refresh(Object entity, Map<String, Object> properties) {
		em().refresh(entity, properties);
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode) {
		em().refresh(entity, lockMode);
	}

	@Override
	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
		em().refresh(entity, lockMode, properties);
	}

	@Override
	public void clear() {
		em().clear();
	}

	@Override
	public void detach(Object entity) {
		em().detach(entity);
	}

	@Override
	public boolean contains(Object entity) {
		return em().contains(entity);
	}

	@Override
	public LockModeType getLockMode(Object entity) {
		return em().getLockMode(entity);
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		em().setProperty(propertyName, value);
	}

	@Override
	public Map<String, Object> getProperties() {
		return em().getProperties();
	}

	@Override
	public Query createQuery(String qlString) {
		return em().createQuery(qlString);
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		return em().createQuery(criteriaQuery);
	}

	@Override
	public Query createQuery(CriteriaUpdate updateQuery) {
		return em().createQuery(updateQuery);
	}

	@Override
	public Query createQuery(CriteriaDelete deleteQuery) {
		return em().createQuery(deleteQuery);
	}

	@Override
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		return em().createQuery(qlString, resultClass);
	}

	@Override
	public Query createNamedQuery(String name) {
		return em().createNamedQuery(name);
	}

	@Override
	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		return em().createNamedQuery(name, resultClass);
	}

	@Override
	public Query createNativeQuery(String sqlString) {
		return em().createNativeQuery(sqlString);
	}

	@Override
	public Query createNativeQuery(String sqlString, Class resultClass) {
		return em().createNativeQuery(sqlString, resultClass);
	}

	@Override
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		return em().createNativeQuery(sqlString, resultSetMapping);
	}

	@Override
	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
		return em().createNamedStoredProcedureQuery(name);
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
		return em().createStoredProcedureQuery(procedureName);
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
		return em().createStoredProcedureQuery(procedureName, resultClasses);
	}

	@Override
	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
		return em().createStoredProcedureQuery(procedureName, resultSetMappings);
	}

	@Override
	public void joinTransaction() {
		em().joinTransaction();
	}

	@Override
	public boolean isJoinedToTransaction() {
		return em().isJoinedToTransaction();
	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		return em().unwrap(cls);
	}

	@Override
	public Object getDelegate() {
		return em().getDelegate();
	}

	@Override
	public void close() {
		em().close();
	}

	@Override
	public boolean isOpen() {
		return em().isOpen();
	}

	@Override
	public EntityTransaction getTransaction() {
		return em().getTransaction();
	}

	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		return em().getEntityManagerFactory();
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return em().getCriteriaBuilder();
	}

	@Override
	public Metamodel getMetamodel() {
		return em().getMetamodel();
	}

	@Override
	public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
		return em().createEntityGraph(rootType);
	}

	@Override
	public EntityGraph<?> createEntityGraph(String graphName) {
		return em().createEntityGraph(graphName);
	}

	@Override
	public EntityGraph<?> getEntityGraph(String graphName) {
		return em().getEntityGraph(graphName);
	}

	@Override
	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
		return em().getEntityGraphs(entityClass);
	}
}
