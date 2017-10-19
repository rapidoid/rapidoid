package org.rapidoid.group;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.u.U;

import java.util.Collections;
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
@Since("5.3.0")
public class GroupOf<E extends Manageable> extends RapidoidThing {

	private final String kind;

	private final Class<E> itemType;

	private final List<E> items = Coll.synchronizedList();

	private final GroupStats stats = new GroupStats();

	public GroupOf(Class<E> itemType) {
		this.kind = Manageables.kindOf(itemType);
		this.itemType = itemType;
		Groups.ALL.add(this);
	}

	public String kind() {
		return kind;
	}

	public Class<E> itemType() {
		return itemType;
	}

	public List<E> items() {
		return Collections.unmodifiableList(items);
	}

	public GroupStats stats() {
		return stats;
	}

	private void checkType(E element) {
		U.notNull(element, "group element");
		U.must(itemType.isAssignableFrom(element.getClass()));
	}

	private void checkDuplicates(E element) {
		U.must(!items.contains(element), "The item is already in the group!");
	}

	public void add(E element) {
		checkType(element);
		checkDuplicates(element);

		items.add(element);
		stats.added.incrementAndGet();
	}

	public void add(int index, E element) {
		checkType(element);
		checkDuplicates(element);

		items.add(index, element);
		stats.added.incrementAndGet();
	}

	public E set(int index, E element) {
		checkType(element);
		checkDuplicates(element);

		return items.set(index, element);
	}

	public E get(int index) {
		return items.get(index);
	}

	public E get(String id) {
		E item = find(id);
		U.must(item != null, "Cannot find item with id='%s'!", id);
		return item;
	}

	public E find(String id) {
		U.notNull(id, "id");

		for (E item : items) {
			if (U.eq(id, item.id())) return item;
		}

		return null;
	}

	public E remove(int index) {
		return items.remove(index);
	}

	public int indexOf(E element) {
		checkType(element);

		return items.indexOf(element);
	}

	public void clear() {
		items.clear();
	}

	public boolean remove(E element) {
		checkType(element);

		return items.remove(element);
	}

	public int size() {
		return items.size();
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public boolean contains(E element) {
		checkType(element);

		return items.contains(element);
	}

	@Override
	public String toString() {
		return "GroupOf{" +
			"kind='" + kind + '\'' +
			", size=" + items.size() +
			", stats=" + stats +
			'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GroupOf<?> groupOf = (GroupOf<?>) o;

		if (kind != null ? !kind.equals(groupOf.kind) : groupOf.kind != null) return false;
		return itemType != null ? itemType.equals(groupOf.itemType) : groupOf.itemType == null;
	}

	@Override
	public int hashCode() {
		int result = kind != null ? kind.hashCode() : 0;
		result = 31 * result + (itemType != null ? itemType.hashCode() : 0);
		return result;
	}
}
