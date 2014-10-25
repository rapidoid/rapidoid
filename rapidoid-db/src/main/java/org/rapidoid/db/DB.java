package org.rapidoid.db;

/*
 * #%L
 * rapidoid-db
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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
import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.rapidoid.lambda.F1;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.lambda.V1;
import org.rapidoid.util.U;

@SuppressWarnings("unchecked")
public class DB {

	private static final Class<Db> DB_IMPL_CLASS;

	private static final Db DEFAULT_DB_INSTANCE;

	private static final Map<String, Db> DB_INSTANCES;

	static {
		DB_IMPL_CLASS = (Class<Db>) U.getClassIfExists("org.rapidoid.db.DbImpl");
		U.must(DB_IMPL_CLASS != null, "Cannot find Db implementation (org.rapidoid.db.DbImpl)!");
		U.must(Db.class.isAssignableFrom(DB_IMPL_CLASS), "org.rapidoid.db.DbImpl must implement org.rapidoid.db.Db!");

		DB_INSTANCES = U.<String, Db> autoExpandingMap(new F1<Db, String>() {
			@Override
			public Db execute(String name) throws Exception {
				String dbPath = U.option("db", "");
				if (!dbPath.isEmpty() && !dbPath.endsWith(File.separator)) {
					dbPath += File.separator;
				}
				String dbFilename = dbPath + name + ".db";
				return (Db) U.newInstance(DB_IMPL_CLASS, name, dbFilename);
			}
		});

		DEFAULT_DB_INSTANCE = instance("default");
	}

	public static Db db() {
		assert U.must(DEFAULT_DB_INSTANCE != null, "Database not initialized!");
		return DEFAULT_DB_INSTANCE;
	}

	public static Db instance(String dbName) {
		return DB_INSTANCES.get(dbName);
	}

	public static long insert(Object record) {
		return db().insert(record);
	}

	public static void delete(long id) {
		db().delete(id);
	}

	public static <T> T get(long id) {
		return db().get(id);
	}

	public static <T> T get(long id, Class<T> clazz) {
		return db().get(id, clazz);
	}

	public static <E> List<E> getAll(Class<E> clazz) {
		return db().getAll(clazz);
	}

	public static void update(long id, Object record) {
		db().update(id, record);
	}

	public static void update(Object record) {
		db().update(record);
	}

	public static <E> E read(long id, String column) {
		return db().read(id, column);
	}

	public static <E> List<E> find(Predicate<E> match) {
		return db().find(match);
	}

	public static <E> void each(V1<E> lambda) {
		db().each(lambda);
	}

	public static void transaction(Runnable transaction) {
		db().transaction(transaction);
	}

	public static void save(OutputStream output) {
		db().save(output);
	}

}
