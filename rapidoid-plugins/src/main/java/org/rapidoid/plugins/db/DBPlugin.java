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

import java.util.Comparator;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.plugins.Plugin;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public interface DBPlugin extends Plugin {

	String insert(Object entity);

	void update(Object entity);

	void update(String id, Object entity);

	String persist(Object record);

	String insertOrGetId(Object record);

	<T> T get(Class<T> clazz, String id);

	<T> T getIfExists(Class<T> clazz, String id);

	<E> Iterable<E> getAll();

	<T> Iterable<T> getAll(Class<T> clazz);

	<E> Iterable<E> getAll(Class<E> clazz, int pageNumber, int pageSize);

	<E> Iterable<E> getAll(Class<E> clazz, Iterable<String> ids);

	void refresh(Object entity);

	<E> void delete(Class<E> clazz, String id);

	void delete(Object entity);

	<E> void each(final Operation<E> lambda);

	long size();

	<T> Iterable<T> fullTextSearch(String query);

	<T> Iterable<T> find(Class<T> clazz, Predicate<T> match, Comparator<T> orderBy);

	<E> Iterable<E> find(Predicate<E> match);

	<E> E entity(Class<E> entityType, Map<String, ?> properties);

	<E> Iterable<E> query(Class<E> clazz, String query, Object... args);

	<RESULT> RESULT sql(String sql, Object... args);

	void transaction(Runnable transaction, boolean readOnly);

	void transaction(Runnable tx, boolean readonly, Callback<Void> callback);

	/**
	 * WARNING: Deletes ALL data in the database! Use with care!
	 */
	void deleteAllData();

}
