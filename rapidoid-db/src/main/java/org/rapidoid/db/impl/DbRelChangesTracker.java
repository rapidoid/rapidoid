package org.rapidoid.db.impl;

/*
 * #%L
 * rapidoid-db
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

import java.util.Set;

import org.rapidoid.util.U;

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
