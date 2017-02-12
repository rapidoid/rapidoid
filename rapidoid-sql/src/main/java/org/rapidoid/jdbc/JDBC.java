package org.rapidoid.jdbc;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
import org.rapidoid.collection.Coll;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
@Since("3.0.0")
public class JDBC extends RapidoidThing {

	private static final Map<String, JdbcClient> APIS = Coll.autoExpandingMap(String.class, JdbcClient.class);

	public static synchronized void reset() {
		APIS.clear();
	}

	/**
	 * Use JDBC#api instead.
	 */
	@Deprecated
	public static JdbcClient newApi() {
		return api(UUID.randomUUID().toString());
	}

	public static synchronized JdbcClient api() {
		return APIS.get("default");
	}

	public static synchronized JdbcClient api(String name) {
		return APIS.get(name);
	}

	/**
	 * Use JDBC#api instead.
	 */
	@Deprecated
	public static synchronized JdbcClient defaultApi() {
		return api();
	}

	public static JdbcClient username(String username) {
		return api().username(username);
	}

	public static JdbcClient password(String password) {
		return api().password(password);
	}

	public static JdbcClient driver(String driver) {
		return api().driver(driver);
	}

	public static JdbcClient url(String url) {
		return api().url(url);
	}

	public static JdbcClient mysql(String host, int port, String databaseName) {
		return api().mysql(host, port, databaseName);
	}

	public static JdbcClient h2(String databaseName) {
		return api().h2(databaseName);
	}

	public static JdbcClient hsql(String databaseName) {
		return api().hsql(databaseName);
	}

	public static int execute(String sql, Object... args) {
		return api().execute(sql, args);
	}

	public static void tryToExecute(String sql, Object... args) {
		api().tryToExecute(sql, args);
	}

	public static <T> List<T> query(Class<T> resultType, String sql, Object... args) {
		return api().query(resultType, sql, args);
	}

	public static <T> List<Map<String, Object>> query(String sql, Object... args) {
		return api().query(sql, args);
	}

	public static Connection getConnection() {
		return api().getConnection();
	}

	public static void release(Connection connection) {
		api().release(connection);
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
			Object arg = args[i];

			if (arg instanceof byte[]) {
				byte[] bytes = (byte[]) arg;
				stmt.setBytes(i + 1, bytes);

			} else if (arg instanceof UUID) {
				UUID uuid = (UUID) arg;
				byte[] bytes = Msc.uuidToBytes(uuid);
				stmt.setBytes(i + 1, bytes);

			} else {
				stmt.setObject(i + 1, arg);
			}
		}
	}

	public static <T> List<T> rows(Class<T> resultType, ResultSet rs) throws SQLException {
		List<T> rows = U.list();

		ResultSetMetaData meta = rs.getMetaData();
		int columnsN = meta.getColumnCount();
		Prop[] props = new Prop[columnsN];

		for (int i = 0; i < props.length; i++) {
			String name = meta.getColumnLabel(i + 1);
			props[i] = Beany.property(resultType, name, false);
		}

		while (rs.next()) {
			T row = Cls.newInstance(resultType);

			for (int i = 0; i < columnsN; i++) {
				if (props[i] != null) {
					Object value = rs.getObject(i + 1); // 1-indexed
					props[i].set(row, value);
				}
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
