package org.rapidoid.pages.bootstrap;

import java.util.List;

import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.TbodyTag;
import org.rapidoid.html.tag.TrTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Property;
import org.rapidoid.reactive.Var;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-pages
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

public class TableWidget extends BootstrapWidget {

	private final Items items;

	private final TableItemAction itemAction;

	public TableWidget(final Items items) {
		this(items, null);
	}

	public TableWidget(final Items items, TableItemAction itemAction) {
		this.items = items;
		this.itemAction = itemAction;
	}

	protected int pageSize() {
		return 10;
	}

	protected TrTag itemRow(List<Property> properties, Item item) {
		TrTag row = tr();

		for (Property prop : properties) {
			row.append(td(U.or(item.get(prop.name()), "")));
		}

		return row;
	}

	@Override
	public Tag<?> view(HttpExchange x) {
		final List<Property> properties = items.properties();

		final Var<Integer> pageNumber = var(1);

		TrTag header = tr();

		for (Property prop : properties) {
			header.append(th(prop.caption()));
		}

		Integer pageN = pageNumber.get();
		Items page = items.range((pageN - 1) * pageSize(), Math.min((pageN) * pageSize(), items.size()));

		TbodyTag body = tbody();

		for (Item item : page) {
			TrTag row = itemRow(properties, item);
			if (TableWidget.this.itemAction != null) {
				row.click(new TableItemActionImpl(item, TableWidget.this.itemAction));
			}
			body.append(row);
		}

		int total = items.size();
		int pages = (int) Math.ceil(total / (double) pageSize());

		PagerWidget pager = new PagerWidget(1, pages, pageNumber);
		return rowFull(table_(thead(header), body), pager);
	}

}
