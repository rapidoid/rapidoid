package org.rapidoid.widget;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.rapidoid.beany.Beany;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.html.customtag.ColspanTag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.html.tag.OptionTag;
import org.rapidoid.html.tag.SelectTag;
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.html.tag.TextareaTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpExchanges;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Models;
import org.rapidoid.pages.Pages;
import org.rapidoid.util.Cls;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

/*
 * #%L
 * rapidoid-pages
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

public abstract class BootstrapWidgets extends HTML {

	public static final ButtonTag SAVE = cmd("^Save");

	public static final ButtonTag ADD = cmd("^Add");

	public static final ButtonTag UPDATE = cmd("^Update");

	public static final ButtonTag INSERT = cmd("^Insert");

	public static final ButtonTag DELETE = cmd("!Delete");

	public static final ButtonTag YES_DELETE = cmd("!YesDelete").content("Yes, delete!");

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

	public static Tag render(String templateFileName, Object... namesAndValues) {
		return HtmlWidgets.render(templateFileName, namesAndValues);
	}

	public static Tag hardcoded(String content) {
		return HtmlWidgets.hardcoded(content);
	}

	public static TableTag table_(Object... contents) {
		return table(contents).class_("table table-striped table-hover");
	}

	public static Tag row(ColspanTag... cols) {
		return div((Object[]) cols).class_("row");
	}

	public static Tag row(Object... contents) {
		return row(col12(contents));
	}

	public static Tag container(Object... contents) {
		return div(contents).class_("container");
	}

	public static Tag containerFluid(Object... contents) {
		return div(contents).class_("container-fluid");
	}

	public static Tag icon(String icon) {
		return span().class_("icon-" + icon);
	}

	public static Tag glyphicon(String glyphicon) {
		return span().class_("glyphicon glyphicon-" + glyphicon);
	}

	public static Tag awesome(String fontAwesomeIcon) {
		return span().class_("fa fa-" + fontAwesomeIcon);
	}

	public static Tag awesomeFw(String fontAwesomeIcon) {
		return span().class_("fa fa-fw fa-" + fontAwesomeIcon);
	}

	public static ATag a_glyph(String glyphicon, Object... contents) {
		return a_void(glyphicon(glyphicon), NBSP, contents);
	}

	public static ATag a_awesome(String fontAwesomeIcon, Object... contents) {
		return a_void(awesome(fontAwesomeIcon), NBSP, contents);
	}

	public static ButtonTag btn(Object... contents) {
		return button(contents).type("button").class_("btn btn-default");
	}

	public static ButtonTag btnPrimary(Object... contents) {
		return button(contents).type("button").class_("btn btn-primary");
	}

	public static ButtonTag btnSuccess(Object... contents) {
		return button(contents).type("button").class_("btn btn-success");
	}

	public static ButtonTag btnInfo(Object... contents) {
		return button(contents).type("button").class_("btn btn-info");
	}

	public static ButtonTag btnWarning(Object... contents) {
		return button(contents).type("button").class_("btn btn-warning");
	}

	public static ButtonTag btnDanger(Object... contents) {
		return button(contents).type("button").class_("btn btn-danger");
	}

	public static Tag nav_(boolean fluid, boolean inverse, Tag brand, Object[] navbarContent) {
		brand = brand.class_("navbar-brand");
		Tag hdr = div(btnCollapse(), brand).class_("navbar-header");

		Tag collapsable = div(navbarContent).class_("collapse navbar-collapse").id("collapsable");

		Tag cnt = div(hdr, collapsable).class_(containerMaybeFluid(fluid));

		String navDefOrInv = inverse ? "navbar-inverse" : "navbar-default";
		return nav(cnt).class_("navbar " + navDefOrInv).role("navigation");
	}

	public static String containerMaybeFluid(boolean fluid) {
		return fluid ? "container-fluid" : "container";
	}

	public static ButtonTag btnCollapse() {
		ButtonTag btn = button(span("Toggle navigation").class_("sr-only"), icon("bar"), icon("bar"), icon("bar"));

		btn = btn.type("button").class_("navbar-toggle collapsed").attr("data-toggle", "collapse")
				.attr("data-target", "#collapsable");

		return btn;
	}

	public static Tag navbarMenu(boolean onLeft, int activeIndex, Object... menuItems) {
		Tag menu = ul().class_("nav navbar-nav navbar-" + leftOrRight(onLeft));

		for (int i = 0; i < menuItems.length; i++) {
			Object item = menuItems[i];

			Tag li = li(item);
			if (i == activeIndex) {
				li = li.class_("active");
			}

			menu = menu.append(li);
		}

		return menu;
	}

	public static Tag navbarDropdown(boolean onLeft, Tag menu, Object... subItems) {
		Tag ul1 = ul_li(subItems).class_("dropdown-menu").role("menu");
		menu = menu.class_("dropdown-toggle").attr("data-toggle", "dropdown");
		Tag drop1 = li(menu, ul1).class_("dropdown");
		return ul(drop1).class_("nav navbar-nav navbar-" + leftOrRight(onLeft));
	}

	public static Tag menuDivider() {
		return li().class_("divider");
	}

	public static FormTag navbarForm(boolean onLeft, Object btnContent, String[] fields, String[] placeholders) {
		U.must(fields.length == placeholders.length, "");

		Tag ctrls = div().class_("form-group");

		for (int i = 0; i < fields.length; i++) {
			InputTag inp = input().type("text").class_("form-control").name(fields[i]).placeholder(placeholders[i]);
			ctrls = ctrls.append(inp);
		}

		ButtonTag btn = button(btnContent).class_("btn btn-default").type("submit");
		return form(ctrls, btn).class_("navbar-form navbar-" + leftOrRight(onLeft));
	}

	public static FormTag navbarSearchForm(String action) {
		return navbarForm(false, awesome("search"), arr("q"), arr("Search")).attr("action", action).attr("method",
				"GET");
	}

	public static Tag navbarPage(boolean fluid, Tag brand, Object[] navbarContent, Object pageContent) {
		Object cont = div(pageContent).class_(containerMaybeFluid(fluid));
		return body(nav_(fluid, false, brand, navbarContent), cont);
	}

	public static Tag modal(Object title, Object content, Object footer) {
		return render("modal.html", "title", title, "content", content, "footer", footer, "cmdCloseModal",
				xClose("closeModal"));
	}

	public static Tag xClose(String cmd) {
		Tag sp1 = span(hardcoded("&times;")).attr("aria-hidden", "true");
		Tag sp2 = span("Close").class_("sr-only");
		return cmd(cmd).class_("close").content(sp1, sp2);
	}

	private static String leftOrRight(boolean onLeft) {
		return onLeft ? "left" : "right";
	}

	public static Tag caret() {
		return span().class_("caret");
	}

	public static Tag jumbotron(Object... contents) {
		return div(contents).class_("jumbotron");
	}

	public static Tag well(Object... contents) {
		return div(contents).class_("well");
	}

	public static ColspanTag col_(int cols, Object... contents) {
		return (ColspanTag) tag(ColspanTag.class, "div", contents).class_("col-md-" + cols);
	}

	public static ColspanTag col1(Object... contents) {
		return col_(1, contents);
	}

	public static ColspanTag col2(Object... contents) {
		return col_(2, contents);
	}

	public static ColspanTag col3(Object... contents) {
		return col_(3, contents);
	}

	public static ColspanTag col4(Object... contents) {
		return col_(4, contents);
	}

	public static ColspanTag col5(Object... contents) {
		return col_(5, contents);
	}

	public static ColspanTag col6(Object... contents) {
		return col_(6, contents);
	}

	public static ColspanTag col7(Object... contents) {
		return col_(7, contents);
	}

	public static ColspanTag col8(Object... contents) {
		return col_(8, contents);
	}

	public static ColspanTag col9(Object... contents) {
		return col_(9, contents);
	}

	public static ColspanTag col10(Object... contents) {
		return col_(10, contents);
	}

	public static ColspanTag col11(Object... contents) {
		return col_(11, contents);
	}

	public static ColspanTag col12(Object... contents) {
		return col_(12, contents);
	}

	public static ButtonTag cmd(String cmd, Object... args) {
		boolean primary = cmd.startsWith("^");
		boolean danger = cmd.startsWith("!");
		boolean warning = cmd.startsWith("?");

		if (primary || danger || warning) {
			cmd = cmd.substring(1);
		}

		String caption = U.capitalized(cmd);

		ButtonTag btn = primary ? btnPrimary(caption) : btn();
		if (primary) {
			btn = btnPrimary(caption);
		} else if (danger) {
			btn = btnDanger(caption);
		} else if (warning) {
			btn = btnWarning(caption);
		} else {
			btn = btn(caption);
		}

		return btn.cmd(cmd, args);
	}

	public static ButtonTag[] cmds(String... commands) {
		ButtonTag[] cmds = new ButtonTag[commands.length];

		for (int i = 0; i < cmds.length; i++) {
			cmds[i] = cmd(commands[i]);
		}

		return cmds;
	}

	public static Tag titleBox(Object... contents) {
		return div(contents).class_("title-box");
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
		return show(Models.item(bean), properties);
	}

	public static FormWidget show(final Item item, String... properties) {
		return show(null, item, properties);
	}

	public static FormWidget show(DataManager dataManager, final Item item, String... properties) {
		return U.customizable(FormWidget.class, dataManager, FormMode.SHOW, item, properties);
	}

	public static FormWidget edit(Object bean, String... properties) {
		return edit(Models.item(bean), properties);
	}

	public static FormWidget edit(final Item item, String... properties) {
		return edit(null, item, properties);
	}

	public static FormWidget edit(DataManager dataManager, final Item item, String... properties) {
		return U.customizable(FormWidget.class, dataManager, FormMode.EDIT, item, properties);
	}

	public static FormWidget create(Object bean, String... properties) {
		return create(Models.item(bean), properties);
	}

	public static FormWidget create(final Item item, String... properties) {
		return create(null, item, properties);
	}

	public static FormWidget create(DataManager dataManager, final Item item, String... properties) {
		return U.customizable(FormWidget.class, dataManager, FormMode.CREATE, item, properties);
	}

	public static Tag page(boolean devMode, String pageTitle, Object head, Object body) {
		String devOrProd = devMode ? "dev" : "prod";
		return render("page-" + devOrProd + ".html", "title", pageTitle, "head", head, "body", body);
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
			long id = Beany.getId(result);
			String url = urlFor(result);

			Tag left = h6("(ID", NBSP, "=", NBSP, id, ")");
			Object header = span(result.getClass().getSimpleName());
			items[ind++] = media(left, header, small(Beany.beanToStr(result, true)), url);
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

	public static boolean isEntity(Object obj) {
		return Cls.kindOf(obj) == TypeKind.OBJECT && !obj.getClass().isEnum() && Beany.hasProperty(obj, "id");
	}

	public static String urlFor(Object entity) {
		long id = Beany.getId(entity);
		String className = entity.getClass().getSimpleName();
		return U.format("/%s/%s", U.uncapitalized(className), id);
	}

	public static Object highlight(String text) {
		return highlight(text, null);
	}

	public static Object highlight(String text, String regex) {
		return U.customizable(HighlightWidget.class, text, regex);
	}

	public static InputTag emailInput(Var<?> var) {
		return input().type("email").class_("form-control").bind(var);
	}

	public static InputTag passwordInput(Var<?> var) {
		return input().type("password").class_("form-control").bind(var);
	}

	public static InputTag textInput(Var<?> var) {
		return input().type("text").class_("form-control").bind(var);
	}

	public static TextareaTag textareaInput(Var<?> var) {
		return textarea().class_("form-control").bind(var);
	}

	public static InputTag checkbox(Var<?> var) {
		return input().type("checkbox").bind(var);
	}

	public static SelectTag dropdown(Collection<?> options, Var<?> var) {
		U.notNull(options, "dropdown options");
		SelectTag dropdown = select().class_("form-control").multiple(false);

		for (Object opt : options) {
			Var<Boolean> optVar = Vars.eq(var, opt);
			OptionTag op = option(opt).value(str(opt)).bind(optVar);
			dropdown = dropdown.append(op);
		}

		return dropdown;
	}

	public static SelectTag multiSelect(Collection<?> options, Var<?> var) {
		U.notNull(options, "multi-select options");
		SelectTag select = select().class_("form-control").multiple(true);

		for (Object opt : options) {
			Var<Boolean> optVar = Vars.has(var, opt);
			OptionTag op = option(opt).value(str(opt)).bind(optVar);
			select = select.append(op);
		}

		return select;
	}

	public static Tag[] radios(String name, Collection<?> options, Var<?> var) {
		U.notNull(options, "radios options");
		Tag[] radios = new Tag[options.size()];

		int i = 0;
		for (Object opt : options) {
			Var<Boolean> optVar = Vars.eq(var, opt);
			InputTag radio = input().type("radio").name(name).value(str(opt)).bind(optVar);
			radios[i] = label(radio, opt).class_("radio-inline");
			i++;
		}
		return radios;
	}

	public static Tag[] radios(Collection<?> options, Var<?> var) {
		return radios(U.rndStr(30), options, var);
	}

	public static Tag[] checkboxes(String name, Collection<?> options, Var<?> var) {
		U.notNull(options, "checkboxes options");
		Tag[] checkboxes = new Tag[options.size()];
		int i = 0;
		for (Object opt : options) {
			Var<Boolean> optVar = Vars.has(var, opt);
			InputTag cc = input().type("checkbox").name(name).value(str(opt)).bind(optVar);
			checkboxes[i] = label(cc, opt).class_("radio-checkbox");
			i++;
		}
		return checkboxes;
	}

	public static Tag[] checkboxes(Collection<?> options, Var<?> var) {
		return checkboxes(U.rndStr(30), options, var);
	}

	public static Object display(Object item) {
		if (item instanceof Var<?>) {
			Var<?> var = (Var<?>) item;
			return display(var.get());
		} else if (item instanceof Iterable) {
			Iterable<?> iter = (Iterable<?>) item;
			return display(iter.iterator());
		} else if (item instanceof Object[]) {
			Object[] arr = (Object[]) item;
			return display(U.arrayIterator(arr));
		}

		return isEntity(item) ? a(item).href(urlFor(item)) : Cls.str(item);
	}

	private static Object display(Iterator<?> it) {
		Tag icon = awesome("circle-o");
		Tag wrap = div();

		while (it.hasNext()) {
			Object item = (Object) it.next();
			wrap = wrap.append(div(icon, " ", display(item)).class_("value-line"));
		}

		if (wrap.isEmpty()) {
			return span(nothing()).class_("value-line");
		}

		return wrap;
	}

	public static Tag nothing() {
		return span(awesome("ban"), " N/A").class_("nothing");
	}

}
