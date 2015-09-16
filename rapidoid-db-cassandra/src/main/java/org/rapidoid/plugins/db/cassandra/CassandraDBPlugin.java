package org.rapidoid.plugins.db.cassandra;

import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.concurrent.Callbacks;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.plugins.db.DBPluginBase;
import org.rapidoid.util.U;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.UDTMapper;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

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

	private volatile Cluster cluster;

	private volatile Session sharedSession;

	public CassandraDBPlugin() {
		super("cassandra");
	}

	@Override
	protected void doRestart() throws Exception {
		stopIfRunning();
		initialize();
	}

	private void stopIfRunning() {
		if (sharedSession != null && !sharedSession.isClosed()) {
			try {
				sharedSession.close();
			} catch (Exception e) {
				Log.error("Couldn't close the session!", e);
			}
		}

		if (cluster != null && !cluster.isClosed()) {
			try {
				cluster.close();
			} catch (Exception e) {
				Log.error("Couldn't close the cluster!", e);
			}
		}
	}

	private void initialize() {
		Builder builder = Cluster.builder();

		List<String> servers = option("servers", U.list("127.0.0.1"));

		for (String server : servers) {
			Log.warn("Adding Cassandra peer (contact point)", "peer", server);
			builder.addContactPoint(server);
		}

		PoolingOptions poolingOptions = new PoolingOptions(); // TODO improve
		this.cluster = builder.withPoolingOptions(poolingOptions).build();
	}

	public synchronized Session provideSession() {
		try {
			if (cluster.isClosed()) {
				restart();
			}

			if (sharedSession == null || sharedSession.isClosed()) {
				sharedSession = cluster.connect();
			}

			return sharedSession;
		} catch (Exception e) {
			throw U.rte("Couldn't initialize the Cassandra session!", e);
		}
	}

	@Override
	public String insert(Object entity) {
		Mapper<Object> mapper = getMapperFor(entity, provideSession());
		mapper.save(entity);

		return Beany.getId(entity);
	}

	@Override
	public void update(String id, Object entity) {
		Mapper<Object> mapper = getMapperFor(entity, provideSession());
		mapper.save(entity);
	}

	@Override
	public <T> T getIfExists(Class<T> clazz, String id) {
		Mapper<T> mapper = new MappingManager(provideSession()).mapper(clazz);

		Object idCorrectType = castId(clazz, id);
		T entity = mapper.get(idCorrectType);

		return entity;
	}

	@Override
	public <E> void delete(Class<E> clazz, String id) {
		Mapper<E> mapper = new MappingManager(provideSession()).mapper(clazz);
		Object idCorrectType = castId(clazz, id);
		mapper.delete(idCorrectType);
	}

	@Override
	public List<Map<String, Object>> query(String cql, Object... args) {
		ResultSet rs = provideSession().execute(cql, args);
		return results(rs.all());
	}

	@Override
	public void queryAsync(String cql, final Callback<List<Map<String, Object>>> callback, Object... args) {
		final ResultSetFuture future = provideSession().executeAsync(cql, args);

		Futures.addCallback(future, new FutureCallback<ResultSet>() {

			@Override
			public void onSuccess(ResultSet rs) {
				List<Map<String, Object>> result = results(rs.all());
				Callbacks.done(callback, result, null);
			}

			@Override
			public void onFailure(Throwable t) {
				Callbacks.done(callback, null, t);
			}

		}, Jobs.executor());
	}

	private static List<Map<String, Object>> results(List<Row> rows) {
		List<Map<String, Object>> results = U.list();

		for (Row row : rows) {
			Map<String, Object> result = U.map();
			ColumnDefinitions cols = row.getColumnDefinitions();

			for (Definition col : cols.asList()) {
				String name = col.getName();
				Object val = row.getObject(name);
				result.put(name, val);
			}

			results.add(result);
		}

		return results;
	}

	@Override
	public <E> List<E> query(Class<E> clazz, String query, Object... args) {
		Session session = provideSession();

		ResultSet rs = session.execute(query, args);
		Mapper<E> mapper = new MappingManager(session).mapper(clazz);
		Result<E> result = mapper.map(rs);

		return result.all();
	}

	@Override
	public <E> List<E> getAll(Class<E> clazz) {
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

	public synchronized Cluster cluster() {
		return cluster;
	}

	public synchronized Session session() {
		return provideSession();
	}

}
