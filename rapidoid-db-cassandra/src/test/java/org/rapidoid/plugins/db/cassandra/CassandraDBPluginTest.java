package org.rapidoid.plugins.db.cassandra;

import java.util.List;
import java.util.UUID;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.U;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.Session;

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

/**
 * This integration test must be manually enabled and executed, due to its delicate requirement: having access to at
 * least one Cassandra node (hostnames "cassandra_test1" and "cassandra_test2").
 */
@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class CassandraDBPluginTest extends TestCommons {

	private static final String CREATE_KEYSPACE = "CREATE KEYSPACE rapidoid_test WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': '1' }";
	private static final String DROP_KEYSPACE = "DROP KEYSPACE rapidoid_test";
	private static final String CREATE_TABLE_MOVIE = "CREATE TABLE rapidoid_test.movie (id uuid primary key, title varchar, year int)";

	// @Test
	public void testBasicCassandraCRUD() {
		PoolingOptions poolingOptions = new PoolingOptions();

		Cluster cluster = Cluster.builder().addContactPoint("cassandra_test1").addContactPoint("cassandra_test2")
				.withPoolingOptions(poolingOptions).build();

		Plugins.register(new CassandraDBPlugin(cluster));

		Session session = cluster.connect();

		try {
			session.execute(DROP_KEYSPACE);
		} catch (Exception e) {
			// do nothing
		}

		session.execute(CREATE_KEYSPACE);
		session.execute(CREATE_TABLE_MOVIE);

		Movie movie = new Movie(UUID.randomUUID(), "Rambo");
		String id = DB.insert(movie);
		notNull(id);

		eq(U.list(DB.query(Movie.class, "SELECT * FROM rapidoid_test.movie")).size(), 1);

		DB.update(movie);

		eq(U.list(DB.query(Movie.class, "SELECT * FROM rapidoid_test.movie")).size(), 1);

		Movie m2 = DB.get(Movie.class, id);
		notNull(m2);
		eq(m2.getTitle(), "Rambo");

		eq(U.list(DB.query(Movie.class, "SELECT * FROM rapidoid_test.movie")).size(), 1);

		Iterable<Movie> movies = DB.query(Movie.class, "SELECT * FROM rapidoid_test.movie");
		List<Movie> movies2 = U.list(movies);
		eq(movies2.size(), 1);
		eq(movies2.get(0).getTitle(), "Rambo");

		DB.delete(movie);

		eq(U.list(DB.query(Movie.class, "SELECT * FROM rapidoid_test.movie")).size(), 0);

		isNull(DB.getIfExists(Movie.class, id));
	}

}
