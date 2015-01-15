package org.rapidoid.db.impl.inmem;

import java.util.Collection;

import org.rapidoid.db.Database;
import org.rapidoid.db.impl.DbRelChangesTracker;
import org.rapidoid.db.impl.DbRelsCommons;
import org.rapidoid.inmem.EntityLinks;
import org.rapidoid.util.U;

public class DbEntityLinks implements EntityLinks {

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
