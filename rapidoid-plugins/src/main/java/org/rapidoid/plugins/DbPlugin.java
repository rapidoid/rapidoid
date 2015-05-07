package org.rapidoid.plugins;

/*
 * #%L
 * rapidoid-plugins
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

import java.util.Comparator;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Callback;
import org.rapidoid.lambda.Predicate;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public interface DbPlugin {

	<T> T get(long id);

	<T> T getIfExists(long id);

	<T> List<T> getAll(Class<T> clazz);

	void update(Object entity);

	void update(long id, Object entity);

	void insert(Object entity);

	void delete(long id);

	<T> List<T> find(String query);

	<T> List<T> find(Class<T> clazz, Predicate<T> match, Comparator<T> orderBy);

	void transaction(Runnable tx, boolean readonly, Callback<Void> callback);

	/**
	 * WARNING: Deletes ALL data in the database! Use with care!
	 */
	void deleteAllData();

}
