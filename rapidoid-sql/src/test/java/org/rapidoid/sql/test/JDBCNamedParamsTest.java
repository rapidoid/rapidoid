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
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class JDBCNamedParamsTest extends SQLTestCommons {

	@Test
	public void testSQLWithNamedParams() {
		JDBC.execute("CREATE TABLE movie (id int, title varchar(99))");

		eq(JDBC.execute("INSERT INTO movie VALUES ($id, $name)", U.map("id", 10, "name", "Rambo", "xx", "yy")), 1);
		eq(JDBC.tryToExecute("INSERT INTO movie VALUES ($id, $name)", U.map("id", 20, "name", "Hackers")), 1);
		eq(JDBC.execute("INSERT INTO movie VALUES ($id, '$abc')", U.map("id", 30, "xx", "yy")), 1);

		List<Map<String, Object>> movies1 = JDBC.query("SELECT title as T FROM movie WHERE id = $id", U.map("id", 20)).all();
		eq(movies1, U.list(U.map("T", "Hackers")));


		List<Map<String, Object>> movies2 = JDBC.query("SELECT count(*) as N FROM movie WHERE id = $ID AND title = '$abc'", U.map("ID", 30)).all();
		eq(movies2, U.list(U.map("N", 1L)));
	}

}
