package org.rapidoid.widget;

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

import java.util.List;

import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.TdTag;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Property;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;

public class GridWidget extends AbstractWidget {

	private final Items items;
	private final String sortOrder;
	private final int pageSize;
	private final String[] properties;

	public GridWidget(Items items, String sortOrder, int pageSize, String... properties) {
		this.items = items;
		this.sortOrder = sortOrder;
		this.pageSize = pageSize;
		this.properties = properties;
	}

	@Override
	protected Tag create() {
		final List<Property> props = items.properties(properties);

		int total = items.size();
		int pages = (int) Math.ceil(total / (double) pageSize);

		boolean ordered = !U.isEmpty(sortOrder);
		Var<String> order = null;

		Items slice = items;

		String currentOrder = sortOrder;

		if (ordered) {
			order = localVar("_order_" + items.uri(), sortOrder);
			currentOrder = order.get();
			slice = slice.orderedBy(currentOrder);
		}

		boolean paging = pageSize > 0;
		Var<Integer> pageNumber = null;

		if (paging) {
			pageNumber = localVar("_page_" + items.uri(), 1, 1, pages);
			slice = getPage(slice, pageNumber.get());
		}

		Tag header = tableHeader(props, order);
		Tag body = tableBody(props, slice);
		PagerWidget pager = paging ? pager(1, pages, pageNumber) : noPager();

		return fullTable(header, body, pager);
	}

	protected PagerWidget noPager() {
		return null;
	}

	protected Tag fullTable(Tag header, Tag body, PagerWidget pager) {
		return row(table_(thead(header), body), pager);
	}

	protected Items getPage(Items items, Integer pageN) {
		Items pageOrAll;
		int pageFrom = Math.max((pageN - 1) * pageSize, 0);
		int pageTo = Math.min((pageN) * pageSize, items.size());

		pageOrAll = items.range(pageFrom, pageTo);
		return pageOrAll;
	}

	protected Tag tableBody(final List<Property> props, Items pageOrAll) {
		Tag body = tbody();

		for (Item item : pageOrAll) {
			Tag row = itemRow(props, item);
			body = body.append(row);
		}
		return body;
	}

	protected Tag tableHeader(final List<Property> props, Var<String> order) {
		Tag header = tr();

		for (Property prop : props) {
			Tag sortIcon = null;

			Object sort;
			if (order != null) {

				String currentOrder = order.get();

				if (currentOrder.equals(prop.name())) {
					sortIcon = glyphicon("chevron-down");
				}

				if (order != null && currentOrder.equals("-" + prop.name())) {
					sortIcon = glyphicon("chevron-up");
				}

				sort = a_void(prop.caption(), " ", sortIcon).cmd("_sort", order, prop.name());
			} else {
				sort = prop.caption();
			}

			header = header.append(th(sort));
		}
		return header;
	}

	protected Tag itemRow(List<Property> properties, Item item) {
		Tag row = tr();

		for (Property prop : properties) {
			Object value = item.get(prop.name());
			value = U.or(value, "");
			row = row.append(cell(value));
		}

		String js = onClickScript(item);
		row = row.onclick(js).class_("pointer");

		return row;
	}

	protected String onClickScript(Item item) {
		String type = U.uncapitalized(item.value().getClass().getSimpleName());
		String js = U.format("goAt('/%s/%s');", type, item.id());
		return js;
	}

	protected TdTag cell(Object value) {
		return td(value);
	}

}
