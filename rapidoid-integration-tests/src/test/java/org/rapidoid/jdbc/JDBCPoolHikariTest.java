package org.rapidoid.jdbc;

/*
 * #%L
 * rapidoid-integration-tests
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

import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.4")
public class JDBCPoolHikariTest extends IsolatedIntegrationTest {

	@Test(timeout = 30000)
	public void testHikariPool() {

		Conf.HIKARI.set("maximumPoolSize", 234);

		JdbcClient jdbc = JDBC.api();
		jdbc.h2("hikari-test");
		jdbc.dataSource(HikariFactory.createDataSourceFor(jdbc));

		jdbc.execute("create table abc (id int, name varchar)");
		jdbc.execute("insert into abc values (?, ?)", 123, "xyz");

		final Map<String, ?> expected = U.map("id", 123, "name", "xyz");

		Msc.benchmarkMT(100, "select", 100000, () -> {
			Map<String, Object> record = U.single(JDBC.query("select id, name from abc").all());
			record = Msc.lowercase(record);
			eq(record, expected);
		});

		HikariDataSource hikari = (HikariDataSource) jdbc.dataSource();

		eq(hikari.getMaximumPoolSize(), 234);
	}

}
