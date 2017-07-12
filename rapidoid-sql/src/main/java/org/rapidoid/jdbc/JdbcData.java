package org.rapidoid.jdbc;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.datamodel.PageableData;
import org.rapidoid.lambda.Mapper;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class JdbcData<T> extends RapidoidThing implements PageableData<T> {

	private final JdbcClient jdbc;
	private final Class<T> resultType;
	private final Mapper<ResultSet, T> resultMapper;
	private final String sql;
	private final Map<String, ?> namedArgs;
	private final Object[] args;

	public JdbcData(JdbcClient jdbc, Class<T> resultType, Mapper<ResultSet, T> resultMapper, String sql, Map<String, ?> namedArgs, Object[] args) {
		this.jdbc = jdbc;
		this.resultType = resultType;
		this.resultMapper = resultMapper;
		this.sql = sql;
		this.namedArgs = namedArgs;
		this.args = args;
	}

	@Override
	public List<T> getPage(long skip, long limit) {
		return jdbc.runQuery(resultType, resultMapper, sql, namedArgs, args, skip, limit);
	}

	@Override
	public long getCount() {
		return jdbc.getQueryCount(sql, namedArgs, args);
	}
}
