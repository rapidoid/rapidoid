package org.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import java.util.Collections;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DBs {

	private static final Map<String, Database> DB_INSTANCES;

	static {
		DB_INSTANCES = UTILS.autoExpandingMap(new Mapper<String, Database>() {
			@Override
			public Database map(String name) throws Exception {
				String dbFilename = XDB.path() + name + ".db";
				return (Database) Cls.newInstance(XDB.DB_IMPL_CLASS, name, dbFilename);
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
