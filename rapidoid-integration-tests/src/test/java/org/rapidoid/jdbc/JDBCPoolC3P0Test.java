package org.rapidoid.jdbc;

/*
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class JDBCPoolC3P0Test extends IsolatedIntegrationTest {

	@Test(timeout = 30000)
	public void testJDBCPoolC3P0() {

		JDBC.h2("test1").pooled();

		C3P0ConnectionPool pool = (C3P0ConnectionPool) JDBC.defaultApi().pool();
		ComboPooledDataSource c3p0 = pool.pool();

		// validate default config
		eq(c3p0.getMinPoolSize(), 5);
		eq(c3p0.getInitialPoolSize(), 5);
		eq(c3p0.getAcquireIncrement(), 5);
		eq(c3p0.getMaxPoolSize(), 100);
		eq(c3p0.getMaxStatementsPerConnection(), 10);

		JDBC.execute("create table abc (id int, name varchar)");
		JDBC.execute("insert into abc values (?, ?)", 123, "xyz");

		final Map<String, ?> expected = U.map("id", 123, "name", "xyz");

		Msc.benchmarkMT(100, "select", 100000, () -> {
			Map<String, Object> record = U.single(JDBC.query("select id, name from abc"));
			record = Msc.lowercase(record);
			eq(record, expected);
		});
	}

	@Test(timeout = 30000)
	public void testJDBCWithTextConfig() {

		Conf.JDBC.set("driver", "org.h2.Driver");
		Conf.JDBC.set("url", "jdbc:h2:mem:mydb");
		Conf.JDBC.set("username", "sa");
		Conf.C3P0.set("maxPoolSize", "123");

		JDBC.defaultApi().pooled();

		JdbcClient jdbc = JDBC.defaultApi();
		eq(jdbc.driver(), "org.h2.Driver");

		C3P0ConnectionPool pool = (C3P0ConnectionPool) JDBC.defaultApi().pool();
		ComboPooledDataSource c3p0 = pool.pool();

		eq(c3p0.getMinPoolSize(), 5);
		eq(c3p0.getMaxPoolSize(), 123);

		JDBC.execute("create table abc (id int, name varchar)");
		JDBC.execute("insert into abc values (?, ?)", 123, "xyz");

		final Map<String, ?> expected = U.map("id", 123, "name", "xyz");

		Msc.benchmarkMT(100, "select", 100000, () -> {
			Map<String, Object> record = U.single(JDBC.query("select id, name from abc"));
			record = Msc.lowercase(record);
			eq(record, expected);
		});
	}

}
