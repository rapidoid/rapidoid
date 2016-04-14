package org.rapidoid.test;

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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.sql.JDBC;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class JDBCPoolC3P0Test extends TestCommons {

	@Test(timeout = 30000)
	public void testJDBCPoolC3P0() {

		JDBC.h2("test1").pooled();

		JDBC.execute("create table abc (id int, name varchar)");
		JDBC.execute("insert into abc values (?, ?)", 123, "xyz");

		final Map<String, ?> expected = U.map("id", 123, "name", "xyz");

		Msc.benchmarkMT(100, "select", 100000, new Runnable() {
			@Override
			public void run() {
				Map<String, Object> record = U.single(JDBC.query("select id, name from abc"));
				record = Msc.lowercase(record);
				eq(record, expected);
			}
		});
	}

	@Test(timeout = 30000)
	public void testJDBCWithTextconfig() {

		Conf.JDBC.set("url", "jdbc:h2:mem:mydb");
		Conf.JDBC.set("username", "sa");

		JDBC.defaultApi().pooled();

		JDBC.execute("create table abc (id int, name varchar)");
		JDBC.execute("insert into abc values (?, ?)", 123, "xyz");

		final Map<String, ?> expected = U.map("id", 123, "name", "xyz");

		Msc.benchmarkMT(100, "select", 100000, new Runnable() {
			@Override
			public void run() {
				Map<String, Object> record = U.single(JDBC.query("select id, name from abc"));
				record = Msc.lowercase(record);
				eq(record, expected);
			}
		});
	}

}
