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
public class SQLSelectDSL extends AbstractSQLDSL {

	private final JdbcClient client;
	private final String[] columns;

	public SQLSelectDSL(JdbcClient client, String[] columns) {
		this.client = client;
		this.columns = columns;
	}

	public SQLSelectFromDSL from(String table) {
		return new SQLSelectFromDSL(client, table, columns);
	}

}
