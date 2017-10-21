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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class JDBCPagingTest extends SQLTestCommons {

	@Test
	public void testExplicitPaging() {
		testPagingWithSql("SELECT * FROM movie WHERE id < ? OFFSET $skip LIMIT $limit");
	}

	@Test
	public void testImplicitPaging() {
		testPagingWithSql("SELECT * FROM movie WHERE id < ?");
	}

	private void testPagingWithSql(String sql) {
		JdbcClient client = JDBC.api("a").hsql("test");

		client.tryToExecute("DROP TABLE movie");
		client.execute("CREATE TABLE movie (id int, title varchar(99))");

		for (int id = 1; id <= 100; id++) {
			client.execute("INSERT INTO movie VALUES (?, ?)", id, "movie" + id);
		}

		List<Movie> movies = client.query(Movie.class, sql, 1000).all();

		eq(movies.size(), 100);

		for (int i = 1; i <= 10; i++) {
			checkRecord(sql, client, i);
		}

		checkPageSize(sql, client, 0, 1, 1);
		checkPageSize(sql, client, 0, 2, 2);
		checkPageSize(sql, client, 0, 100, 100);

		checkPageSize(sql, client, 1, 2, 2);
		checkPageSize(sql, client, 1, 10, 10);

		checkPageSize(sql, client, 99, 1000, 1);
		checkPageSize(sql, client, 100, 1000, 0);
		checkPageSize(sql, client, 1000, 100000, 0);
	}

	private void checkPageSize(String sql, JdbcClient client, int skip, int limit, int pageSize) {
		eq(pageSize, client.query(sql, Integer.MAX_VALUE).page(skip, limit).size());
	}

	private void checkRecord(String sql, JdbcClient client, int n) {
		List<Map<String, Object>> page = client.query(sql, 15).page(n - 1, 2);

		eq(2, page.size());

		eq(U.map("id", n, "title", "movie" + n), Msc.lowercase(page.get(0)));
		eq(U.map("id", n + 1, "title", "movie" + (n + 1)), Msc.lowercase(page.get(1)));
	}

}
