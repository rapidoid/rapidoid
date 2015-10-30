package org.rapidoid.sql;

/*
 * #%L
 * rapidoid-sql
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

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

public class SQLTest extends TestCommons {

	@Test
	public void testWithMySQL() {
		SQL.mysql().db("test").host("non-existing.non-existing--host").port(12345);

		// only testing if the driver is properly loaded and trying to connect
		try {
			SQL.execute("create table abc (id int)");
		} catch (Exception e) {
			eq(e.getCause().getClass(), CommunicationsException.class);
		}
	}

	@Test
	public void testWithH2() {
		SQL.h2().db("test");
		insertAndCheckData();
	}

	@Test
	public void testWithH2AndC3P0() {
		new C3P0ConnectionPool(SQL.h2().db("test"));
		insertAndCheckData();
	}

	@Test
	public void testWithHSQLDB() {
		SQL.hsql().db("test");
		insertAndCheckData();
	}

	@Test
	public void testWithHSQLDBAndC3P0() {
		new C3P0ConnectionPool(SQL.hsql().db("test"));
		insertAndCheckData();
	}

	private void insertAndCheckData() {
		SQL.tryToExecute("DROP TABLE movie");
		SQL.execute("CREATE TABLE movie (id int, title varchar(99))");

		SQL.execute("INSERT INTO movie VALUES (?, ?)", 10, "Rambo");
		SQL.execute("INSERT INTO movie VALUES (?, ?)", 20, "Hackers");

		for (int i = 0; i < 1000; i++) {
			SQL.execute("INSERT INTO movie VALUES (?, ?)", 100 + i, "movie" + i);
		}

		List<Map<String, Object>> rows = SQL.get("SELECT * FROM movie WHERE id < ?", 25);
		System.out.println(rows);

		eq(rows.size(), 2);
		eq(UTILS.lowercase(rows.get(0)), U.map("id", 10, "title", "rambo"));
		eq(UTILS.lowercase(rows.get(1)), U.map("id", 20, "title", "hackers"));
	}

}
