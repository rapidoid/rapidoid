package org.rapidoid.dbadmin;

/*
 * #%L
 * rapidoid-web
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.group.AbstractManageable;
import org.rapidoid.group.Manageable;
import org.rapidoid.group.ManageableBean;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.lambda.Operation;
import org.rapidoid.u.U;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.5")
@ManageableBean(kind = "database")
public class ManageableDatabase extends AbstractManageable {

	private final JdbcClient jdbc;
	private final String name;

	public final List<ManageableTable> tables = Coll.synchronizedList();

	public ManageableDatabase(JdbcClient jdbc, String name) {
		this.jdbc = jdbc;
		this.name = name;
	}

	@Override
	public String id() {
		return name;
	}

	@Override
	public Map<String, List<Manageable>> getManageableChildren() {
		return U.<String, List<Manageable>>map("tables", this.tables);
	}

	@Override
	protected void doReloadManageable(Callback<Void> callback) {
		jdbc.execute(callback, new Operation<Connection>() {
			@Override
			public void execute(Connection conn) throws SQLException {
				reload(conn);
			}
		});
	}

	private synchronized void reload(Connection conn) throws SQLException {
		DatabaseMetaData meta = conn.getMetaData();

		ResultSet tablesRS = meta.getTables(name, null, null, null);

		this.tables.clear();
		while (tablesRS.next()) {

			String tableName = tablesRS.getString("TABLE_NAME");
			ManageableTable table = new ManageableTable(jdbc, name, tableName);

			table.remarks = tablesRS.getString("REMARKS");
			table.tableType = tablesRS.getString("TABLE_TYPE");

			this.tables.add(table);
		}
	}

}
