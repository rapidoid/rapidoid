package org.rapidoid.pages;

import java.util.Collection;
import java.util.List;

import org.rapidoid.html.Bootstrap;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.DivTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.H4Tag;
import org.rapidoid.html.tag.LiTag;
import org.rapidoid.html.tag.NavTag;
import org.rapidoid.html.tag.SpanTag;
import org.rapidoid.html.tag.TbodyTag;
import org.rapidoid.html.tag.TrTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchanges;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Model;
import org.rapidoid.model.Property;
import org.rapidoid.util.Cls;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;

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

public abstract class BootstrapWidgets extends Bootstrap {

	public static final ButtonTag[] SAVE_CANCEL = cmds("Save", "Cancel");

	public static final ButtonTag[] ADD_CANCEL = cmds("Add", "Cancel");

	public static final ButtonTag[] UPDATE_CANCEL = cmds("Update", "Cancel");

	public static final ButtonTag[] DELETE_CANCEL = cmds("Delete", "Cancel");

	public static final ButtonTag[] INSERT_CANCEL = cmds("Insert", "Cancel");

	public static final ButtonTag[] YES_NO = cmds("Yes", "No");

	public static final ButtonTag[] YES_NO_CANCEL = cmds("Yes", "No", "Cancel");

	public static final ButtonTag[] OK_CANCEL = cmds("OK", "Cancel");

	public static Object i18n(String multiLanguageText, Object... formatArgs) {
		return HtmlWidgets.i18n(multiLanguageText, formatArgs);
	}

	public static <T> Var<T> property(Item item, String property) {
		return HtmlWidgets.property(item, property);
	}

	public static Tag<?> template(String templateFileName, Object... namesAndValues) {
		return HtmlWidgets.template(templateFileName, namesAndValues);
	}

	public static Tag<?> hardcoded(String content) {
		return HtmlWidgets.hardcoded(content);
	}

	public static <T> Tag<?> grid(Class<T> type, Object[] items, int pageSize, String... properties) {
		return grid(Model.beanItems(type, items), pageSize, properties);
	}

	public static <T> Tag<?> grid(Class<T> type, Collection<T> items, int pageSize, String... properties) {
		return grid(type, items.toArray(), pageSize, properties);
	}

	public static Tag<?> grid(Items items, int pageSize, String... properties) {
		final List<Property> props = items.properties(properties);

		int total = items.size();
		int pages = (int) Math.ceil(total / (double) pageSize);

		TrTag header = tr();

		for (Property prop : props) {
			header = header.append(th(prop.caption()));
		}

		boolean paging = pageSize > 0;
		Var<Integer> pageNumber = null;
		Items pageOrAll = items;

		if (paging) {
			HttpExchange x = HttpExchanges.getThreadLocalExchange();

			pageNumber = HttpExchanges.sessionVar("_pageN_" + items.uri() + ":" + Pages.viewId(x), 1);
			Integer pageN = U.limit(1, pageNumber.get(), pages);
			pageNumber.set(pageN);

			int pageFrom = Math.max((pageN - 1) * pageSize, 0);
			int pageTo = Math.min((pageN) * pageSize, items.size());

			pageOrAll = items.range(pageFrom, pageTo);
		}

		TbodyTag body = tbody();

		for (Item item : pageOrAll) {
			TrTag row = itemRow(props, item);
			body = body.append(row);
		}

		Tag<?> pager = paging ? pager(1, pages, pageNumber) : null;
		return row(table_(thead(header), body), pager);
	}

	protected static TrTag itemRow(List<Property> properties, Item item) {
		TrTag row = tr();

		for (Property prop : properties) {
			row = row.append(td(U.or(item.get(prop.name()), "")));
		}

		return row;
	}

