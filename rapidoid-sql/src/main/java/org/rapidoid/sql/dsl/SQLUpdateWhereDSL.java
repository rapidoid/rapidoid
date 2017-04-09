package org.rapidoid.sql.dsl;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Str;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.u.U;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class SQLUpdateWhereDSL extends AbstractSQLDSL {

	private final JdbcClient client;
	private final String table;
	private final Map<String, Object> updates;
	private final Map<String, Object> conditions = Coll.concurrentMap();

	public SQLUpdateWhereDSL(JdbcClient client, String table, Map<String, Object> updates) {
		this.client = client;
		this.table = table;
		this.updates = updates;
	}

	public SQLUpdateWhereDSL and(String column, Object... values) {
		//conditions.put(column, value);
		return this;
	}

	public int execute() {
		String set = Str.render(updates.keySet(), "%s = ?", ", ");
		String where = Str.render(conditions.keySet(), "%s = ?", ", ");

		String sql = U.frmt("UPDATE %s SET %s WHERE %s", table, set, where);

		// FIXME
		// client.execute(sql, values);

		return 0;
	}

}
