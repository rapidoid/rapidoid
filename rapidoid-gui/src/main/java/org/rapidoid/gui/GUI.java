package org.rapidoid.gui;

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
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.*;
import org.rapidoid.gui.base.AbstractWidget;
import org.rapidoid.gui.reqinfo.IReqInfo;
import org.rapidoid.gui.reqinfo.ReqInfo;
import org.rapidoid.gui.var.ArrayContainerVar;
import org.rapidoid.gui.var.CollectionContainerVar;
import org.rapidoid.gui.var.EqualityVar;
import org.rapidoid.gui.var.LocalVar;
import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagWidget;
import org.rapidoid.html.customtag.ColspanTag;
import org.rapidoid.html.tag.*;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.lambda.Calc;
import org.rapidoid.lambda.ToMap;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Models;
import org.rapidoid.model.Property;
import org.rapidoid.render.Templates;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.var.Var;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public abstract class GUI extends HTML {

	public static final Tag NOTHING = span(fa("ban"), " N/A").class_("nothing");

	public static final Tag N_A = NOTHING;

	public static final Btn SAVE = cmd("^Save");

	public static final Btn ADD = cmd("^Add");

	public static final Btn UPDATE = cmd("^Update");

	public static final Btn INSERT = cmd("^Insert");

	public static final Btn DELETE = cmd("!Delete");

	public static final Btn YES_DELETE = cmd("!YesDelete").contents("Yes, delete!");

	public static final Btn REMOVE = cmd("!Remove");

	public static final Btn DESTROY = cmd("!Destroy");

	public static final Btn YES = cmd("^Yes");

	public static final Btn NO = cmd("No");

	public static final Btn OK = cmd("^OK");

	public static final Btn REFRESH = cmd("refresh");

	public static final Btn CANCEL = navigate("Cancel");

	public static final Btn BACK = navigate("Back");

	public static final Btn EDIT = cmd("^Edit");

	public static final Tag WARN = FA.WARNING.style("color: #955; padding-bottom: 23px;");

	private static final AtomicLong ID_GEN = new AtomicLong();

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

	public static Tag columns(Object left, Object right) {
		return row(col6(left), col6(right));
	}

	public static Tag columns(Object left, Object middle, Object right) {
		return row(col4(left), col4(middle), col4(right));
	}

	public static Tag columns(Object left, Object middleLeft, Object middleRight, Object right) {
		return row(col3(left), col3(middleLeft), col3(middleRight), col3(right));
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

	public static Tag fa(String fontAwesomeIcon) {
		return i().class_("fa fa-" + fontAwesomeIcon);
	}

	public static Tag awesomeFw(String fontAwesomeIcon) {
		return span().class_("fa fa-fw fa-" + fontAwesomeIcon);
	}

	public static ATag a_glyph(String glyphicon, Object... contents) {
		return a_void(glyphicon(glyphicon), NBSP, contents);
	}

	public static ATag a_awesome(String fontAwesomeIcon, Object... contents) {
		return a_void(fa(fontAwesomeIcon), NBSP, contents);
	}

	public static Btn btn(Object... contents) {
		Btn btn = Cls.customizable(Btn.class).contents(contents);

		for (Object content : contents) {
			if (content instanceof String) {
				String cmd = (String) content;
				btn = btn.command(cmd);
				break;
			}
		}

		return btn;
	}

	public static BtnMenu btnMenu() {
		return new BtnMenu();
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
		return navbarForm(false, fa("search"), arr("q"), arr("Search")).attr("action", action).attr("method",
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

	public static Btn cmd(String cmd, Object... args) {
		boolean primary = cmd.startsWith("^");
		boolean danger = cmd.startsWith("!");
		boolean warning = cmd.startsWith("?");

		if (primary || danger || warning) {
			cmd = cmd.substring(1);
		}

		String caption = Str.capitalized(cmd);

		Btn btn = btn(caption);
		if (primary) {
			btn = btn.primary();
		} else if (danger) {
			btn = btn.danger();
		} else if (warning) {
			btn = btn.warning();
		}

		return btn.command(cmd, args);
	}

	public static Btn navigate(String cmd) {
		String caption = Str.capitalized(cmd);
		return btn(caption).go(cmd);
	}

	public static Btn[] cmds(String... commands) {
		Btn[] cmds = new Btn[commands.length];

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

	public static Panel panel(Object... contents) {
		return Cls.customizable(Panel.class, new Object[]{AnyObj.flat(contents)});
	}

	public static HtmlPage page(Object... contents) {
		return Cls.customizable(HtmlPage.class, new Object[]{AnyObj.flat(contents)});
	}

	public static <T> Grid grid(Class<T> type, Object[] items, String sortOrder, int pageSize, String... properties) {
		return grid(Models.beanItems(type, items), sortOrder, pageSize, properties);
	}

	public static <T> Grid grid(Class<T> type, Iterable<T> items, String sortOrder, int pageSize, String... properties) {
		return grid(type, U.array(items), sortOrder, pageSize, properties);
	}

	public static <T> Grid grid(Iterable<T> items, String... properties) {
		Iterator<T> it = items.iterator();
		Class<? extends Object> type = it.hasNext() ? it.next().getClass() : Object.class;
		return grid(type, U.array(items), null, -1, properties);
	}

	public static Grid grid(Items items, String sortOrder, int pageSize, String... properties) {
		return Cls.customizable(Grid.class).items(items).orderBy(sortOrder).pageSize(pageSize).columns(properties);
	}

	public static <T> KVGrid grid(Map<?, ?> map) {
		return Cls.customizable(KVGrid.class).map(map);
	}

	public static <T> List<Object> grids(Map<String, Map<?, ?>> maps) {
		List<Object> grids = U.list();

		for (Map.Entry<String, Map<?, ?>> entry : maps.entrySet()) {
			String key = entry.getKey();
			Map<?, ?> map = entry.getValue();

			grids.add(h4(b(key, ":")));
			grids.add(grid(map));
		}

		return grids;
	}

	public static Pager pager(String param) {
		return Cls.customizable(Pager.class, param);
	}

	public static Form show(Object bean, String... properties) {
		return show(Models.item(bean), properties);
	}

	public static Form show(final Item item, String... properties) {
		// FIXME make fluent and customizable!
		return new Form(FormMode.SHOW, item, properties);
	}

	public static Form edit(Object bean, String... properties) {
		return edit(Models.item(bean), properties);
	}

	public static Form edit(final Item item, String... properties) {
		// FIXME make fluent and customizable!
		return new Form(FormMode.EDIT, item, properties);
	}

	public static Form create(Object bean, String... properties) {
		return create(Models.item(bean), properties);
	}

	public static Form create(final Item item, String... properties) {
		// FIXME make fluent and customizable!
		return new Form(FormMode.CREATE, item, properties);
	}

	public static Field field(Item item, Property prop) {
		return new Field(item, prop);
	}

	public static <T> Property prop(String name, Calc<T> calc) {
		return Models.property(name, calc);
	}

	public static Item item(Object value) {
		return Models.item(value);
	}

	public static Tag media(Object left, Object title, Object body, String targetUrl) {

		Tag mhead = h4(title).class_("media-heading");
		Tag mleft = div(left).class_("media-left");
		Tag mbody = div(mhead, body).class_("media-body");

		String divClass = targetUrl != null ? "media pointer" : "media";
		String js = targetUrl != null ? U.frmt("goAt('%s');", targetUrl) : null;

		return div(mleft, mbody).class_(divClass).onclick(js);
	}

	public static Tag[] mediaList(List<Object> found) {
		Tag[] items = new Tag[found.size()];
		int ind = 0;

		for (Object result : found) {
			Object id = Beany.getId(result);
			String uri = uriFor(result);

			Tag left = h6("(ID", NBSP, "=", NBSP, id, ")");
			Object header = span(result.getClass().getSimpleName());
			items[ind++] = media(left, header, small(Beany.beanToNiceText(result, true)), uri);
		}

		return items;
	}

	public static <T extends Serializable> Var<T> providedVar(String name, T defaultValue) {
		return local(name, defaultValue);
	}

	public static <T extends Serializable> Var<T> var(String name, T defaultValue) {
		return providedVar(name, defaultValue);
	}

	public static <T extends Serializable> Var<T> var(String name) {
		return var(name, null);
	}

	public static <T extends Serializable> Var<T> local(String name, T defaultValue) {
		return new LocalVar<T>(name, defaultValue, req().isGetReq());
	}

	public static Var<Integer> local(String name, int defaultValue, int min, int max) {
		Var<Integer> var = local(name, defaultValue);

		// TODO put the constraints into the variable implementation
		Integer pageN = U.bounds(min, var.get(), max);
		var.set(pageN);

		return var;
	}

	public static Object highlight(String text) {
		return highlight(text, null);
	}

	public static Object highlight(String text, String regex) {
		return Cls.customizable(Highlight.class, text, regex);
	}

	public static InputTag email(Var<?> var) {
		return input().type("email").class_("form-control").var(var);
	}

	public static InputTag email(String var) {
		return email(var, "");
	}

	public static InputTag email(String var, String defaultValue) {
		return email(providedVar(var, defaultValue));
	}

	public static InputTag password(Var<?> var) {
		return input().type("password").class_("form-control").var(var);
	}

	public static InputTag password(String var) {
		return password(var, "");
	}

	public static InputTag password(String var, String defaultValue) {
		return password(providedVar(var, defaultValue));
	}

	public static InputTag txt(Var<?> var) {
		return input().type("text").class_("form-control").var(var);
	}

	public static InputTag txt(String var) {
		return txt(var, "");
	}

	public static InputTag txt(String var, String defaultValue) {
		return txt(providedVar(var, defaultValue));
	}

	public static TextareaTag txtbig(Var<?> var) {
		return textarea().class_("form-control").var(var);
	}

	public static TextareaTag txtbig(String var) {
		return txtbig(var, "");
	}

	public static TextareaTag txtbig(String var, String defaultValue) {
		return txtbig(providedVar(var, defaultValue));
	}

	public static InputTag checkbox(Var<?> var) {
		return input().type("checkbox").var(var);
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
			Var<Boolean> optVar = varEq(var, opt);
			OptionTag op = option(opt).value(str(opt)).var(optVar);
			dropdown = dropdown.append(op);
		}

		return dropdown;
	}

	public static SelectTag dropdown(Collection<?> options, String var) {
		return dropdown(options, var, null);
	}

	public static SelectTag dropdown(Collection<?> options, String var, Object defaultValue) {
		return dropdown(options, providedVar(var, serializable(defaultValue)));
	}

	public static SelectTag multiSelect(Collection<?> options, Var<?> var) {
		U.notNull(options, "multi-select options");
		SelectTag select = select().class_("form-control").multiple(true);

		for (Object opt : options) {
			Var<Boolean> optVar = varHas(var, opt);
			OptionTag op = option(opt).value(str(opt)).var(optVar);
			select = select.append(op);
		}

		return select;
	}

	public static SelectTag multiSelect(Collection<?> options, String var) {
		return multiSelect(options, var, U.list());
	}

	public static SelectTag multiSelect(Collection<?> options, String var, Collection<?> defaultValue) {
		return multiSelect(options, providedVar(var, serializable(defaultValue)));
	}

	public static Tag[] radios(String name, Collection<?> options, Var<?> var) {
		U.notNull(options, "radios options");
		Tag[] radios = new Tag[options.size()];

		int i = 0;
		for (Object opt : options) {
			Var<Boolean> optVar = varEq(var, opt);
			InputTag radio = input().type("radio").name(name).value(str(opt)).var(optVar);
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
		return radios(options, providedVar(var, serializable(defaultValue)));
	}

	public static Tag[] checkboxes(String name, Collection<?> options, Var<?> var) {
		U.notNull(options, "checkboxes options");
		Tag[] checkboxes = new Tag[options.size()];

		int i = 0;
		for (Object opt : options) {
			Var<Boolean> optVar = varHas(var, opt);
			InputTag cc = input().type("checkbox").name(name).value(str(opt)).var(optVar);
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
		return checkboxes(options, providedVar(var, serializable(defaultValue)));
	}

	@SuppressWarnings("unchecked")
	private static <T extends Serializable> T serializable(Object value) {
		if (value == null || value instanceof Serializable) {
			return (T) value;
		} else {
			throw U.rte("Not serializable: " + value);
		}
	}

	public static Object display(Object item) {
		try {
			return _display(item);
		} catch (Exception e) {
			return N_A;
		}
	}

	@SuppressWarnings("unchecked")
	private static Object _display(Object item) {
		if (item instanceof Tag) {
			return item;
		}

		if (item == null) return N_A;

		if (item instanceof Var<?>) {
			Var<?> var = (Var<?>) item;
			return display(var.get());

		} else if (item instanceof Iterable) {
			Iterable<?> iter = (Iterable<?>) item;
			return display(iter.iterator());

		} else if (item instanceof Object[]) {
			Object[] arr = (Object[]) item;
			return display(U.iterator(arr));

		} else if (item instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) item;
			return grid(map);

		} else if (item instanceof ToMap<?, ?>) {
			Map<?, ?> map = ((ToMap<?, ?>) item).toMap();
			return grid(map);

		} else if (item instanceof TagWidget) {
			TagWidget<Object> widget = (TagWidget<Object>) item;
			return widget.render(null);
		}

		if (isEntity(item)) {
			return a(escape(item.toString())).href(uriFor(item) + "/view");
		}

		if (isBean(item)) {
			return GUI.show(item);
		}

		String str = Cls.str(item);
		Tag tag = hardcoded(escape(str));

		return (str.contains("{") || str.contains("}")) ? span(tag).is("ng-non-bindable", true) : tag;
	}

	private static boolean isBean(Object obj) {
		return Cls.isBean(obj)
				&& !(obj instanceof Tag)
				&& !(obj instanceof AbstractWidget);
	}

	private static Object display(Iterator<?> it) {
		Tag wrap = div();

		while (it.hasNext()) {
			Object item = it.next();
			wrap = wrap.append(div(FA.CIRCLE_O, NBSP, display(item)).class_("value-line"));
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

	public static Layout layout(Object... contents) {
		return Cls.customizable(Layout.class).contents(contents);
	}

	public static Layout layout(Iterable<?> contents) {
		return layout(U.array(contents));
	}

	public static Snippet snippet(String code) {
		return new Snippet(code);
	}

	public static VStream stream(Object ngTemplate) {
		return Cls.customizable(VStream.class).template(ngTemplate);
	}

	public static Object values(Object... values) {
		List<Object> list = U.list();

		for (Object value : values) {
			if (Msc.isArray(value) && !hasGUIElements(value)) {
				value = U.str(value);
			}
			if (value == null || value instanceof Iterable<?>) {
				value = U.str(value);
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

	public static Card card(Object... contents) {
		return Cls.customizable(Card.class).contents(contents);
	}

	@SuppressWarnings("unchecked")
	public static Var<Boolean> varHas(Var<?> container, Object item) {
		Object arrOrColl = container.get();

		Object itemId = Beany.hasProperty(item, "id") ? Beany.getIdIfExists(item) : String.valueOf(item);
		String varName = container.name() + "[" + itemId + "]";

		if (arrOrColl instanceof Collection) {
			return new CollectionContainerVar(varName, (Var<Collection<Object>>) container, item, req().isGetReq());
		} else {
			return new ArrayContainerVar(varName, (Var<Object>) container, item, req().isGetReq());
		}
	}

	@SuppressWarnings("unchecked")
	public static Var<Boolean> varEq(Var<?> var, Object item) {
		Object itemId = Beany.hasProperty(item, "id") ? Beany.getIdIfExists(item) : String.valueOf(item);
		String varName = var.name() + "[" + itemId + "]";
		return new EqualityVar(varName, (Var<Object>) var, item, req().isGetReq());
	}

	public static Object i18n(String multiLanguageText, Object... formatArgs) {
		return new I18N(multiLanguageText, formatArgs);
	}

	public static Btn xClose(String cmd) {
		Tag sp1 = span(hardcoded("&times;")).attr("aria-hidden", "true");
		Tag sp2 = span("Close").class_("sr-only");
		return cmd(cmd).class_("close").contents(sp1, sp2);
	}

	public static Debug debug() {
		return Cls.customizable(Debug.class);
	}

	public static Object box(Object... contents) {
		return span(contents).class_("box");
	}

	public static String getCommand() {
		IReqInfo req = req();
		return !req.isGetReq() ? (String) req.posted().get("_cmd") : null;
	}

	public static Tag verb(HttpVerb verb) {
		Tag tag = span(verb);
		switch (verb) {
			case GET:
				tag = tag.class_("label label-success");
				break;

			case POST:
				tag = tag.class_("label label-primary");
				break;

			case PUT:
				tag = tag.class_("label label-warning");
				break;

			case DELETE:
				tag = tag.class_("label label-danger");
				break;

			default:
				tag = tag.class_("label label-default");
				break;
		}
		return tag;
	}

	public static Tag trTd(Object... cells) {
		Tag row = tr();

		for (Object cell : cells) {
			row = row.append(td(cell));
		}

		return row;
	}

	public static long newId() {
		return ID_GEN.incrementAndGet();
	}

	public static Object dygraph(String uri, TimeSeries ts, String divClass) {
		List<Object> points = U.list();

		NavigableMap<Long, Double> values = ts.overview();

		for (Map.Entry<Long, Double> e : values.entrySet()) {
			points.add(U.map("date", e.getKey(), "values", e.getValue()));
		}

		Map<String, ?> model = U.map("points", points, "names", U.list(ts.title()), "title", ts.title(),
				"id", newId(), "class", divClass, "uri", Str.triml(uri, "/"));

		Tag graph = hardcoded(Templates.fromFile("dygraphs.html").render(model));

		return div(graph);
	}

	public static Object dygraph(String uri, TimeSeries ts) {
		return dygraph(uri, ts, "rapidoid-dygraph");
	}

	public static String uriFor(Object target) {
		if (!isEntity(target)) {
			return "";
		}

		return uriFor(typeUri(target.getClass()), target);
	}

	public static String uriFor(String baseUri, Object target) {
		if (!isEntity(target)) {
			return "";
		}

		Object id = getIdentifier(target);
		return id != null ? Msc.uri(baseUri, id + "") : "";
	}

	public static String typeUri(Class<?> entityType) {
		return typeUri(entityType.getSimpleName());
	}

	public static String typeUri(String entityType) {
		String contextPath = req().contextPath();
		String typeUri = English.plural(Str.uncapitalized(entityType)).toLowerCase();
		return Msc.uri(contextPath, typeUri);
	}

	private static IReqInfo req() {
		return ReqInfo.get();
	}

	public static void markValidationErrors() {
		req().attrs().put("has-validation-errors", true);
	}

	public static boolean hasValidationErrors() {
		return Boolean.TRUE.equals(req().attrs().get("has-validation-errors"));
	}

	private static boolean isEntity(Object item) {
		return Msc.hasRapidoidJPA() && GuiJpaUtil.isEntity(item);
	}

	private static Object getIdentifier(Object bean) {
		return Msc.hasRapidoidJPA() ? GuiJpaUtil.getIdentifier(bean) : null;
	}

	public static String uri(String path, Map<String, String> query) {
		StringBuilder sb = new StringBuilder();

		sb.append(path);

		boolean first = true;
		for (Map.Entry<String, String> e : query.entrySet()) {
			if (first) {
				sb.append("?");
				first = false;
			} else {
				sb.append("&");
			}

			sb.append(Msc.urlEncode(e.getKey()));
			sb.append("=");
			sb.append(Msc.urlEncode(e.getValue()));
		}

		return sb.toString();
	}

	public static Tag copy(Object... content) {
		return div(content).class_("copy-snippet");
	}

}
