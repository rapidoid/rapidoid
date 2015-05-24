package org.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public interface RichEntity {

	String id();

	void id(String id);

	String version();

	void version(String version);

	String createdBy();

	void createdBy(String createdBy);

	Date createdOn();

	void createdOn(Date createdOn);

	String lastUpdatedBy();

	void lastUpdatedBy(String updatedBy);

	Date lastUpdatedOn();

	void lastUpdatedOn(Date updatedOn);

	<T> T get(String attr);

	void set(String attr, Object value);

	<K, V> ConcurrentMap<K, V> _map(Object key);

	<T> List<T> _list(Object key);

	<T> Set<T> _set(Object key);

	Map<Object, Object> _extras();

	<T> T _tmp(Object key);

	void _tmp(Object key, Object value);

	Map<Object, Object> _tmps();

}
