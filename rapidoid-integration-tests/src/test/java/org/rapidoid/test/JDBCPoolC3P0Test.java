package org.rapidoid.test;

/*
 * #%L
 * rapidoid-integration-tests
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

import java.util.Map;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.sql.C3P0ConnectionPool;
import org.rapidoid.sql.SQL;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class JDBCPoolC3P0Test extends IntegrationTestCommons {

	@Test(timeout = 30000)
	public void testJDBCPoolC3P0() {

		new C3P0ConnectionPool(SQL.h2());

		SQL.execute("create table abc (id int, name varchar)");
		SQL.execute("insert into abc values (?, ?)", 123, "xyz");

		final Map<String, ?> expected = U.map("id", 123, "name", "xyz");

		UTILS.benchmarkMT(100, "select", 100000, new Runnable() {
			@Override
			public void run() {
				Map<String, Object> record = U.single(SQL.get("select id, name from abc"));
				record = UTILS.lowercase(record);
				eq(record, expected);
			}
		});
	}

	@Test(timeout = 30000)
	public void testJDBCWithTextconfig() {

		Conf.set("jdbc", "url", "jdbc:h2:mem:mydb");
		Conf.set("jdbc", "username", "sa");

		new C3P0ConnectionPool(SQL.defaultInstance());

		SQL.execute("create table abc (id int, name varchar)");
		SQL.execute("insert into abc values (?, ?)", 123, "xyz");

		final Map<String, ?> expected = U.map("id", 123, "name", "xyz");

		UTILS.benchmarkMT(100, "select", 100000, new Runnable() {
			@Override
			public void run() {
				Map<String, Object> record = U.single(SQL.get("select id, name from abc"));
				record = UTILS.lowercase(record);
				eq(record, expected);
			}
		});
	}

}
