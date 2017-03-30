package org.rapidoid.jdbc;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.commons.StringRewriter;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.TUUID;
import org.rapidoid.util.WebData;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
@Since("5.3.0")
public class JdbcUtil extends RapidoidThing {

	private static final StringRewriter NAMED_PARAMS_REWRITER = new StringRewriter(StringRewriter.ALL_QUOTES, "\\$(\\w+)\\b");

	public static PreparedStatement prepare(Connection conn, String sql, final Map<String, ?> namedArgs, Object[] args) {
		try {

			if (namedArgs != null) {
				U.must(args == null);

				List<Object> arguments = U.list();
				sql = substituteNamedParams(sql, namedArgs, arguments);
				args = arguments.toArray();
			}

			PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			bind(stmt, args);

			return stmt;

		} catch (SQLException e) {
			throw new RuntimeException("Cannot create prepared statement!", e);
		}
	}

	private static String substituteNamedParams(String sql, final Map<String, ?> namedArgs, final List<Object> arguments) {
		return NAMED_PARAMS_REWRITER.rewrite(sql, new Mapper<String[], String>() {
			@Override
			public String map(String[] groups) throws Exception {
				String name = groups[1];

				if (namedArgs.containsKey(name)) {
					Object value = namedArgs.get(name);
					arguments.add(value);
					return "?";

				} else {
					return groups[0]; // not in the args -> leave it untouched
				}
			}
		});
	}

	public static void bind(PreparedStatement stmt, Object[] args) throws SQLException {
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];

			if (arg instanceof WebData) {
				// unwrap the arg to a real value represented by the web data
				arg = ((WebData) arg).unwrap();
			}

			if (arg instanceof byte[]) {
				byte[] bytes = (byte[]) arg;
				stmt.setBytes(i + 1, bytes);

			} else if (arg instanceof UUID) {
				UUID uuid = (UUID) arg;
				byte[] bytes = Msc.uuidToBytes(uuid);
				stmt.setBytes(i + 1, bytes);

			} else if (arg instanceof TUUID) {
				TUUID tuuid = (TUUID) arg;
				stmt.setBytes(i + 1, tuuid.toBytes());

			} else {
				stmt.setObject(i + 1, arg);
			}
		}
	}

	public static <T> List<T> rows(Mapper<ResultSet, T> resultMapper, ResultSet rs) throws Exception {
		List<T> rows = U.list();

		while (rs.next()) {
			rows.add(resultMapper.map(rs));
		}

		return rows;
	}

	public static <T> List<T> rows(Class<T> resultType, ResultSet rs) throws Exception {
		List<T> rows = U.list();

		ResultSetMetaData meta = rs.getMetaData();
		int columnsN = meta.getColumnCount();
		Prop[] props = new Prop[columnsN];

		for (int i = 0; i < props.length; i++) {
			String name = meta.getColumnLabel(i + 1);
			props[i] = Beany.property(resultType, name, false);
		}

		while (rs.next()) {
			T row = resultType.newInstance();

			for (int i = 0; i < columnsN; i++) {
				if (props[i] != null) {
					Object value = rs.getObject(i + 1); // 1-indexed
					props[i].set(row, value);
				}
			}

			rows.add(row);
		}

		return rows;
	}

	private static Object convertResultValue(String type, Object value) {
		byte[] bytes;

		switch (type) {

			case "TUUID":
				U.must(value instanceof byte[], "Expecting byte[] value to convert to TUUID!");
				bytes = (byte[]) value;
				return TUUID.fromBytes(bytes);

			case "UUID":
				U.must(value instanceof byte[], "Expecting byte[] value to convert to UUID!");
				bytes = (byte[]) value;
				return Msc.bytesToUUID(bytes);

			default:
				throw U.rte("Unknown type: '%s'", type);
		}
	}

	public static List<Map<String, Object>> rows(ResultSet rs) throws SQLException {
		List<Map<String, Object>> rows = U.list();

		while (rs.next()) {
			rows.add(row(rs));
		}

		return rows;
	}

	public static Map<String, Object> row(ResultSet rs) throws SQLException {
		Map<String, Object> row = U.map();

		ResultSetMetaData meta = rs.getMetaData();
		int columnsNumber = meta.getColumnCount();

		for (int i = 1; i <= columnsNumber; i++) {

			String name = meta.getColumnLabel(i);
			Object value = rs.getObject(i);

			String[] nameParts = name.split("__");

			if (nameParts.length == 2) {
				name = nameParts[0];
				value = convertResultValue(nameParts[1], value);
			}

			row.put(name, value);
		}

		return row;
	}

}
