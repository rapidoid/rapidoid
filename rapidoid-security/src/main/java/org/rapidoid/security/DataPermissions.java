package org.rapidoid.security;

/*
 * #%L
 * rapidoid-security
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

public class DataPermissions {

	public static final DataPermissions ALL = new DataPermissions(true, true);

	public static final DataPermissions NONE = new DataPermissions(false, false);

	public static final DataPermissions READ_ONLY = new DataPermissions(true, false);

	public static final DataPermissions WRITE_ONLY = new DataPermissions(false, true);

	public final boolean read;

	public final boolean change;

	private DataPermissions(boolean read, boolean change) {
		this.read = read;
		this.change = change;
	}

	public static DataPermissions from(boolean read, boolean change) {
		if (read) {
			return change ? ALL : READ_ONLY;
		} else {
			return change ? WRITE_ONLY : NONE;
		}
	}

	public DataPermissions and(DataPermissions dp) {
		return from(read && dp.read, change && dp.change);
	}

	public DataPermissions or(DataPermissions dp) {
		return from(read || dp.read, change || dp.change);
	}

	@Override
	public String toString() {
		return "DataPermissions [read=" + read + ", change=" + change + "]";
	}

}
