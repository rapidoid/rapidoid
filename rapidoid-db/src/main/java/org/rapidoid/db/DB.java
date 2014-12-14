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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.U;

@SuppressWarnings("unchecked")
public class DB {

	private static final Class<Db> DB_IMPL_CLASS;

	private static Db defaultDb;

	private static final Map<String, Db> DB_INSTANCES;

	static {
		DB_IMPL_CLASS = (Class<Db>) U.getClassIfExists("org.rapidoid.db.DbImpl");
		U.must(DB_IMPL_CLASS != null, "Cannot find Db implementation (org.rapidoid.db.DbImpl)!");
		U.must(Db.class.isAssignableFrom(DB_IMPL_CLASS), "org.rapidoid.db.DbImpl must implement org.rapidoid.db.Db!");

		DB_INSTANCES = U.autoExpandingMap(new Mapper<String, Db>() {
			@Override
			public Db map(String name) throws Exception {
				String dbPath = U.option("db", "");
				if (!dbPath.isEmpty() && !dbPath.endsWith(File.separator)) {
					dbPath += File.separator;
				}
				String dbFilename = dbPath + name + ".db";
				return (Db) U.newInstance(DB_IMPL_CLASS, name, dbFilename);
			}
		});

		defaultDb = instance("default");
	}

	public static Db db() {
		assert U.must(defaultDb != null, "Database not initialized!");
		return defaultDb;
	}

	public static Db instance(String dbName) {
		return DB_INSTANCES.get(dbName);
	}

	public static Map<String, Db> instances() {
		return Collections.unmodifiableMap(DB_INSTANCES);
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

	public static <E> List<E> getAll(Class<E> clazz, String orderBy) {
		return db().getAll(clazz, orderBy);
	}

	public static <E> List<E> getAll(long... ids) {
		return db().getAll(ids);
	}

	public static <E> List<E> getAll(Collection<Long> ids) {
		return db().getAll(ids);
	}

	public static void update(long id, Object record) {
		db().update(id, record);
	}

	public static void update(Object record) {
		db().update(record);
	}

	public static long persist(Object record) {
		return db().persist(record);
	}

	public static <E> E read(long id, String column) {
		return db().read(id, column);
	}

	public static <E> List<E> find(Predicate<E> match) {
		return db().find(match);
	}

	public static <E> List<E> find(String searchPhrase) {
		return db().find(searchPhrase);
	}

	public static <E> void each(Operation<E> lambda) {
		db().each(lambda);
	}

	public static void transaction(Runnable transaction, boolean readOnly) {
		db().transaction(transaction, readOnly);
	}

	public static void transaction(Runnable transaction, boolean readOnly, Callback<Void> callback) {
		db().transaction(transaction, readOnly, callback);
	}

	public static void shutdown() {
		db().shutdown();
	}

	public static long size() {
		return db().size();
	}

	public static boolean isActive() {
		return db().isActive();
	}

	public static void halt() {
		db().halt();
	}

	public static void destroy() {
		db().destroy();
	}

	public static void destroy(String name) {
		instance(name).destroy();
		remove(name);
	}

	public synchronized static void destroyAll() {
		for (Db db : DB_INSTANCES.values()) {
			db.destroy();
		}

		DB_INSTANCES.clear();
		defaultDb = instance("default");
	}

	public static void remove(String name) {
		DB_INSTANCES.remove(name);
		if (name.equals("default")) {
			defaultDb = instance("default");
		}
	}

	public static <E> DbList<E> list() {
		return db().list();
	}

	public static <E> DbSet<E> set() {
		return db().set();
	}

	public static long getIdOf(Object record) {
		return db().getIdOf(record);
	}

}
