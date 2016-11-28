package org.rapidoid.sql;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.sql.dsl.*;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

/*
 * #%L
 * rapidoid-sql
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
@Since("5.3.0")
public class SQL extends RapidoidThing {

	private final JdbcClient client;

	public SQL(JdbcClient client) {
		this.client = client;
	}

	public JdbcClient client() {
		return client;
	}

	/**
	 * Equivalent to SELECT [columns] ...
	 */
	public SQLSelectDSL select(String... columns) {
		return new SQLSelectDSL(client, columns);
	}

	/**
	 * Equivalent to SELECT * FROM [table] ...
	 */
	public SQLSelectFromDSL from(String table) {
		return select("*").from(table);
	}

	/**
	 * Equivalent to SELECT * FROM [table] ...
	 */
	public Map<String, Object> get(String table, Object id) {
		return select("*").from(table).where("id = ?", id).getOnly();
	}

	/**
	 * Equivalent to SELECT * FROM [table] ...
	 */
	public <T> T get(Class<T> resultType, Object id) {
		List<T> list = select("*").from(tableOf(resultType)).where("id = ?", id).as(resultType);
		return U.single(list);
	}

	public String tableOf(Class<?> resultType) {
		return resultType.getSimpleName().toLowerCase();
	}

	/**
	 * Equivalent to INSERT INTO [table] ...
	 */
	public SQLInsertDSL insert(String table) {
		return new SQLInsertDSL(client, table);
	}

	/**
	 * Equivalent to INSERT INTO [table] ...
	 */
	public SQLInsertDSL insert(Class<?> entityType) {
		return insert(tableOf(entityType));
	}

	/**
	 * Equivalent to UPDATE [table] ...
	 */
	public SQLUpdateDSL update(String table) {
		return new SQLUpdateDSL(client, table);
	}

	/**
	 * Equivalent to DELETE FROM [table] ...
	 */
	public SQLDeleteDSL delete(String table) {
		return new SQLDeleteDSL(client, table);
	}

	/**
	 * Equivalent to DROP TABLE [table]
	 */
	public void dropTable(String table) {
		execute("DROP TABLE " + table + "");
	}

	/**
	 * Equivalent to DROP TABLE [table] IF EXISTS
	 */
	public void dropTableIfExists(String table) {
		execute("DROP TABLE " + table + " IF EXISTS");
	}

	/**
	 * Equivalent to CREATE TABLE [table]
	 */
	public void createTable(String table, Map<String, String> columns) {
		String cols = Str.render(columns, "%s %s", ", ");
		execute(U.frmt("CREATE TABLE %s (%s)", table, cols));
	}

	public void execute(String sql, Object... args) {
		client.execute(sql, args);
	}

	public void tryToExecute(String sql, Object... args) {
		client.tryToExecute(sql, args);
	}

	public <T> List<T> query(Class<T> resultType, String sql, Object... args) {
		return client.query(resultType, sql, args);
	}

	public List<Map<String, Object>> query(String sql, Object... args) {
		return client.query(sql, args);
	}
}
