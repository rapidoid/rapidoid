package org.rapidoid.db;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.CommonRoles;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

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

@Authors("Nikolche Mihajlovski")
@Since("2.2.0")
public class EntityCommons implements IEntityCommons, CommonRoles, Serializable {

	private static final long serialVersionUID = 8414835674684110203L;

	private volatile long id;

	private volatile long version;

	private volatile String createdBy;

	private volatile Date createdOn;

	private volatile String lastUpdatedBy;

	private volatile Date lastUpdatedOn;

	private final ConcurrentMap<String, Object> extras = U.concurrentMap();

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> ConcurrentMap<K, V> _map(String name) {
		if (!extras.containsKey(name)) {
			extras.putIfAbsent(name, U.concurrentMap());
		}

		return (ConcurrentMap<K, V>) extras.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> _list(String name) {
		if (!extras.containsKey(name)) {
			extras.putIfAbsent(name, U.list());
		}

		return (List<T>) extras.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Set<T> _set(String name) {
		if (!extras.containsKey(name)) {
			extras.putIfAbsent(name, U.set());
		}

		return (Set<T>) extras.get(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Var<T> _var(String name, T defaultValue) {
		if (!extras.containsKey(name)) {
			extras.putIfAbsent(name, Vars.var(defaultValue));
		}

		return (Var<T>) extras.get(name);
	}

	@Override
	public long id() {
		return id;
	}

	@Override
	public void id(long id) {
		this.id = id;
	}

	@Override
	public long version() {
		return version;
	}

	@Override
	public void version(long version) {
		this.version = version;
	}

	@Override
	public String createdBy() {
		return createdBy;
	}

	@Override
	public void createdBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public Date createdOn() {
		return createdOn;
	}

	@Override
	public void createdOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String lastUpdatedBy() {
		return lastUpdatedBy;
	}

	@Override
	public void lastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	@Override
	public Date lastUpdatedOn() {
		return lastUpdatedOn;
	}

	@Override
	public void lastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if (id == 0) {
			return false;
		}

		if (!(obj instanceof IEntityCommons)) {
			return false;
		}

		IEntityCommons other = (IEntityCommons) obj;
		if (id != other.id())
			return false;

		return true;
	}

}
