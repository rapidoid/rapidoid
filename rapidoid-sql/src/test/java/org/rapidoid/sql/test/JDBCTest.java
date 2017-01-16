package org.rapidoid.sql.test;

/*
 * #%L
 * rapidoid-sql
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

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.jdbc.C3P0ConnectionPool;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.x")
public class JDBCTest extends SQLTestCommons {

	@Test
	public void testWithMySQL() {
		JDBC.mysql("non-existing.non-existing--host", 12345, "testdb").usePool(false);

		// only testing if the driver is properly loaded and trying to connect
		try {
			JDBC.execute("create table abc (id int)");
		} catch (Exception e) {
			eq(e.getCause().getClass(), CommunicationsException.class);
		}

		isFalse(JDBC.defaultApi().usePool());
	}

	@Test
	public void testWithH2() {
		JDBC.h2("test");
		insertAndCheckData(JDBC.defaultApi());
	}

	@Test
	public void testWithH2AndC3P0() {
		new C3P0ConnectionPool(JDBC.h2("test"));
		insertAndCheckData(JDBC.defaultApi());
	}

	@Test
	public void testWithHSQLDB() {
		JDBC.hsql("test");
		insertAndCheckData(JDBC.defaultApi());
		isTrue(JDBC.defaultApi().usePool());
	}

	@Test
	public void testWithHSQLDBAndC3P0() {
		new C3P0ConnectionPool(JDBC.hsql("test"));
		insertAndCheckData(JDBC.defaultApi());
	}

	@Test
	public void testMultiAPI() {
		JdbcClient client1 = JDBC.newApi().hsql("test");
		JdbcClient client2 = JDBC.newApi().h2("test");

		insertAndCheckData(client1);
		insertAndCheckData(client2);
	}

	private void insertAndCheckData(JdbcClient client) {
		client.tryToExecute("DROP TABLE movie");
		client.execute("CREATE TABLE movie (id int, title varchar(99))");

		client.execute("INSERT INTO movie VALUES (?, ?)", 10, "Rambo");
		client.execute("INSERT INTO movie VALUES (?, ?)", 20, "Hackers");

		for (int i = 0; i < 1000; i++) {
			client.execute("INSERT INTO movie VALUES (?, ?)", 100 + i, "movie" + i);
		}

		List<Map<String, Object>> rows = client.query("SELECT * FROM movie WHERE id < ?", 25);

		eq(rows.size(), 2);
		eq(Msc.lowercase(rows.get(0)), U.map("id", 10, "title", "rambo"));
		eq(Msc.lowercase(rows.get(1)), U.map("id", 20, "title", "hackers"));

		List<Movie> movies = client.query(Movie.class, "SELECT * FROM movie WHERE id < ?", 25);

		eq(movies.size(), 2);

		Movie movie1 = movies.get(0);
		eq(movie1.id, 10);
		eq(movie1.getTitle(), "Rambo");

		Movie movie2 = movies.get(1);
		eq(movie2.id, 20);
		eq(movie2.getTitle(), "Hackers");
	}

}
