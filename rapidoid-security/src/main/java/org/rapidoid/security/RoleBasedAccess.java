package org.rapidoid.security;

/*
 * #%L
 * rapidoid-security
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

public class RoleBasedAccess {

	public final String role;

	public final boolean create;

	public final boolean read;

	public final boolean update;

	public final boolean delete;

	public RoleBasedAccess(String role, boolean create, boolean read, boolean update, boolean delete) {
		this.role = role;
		this.create = create;
		this.read = read;
		this.update = update;
		this.delete = delete;
	}

	public static RoleBasedAccess from(String role, boolean fullAccess, boolean create, boolean read, boolean update,
			boolean delete) {

		create |= fullAccess;
		read |= fullAccess;
		update |= fullAccess;
		delete |= fullAccess;

		return new RoleBasedAccess(role, create, read, update, delete);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoleBasedAccess other = (RoleBasedAccess) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}

}
