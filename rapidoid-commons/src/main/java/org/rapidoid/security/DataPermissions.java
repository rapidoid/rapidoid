package org.rapidoid.security;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
@Since("2.0.0")
public class DataPermissions extends RapidoidThing {

	public static final DataPermissions ALL = new DataPermissions(true, true, true, true);

	public static final DataPermissions NONE = new DataPermissions(false, false, false, false);

	public static final DataPermissions READ_ONLY = new DataPermissions(true, false, false, false);

	public final boolean read;

	public final boolean insert;

	public final boolean change;

	public final boolean delete;

	private DataPermissions(boolean read, boolean insert, boolean change, boolean delete) {
		this.read = read;
		this.insert = insert;
		this.change = change;
		this.delete = delete;
	}

	public static DataPermissions from(boolean read, boolean insert, boolean change, boolean delete) {
		return new DataPermissions(read, insert, change, delete);
	}

	public DataPermissions and(DataPermissions dp) {
		return from(read && dp.read, insert && dp.insert, change && dp.change, delete && dp.delete);
	}

	public DataPermissions or(DataPermissions dp) {
		return from(read || dp.read, insert || dp.insert, change || dp.change, delete || dp.delete);
	}

	@Override
	public String toString() {
		return "DataPermissions [read=" + read + ", insert=" + insert + ", change=" + change + ", delete=" + delete
			+ "]";
	}

}
