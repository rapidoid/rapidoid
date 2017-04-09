package org.rapidoid.orm;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.beany.Prop;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;
import org.rapidoid.sql.SQL;
import org.rapidoid.u.U;

import java.util.Map;

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
@Since("6.0.0")
public class ORM extends RapidoidThing {

	private static JdbcClient jdbc() {
		return JDBC.defaultApi();
	}

	public static void bootstrap(Class<?>... entityClasses) {
		for (Class<?> entityClass : entityClasses) {
			defineTable(entityClass);
		}
	}

	private static void defineTable(Class<?> entityClass) {

		String table = SQL.tableOf(entityClass);
		Map<String, String> columns = U.map();

		for (Prop prop : Beany.propertiesOf(entityClass)) {

			String columnType = columnTypeOf(prop.getRawType());
			if (columnType != null) {
				columns.put(prop.getName(), columnType);
			}
		}

		SQL.createTable(table, columns);
	}

	private static String columnTypeOf(Class<?> rawType) {
		TypeKind kind = Cls.kindOf(rawType);

		if (rawType.equals(Ref.class)) {
			return "int";
		}

		if (rawType.equals(Refs.class)) {
			return null;
		}

		switch (kind) {
			case STRING:
				return "varchar(256)";

			case INT:
			case INT_OBJ:
				return "int";

			case LONG:
			case LONG_OBJ:
				return "int";

			default:
				throw U.rte("Unsupported column type: " + rawType);
		}
	}

}
