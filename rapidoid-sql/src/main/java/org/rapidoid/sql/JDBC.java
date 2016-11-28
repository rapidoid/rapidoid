package org.rapidoid.sql;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
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
public class JDBC extends RapidoidThing {

	private static volatile JdbcClient DEFAULT;

	public static synchronized void reset() {
		DEFAULT = null;
	}

	public static JdbcClient newApi() {
		return new JdbcClient();
	}

	public static synchronized JdbcClient setup(String url, String driver, String username, String password) {
		DEFAULT = new JdbcClient();

		DEFAULT.url(url);
		DEFAULT.driver(driver);
		DEFAULT.username(username);
		DEFAULT.password(password);

		return DEFAULT;
	}

	public static synchronized JdbcClient defaultApi() {
		if (DEFAULT == null) {
			String url = JDBCConfig.url();
			String driver = JDBCConfig.driver();
			String username = JDBCConfig.username();
			String password = JDBCConfig.password();

			DEFAULT = setup(url, driver, username, password);
		}

		return DEFAULT;
	}

	public static JdbcClient username(String username) {
		return defaultApi().username(username);
	}

	public static JdbcClient password(String password) {
		return defaultApi().password(password);
	}

	public static JdbcClient driver(String driver) {
		return defaultApi().driver(driver);
	}

	public static JdbcClient url(String url) {
		return defaultApi().url(url);
	}

	public static JdbcClient mysql(String host, int port, String databaseName) {
		return defaultApi().mysql(host, port, databaseName);
	}

	public static JdbcClient h2(String databaseName) {
		return defaultApi().h2(databaseName);
	}

	public static JdbcClient hsql(String databaseName) {
		return defaultApi().hsql(databaseName);
	}

	public static void execute(String sql, Object... args) {
		defaultApi().execute(sql, args);
	}

	public static void tryToExecute(String sql, Object... args) {
		defaultApi().tryToExecute(sql, args);
	}

	public static <T> List<Map<String, Object>> query(String sql, Object... args) {
		return defaultApi().query(sql, args);
	}

	public static Connection getConnection() {
		return defaultApi().getConnection();
	}

	public static void release(Connection connection) {
		defaultApi().release(connection);
	}

	public static PreparedStatement prepare(Connection conn, String sql, Object... args) {
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);

			bind(stmt, args);

			return stmt;

		} catch (SQLException e) {
			throw new RuntimeException("Cannot create prepared statement!", e);
		}
	}

	public static void bind(PreparedStatement stmt, Object... args) throws SQLException {
		for (int i = 0; i < args.length; i++) {
			stmt.setObject(i + 1, args[i]);
		}
	}

	public static <T> List<T> rows(Class<T> resultType, ResultSet rs) throws SQLException {
		List<T> rows = U.list();

		ResultSetMetaData meta = rs.getMetaData();
		int columnsN = meta.getColumnCount();
		Prop[] props = new Prop[columnsN];

		for (int i = 0; i < props.length; i++) {
			String name = meta.getColumnLabel(i + 1);
			props[i] = Beany.property(resultType, name, true);
		}

		while (rs.next()) {
			T row = Cls.newInstance(resultType);

			for (int i = 0; i < columnsN; i++) {
				Object value = rs.getObject(i + 1); // 1-indexed
				props[i].set(row, value);
			}

			rows.add(row);
		}

		return rows;
	}

	public static List<Map<String, Object>> rows(ResultSet rs) throws SQLException {
		List<Map<String, Object>> rows = U.list();

		while (rs.next()) {
			rows.add(row(rs));
		}

		return rows;
	}

	public static Map<String, Object> row(ResultSet rs) throws SQLException {
		Map<String, Object> row = U.map();

		ResultSetMetaData meta = rs.getMetaData();
		int columnsNumber = meta.getColumnCount();

		for (int i = 1; i <= columnsNumber; i++) {
			String name = meta.getColumnLabel(i);
			Object value = rs.getObject(i);
			row.put(name, value);
		}

		return row;
	}

}
