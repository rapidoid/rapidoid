package org.rapidoidx.db.impl;

/*
 * #%L
 * rapidoid-x-db-impl
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

import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbRelChangesTracker {

	private final Set<Long> addedRelations = U.set();

	private final Set<Long> removedRelations = U.set();

	public void addedRelTo(long id) {
		addedRelations.add(id);
		removedRelations.remove(id);
	}

	public void removedRelTo(long id) {
		removedRelations.add(id);
		addedRelations.remove(id);
	}

	public Set<Long> getAddedRelations() {
		return addedRelations;
	}

	public Set<Long> getRemovedRelations() {
		return removedRelations;
	}

}
