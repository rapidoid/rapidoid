package org.rapidoid.sql;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.datamodel.Results;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.sql.dsl.*;
import org.rapidoid.u.U;

import java.util.Map;

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
@Since("6.0.0")
public class SQL extends RapidoidThing {

	/**
	 * Equivalent to SELECT [columns] ...
	 */
	public static SQLSelectDSL select(String... columns) {
		return new SQLSelectDSL(jdbc(), columns);
	}

	/**
	 * Equivalent to SELECT * FROM [table] ...
	 */
	public static SQLSelectFromDSL from(String table) {
		return select("*").from(table);
	}

	/**
	 * Equivalent to SELECT * FROM [table] ...
	 */
	public static Map<String, Object> get(String table, Object id) {
		return select("*").from(table).where("id = ?", id).getOnly();
	}

	/**
	 * Equivalent to SELECT * FROM [table] ...
	 */
	public static <T> T get(Class<T> resultType, Object id) {
		return select("*").from(tableOf(resultType)).where("id = ?", id).as(resultType).single();
	}

	public static String tableOf(Class<?> resultType) {
		return resultType.getSimpleName().toLowerCase();
	}

	/**
	 * Equivalent to INSERT INTO [table] ...
	 */
	public static SQLInsertDSL insert(String table) {
		return new SQLInsertDSL(jdbc(), table);
	}

	/**
	 * Equivalent to INSERT INTO [table] ...
	 */
	public static SQLInsertDSL insert(Class<?> entityType) {
		return insert(tableOf(entityType));
	}

	/**
	 * Equivalent to UPDATE [table] ...
	 */
	public static SQLUpdateDSL update(String table) {
		return new SQLUpdateDSL(jdbc(), table);
	}

	/**
	 * Equivalent to DELETE FROM [table] ...
	 */
	public static SQLDeleteDSL delete(String table) {
		return new SQLDeleteDSL(jdbc(), table);
	}

	/**
	 * Equivalent to DROP TABLE [table]
	 */
	public static void dropTable(String table) {
		execute("DROP TABLE " + table + "");
	}

	/**
	 * Equivalent to DROP TABLE [table] IF EXISTS
	 */
	public static void dropTableIfExists(String table) {
		execute("DROP TABLE " + table + " IF EXISTS");
	}

	/**
	 * Equivalent to CREATE TABLE [table]
	 */
	public static void createTable(String table, Map<String, String> columns) {
		String cols = Str.render(columns, "%s %s", ", ");
		execute(U.frmt("CREATE TABLE %s (%s)", table, cols));
	}

	public static void execute(String sql, Object... args) {
		jdbc().execute(sql, args);
	}

	public static void tryToExecute(String sql, Object... args) {
		jdbc().tryToExecute(sql, args);
	}

	public static <T> Results<T> query(Class<T> resultType, String sql, Object... args) {
		return jdbc().query(resultType, sql, args);
	}

	public static Results<Map<String, Object>> query(String sql, Object... args) {
		return jdbc().query(sql, args);
	}

	private static JdbcClient jdbc() {
		return JDBC.defaultApi();
	}

}
