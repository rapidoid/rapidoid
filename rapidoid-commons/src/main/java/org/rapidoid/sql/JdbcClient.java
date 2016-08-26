package org.rapidoid.sql;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.sql.*;
import java.util.List;
import java.util.Map;

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
@Since("3.0.0")
public class JdbcClient extends RapidoidThing {

	private boolean initialized;

	private String username;
	private String password;
	private String driver;
	private String url;

	private volatile ConnectionPool pool = new NoConnectionPool();

	public synchronized JdbcClient username(String username) {
		this.username = username;
		this.initialized = false;
		return this;
	}

	public synchronized JdbcClient password(String password) {
		this.password = password;
		this.initialized = false;
		return this;
	}

	public synchronized JdbcClient driver(String driver) {
		this.driver = driver;
		this.initialized = false;
		return this;
	}

	public synchronized JdbcClient pool(ConnectionPool connectionPool) {
		this.pool = connectionPool;
		this.initialized = false;
		return this;
	}

	public synchronized JdbcClient url(String url) {
		this.url = url;
		this.initialized = false;
		return this;
	}

	public JdbcClient mysql(String host, int port, String databaseName) {
		return driver("com.mysql.jdbc.Driver").url(U.frmt("jdbc:mysql://%s:%s/%s", host, port, databaseName));
	}

	public JdbcClient h2(String databaseName) {
		return driver("org.h2.Driver").url("jdbc:h2:mem:" + databaseName + ";DB_CLOSE_DELAY=-1").username("sa").password("");
	}

	public JdbcClient hsql(String databaseName) {
		return driver("org.hsqldb.jdbc.JDBCDriver").url("jdbc:hsqldb:mem:" + databaseName).username("sa").password("");
	}

	private void registerJDBCDriver() {
		if (driver == null) {
			driver = JDBCConfig.driver();
		}

		validateArgNotNull("driver", driver);

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw U.rte("Cannot find JDBC driver class: " + driver);
		}
	}

	private void validateArgNotNull(String argName, String argValue) {
		if (argValue == null) {
			throw U.rte("The JDBC parameter '" + argName + "' must be configured!");
		}
	}

	private synchronized void ensureIsInitialized() {
		if (!initialized) {
			validate();
			registerJDBCDriver();

			String maskedPassword = U.isEmpty(password) ? "<empty>" : "<specified>";
			Log.info("Initialized JDBC API", "!url", url, "!driver", driver, "!username", username, "!password", maskedPassword);

			initialized = true;
		}
	}

	private void validate() {
		U.must(U.notEmpty(username != null), "The database username must be specified!");
		U.must(U.notEmpty(password != null), "The database password must be specified!");
		U.must(U.notEmpty(url != null), "The database connection URL must be specified!");
		U.must(U.notEmpty(driver != null), "The database driver must be specified!");
	}

	public Connection getConnection() {
		ensureIsInitialized();

		return provideConnection();
	}

	private static void close(Connection conn) {
		try {
			if (conn != null) conn.close();

		} catch (SQLException e) {
			throw U.rte("Error occurred while closing the connection!", e);
		}
	}

	private static void close(PreparedStatement stmt) {
		try {
			if (stmt != null) stmt.close();

		} catch (SQLException e) {
			throw U.rte("Error occurred while closing the statement!", e);
		}
	}

	private static void close(ResultSet rs) {
		try {
			if (rs != null) rs.close();

		} catch (SQLException e) {
			throw U.rte("Error occurred while closing the ResultSet!", e);
		}
	}

	public void execute(String sql, Object... args) {
		ensureIsInitialized();

		Connection conn = provideConnection();
		PreparedStatement stmt = null;

		try {
			stmt = JDBC.prepare(conn, sql, args);
			stmt.execute();

		} catch (SQLException e) {
			throw U.rte(e);

		} finally {
			close(stmt);
			close(conn);
		}
	}

	public void tryToExecute(String sql, Object... args) {
		try {
			execute(sql, args);

		} catch (Exception e) {
			// ignore the exception
			Log.warn("Ignoring exception", "error", U.safe(e.getMessage()));
		}
	}

	public <T> List<T> query(Class<T> resultType, String sql, Object... args) {
		ensureIsInitialized();

		Connection conn = provideConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = JDBC.prepare(conn, sql, args);
			rs = stmt.executeQuery();

			if (resultType.equals(Map.class)) {
				return U.cast(JDBC.rows(rs));
			} else {
				return JDBC.rows(resultType, rs);
			}

		} catch (SQLException e) {
			throw U.rte(e);

		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
	}

	public List<Map<String, Object>> query(String sql, Object... args) {
		return U.cast(query(Map.class, sql, args));
	}

	private Connection provideConnection() {
		try {
			Connection conn;

			if (username != null) {
				String pass = U.safe(password);
				conn = pool.getConnection(url, username, pass);

				if (conn == null) {
					conn = DriverManager.getConnection(url, username, pass);
				}
			} else {
				conn = pool.getConnection(url);

				if (conn == null) {
					conn = DriverManager.getConnection(url);
				}
			}

			return conn;

		} catch (SQLException e) {
			throw U.rte("Cannot create JDBC connection!", e);
		}
	}

	public void release(Connection connection) {
		try {
			pool.releaseConnection(connection);
		} catch (SQLException e) {
			Log.error("Error while releasing a JDBC connection!", e);
		}
	}

	public String username() {
		return username;
	}

	public String password() {
		return password;
	}

	public String driver() {
		return driver;
	}

	public String url() {
		return url;
	}

	public ConnectionPool pool() {
		return pool;
	}

	public JdbcClient pooled() {
		ensureIsInitialized();

		this.pool = new C3P0ConnectionPool(this);
		return this;
	}

}
