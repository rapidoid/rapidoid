package org.rapidoidx.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public abstract class AbstractRichEntity implements RichEntity {

	private long id;

	private long version;

	private String createdBy;

	private Date createdOn;

	private String lastUpdatedBy;

	private Date lastUpdatedOn;

	private Map<Object, Object> extras;

	private Map<Object, Object> tmps;

	@Override
	public synchronized <T> T get(String attr) {
		return _extra(attr);
	}

	@Override
	public synchronized void set(String attr, Object value) {
		_extra(attr, value);
	}

	@Override
	public synchronized long id() {
		return id;
	}

	@Override
	public synchronized void id(long id) {
		this.id = id;
	}

	@Override
	public synchronized long version() {
		return version;
	}

	@Override
	public synchronized void version(long version) {
		this.version = version;
	}

	@Override
	public synchronized String createdBy() {
		return createdBy;
	}

	@Override
	public synchronized void createdBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public synchronized Date createdOn() {
		return createdOn;
	}

	@Override
	public synchronized void createdOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public synchronized String lastUpdatedBy() {
		return lastUpdatedBy;
	}

	@Override
	public synchronized void lastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	@Override
	public synchronized Date lastUpdatedOn() {
		return lastUpdatedOn;
	}

	@Override
	public synchronized void lastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	@Override
	public synchronized int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public synchronized boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if (id == 0) {
			return false;
		}

		if (!(obj instanceof RichEntity)) {
			return false;
		}

		RichEntity other = (RichEntity) obj;
		if (id != other.id())
			return false;

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <K, V> ConcurrentMap<K, V> _map(Object key) {
		_extras();

		if (!extras.containsKey(key)) {
			extras.put(key, new ConcurrentHashMap<K, V>());
		}

		return (ConcurrentMap<K, V>) extras.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> List<T> _list(Object key) {
		_extras();

		if (!extras.containsKey(key)) {
			extras.put(key, new ArrayList<T>());
		}

		return (List<T>) extras.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> Set<T> _set(Object key) {
		_extras();

		if (!extras.containsKey(key)) {
			extras.put(key, new LinkedHashSet<T>());
		}

		return (Set<T>) extras.get(key);
	}

	@SuppressWarnings("unchecked")
	private <T> T _extra(Object key) {
		return (T) _extras().get(key);
	}

	private void _extra(Object key, Object value) {
		_extras().put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T _tmp(Object key) {
		return (T) _tmps().get(key);
	}

	@Override
	public synchronized void _tmp(Object key, Object value) {
		_tmps().put(key, value);
	}

	@Override
	public synchronized Map<Object, Object> _extras() {
		if (extras == null) {
			extras = Collections.synchronizedMap(new HashMap<Object, Object>());
		}
		return extras;
	}

	@Override
	public synchronized Map<Object, Object> _tmps() {
		if (tmps == null) {
			tmps = Collections.synchronizedMap(new HashMap<Object, Object>());
		}
		return tmps;
	}

}
