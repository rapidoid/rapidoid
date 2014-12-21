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

public class DataPermissions {

	public static final DataPermissions ALL = new DataPermissions(true, true, true, true);

	public static final DataPermissions NONE = new DataPermissions(false, false, false, false);

	public static final DataPermissions READ_ONLY = new DataPermissions(false, true, false, false);

	public static final DataPermissions NO_DELETE = new DataPermissions(true, true, true, false);

	public final boolean create;

	public final boolean read;

	public final boolean update;

	public final boolean delete;

	public final boolean denied;

	DataPermissions(boolean create, boolean read, boolean update, boolean delete) {
		this.create = create;
		this.read = read;
		this.update = update;
		this.delete = delete;
		this.denied = !create && !read && !update && !delete;
	}

	public static DataPermissions from(boolean create, boolean read, boolean update, boolean delete) {
		return new DataPermissions(create, read, update, delete);
	}

	public DataPermissions and(DataPermissions dp) {
		return from(create && dp.create, read && dp.read, update && dp.update, delete && dp.delete);
	}

	public DataPermissions or(DataPermissions dp) {
		return from(create || dp.create, read || dp.read, update || dp.update, delete || dp.delete);
	}

	public boolean denied() {
		return denied;
	}

}
