package org.rapidoid.sql;

/*
 * #%L
 * rapidoid-sql
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SQLAPI {

	private boolean initialized;
	private String user;
	private String password;
	private String driver;
	private String db = "";
	private String url;
	private String completeUrl;
	private String host = "localhost";
	private int port;

	public synchronized SQLAPI user(String user) {
		this.user = user;
		this.initialized = false;
		return this;
	}

	public synchronized SQLAPI password(String password) {
		this.password = password;
		this.initialized = false;
		return this;
	}

	public synchronized SQLAPI driver(String driver) {
		this.driver = driver;
		this.initialized = false;
		return this;
	}

	public synchronized SQLAPI host(String host) {
		this.host = host;
		this.initialized = false;
		return this;
	}

	public synchronized SQLAPI port(int port) {
		this.port = port;
		this.initialized = false;
		return this;
	}

	public synchronized SQLAPI db(String databaseName) {
		this.db = databaseName;
		this.initialized = false;
		return this;
	}

	public synchronized SQLAPI url(String url) {
		this.url = url;
		this.initialized = false;
		return this;
	}

	public SQLAPI mysql() {
		return driver("com.mysql.jdbc.Driver").url("jdbc:mysql://<host>:<port>/<db>");
	}

	public SQLAPI h2() {
		return driver("org.h2.Driver").url("jdbc:h2:mem:<db>;DB_CLOSE_DELAY=-1").user("sa").password("");
	}

	public SQLAPI hsql() {
		return driver("org.hsqldb.jdbc.JDBCDriver").url("jdbc:h2:mem:<db>").user("sa").password("");
	}

	private String dbUrl() {
		return completeUrl;
	}

	private void registerJDBCDriver() {
		validateArgNotNull("driver", driver);

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot find JDBC driver class: " + driver);
		}
	}

	private void validateArgNotNull(String argName, String argValue) {
		if (argValue == null) {
			throw new RuntimeException("The JDBC parameter '" + argName + "' must be configured!");
		}
	}

	private synchronized void ensureIsInitialized() {
		if (!initialized) {
			registerJDBCDriver();
			setupCompleteUrl();
			initialized = true;
		}
	}

	private void setupCompleteUrl() {
		completeUrl = url.replace("<db>", db).replace("<user>", user).replace("<password>", password)
				.replace("<host>", host);

		if (port > 0) {
			completeUrl = completeUrl.replace("<port>", "" + port);
		} else {
			completeUrl = completeUrl.replace(":<port>", "").replace("<port>", "");
		}
	}

	private Connection getConnection() {
		// TODO use a connection pool!

		try {
			if (user != null && password != null) {
				return DriverManager.getConnection(dbUrl(), user, password);
			} else {
				return DriverManager.getConnection(dbUrl());
			}

		} catch (SQLException e) {
			throw new RuntimeException("Cannot create JDBC connection!", e);
		}
	}

	private static void close(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			throw new RuntimeException("Error occured while closing the connection!", e);
		}
	}

	private static void close(PreparedStatement stmt) {
		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException("Error occured while closing the statement!", e);
		}
	}

	private static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			throw new RuntimeException("Error occured while closing the ResultSet!", e);
		}
	}

	static PreparedStatement createStatement(Connection conn, String sql, Object[] args) {
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);

			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				stmt.setObject(i + 1, arg);
			}

			return stmt;
		} catch (SQLException e) {
			throw new RuntimeException("Cannot create prepared statement!", e);
		}
	}

	public void run(String sql, Object... args) {
		ensureIsInitialized();
		Connection conn = getConnection();
		PreparedStatement stmt = null;

		try {
			stmt = createStatement(conn, sql, args);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(stmt);
			close(conn);
		}
	}

	public void tryToRun(String sql, Object... args) {
		try {
			run(sql, args);
		} catch (Exception e) {
			// ignore the exception
		}
	}

	public <T> List<Map<String, Object>> getRows(String sql, Object... args) {
		ensureIsInitialized();
		Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

		try {
			stmt = createStatement(conn, sql, args);
			rs = stmt.executeQuery();

			while (rs.next()) {
				rows.add(readRow(rs));
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}

		return rows;
	}

	static Map<String, Object> readRow(ResultSet rs) throws SQLException {
		Map<String, Object> row = new LinkedHashMap<String, Object>();

		ResultSetMetaData meta = rs.getMetaData();
		int columnsNumber = meta.getColumnCount();

		for (int i = 1; i <= columnsNumber; i++) {
			Object obj = rs.getObject(i);
			row.put(meta.getColumnLabel(i), obj);
		}

		return row;
	}

}
