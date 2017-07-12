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
import org.rapidoid.datamodel.Results;
import org.rapidoid.group.AbstractManageable;
import org.rapidoid.group.ManageableBean;
import org.rapidoid.gui.GUI;
import org.rapidoid.gui.Grid;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.5")
@ManageableBean(kind = "table")
public class ManageableTable extends AbstractManageable {

	private final JdbcClient jdbc;

	private final String db;
	private final String name;

	public volatile String tableType;

	public volatile String remarks;

	public final List<Object> records = Coll.synchronizedList();

	public ManageableTable(JdbcClient jdbc, String db, String name) {
		this.jdbc = jdbc;
		this.db = db;
		this.name = name;
	}

	@Override
	public String id() {
		return name;
	}

	@Override
	public Object getManageableDisplay() {

		Results<Map<String, Object>> results = jdbc.query(U.frmt("select * from %s.%s", db, name));

		Grid grid = GUI.grid(results).pageSize(100);

		return grid;
	}
}
