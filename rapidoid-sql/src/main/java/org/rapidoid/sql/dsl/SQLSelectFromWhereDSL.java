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
import org.rapidoid.datamodel.Results;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("6.0.0")
public class SQLSelectFromWhereDSL extends AbstractSQLDSL {

	private final JdbcClient client;
	private final String table;
	private final String[] columns;
	private final String criteria;
	private final Object[] args;

	public SQLSelectFromWhereDSL(JdbcClient client, String table, String[] columns, String criteria, Object[] args) {
		this.client = client;
		this.table = table;
		this.columns = columns;
		this.criteria = criteria;
		this.args = args;
	}

	public Map<String, Object> getOnly() {
		return U.single(asList());
	}

	public List<Map<String, Object>> asList() {
		return client.query(generateSql(), args).all();
	}

	public SQLSelectFromWhereDSL and(String criteria, Object... args) {
		// FIXME
		return this;
	}

	public <T> Results<T> as(Class<T> resultType) {
		return client.query(resultType, generateSql(), args);
	}

	public String generateSql() {
		String cols = U.join(", ", columns);
		return U.frmt("SELECT %s FROM %s WHERE %s", cols, table, criteria);
	}

}
