package org.rapidoid.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;

import java.sql.Connection;
import java.sql.SQLException;

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

@Authors("Nikolche Mihajlovski")
@Since("5.3.4")
public class HikariConnectionPool extends RapidoidThing implements ConnectionPool {

	private final HikariDataSource pool;

	public HikariConnectionPool(String jdbcUrl, String driverClass, String username, String password) {
		this.pool = init(jdbcUrl, driverClass, username, password);
	}

	public HikariConnectionPool(JdbcClient jdbc) {
		this(jdbc.url(), jdbc.driver(), jdbc.username(), jdbc.password());
		jdbc.pool(this);
	}

	private HikariDataSource init(String jdbcUrl, String driverClass, String username, String password) {
		HikariConfig config = new HikariConfig();

		config.setJdbcUrl(jdbcUrl);
		config.setUsername(username);
		config.setPassword(password);
		config.setDriverClassName(driverClass);

		Conf.HIKARI.applyTo(config);

		return new HikariDataSource(config);
	}

	@Override
	public Connection getConnection(String jdbcUrl) throws SQLException {
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

	public HikariDataSource pool() {
		return pool;
	}
}