	public static Tag<?> pager(int from, int to, Var<Integer> pageNumber) {

		int pageN = pageNumber.get();

		SpanTag firstIcon = span(LAQUO).attr("aria-hidden", "true");
		ATag first = a(firstIcon, span("First").class_("sr-only")).cmd("_set", pageNumber, from);

		SpanTag prevIcon = span(LT).attr("aria-hidden", "true");
		ATag prev = a(prevIcon, span("Previous").class_("sr-only")).cmd("_dec", pageNumber, 1);

		ATag current = a("Page ", pageN, " of " + to);

		SpanTag nextIcon = span(GT).attr("aria-hidden", "true");
		ATag next = a(nextIcon, span("Next").class_("sr-only")).cmd("_inc", pageNumber, 1);

		SpanTag lastIcon = span(RAQUO).attr("aria-hidden", "true");
		ATag last = a(lastIcon, span("Last").class_("sr-only")).cmd("_set", pageNumber, to);

		LiTag firstLi = pageN > from ? li(first) : li(first.cmd(null)).class_("disabled");
		LiTag prevLi = pageN > from ? li(prev) : li(prev.cmd(null)).class_("disabled");
		LiTag currentLi = li(current);
		LiTag nextLi = pageN < to ? li(next) : li(next.cmd(null)).class_("disabled");
		LiTag lastLi = pageN < to ? li(last) : li(last.cmd(null)).class_("disabled");

		NavTag pagination = nav(ul_li(firstLi, prevLi, currentLi, nextLi, lastLi).class_("pagination"));
		return div(pagination).class_("pull-right");
	}

	public static FormTag edit(Object bean, final Tag<?>[] buttons, String... properties) {
		Item item = Model.item(bean);
		return edit(item, buttons, properties);
	}

	public static FormTag edit(final Item item, final Tag<?>[] buttons, String... properties) {
		final List<Property> props = item.editableProperties(properties);

		int propN = props.size();

		String[] names = new String[propN];
		String[] desc = new String[propN];
		FieldType[] types = new FieldType[propN];
		Object[][] options = new Object[propN][];
		Var<?>[] vars = new Var[propN];

		for (int i = 0; i < propN; i++) {
			Property prop = props.get(i);
			names[i] = prop.name();
			desc[i] = prop.caption();
			types[i] = getPropertyFieldType(prop);
			options[i] = getPropertyOptions(prop);
			vars[i] = property(item, prop.name());
		}

		return form_(FormLayout.VERTICAL, names, desc, types, options, vars, buttons);
	}

	protected static FieldType getPropertyFieldType(Property prop) {
		Class<?> type = prop.type();

		if (type.isEnum()) {
			return type.getEnumConstants().length <= 3 ? FieldType.RADIOS : FieldType.DROPDOWN;
		}

		if (prop.name().toLowerCase().contains("email")) {
			return FieldType.EMAIL;
		}

		if (Collection.class.isAssignableFrom(type)) {
			return FieldType.MULTI_SELECT;
		}

		if (Cls.kindOf(type) == TypeKind.OBJECT) {
			return FieldType.DROPDOWN;
		}

		return FieldType.TEXT;
	}

	protected static Object[] getPropertyOptions(Property prop) {
		Class<?> type = prop.type();

		if (type.isEnum()) {
			return type.getEnumConstants();
		}

		if (Cls.kindOf(type) == TypeKind.OBJECT) {
			return new Object[] {};
		}

		return null;
	}

	public static Tag<?> page(boolean devMode, String pageTitle, Object head, Object body) {
		String devOrProd = devMode ? "dev" : "prod";
		return template("bootstrap-page-" + devOrProd + ".html", "title", pageTitle, "head", head, "body", body);
	}

	public static Tag<?> page(boolean devMode, String pageTitle, Object body) {
		return page(devMode, pageTitle, "", body);
	}

	public static Tag<?> media(Object left, Object title, Object body) {
		H4Tag mhead = h4(title).class_("media-heading");
		DivTag mleft = div(left).class_("media-left");
		DivTag mbody = div(mhead, body).class_("media-body");
		return div(mleft, mbody).class_("media");
	}

}
