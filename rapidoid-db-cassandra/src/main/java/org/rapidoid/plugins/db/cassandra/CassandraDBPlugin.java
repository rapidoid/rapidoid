package org.rapidoid.plugins.db.cassandra;

import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.config.Conf;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.db.DBPluginBase;
import org.rapidoid.util.U;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.UDTMapper;

/*
 * #%L
 * rapidoid-db-cassandra
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
@Since("4.1.0")
public class CassandraDBPlugin extends DBPluginBase {

	private final Cluster cluster;

	public CassandraDBPlugin() {
		this(defaultCluster());
	}

	public static Cluster defaultCluster() {
		Builder builder = Cluster.builder();

		List<String> peers = Conf.nested("cassandra", "peers");

		if (peers != null) {
			for (String peer : peers) {
				Log.warn("Adding Cassandra peer (contact point)", "peer", peer);
				builder.addContactPoint(peer);
			}
		} else {
			Log.warn("Cassandra peers (contact points) were not configured, using 127.0.0.1 as default!");
			builder.addContactPoint("127.0.0.1");
		}

		PoolingOptions poolingOptions = new PoolingOptions(); // TODO improve
		Cluster cluster = builder.withPoolingOptions(poolingOptions).build();

		return cluster;
	}

	public CassandraDBPlugin(Cluster cluster) {
		super("cassandra");
		this.cluster = cluster;
	}

	@Override
	public String insert(Object entity) {
		Session session = getSession();

		try {
			Mapper<Object> mapper = getMapperFor(entity, session);
			mapper.save(entity);

			return Beany.getId(entity);
		} finally {
			close(session);
		}
	}

	@Override
	public void update(String id, Object entity) {
		Session session = getSession();

		try {
			Mapper<Object> mapper = getMapperFor(entity, session);
			mapper.save(entity);
		} finally {
			close(session);
		}
	}

	@Override
	public <T> T getIfExists(Class<T> clazz, String id) {
		Session session = getSession();

		try {
			Mapper<T> mapper = new MappingManager(session).mapper(clazz);

			Object idCorrectType = castId(clazz, id);
			T entity = mapper.get(idCorrectType);

			return entity;
		} finally {
			close(session);
		}
	}

	@Override
	public <E> void delete(Class<E> clazz, String id) {
		Session session = getSession();

		try {
			Mapper<E> mapper = new MappingManager(session).mapper(clazz);
			Object idCorrectType = castId(clazz, id);
			mapper.delete(idCorrectType);
		} finally {
			close(session);
		}
	}

	@Override
	public <E> Iterable<E> query(Class<E> clazz, String query, Object... args) {
		Session session = getSession();

		try {
			ResultSet rs = session.execute(query, args);
			Mapper<E> mapper = new MappingManager(session).mapper(clazz);
			Result<E> result = mapper.map(rs);

			return result;
		} finally {
			close(session);
		}
	}

	@Override
	public <E> Iterable<E> getAll(Class<E> clazz) {
		throw U.notSupported();
	}

	@Override
	public void transaction(final Runnable tx, final boolean readonly, final Callback<Void> callback) {
		throw U.notSupported();
	}

	@Override
	public void transaction(Runnable transaction, boolean readOnly) {
		throw U.notSupported();
	}

	@SuppressWarnings("unchecked")
	public <E> Mapper<E> getMapperFor(E entity, Session session) {
		Class<E> clazz = (Class<E>) entity.getClass();
		Mapper<E> mapper = new MappingManager(session).mapper(clazz);
		return mapper;
	}

	@SuppressWarnings("unchecked")
	public <E> UDTMapper<E> getUdtMapperFor(E entity, Session session) {
		Class<E> clazz = (Class<E>) entity.getClass();
		UDTMapper<E> mapper = new MappingManager(session).udtMapper(clazz);
		return mapper;
	}

	public String getTableNameFor(Class<?> entityType) {
		return entityType.getSimpleName().toLowerCase();
	}

	public Session getSession() {
		return cluster.connect();
	}

	public void close(Session session) {
		try {
			session.close();
		} catch (Exception e) {
			Log.error("Couldn't close the session!", e);
		}
	}

}
