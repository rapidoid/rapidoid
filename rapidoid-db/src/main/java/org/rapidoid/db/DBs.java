package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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
import java.util.Collections;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.Cls;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
public class DBs {

	private static final Map<String, Database> DB_INSTANCES;

	static {
		DB_INSTANCES = UTILS.autoExpandingMap(new Mapper<String, Database>() {
			@Override
			public Database map(String name) throws Exception {
				String dbFilename = DB.path() + name + ".db";
				return (Database) Cls.newInstance(DB.DB_IMPL_CLASS, name, dbFilename);
			}
		});
	}

	public static Database instance(String dbName) {
		return DB_INSTANCES.get(dbName);
	}

	public static Map<String, Database> instances() {
		return Collections.unmodifiableMap(DB_INSTANCES);
	}

	public static void destroy(String name) {
		instance(name).destroy();
		remove(name);
	}

	public synchronized static void destroyAll() {
		for (Database db : DB_INSTANCES.values()) {
			db.destroy();
		}

		DB_INSTANCES.clear();
	}

	public static void remove(String name) {
		DB_INSTANCES.remove(name);
	}

}
