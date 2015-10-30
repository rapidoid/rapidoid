package org.rapidoidx.db.impl.inmem;

/*
 * #%L
 * rapidoid-x-db-inmem
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.io.Serializable;
import java.util.Collection;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoidx.db.Database;
import org.rapidoidx.db.impl.DbRelChangesTracker;
import org.rapidoidx.db.impl.DbRelsCommons;
import org.rapidoidx.inmem.EntityLinks;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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
