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
import java.util.List;
import java.util.Map;

public class SQL {

	private static final SQLAPI API = new SQLAPI();

	public static SQLAPI newInstance() {
		return new SQLAPI();
	}

	public static SQLAPI user(String user) {
		return API.user(user);
	}

	public static SQLAPI password(String password) {
		return API.password(password);
	}

	public static SQLAPI driver(String driver) {
		return API.driver(driver);
	}

	public static SQLAPI host(String host) {
		return API.host(host);
	}

	public static SQLAPI port(int port) {
		return API.port(port);
	}

	public static SQLAPI db(String databaseName) {
		return API.db(databaseName);
	}

	public static SQLAPI url(String url) {
		return API.url(url);
	}

	public static SQLAPI mysql() {
		return API.mysql();
	}

	public static SQLAPI h2() {
		return API.h2();
	}

	public static SQLAPI hsql() {
		return API.hsql();
	}

	public static void run(String sql, Object... args) {
		API.run(sql, args);
	}

	public static void tryToRun(String sql, Object... args) {
		API.tryToRun(sql, args);
	}

	public static PreparedStatement statement(Connection conn, String sql, Object... args) {
		return SQLAPI.createStatement(conn, sql, args);
	}

	public static <T> List<Map<String, Object>> get(String sql, Object... args) {
		return API.getRows(sql, args);
	}

}
