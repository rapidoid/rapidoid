package org.rapidoid.widget;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.config.Conf;
import org.rapidoid.html.FieldType;
import org.rapidoid.html.FormLayout;
import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagWidget;
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
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.security.DataPermissions;
import org.rapidoid.util.AppCtx;
import org.rapidoid.util.Arr;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Rnd;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;
import org.rapidoid.widget.impl.SessionVar;

/*
 * #%L
 * rapidoid-widget
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class BootstrapWidgets extends HTML {

	public static final Tag NOTHING = span(awesome("ban"), " N/A").class_("nothing");

	public static final DebugWidget DEBUG = Cls.customizable(DebugWidget.class);

	public static final ButtonWidget SAVE = cmd("^Save");

	public static final ButtonWidget ADD = cmd("^Add");

	public static final ButtonWidget UPDATE = cmd("^Update");

	public static final ButtonWidget INSERT = cmd("^Insert");

	public static final ButtonWidget DELETE = cmd("!Delete");

	public static final ButtonWidget YES_DELETE = cmd("!YesDelete").contents("Yes, delete!");

	public static final ButtonWidget REMOVE = cmd("!Remove");

	public static final ButtonWidget DESTROY = cmd("!Destroy");

	public static final ButtonWidget YES = cmd("^Yes");

	public static final ButtonWidget NO = cmd("No");

	public static final ButtonWidget OK = cmd("^OK");

	public static final ButtonWidget CANCEL = navigate("Cancel");

	public static final ButtonWidget BACK = navigate("Back");

	public static final ButtonWidget EDIT = cmd("^Edit");

	public static TableTag table_(Object... contents) {
		return table(contents).class_("table table-striped table-hover");
	}

	public static Tag row(ColspanTag... cols) {
		return div((Object[]) cols).class_("row");
	}

	public static Tag rowSeparated(ColspanTag... cols) {
		return row(cols).class_("row row-separated");
	}

	public static Tag row(Object... contents) {
		return row(col12(contents));
	}

	public static Tag rowSeparated(Object... contents) {
		return row(contents).class_("row row-separated");
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

	public static ButtonWidget btn(Object... contents) {
		return Cls.customizable(ButtonWidget.class).contents(contents);
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
			InputTag inp = input().type("text").class_("form-control").name(fields[i]).id("navbar-" + fields[i])
					.placeholder(placeholders[i]);
			ctrls = ctrls.append(inp);
		}

		ButtonTag btn = button(btnContent).class_("btn btn-default").type("submit");
		return form(ctrls, btn).class_("navbar-form navbar-" + leftOrRight(onLeft));
	}

	public static FormTag navbarSearchForm(String action) {
		return navbarForm(false, awesome("search"), arr("q"), arr("Search")).attr("action", action).attr("method",
				"GET");
	}

	public static Object navbarPage(boolean fluid, Tag brand, Object[] navbarContent, Object pageContent) {
		Object cont = div(pageContent).class_(containerMaybeFluid(fluid));
		Tag navbar = nav_(fluid, false, brand, navbarContent);
		return arr(navbar, cont);
	}

	public static Object cleanPage(boolean fluid, Object pageContent) {
		return div(pageContent).class_(containerMaybeFluid(fluid));
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

	public static Tag mid2(Object... contents) {
		return row(col5(), col2(contents), col5());
	}

	public static Tag mid4(Object... contents) {
		return row(col4(), col4(contents), col4());
	}

	public static Tag mid6(Object... contents) {
		return row(col3(), col6(contents), col3());
	}

	public static Tag mid8(Object... contents) {
		return row(col2(), col8(contents), col2());
	}

	public static Tag mid10(Object... contents) {
		return row(col1(), col10(contents), col1());
	}

	public static ButtonWidget cmd(String cmd, Object... args) {
		boolean primary = cmd.startsWith("^");
		boolean danger = cmd.startsWith("!");
		boolean warning = cmd.startsWith("?");

		if (primary || danger || warning) {
			cmd = cmd.substring(1);
		}

		String caption = U.capitalized(cmd);

		ButtonWidget btn = btn(caption);
		if (primary) {
			btn = btn.primary();
		} else if (danger) {
			btn = btn.danger();
		} else if (warning) {
			btn = btn.warning();
		}

		return btn.command(cmd, args);
	}

	public static ButtonWidget navigate(String cmd) {
		String caption = U.capitalized(cmd);
		return btn(caption).linkTo(cmd);
	}

	public static ButtonWidget[] cmds(String... commands) {
		ButtonWidget[] cmds = new ButtonWidget[commands.length];

		for (int i = 0; i < cmds.length; i++) {
			cmds[i] = cmd(commands[i]);
		}

		return cmds;
	}

	public static Tag titleBox(Object... contents) {
		return div(contents).class_("title-box");
	}

	public static Tag right(Object... contents) {
		return span(contents).class_("pull-right");
	}

	public static PanelWidget panel(Object... contents) {
		return Cls.customizable(PanelWidget.class, new Object[] { UTILS.flat(contents) });
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
		return Cls.customizable(GridWidget.class, items, sortOrder, pageSize, properties);
	}

	public static PagerWidget pager(int from, int to, Var<Integer> pageNumber) {
		return Cls.customizable(PagerWidget.class, from, to, pageNumber);
	}

	public static FormWidget form_(FormLayout layout, String[] fieldsNames, String[] fieldsDesc,
			FieldType[] fieldTypes, Object[][] options, Var<?>[] vars, ButtonWidget[] buttons) {
		return Cls.customizable(FormWidget.class, layout, fieldsNames, fieldsDesc, fieldTypes, options, vars, buttons);
	}

	public static FormWidget show(Object bean, String... properties) {
		return show(Models.item(bean), properties);
	}

	public static FormWidget show(final Item item, String... properties) {
		return show(null, item, properties);
	}

	public static FormWidget show(DataManager dataManager, final Item item, String... properties) {
		return Cls.customizable(FormWidget.class, dataManager, FormMode.SHOW, item, properties);
	}

	public static FormWidget edit(Object bean, String... properties) {
		return edit(Models.item(bean), properties);
	}

	public static FormWidget edit(final Item item, String... properties) {
		return edit(null, item, properties);
	}

	public static FormWidget edit(DataManager dataManager, final Item item, String... properties) {
		return Cls.customizable(FormWidget.class, dataManager, FormMode.EDIT, item, properties);
	}

	public static FormWidget create(Object bean, String... properties) {
		return create(Models.item(bean), properties);
	}

	public static FormWidget create(final Item item, String... properties) {
		return create(null, item, properties);
	}

	public static FormWidget create(DataManager dataManager, final Item item, String... properties) {
		return Cls.customizable(FormWidget.class, dataManager, FormMode.CREATE, item, properties);
	}

	public static FormFieldWidget field(DataManager dataManager, FormMode mode, FormLayout layout, Property prop,
			String name, String desc, FieldType type, Collection<?> options, boolean required, Var<?> var,
			DataPermissions permissions) {
		return new FormFieldWidget(dataManager, mode, layout, prop, name, desc, type, options, required, var,
				permissions);
	}

	public static FormFieldWidget field(DataManager dataManager, FormMode mode, FormLayout layout, Item item,
			Property prop) {
		return new FormFieldWidget(dataManager, mode, layout, item, prop);
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

	public static <T> Var<T> providedVar(String name, T defaultValue) {
		return session(name, defaultValue);
	}

	public static <T> Var<T> var(String name, T defaultValue) {
		return providedVar(name, defaultValue);
	}

	public static <T> Var<T> var(String name) {
		return var(name, null);
	}

	public static HttpExchange httpExchange() {
		return AppCtx.exchange();
	}

	public static <T> Var<T> session(String name, T defaultValue) {
		return new SessionVar<T>(httpExchange(), name, defaultValue);
	}

	public static <T> Var<T> session(String name) {
		return session(name, null);
	}

	public static <T> Var<T> local(String name, T defaultValue) {
		return session(name + ":" + viewId(httpExchange()), defaultValue);
	}

	public static Var<Integer> local(String name, int defaultValue, int min, int max) {
		Var<Integer> var = session(name + ":" + viewId(httpExchange()), defaultValue);

		// TODO put the constraints into the variable implementation
		Integer pageN = U.limited(min, var.get(), max);
		var.set(pageN);

		return var;
	}

	public static String viewId(HttpExchange x) {
		return x.uri();
	}

	public static boolean isEntity(Object obj) {
		return Cls.kindOf(obj) == TypeKind.OBJECT && !obj.getClass().isEnum() && Beany.hasProperty(obj, "id");
	}

	public static String urlFor(Object entity) {

		Long id = Beany.getIdIfExists(entity);
		if (id != null) {
			String className = Cls.entityName(entity);
			String frm = Conf.is("generate") ? "%s%s.html" : "/%s/%s";
			return U.format(frm, U.uncapitalized(className), id);
		} else {
			return "";
		}
	}

	public static Object highlight(String text) {
		return highlight(text, null);
	}

	public static Object highlight(String text, String regex) {
		return Cls.customizable(HighlightWidget.class, text, regex);
	}

	public static InputTag email(Var<?> var) {
		return input().type("email").class_("form-control").bind(var);
	}

	public static InputTag email(String var) {
		return email(var, "");
	}

	public static InputTag email(String var, String defaultValue) {
		return email(providedVar(var, defaultValue));
	}

	public static InputTag password(Var<?> var) {
		return input().type("password").class_("form-control").bind(var);
	}

	public static InputTag password(String var) {
		return password(var, "");
	}

	public static InputTag password(String var, String defaultValue) {
		return password(providedVar(var, defaultValue));
	}

	public static InputTag txt(Var<?> var) {
		return input().type("text").class_("form-control").bind(var);
	}

	public static InputTag txt(String var) {
		return txt(var, "");
	}

	public static InputTag txt(String var, String defaultValue) {
		return txt(providedVar(var, defaultValue));
	}

	public static TextareaTag txtbig(Var<?> var) {
		return textarea().class_("form-control").bind(var);
	}

	public static TextareaTag txtbig(String var) {
		return txtbig(var, "");
	}

	public static TextareaTag txtbig(String var, String defaultValue) {
		return txtbig(providedVar(var, defaultValue));
	}

	public static InputTag checkbox(Var<?> var) {
		return input().type("checkbox").bind(var);
	}

	public static InputTag checkbox(String var) {
		return checkbox(var, false);
	}

	public static InputTag checkbox(String var, boolean defaultValue) {
		return checkbox(providedVar(var, defaultValue));
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

	public static SelectTag dropdown(Collection<?> options, String var) {
		return dropdown(options, var, null);
	}

	public static SelectTag dropdown(Collection<?> options, String var, Object defaultValue) {
		return dropdown(options, providedVar(var, defaultValue));
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

	public static SelectTag multiSelect(Collection<?> options, String var) {
		return multiSelect(options, var, U.list());
	}

	public static SelectTag multiSelect(Collection<?> options, String var, Collection<?> defaultValue) {
		return multiSelect(options, providedVar(var, defaultValue));
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
		return radios(Rnd.rndStr(30), options, var);
	}

	public static Tag[] radios(Collection<?> options, String var) {
		return radios(options, var, null);
	}

	public static Tag[] radios(Collection<?> options, String var, Object defaultValue) {
		return radios(options, providedVar(var, defaultValue));
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
		return checkboxes(Rnd.rndStr(30), options, var);
	}

	public static Tag[] checkboxes(Collection<?> options, String var) {
		return checkboxes(options, var, U.list());
	}

	public static Tag[] checkboxes(Collection<?> options, String var, Collection<?> defaultValue) {
		return checkboxes(options, providedVar(var, defaultValue));
	}

	@SuppressWarnings("unchecked")
	public static Object display(Object item) {
		if (item instanceof Var<?>) {
			Var<?> var = (Var<?>) item;
			return display(var.get());
		} else if (item instanceof Iterable) {
			Iterable<?> iter = (Iterable<?>) item;
			return display(iter.iterator());
		} else if (item instanceof Object[]) {
			Object[] arr = (Object[]) item;
			return display(U.iterator(arr));
		} else if (item instanceof TagWidget) {
			TagWidget<Object> widget = (TagWidget<Object>) item;
			return widget.render(AppCtx.exchange());
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
			return span(NOTHING).class_("value-line");
		}

		return wrap;
	}

	public static Object[] spaced(Object... contents) {
		Object[] arr = new Object[2 * contents.length - 1];

		int index = 0;
		for (int i = 0; i < contents.length; i++) {
			if (i > 0) {
				arr[index++] = NBSP;
			}
			arr[index++] = contents[i];
		}

		return arr;
	}

	public static Tag inline(Object... contents) {
		Tag ctrls = div(spaced(contents)).class_("form-group");
		return form(ctrls).class_("form-inline");
	}

	public static LayoutWidget layout(Object... contents) {
		return Cls.customizable(LayoutWidget.class).contents(contents);
	}

	public static LayoutWidget layout(Iterable<?> contents) {
		return layout(U.array(contents));
	}

	public static SnippetWidget snippet(String code) {
		return new SnippetWidget(code);
	}

	public static StreamWidget stream(Object ngTemplate) {
		return Cls.customizable(StreamWidget.class).template(ngTemplate);
	}

	public static Object values(Object... values) {
		List<Object> list = U.list();

		for (Object value : values) {
			if (Arr.isArray(value) && !hasGUIElements(value)) {
				value = U.readable(value);
			}
			if (value == null || value instanceof Iterable<?>) {
				value = U.readable(value);
			}
			list.add(row(value));
		}

		return layout(list);
	}

	private static boolean hasGUIElements(Object value) {
		if (value instanceof Object[]) {
			Object[] arr = (Object[]) value;
			for (Object val : arr) {
				if (val instanceof TagWidget<?> || val instanceof Tag) {
					return true;
				}
			}
		}
		return false;
	}

}
