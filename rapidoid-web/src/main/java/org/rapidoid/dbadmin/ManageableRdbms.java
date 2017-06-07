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
import org.rapidoid.group.AutoManageable;
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
@ManageableBean(kind = "rdbms")
public class ManageableRdbms extends AutoManageable<JdbcClient> {

	private final JdbcClient jdbc;

	public ManageableRdbms(JdbcClient jdbc) {
		super(jdbc.id());
		this.jdbc = jdbc;
	}

	public String name;

	public String version;

	public final List<ManageableDatabase> databases = Coll.synchronizedList();

	@Override
	public Map<String, List<Manageable>> getManageableChildren() {
		return U.<String, List<Manageable>>map("databases", databases);
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

		this.name = meta.getDatabaseProductName();
		this.version = meta.getDatabaseProductVersion();

		ResultSet schemas = meta.getCatalogs();

		this.databases.clear();
		while (schemas.next()) {
			String name = schemas.getString("TABLE_CAT");
			ManageableDatabase schema = new ManageableDatabase(jdbc, name);
			this.databases.add(schema);
		}
	}

}
