package org.rapidoid.entity;

/*
 * #%L
 * rapidoid-entity
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Nikolche Mihajlovski
 * @since 2.2.0
 */
public interface IEntity {

	long id();

	void id(long id);

	long version();

	void version(long version);

	String createdBy();

	void createdBy(String createdBy);

	Date createdOn();

	void createdOn(Date createdOn);

	String lastUpdatedBy();

	void lastUpdatedBy(String updatedBy);

	Date lastUpdatedOn();

	void lastUpdatedOn(Date updatedOn);

	<K, V> ConcurrentMap<K, V> _map(Object key);

	<T> List<T> _list(Object key);

	<T> Set<T> _set(Object key);

	<T> T _extra(Object key);

	void _extra(Object key, Object value);

	Map<Object, Object> _extras();

	<T> T _tmp(Object key);

	void _tmp(Object key, Object value);

	Map<Object, Object> _tmps();

}
