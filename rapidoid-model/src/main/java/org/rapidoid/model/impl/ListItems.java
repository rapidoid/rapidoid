package org.rapidoid.model.impl;

/*
 * #%L
 * rapidoid-model
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Model;
import org.rapidoid.model.Property;

public class ListItems implements Items {

	private final List<Item> list = new ArrayList<Item>();

	public ListItems(Object... values) {
		for (Object value : values) {
			list.add(Model.item(value));
		}
	}

	public ListItems(Collection<?> values) {
		for (Object value : values) {
			list.add(Model.item(value));
		}
	}

	@Override
	public void insert(int index, Item item) {
		list.add(index, item);
	}

	@Override
	public void add(Item item) {
		list.add(item);
	}

	@Override
	public void addAll(Items items) {
		for (int i = 0; i < items.size(); i++) {
			list.add(items.get(i));
		}
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public Item get(int index) {
		return list.get(index);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public void remove(int index) {
		list.remove(index);
	}

	@Override
	public void set(int index, Item item) {
		list.set(index, item);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Items range(int fromIndex, int toIndex) {
		return new ListItems(list.subList(fromIndex, toIndex));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> properties() {
		return Collections.EMPTY_LIST;
	}

}
