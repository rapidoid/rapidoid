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
import org.rapidoid.annotation.P;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.plugins.Plugin;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public interface DBPlugin extends Plugin {

	String insert(@P("entity") Object entity);

	void update(@P("entity") Object entity);

	void update(@P("id") String id, @P("entity") Object entity);

	String persist(@P("record") Object record);

	String insertOrGetId(@P("record") Object record);

	<T> T get(@P("clazz") Class<T> clazz, @P("id") String id);

	<T> T getIfExists(@P("clazz") Class<T> clazz, @P("id") String id);

	<E> Iterable<E> getAll();

	<T> Iterable<T> getAll(@P("clazz") Class<T> clazz);

	<E> Iterable<E> getAll(@P("clazz") Class<E> clazz, @P("pageNumber") int pageNumber, @P("pageSize") int pageSize);

	<E> Iterable<E> getAll(@P("clazz") Class<E> clazz, @P("ids") Iterable<String> ids);

	void refresh(@P("entity") Object entity);

	<E> void delete(@P("clazz") Class<E> clazz, @P("id") String id);

	void delete(@P("entity") Object entity);

	<E> void each(@P("lambda") final Operation<E> lambda);

	long size();

	<T> Iterable<T> fullTextSearch(@P("query") String query);

	<T> Iterable<T> find(@P("clazz") Class<T> clazz, @P("match") Predicate<T> match, @P("orderBy") Comparator<T> orderBy);

	<E> Iterable<E> find(@P("match") Predicate<E> match);

	<E> E entity(@P("entityType") Class<E> entityType, @P("properties") Map<String, ?> properties);

	Iterable<Map<String, Object>> query(@P("query") String query, @P("args") Object... args);

	<E> Iterable<E> query(@P("clazz") Class<E> clazz, @P("query") String query, @P("args") Object... args);

	void queryAsync(@P("query") String query, Callback<Iterable<Map<String, Object>>> callback,
			@P("args") Object... args);

	<E> void queryAsync(@P("clazz") Class<E> clazz, @P("query") String query, Callback<Iterable<E>> callback,
			@P("args") Object... args);

	<RESULT> RESULT sql(@P("sql") String sql, @P("args") Object... args);

	void transaction(@P("transaction") Runnable transaction, @P("readOnly") boolean readOnly);

	void transaction(@P("tx") Runnable tx, @P("readonly") boolean readonly, @P("callback") Callback<Void> callback);

	/**
	 * WARNING: Deletes ALL data in the database! Use with care!
	 */
	void deleteAllData();

}
