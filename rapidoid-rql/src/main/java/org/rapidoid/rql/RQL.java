package org.rapidoid.rql;

/*
 * #%L
 * rapidoid-rql
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.plugins.DB;
import org.rapidoid.plugins.Entities;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.1.0")
public class RQL {

	public static ParsedRQL parse(String rql, Object... args) {
		int p = rql.indexOf(' ');
		U.must(p > 0, "Invalid RQL syntax!");

		String cmd = rql.substring(0, p).trim();
		String data = rql.substring(p + 1).trim();

		return new ParsedRQL(cmd, data, args);
	}

	@SuppressWarnings("unchecked")
	public static <T> T entity(String rql, Object... args) {
		String[] parts = rql.split(" ");

		String entityName = U.capitalized(parts[0]);
		Class<?> entityType = Entities.getEntityType(entityName);
		U.must(entityType != null, "Cannot find entity '%s'!", entityName);

		Map<String, Object> properties = U.map();
		if (parts.length > 1) {

			String[] props = rql.substring(entityName.length() + 1).split("\\s*\\,\\s*");

			int argIndex = 0;
			for (String prop : props) {
				String[] kv = prop.trim().split("\\s*=\\s*");
				String key = kv[0];
				Object value;

				if (kv.length > 1) {
					value = kv[1].equals("?") ? args[argIndex++] : kv[1];
				} else {
					value = true;
				}

				properties.put(key, value);
			}
		}

		return (T) Entities.create(entityType, properties);
	}

	@SuppressWarnings("unchecked")
	public static <T> T run(String rql, Object... args) {
		ParsedRQL act = parse(rql, args);

		if ("INSERT".equalsIgnoreCase(act.command)) {
			Object ent = entity(act.target, act.args);
			return (T) new Long(DB.insert(ent));
		} else {
			throw U.rte("Unknown RQL command: '%s'!", act.command);
		}
	}

}
