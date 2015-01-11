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
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.util.Conf;
import org.rapidoid.util.U;

public class DB {

	static final Class<Db> DB_IMPL_CLASS;

	private static Db db;

	static {
		DB_IMPL_CLASS = U.getClassIfExists("org.rapidoid.db.DbImpl");
		U.must(DB_IMPL_CLASS != null, "Cannot find Db implementation (org.rapidoid.db.DbImpl)!");
		U.must(Db.class.isAssignableFrom(DB_IMPL_CLASS), "org.rapidoid.db.DbImpl must implement org.rapidoid.db.Db!");
		init();
	}

	public static String path() {
		String path = Conf.option("db", "");

		if (!path.isEmpty() && !path.endsWith(File.separator)) {
			path += File.separator;
		}

		return path;
	}

	public static void init() {
		db = (Db) U.customizable(DB.DB_IMPL_CLASS, "default", path() + "default.db");
		db.initAndLoad();
	}

	public static Db db() {
		assert U.must(db != null, "Database not initialized!");
		return db;
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

	public static <T> T getIfExists(long id) {
		return db().getIfExists(id);
	}

	public static <T> T get(long id, Class<T> clazz) {
		return db().get(id, clazz);
	}

	public static <E> List<E> getAll(Class<E> clazz) {
		return db().getAll(clazz);
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

	public static long persistedIdOf(Object record) {
		return db().persistedIdOf(record);
	}

	public static <E> E read(long id, String column) {
		return db().read(id, column);
	}

	public static <E> List<E> find(Predicate<E> match) {
		return db().find(match);
	}

	public static <E> List<E> find(Class<E> clazz, Predicate<E> match, Comparator<E> orderBy) {
		return db().find(clazz, match, orderBy);
	}

	public static <E> List<E> find(String searchPhrase) {
		return db().find(searchPhrase);
	}

	public static <E> List<E> find(Class<E> clazz, String query, Object... args) {
		return db().find(clazz, query, args);
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

	public static long getIdOf(Object record) {
		return db().getIdOf(record);
	}

	public static <E> DbList<E> list(Object holder, String relation) {
		return db().list(holder, relation);
	}

	public static <E> DbSet<E> set(Object holder, String relation) {
		return db().set(holder, relation);
	}

	public static <E> DbRef<E> ref(Object holder, String relation) {
		return db().ref(holder, relation);
	}

	public static DbSchema schema() {
		return db().schema();
	}

	public static <E> E dsl(Class<E> entityType) {
		return db().schema().dsl(entityType);
	}

}
