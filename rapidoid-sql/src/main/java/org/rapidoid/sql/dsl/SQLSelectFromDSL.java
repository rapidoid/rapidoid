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
import org.rapidoid.jdbc.JdbcClient;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class SQLSelectFromDSL extends AbstractSQLDSL {

	private final JdbcClient client;
	private final String table;
	private final String[] columns;

	public SQLSelectFromDSL(JdbcClient client, String table, String[] columns) {
		this.client = client;
		this.table = table;
		this.columns = columns;
	}

	public SQLSelectFromWhereDSL where(String criteria, Object... args) {
		return new SQLSelectFromWhereDSL(client, table, columns, criteria, args);
	}

}
