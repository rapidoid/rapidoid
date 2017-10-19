package org.rapidoid.group;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
@Since("5.3.0")
public class Groups extends RapidoidThing {

	static final Set<GroupOf<?>> ALL = Coll.synchronizedSet();

	public static Set<GroupOf<?>> all() {
		return Collections.unmodifiableSet(U.set(ALL)); // snapshot
	}

	@SuppressWarnings("unchecked")
	public static <T extends Manageable> List<GroupOf<T>> find(Class<? extends T> itemType) {
		List<GroupOf<T>> groups = U.list();

		for (GroupOf<?> group : all()) {
			if (group.itemType().equals(itemType)) {
				groups.add((GroupOf<T>) group);
			}
		}

		return groups;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Manageable> List<GroupOf<T>> find(String kind) {
		List<GroupOf<T>> groups = U.list();

		for (GroupOf<?> group : all()) {
			if (group.kind().equals(kind)) {
				groups.add((GroupOf<T>) group);
			}
		}

		return groups;
	}

	public static void reset() {
		for (GroupOf<?> group : all()) {
			group.clear();
		}
		ALL.clear();
		AutoManageable.reset();
	}
}
