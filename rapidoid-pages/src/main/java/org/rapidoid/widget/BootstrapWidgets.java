package org.rapidoid.widget;

import java.util.Collection;
import java.util.List;

import org.rapidoid.html.Bootstrap;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchanges;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Models;
import org.rapidoid.pages.Pages;
import org.rapidoid.util.Cls;
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

	public static final ButtonTag SAVE = cmd("^Save");

	public static final ButtonTag UPDATE = cmd("^Update");

	public static final ButtonTag INSERT = cmd("^Insert");

	public static final ButtonTag DELETE = cmd("!Delete");

	public static final ButtonTag REMOVE = cmd("!Remove");

	public static final ButtonTag DESTROY = cmd("!Destroy");

	public static final ButtonTag YES = cmd("^Yes");

	public static final ButtonTag NO = cmd("No");

	public static final ButtonTag OK = cmd("^OK");

	public static final ButtonTag CANCEL = cmd("Cancel");

	public static final ButtonTag BACK = cmd("Back");

	public static final ButtonTag EDIT = cmd("^Edit");

	public static Object i18n(String multiLanguageText, Object... formatArgs) {
		return HtmlWidgets.i18n(multiLanguageText, formatArgs);
	}

	public static <T> Var<T> property(Item item, String property) {
		return HtmlWidgets.property(item, property);
	}

	public static Tag template(String templateFileName, Object... namesAndValues) {
		return HtmlWidgets.template(templateFileName, namesAndValues);
	}

	public static Tag hardcoded(String content) {
		return HtmlWidgets.hardcoded(content);
	}

	public static <T> GridWidget grid(Class<T> type, Object[] items, String sortOrder, int pageSize,
			String... properties) {
		return grid(Models.beanItems(type, items), sortOrder, pageSize, properties);
	}

	public static <T> GridWidget grid(Class<T> type, Collection<T> items, String sortOrder, int pageSize,
			String... properties) {
		return grid(type, items.toArray(), sortOrder, pageSize, properties);
	}

	public static GridWidget grid(Items items, String sortOrder, int pageSize, String... properties) {
		return U.customizable(GridWidget.class, items, sortOrder, pageSize, properties);
	}

	public static PagerWidget pager(int from, int to, Var<Integer> pageNumber) {
		return U.customizable(PagerWidget.class, from, to, pageNumber);
	}

	public static FormWidget form_(FormLayout layout, String[] fieldsNames, String[] fieldsDesc,
			FieldType[] fieldTypes, Object[][] options, Var<?>[] vars, Tag[] buttons) {
		return U.customizable(FormWidget.class, layout, fieldsNames, fieldsDesc, fieldTypes, options, vars, buttons);
	}

	public static FormWidget show(Object bean, String... properties) {
		Item item = Models.item(bean);
		return show(item, properties);
	}

	public static FormWidget show(final Item item, String... properties) {
		return U.customizable(FormWidget.class, false, item, properties);
	}

	public static FormWidget edit(Object bean, String... properties) {
		Item item = Models.item(bean);
		return edit(item, properties);
	}

	public static FormWidget edit(final Item item, String... properties) {
		return U.customizable(FormWidget.class, true, item, properties);
	}

	public static Tag page(boolean devMode, String pageTitle, Object head, Object body) {
		String devOrProd = devMode ? "dev" : "prod";
		return template("bootstrap-page-" + devOrProd + ".html", "title", pageTitle, "head", head, "body", body);
	}

	public static Tag page(boolean devMode, String pageTitle, Object body) {
		return page(devMode, pageTitle, "", body);
	}

	public static Tag media(Object left, Object title, Object body, String targetUrl) {

		Tag mhead = h4(title).class_("media-heading");
		Tag mleft = div(left).class_("media-left");
		Tag mbody = div(mhead, body).class_("media-body");

		String divClass = targetUrl != null ? "media pointer" : "media";
		String js = targetUrl != null ? U.format("goAt('%s');", targetUrl) : null;

		return div(mleft, mbody).class_(divClass).onclick(js);
	}

	public static Tag[] mediaList(List<Object> found) {
		Tag[] items = new Tag[found.size()];
		int ind = 0;

		for (Object result : found) {
			long id = Cls.getId(result);
			String url = urlFor(result);

			Tag left = h6("(ID", NBSP, "=", NBSP, id, ")");
			Object header = span(result.getClass().getSimpleName());
			items[ind++] = media(left, header, small(Cls.beanToStr(result, true)), url);
		}

		return items;
	}

	public static <T> Var<T> sessionVar(String name, T defaultValue) {
		return HttpExchanges.sessionVar(name, defaultValue);
	}

	public static <T> Var<T> localVar(String name, T defaultValue) {
		HttpExchange x = HttpExchanges.getThreadLocalExchange();
		return HttpExchanges.sessionVar(name + ":" + Pages.viewId(x), defaultValue);
	}

	public static Var<Integer> localVar(String name, int defaultValue, int min, int max) {
		HttpExchange x = HttpExchanges.getThreadLocalExchange();
		Var<Integer> var = HttpExchanges.sessionVar(name + ":" + Pages.viewId(x), defaultValue);

		// TODO put the constraints into the variable implementation
		Integer pageN = U.limit(min, var.get(), max);
		var.set(pageN);

		return var;
	}

	public static String urlFor(Object entity) {
		long id = Cls.getId(entity);
		String className = entity.getClass().getSimpleName();
		return U.format("/%s/%s", U.uncapitalized(className), id);
	}

	public static Object highlight(String text) {
		return highlight(text, null);
	}

	public static Object highlight(String text, String regex) {
		return U.customizable(HighlightWidget.class, text, regex);
	}

}
