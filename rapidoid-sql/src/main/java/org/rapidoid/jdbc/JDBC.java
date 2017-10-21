package org.rapidoid.jdbc;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.datamodel.Results;
import org.rapidoid.lambda.Mapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
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
public class JDBC extends JdbcUtil {

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

	public static int execute(String sql, Map<String, ?> namedArgs) {
		return api().execute(sql, namedArgs);
	}

	public static int tryToExecute(String sql, Object... args) {
		return api().tryToExecute(sql, args);
	}

	public static int tryToExecute(String sql, Map<String, ?> namedArgs) {
		return api().tryToExecute(sql, namedArgs);
	}

	public static <T> Results<T> query(Class<T> resultType, String sql, Object... args) {
		return api().query(resultType, sql, args);
	}

	public static <T> Results<T> query(Class<T> resultType, String sql, Map<String, ?> namedArgs) {
		return api().query(resultType, sql, namedArgs);
	}

	public static <T> Results<T> query(Mapper<ResultSet, T> resultMapper, String sql, Object... args) {
		return api().query(resultMapper, sql, args);
	}

	public static <T> Results<T> query(Mapper<ResultSet, T> resultMapper, String sql, Map<String, ?> namedArgs) {
		return api().query(resultMapper, sql, namedArgs);
	}

	public static Results<Map<String, Object>> query(String sql, Object... args) {
		return api().query(sql, args);
	}

	public static Results<Map<String, Object>> query(String sql, Map<String, ?> namedArgs) {
		return api().query(sql, namedArgs);
	}

	public static Connection getConnection() {
		return api().getConnection();
	}

	public static void release(Connection connection) {
		api().release(connection);
	}

	public static DataSource bootstrapDatasource() {
		return api().bootstrapDatasource();
	}

}
