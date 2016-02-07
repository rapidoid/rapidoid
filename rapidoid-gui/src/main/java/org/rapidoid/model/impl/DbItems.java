package org.rapidoid.model.impl;

/*
 * #%L
 * rapidoid-gui
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.commons.Err;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Models;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.u.U;

import java.util.Comparator;
import java.util.List;

@SuppressWarnings("serial")
@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class DbItems<T> extends BeanListItems<T> {

	private final Predicate<T> match;

	private final Comparator<T> orderBy;

	public DbItems(Class<T> type, Predicate<T> match, Comparator<T> orderBy) {
		super(type);
		this.match = match;
		this.orderBy = orderBy;
	}

	@Override
	public void add(Item item) {
		DB.insert(item.value());
	}

	@Override
	public void insert(int index, Item item) {
		throw Err.notSupported();
	}

	@Override
	public Item get(int index) {
		return data().get(index);
	}

	@Override
	protected List<Item> data() {
		Iterable<T> all = DB.find(beanType, match, orderBy);
		List<Item> records = U.list();

		for (T t : all) {
			records.add(Models.item(t));
		}

		return records;
	}

	@Override
	public void remove(int index) {
		DB.delete(idOf(index));
	}

	@Override
	public void set(int index, Item item) {
		DB.update(idOf(index), item.value());
	}

	@Override
	public Items orderedBy(String sortOrder) {
		Comparator<T> orderBy = Beany.comparator(sortOrder);
		return new DbItems<T>(beanType, match, orderBy);
	}

}
