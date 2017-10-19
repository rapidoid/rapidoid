package org.rapidoid.group;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.util.List;

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
@Since("5.3.5")
public class Manageables extends RapidoidThing {

	public static String kindOf(Class<?> cls) {
		ManageableBean mb = cls.getAnnotation(ManageableBean.class);
		U.must(mb != null, "The type '%s' must be annotated with @%s", ManageableBean.class.getSimpleName());
		return mb.kind();
	}

	public static <T extends Manageable> T find(Class<? extends T> itemType, String id) {

		for (GroupOf<T> group : Groups.find(itemType)) {
			T member = group.find(id);
			if (member != null) {
				return member;
			}
		}

		return null;
	}

	public static Manageable find(String itemType, String id) {

		for (GroupOf<Manageable> group : Groups.find(itemType)) {
			Manageable member = group.find(id);
			if (member != null) {
				return member;
			}
		}

		return null;
	}

	public static Manageable find(String kind, String id, String sub) {

		Manageable target = find(kind, id);
		U.must(target != null, "Cannot find the manageable!");
		target.reloadManageable();

		if (U.isEmpty(sub)) {
			return target;
		}

		return findSub(target, sub);
	}

	private static Manageable findSub(Manageable target, String sub) {

		String[] parts = sub.split("/", 3);
		String seg = parts[0];
		String id = parts[1];

		List<? extends Manageable> segment = target.getManageableChildren().get(seg);
		U.must(segment != null, "Cannot find the manageable segment: %s", seg);

		target = findById(segment, id);
		U.must(target != null, "Cannot find the sub-manageable with id: %s in segment: %s", id, seg);

		target.reloadManageable();

		return parts.length > 2 ? findSub(target, parts[2]) : target;
	}

	private static Manageable findById(List<? extends Manageable> items, String id) {
		U.notNull(id, "id");

		for (Manageable item : items) {
			if (U.eq(id, item.id())) return item;
		}

		return null;
	}

}
