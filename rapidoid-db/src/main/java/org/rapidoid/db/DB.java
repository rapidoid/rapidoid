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
import java.util.List;
import java.util.Map;

import org.rapidoid.lambda.Predicate;
import org.rapidoid.lambda.V1;
import org.rapidoid.util.U;

public class DB {

	private static final Db db = initDb();

	public static long insert(Object record) {
		long id = db().insert(record);
		setIdIfPossible(record, id);
		return id;
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

	public static void update(long id, Object record) {
		db().update(id, record);
	}

	public static void update(Object record) {
		db().update(U.getId(record), record);
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

	private static Db db() {
		U.must(db != null, "Database not initialized!");
		return db;
	}

	private static Db initDb() {
		Class<?> clazz = U.getClassIfExists("org.rapidoid.db.DbImpl");
		U.must(clazz != null, "Cannot find Db implementation (org.rapidoid.db.DbImpl)!");
		U.must(Db.class.isAssignableFrom(clazz), "org.rapidoid.db.DbImpl must implement org.rapidoid.db.Db!");
		return (Db) U.newInstance(clazz);
	}

	@SuppressWarnings("unchecked")
	private static void setIdIfPossible(Object record, long id) {
		if (record instanceof Map) {
			((Map<Object, Object>) record).put("id", id);
		}
		try {
			U.setId(record, id);
		} catch (Exception e) {
			// ignore
		}
	}

}
