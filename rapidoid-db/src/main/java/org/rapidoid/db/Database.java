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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.rapidoid.activity.Activity;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Operation;
import org.rapidoid.lambda.Predicate;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public interface Database extends Activity<Database> {

	void loadAndStart();

	long insert(Object record);

	<E> E get(long id);

	<E> E getIfExists(long id);

	<E> E get(long id, Class<E> clazz);

	<E> List<E> getAll(Class<E> clazz);

	<E> List<E> getAll(long... ids);

	<E> List<E> getAll(Collection<Long> ids);

	<E> void refresh(E record);

	void update(Object record);

	void update(long id, Object record);

	long persist(Object record);

	long persistedIdOf(Object record);

	void delete(long id);

	void delete(Object record);

	<T> T readColumn(long id, String column);

	<E> List<E> find(Predicate<E> match);

	<E> List<E> find(Iterable<Long> ids);

	<E> List<E> find(Class<E> clazz, Predicate<E> match, Comparator<E> orderBy);

	<E> List<E> find(String searchPhrase);

	<E> List<E> find(Class<E> clazz, String query, Object... args);

	<E> void each(Operation<E> lambda);

	void transaction(Runnable transaction, boolean readOnly);

	void transaction(Runnable transaction, boolean readOnly, Callback<Void> callback);

	void saveTo(OutputStream output);

	void load(InputStream in);

	long size();

	void clear();

	void destroy();

	long getIdOf(Object record);

	long getVersionOf(long id);

	<E> DbColumn<E> column(Map<String, Object> map, String name, Class<E> type);

	<E> DbList<E> list(Object holder, String relation);

	<E> DbSet<E> set(Object holder, String relation);

	<E> DbRef<E> ref(Object holder, String relation);

	DbSchema schema();

	Database as(String username);

	Database sudo();

	void init(String data, Object... args);

}
