package org.rapidoid.model.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Err;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Property;
import org.rapidoid.u.U;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * #%L
 * rapidoid-gui
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
@Since("2.0.0")
public class ListItems extends AbstractModel implements Items {

	private static final long serialVersionUID = -4233673233447713903L;

	private final List<Item> list = new ArrayList<Item>();

	@Override
	public void insert(int index, Item item) {
		data().add(index, ifFitsIn(item));
	}

	@Override
	public void add(Item item) {
		data().add(ifFitsIn(item));
	}

	@Override
	public void addAll(Items items) {
		for (int i = 0; i < items.size(); i++) {
			add(ifFitsIn(items.get(i)));
		}
	}

	@Override
	public void addAll(List<Item> items) {
		for (Item item : items) {
			add(ifFitsIn(item));
		}
	}

	@Override
	public void clear() {
		data().clear();
	}

	@Override
	public Item get(int index) {
		return data().get(index);
	}

	@Override
	public boolean isEmpty() {
		return data().isEmpty();
	}

	@Override
	public void remove(int index) {
		data().remove(index);
	}

	@Override
	public void set(int index, Item item) {
		data().set(index, ifFitsIn(item));
	}

	@Override
	public int size() {
		return data().size();
	}

	@Override
	public Items range(int fromIndex, int toIndex) {
		ListItems subitems = new ListItems();
		subitems.addAll(data().subList(fromIndex, toIndex));
		return subitems;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Property> properties(String... properties) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean fitsIn(Item item) {
		return item.value() != null;
	}

	public Item ifFitsIn(Item item) {
		U.must(fitsIn(item), "The item '%s' doesn't fit in the items: %s", item, this);
		return item;
	}

	@Override
	public Iterator<Item> iterator() {
		return data().iterator();
	}

	protected List<Item> data() {
		return list;
	}

	protected String idOf(int index) {
		return data().get(index).id();
	}

	@Override
	public Items orderedBy(String sortOrder) {
		throw Err.notSupported();
	}

}
