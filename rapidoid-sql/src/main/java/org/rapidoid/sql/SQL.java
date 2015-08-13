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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class SQL {

	private static SQLAPI DEFAULT;

	public static SQLAPI newInstance() {
		return new SQLAPI();
	}

	public static synchronized SQLAPI defaultInstance() {
		if (DEFAULT == null) {
			DEFAULT = new SQLAPI();

			String url = JDBCConfig.url();
			String driver = JDBCConfig.driver();
			String username = JDBCConfig.username();
			String password = JDBCConfig.password();

			DEFAULT.url(url);
			DEFAULT.driver(driver);
			DEFAULT.user(username);
			DEFAULT.password(password);
			DEFAULT.pooled();

			String maskedPassword = U.isEmpty(password) ? "<empty>" : "<specified>";
			Log.info("Initialized the default JDBC/SQL API", "url", url, "driver", driver, "username", username,
					"password", maskedPassword);
		}

		return DEFAULT;
	}

	public static SQLAPI user(String user) {
		return defaultInstance().user(user);
	}

	public static SQLAPI password(String password) {
		return defaultInstance().password(password);
	}

	public static SQLAPI driver(String driver) {
		return defaultInstance().driver(driver);
	}

	public static SQLAPI connectionPool(ConnectionPool connectionPool) {
		return defaultInstance().connectionPool(connectionPool);
	}

	public static SQLAPI host(String host) {
		return defaultInstance().host(host);
	}

	public static SQLAPI port(int port) {
		return defaultInstance().port(port);
	}

	public static SQLAPI db(String databaseName) {
		return defaultInstance().db(databaseName);
	}

	public static SQLAPI url(String url) {
		return defaultInstance().url(url);
	}

	public static SQLAPI mysql() {
		return defaultInstance().mysql();
	}

	public static SQLAPI h2() {
		return defaultInstance().h2();
	}

	public static SQLAPI hsql() {
		return defaultInstance().hsql();
	}

	public static SQLAPI pooled() {
		return defaultInstance().pooled();
	}

	public static void execute(String sql, Object... args) {
		defaultInstance().execute(sql, args);
	}

	public static void tryToExecute(String sql, Object... args) {
		defaultInstance().tryToExecute(sql, args);
	}

	public static PreparedStatement statement(Connection conn, String sql, Object... args) {
		return SQLAPI.createStatement(conn, sql, args);
	}

	public static <T> List<Map<String, Object>> get(String sql, Object... args) {
		return defaultInstance().getRows(sql, args);
	}

	public static List<Map<String, Object>> rows(ResultSet rs) throws SQLException {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			rows.add(row(rs));
		}
		return rows;
	}

	public static Map<String, Object> row(ResultSet rs) throws SQLException {
		Map<String, Object> row = new LinkedHashMap<String, Object>();

		ResultSetMetaData meta = rs.getMetaData();
		int columnsNumber = meta.getColumnCount();

		for (int i = 1; i <= columnsNumber; i++) {
			Object obj = rs.getObject(i);
			row.put(meta.getColumnLabel(i), obj);
		}

		return row;
	}

	public static Connection getConnection() {
		return defaultInstance().getConnection();
	}

	public static void release(Connection connection) {
		defaultInstance().release(connection);
	}

}
