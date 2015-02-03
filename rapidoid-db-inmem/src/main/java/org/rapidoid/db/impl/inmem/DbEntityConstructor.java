package org.rapidoid.db.impl.inmem;

/*
 * #%L
 * rapidoid-db-inmem
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

import java.io.Serializable;
import java.util.Collections;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.db.Database;
import org.rapidoid.inmem.EntityConstructor;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbEntityConstructor implements EntityConstructor, Serializable {

	private static final long serialVersionUID = -4132102852835102071L;

	private final Database db;

	public DbEntityConstructor(Database db) {
		this.db = db;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> entityType) {
		return (T) db.schema().entity(entityType, Collections.EMPTY_MAP);
	}

}
