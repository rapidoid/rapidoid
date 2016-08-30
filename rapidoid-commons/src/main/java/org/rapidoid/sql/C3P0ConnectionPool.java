package org.rapidoid.sql;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.u.U;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

/*
 * #%L
 * rapidoid-commons
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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class C3P0ConnectionPool extends RapidoidThing implements ConnectionPool {

	private final ComboPooledDataSource pool = new ComboPooledDataSource();

	public C3P0ConnectionPool(String jdbcUrl, String driverClass, String username, String password) {
		init(jdbcUrl, driverClass, username, password);
	}

	public C3P0ConnectionPool(JdbcClient jdbc) {
		this(jdbc.url(), jdbc.driver(), jdbc.username(), jdbc.password());
		jdbc.pool(this);
	}

	private void init(String jdbcUrl, String driverClass, String username, String password) {

		try {
			pool.setDriverClass(driverClass);
		} catch (PropertyVetoException e) {
			throw U.rte("Cannot load JDBC driver!", e);
		}

		pool.setJdbcUrl(jdbcUrl);
		pool.setUser(username);
		pool.setPassword(password);

		Conf.C3P0.applyTo(pool);
	}

	@Override
	public Connection getConnection(String jdbcUrl) throws SQLException {
		U.must(U.eq(jdbcUrl, pool.getJdbcUrl()), "The JDBC URLs don't match: '%s' and '%s'!", jdbcUrl, pool.getJdbcUrl());
		return pool.getConnection();
	}

	@Override
	public Connection getConnection(String jdbcUrl, String username, String password) throws SQLException {
		return pool.getConnection(username, password);
	}

	@Override
	public void releaseConnection(Connection connection) throws SQLException {
		connection.close();
	}

	public ComboPooledDataSource pool() {
		return pool;
	}
}
