package org.rapidoid.plugins.db;

/*
 * #%L
 * rapidoid-plugins
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DB {

	public static String insert(Object record) {
		return Plugins.db().insert(record);
	}

	public static void update(String id, Object record) {
		Plugins.db().update(id, record);
	}

	public static void update(Object record) {
		Plugins.db().update(record);
	}

	public static String persist(Object record) {
		return Plugins.db().persist(record);
	}

	public static String insertOrGetId(Object record) {
		return Plugins.db().insertOrGetId(record);
	}

	public static <E> void delete(Class<E> clazz, String id) {
		Plugins.db().delete(clazz, id);
	}

	public static void delete(Object record) {
		Plugins.db().delete(record);
	}

	public static <T> T getIfExists(Class<T> clazz, String id) {
		return Plugins.db().getIfExists(clazz, id);
	}

	public static <T> T get(Class<T> clazz, String id) {
		return Plugins.db().get(clazz, id);
	}

	public static <E> List<E> getAll() {
		return Plugins.db().getAll();
	}

	public static <E> List<E> getAll(Class<E> clazz) {
		return Plugins.db().getAll(clazz);
	}

	public static <E> List<E> getAll(Class<E> clazz, int pageNumber, int pageSize) {
		return Plugins.db().getAll(clazz, pageNumber, pageSize);
	}

	public static <E> List<E> getAll(Class<E> clazz, Iterable<String> ids) {
		return Plugins.db().getAll(clazz, ids);
	}

	public static <E> void refresh(E record) {
		Plugins.db().refresh(record);
	}

	public static <E> void each(final Operation<E> lambda) {
		Plugins.db().each(lambda);
	}

	public static long size() {
		return Plugins.db().size();
	}

	public static <E> List<E> find(Predicate<E> match) {
		return Plugins.db().find(match);
	}

	public static <E> List<E> find(Class<E> clazz, Predicate<E> match, Comparator<E> orderBy) {
		return Plugins.db().find(clazz, match, orderBy);
	}

	public static <E> List<E> fullTextSearch(String searchPhrase) {
		return Plugins.db().fullTextSearch(searchPhrase);
	}

	public static void transaction(Runnable transaction, boolean readOnly) {
		Plugins.db().transaction(transaction, readOnly);
	}

	public static void transaction(Runnable transaction, boolean readOnly, Callback<Void> callback) {
		Plugins.db().transaction(transaction, readOnly, callback);
	}

	public static <E> E entity(Class<E> entityType, Map<String, ?> properties) {
		return Plugins.db().entity(entityType, properties);
	}

	@SuppressWarnings("unchecked")
	public static <E> E entity(Class<E> entityType) {
		return (E) entity(entityType, Collections.EMPTY_MAP);
	}

	public static <E> E entity(Class<E> entityType, String prop, Object value) {
		return entity(entityType, U.map(prop, value));
	}

	public static <E> E entity(Class<E> entityType, String prop1, Object value1, String prop2, Object value2) {
		return entity(entityType, U.map(prop1, value1, prop2, value2));
	}

	public static <E> E entity(Class<E> entityType, String prop1, Object value1, String prop2, Object value2,
			String prop3, Object value3) {
		return entity(entityType, U.map(prop1, value1, prop2, value2, prop3, value3));
	}

	public static <E> E entity(Class<E> entityType, String prop1, Object value1, String prop2, Object value2,
			String prop3, Object value3, String prop4, Object value4) {
		return entity(entityType, U.map(prop1, value1, prop2, value2, prop3, value3, prop4, value4));
	}

	public static <E> E entity(Class<E> entityType, String prop1, Object value1, String prop2, Object value2,
			String prop3, Object value3, String prop4, Object value4, String prop5, Object value5) {
		return entity(entityType, U.map(prop1, value1, prop2, value2, prop3, value3, prop4, value4, prop5, value5));
	}

	public static <E> List<E> query(Class<E> clazz, String query, Object... args) {
		return Plugins.db().query(clazz, query, args);
	}

	public static <RESULT> RESULT sql(String sql, Object... args) {
		return Plugins.db().sql(sql, args);
	}

	public static void deleteAllData() {
		Plugins.db().deleteAllData();
	}

	public static <T> List<T> read(Class<T> clazz, String id, String... properties) {
		return null;
	}

	public static void addToSet(String recordId, String setName, String item) {}

	public static void removeFromSet(String recordId, String setName, String item) {}

	public static Set<String> getSetItems(String recordId, String setName) {
		return null;
	}

	public static int getSetSize(String recordId, String setName) {
		return 0;
	}

	public static void addToList(String recordId, String listName, String item) {}

	public static void removeFromList(String recordId, String listName, String item) {}

	public static List<String> getListItems(String recordId, String listName) {
		return null;
	}

	public static int getListSize(String recordId, String listName) {
		return 0;
	}

	public static void putToMap(String recordId, String mapName, String key, String value) {}

	public static String getFromMap(String recordId, String mapName, String key) {
		return null;
	}

	public static void removeFromMap(String recordId, String mapName, String key) {}

	public static Map<String, String> getMapItems(String recordId, String mapName) {
		return null;
	}

	public static int getMapSize(String recordId, String mapName) {
		return 0;
	}

}
