package org.rapidoid.gui;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.TdTag;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.var.Var;
import org.rapidoid.wrap.BoolWrap;

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
public class Grid extends AbstractWidget<Grid> {

	private volatile Iterable<?> items;

	private volatile String orderBy;
	private volatile int pageSize = 10;
	private volatile String[] columns = {};
	private volatile Object[] headers = {};
	private volatile String rowCmd;
	private volatile String highlightRegex;

	private volatile Mapper<Object, String> toUri;

	@Override
	protected Object render() {

		Pager pager = noPager();
		boolean paging = pageSize > 0;
		Iterable<?> rows;

		BoolWrap isLastPage = new BoolWrap();

		if (paging) {
			String pageParam = "_p" + seq("pager");
			pager = GUI.pager(pageParam).min(1);

			Integer size = Coll.getSizeOrNull(items);

			if (size != null) {
				int pages = (int) Math.ceil(size / (double) pageSize);
				pager.max(pages);
			}

			rows = Msc.getPage(items, pager.pageNumber(), pageSize, size, isLastPage);

		} else {
			rows = items;
		}

		return renderGridPage(pager, rows, isLastPage.value);
	}

	private Object renderGridPage(Pager pager, Iterable<?> rows, boolean isLastPage) {
		Iterator<?> it = rows.iterator();
		boolean hasData = it.hasNext();

		if (pager != null && (isLastPage || !hasData)) {
			pager.max(pager.pageNumber());
		}

		if (!hasData) {
			return U.list(noDataAvailable(), pager); // no data
		}

		Class<?> type = it.next().getClass();

		Items itemsModel;
		if (rows instanceof Items) {
			itemsModel = (Items) rows;
		} else {
			itemsModel = Models.beanItems(type, U.array(rows));
		}

		final List<Property> props = itemsModel.properties(columns);
		boolean ordered = !U.isEmpty(orderBy);
		Var<String> order = null;
		String currentOrder = orderBy;

		if (ordered) {
			order = GUI.var("_o" + seq("order"), orderBy);
			currentOrder = order.get();
			itemsModel = itemsModel.orderedBy(currentOrder);
		}

		Tag header = tableHeader(props, order);
		Tag body = tableBody(props, itemsModel);

		return fullTable(header, body, pager);
	}

	protected Tag noDataAvailable() {
		return GUI.NOTHING;
	}

	protected Pager noPager() {
		return null;
	}

	protected Tag fullTable(Tag header, Tag body, Pager pager) {
		return GUI.row(GUI.table_(GUI.thead(header), body), pager);
	}

	protected Tag tableBody(final List<Property> props, Items pageOrAll) {
		Tag body = GUI.tbody();

		for (Item item : pageOrAll) {
			Tag row = itemRow(props, item);
			body = body.append(row);
		}
		return body;
	}

	protected Tag tableHeader(final List<Property> props, Var<String> order) {
		Tag header = tr();

		for (int i = 0; i < props.size(); i++) {
			Property prop = props.get(i);
			Tag sortIcon = null;

			Object caption = U.notEmpty(headers) && headers.length > i ? headers[i] : null;
			caption = U.or(caption, prop.caption());

			Object sort;
			if (order != null) {
				String currentOrder = order.get();

				if (currentOrder.equals(prop.name())) {
					sortIcon = GUI.fa("sort-amount-asc");
				}

				if (currentOrder.equals("-" + prop.name())) {
					sortIcon = GUI.fa("sort-amount-desc");
				}

				sort = GUI.a_void(caption, " ", sortIcon).cmd("_sort", order, prop.name());
			} else {
				sort = caption;
			}

			header = header.append(th(sort));
		}
		return header;
	}

	protected Tag itemRow(List<Property> properties, Item item) {
		Tag row = tr();

		for (Property prop : properties) {
			Object value = prop.get(item);
			value = U.or(value, "");
			row = row.append(cell(GUI.display(value)));
		}

		if (rowCmd != null) {
			row = row.cmd(rowCmd, item.value());
			row = row.class_("pointer");

		} else {
			String js = onClickScript(item);
			if (U.notEmpty(js)) {
				row = row.onclick(js);
				row = row.class_("pointer");
			}
		}

		return row;
	}

	protected String onClickScript(Item item) {
		String uri;
		if (toUri != null) {
			uri = Lmbd.eval(toUri, item.value());
		} else {
			uri = GUI.uriFor(item.value());
			if (U.notEmpty(uri)) {
				uri = Msc.uri(uri, "view");
			}
		}

		return U.notEmpty(uri) ? U.frmt("Rapidoid.goAt('%s');", uri) : null;
	}

	protected TdTag cell(Object value) {
		if (U.notEmpty(highlightRegex)) {
			String s = String.valueOf(value);
			value = GUI.highlight(s, highlightRegex);
		}

		return td(value);
	}

	public Iterable<?> items() {
		return items;
	}

	public Grid items(Iterable<?> items) {
		U.must(!(items instanceof Items));
		this.items = items;
		return this;
	}

	public String orderBy() {
		return orderBy;
	}

	public Grid orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public int pageSize() {
		return pageSize;
	}

	public Grid pageSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public String[] columns() {
		return columns;
	}

	public Grid columns(String... columns) {
		this.columns = columns;
		return this;
	}

	public Grid columns(Iterable<String> columns) {
		return columns(U.arrayOf(String.class, columns));
	}

	public String rowCmd() {
		return rowCmd;
	}

	public Grid rowCmd(String rowCmd) {
		this.rowCmd = rowCmd;
		return this;
	}

	public Mapper<Object, String> toUri() {
		return toUri;
	}

	@SuppressWarnings("unchecked")
	public <T> Grid toUri(Mapper<T, String> toUri) {
		this.toUri = (Mapper<Object, String>) toUri;
		return this;
	}

	public Object[] headers() {
		return headers;
	}

	public Grid headers(Object... headers) {
		this.headers = headers;
		return this;
	}

	public Grid headers(Iterable<?> headers) {
		return headers(U.array(headers));
	}

	public String highlightRegex() {
		return highlightRegex;
	}

	public Grid highlightRegex(String highlightRegex) {
		this.highlightRegex = highlightRegex;
		return this;
	}
}
