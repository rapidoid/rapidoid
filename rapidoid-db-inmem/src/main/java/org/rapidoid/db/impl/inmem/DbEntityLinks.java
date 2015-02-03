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
import java.util.Collection;

import org.rapidoid.annotation.Authors;
import org.rapidoid.db.Database;
import org.rapidoid.db.impl.DbRelChangesTracker;
import org.rapidoid.db.impl.DbRelsCommons;
import org.rapidoid.inmem.EntityLinks;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
public class DbEntityLinks implements EntityLinks, Serializable {

	private static final long serialVersionUID = 2212521452392734563L;

	private final Database db;

	private final DbRelsCommons<?> rel;

	private final DbRelChangesTracker tracker;

	public DbEntityLinks(Database db, DbRelsCommons<?> rel, DbRelChangesTracker tracker) {
		this.db = db;
		this.rel = rel;
		this.tracker = tracker;
	}

	@Override
	public String relationName() {
		return rel.getName();
	}

	@Override
	public long fromId() {
		Object holder = rel.getHolder();
		U.notNull(holder, "holder");
		return db.getIdOf(holder);
	}

	@Override
	public Collection<Long> addedRelIds() {
		return tracker.getAddedRelations();
	}

	@Override
	public Collection<Long> removedRelIds() {
		return tracker.getRemovedRelations();
	}

	@Override
	public Collection<Long> allRelIds() {
		return rel.getIdsView();
	}

	@Override
	public void addRelTo(long id) {
		rel.addIdWithoutTracking(id);
	}

	@Override
	public void removeRelTo(long id) {
		rel.removeIdWithoutTracking(id);
	}

}
