package org.rapidoid.html;

/*
 * #%L
 * rapidoid-html
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

import org.rapidoid.html.customtag.ColspanTag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.html.tag.OptionTag;
import org.rapidoid.html.tag.SelectTag;
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.html.tag.TextareaTag;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

public class Bootstrap extends HTML {

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

	public static FormTag navbarForm(boolean onLeft, String buttonCaption, String[] fields, String[] placeholders) {
		U.must(fields.length == placeholders.length, "");

		Tag ctrls = div().class_("form-group");

		for (int i = 0; i < fields.length; i++) {
			InputTag inp = input().type("text").class_("form-control").name(fields[i]).placeholder(placeholders[i]);
			ctrls = ctrls.append(inp);
		}

		ButtonTag btn = button(buttonCaption).class_("btn btn-default").type("submit");
		return form(ctrls, btn).class_("navbar-form navbar-" + leftOrRight(onLeft));
	}

	public static Tag navbarPage(boolean fluid, Tag brand, Object[] navbarContent, Object pageContent) {
		Object cont = div(pageContent).class_(containerMaybeFluid(fluid));
		return body(nav_(fluid, false, brand, navbarContent), cont);
	}

	public static FormTag form_(FormLayout layout, String[] fieldsNames, String[] fieldsDesc, FieldType[] fieldTypes,
			Object[][] options, Var<?>[] vars, Tag[] buttons) {
		// FIXME form = form.append(formBtns(layout, buttons));
		return form_(layout, fieldsNames, fieldsDesc, fieldTypes, options, vars).buttons(buttons);
	}

	public static FormTag form_(FormLayout layout, String[] fieldsNames, String[] fieldsDesc, FieldType[] fieldTypes,
			Object[][] options, Var<?>[] vars) {
		U.notNull(fieldsNames, "field names");
		fieldsDesc = U.or(fieldsDesc, fieldsNames);
		U.must(fieldsNames.length == fieldsDesc.length, "");

		FormTag form = form().class_(formLayoutClass(layout)).role("form");

		for (int i = 0; i < fieldsNames.length; i++) {
			form = form.append(field(layout, fieldsNames[i], fieldsDesc[i], fieldTypes[i], options[i], vars[i]));
		}

		return form;
	}

	public static Tag formBtns(FormLayout layout, Object[] buttons) {
		Tag btns;

		if (layout == FormLayout.HORIZONTAL) {
			btns = div().class_("col-sm-offset-4 col-sm-8");
		} else {
			btns = div().class_("form-group");
		}

		for (Object btn : buttons) {
			btns = btns.append(btn);
		}

		if (layout == FormLayout.HORIZONTAL) {
			return div(btns).class_("form-group");
		} else {
			return btns;
		}
	}

	private static String formLayoutClass(FormLayout layout) {
		switch (layout) {
		case VERTICAL:
			return "";
		case HORIZONTAL:
			return "form-horizontal";
		case INLINE:
			return "form-inline";
		default:
			throw U.notExpected();
		}
	}

	public static Tag field(FormLayout layout, String name, String desc, FieldType type, Object[] options, Var<?> var) {
		desc = U.or(desc, name);

		Object inp = input_(name, desc, type, options, var);
		Tag label;
		Object inputWrap;

		if (type == FieldType.RADIOS || type == FieldType.CHECKBOXES) {
			inp = layout == FormLayout.VERTICAL ? div(inp) : span(inp);
		}

		if (type == FieldType.CHECKBOX) {
			label = null;

			inp = div(label(inp, desc)).class_("checkbox");

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).class_("col-sm-offset-4 col-sm-8") : inp;

		} else {
			if (layout != FormLayout.INLINE) {
				label = label(desc);
			} else {
				if (type == FieldType.RADIOS) {
					label = label(desc);
				} else {
					label = null;
				}
			}

			if (layout == FormLayout.HORIZONTAL) {
				label = label.class_("col-sm-4 control-label");
			}

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).class_("col-sm-8") : inp;
		}

		Tag group = label != null ? div(label, inputWrap) : div(inputWrap);
		group = group.class_("form-group");
		return group;
	}

	public static Object input_(String name, String desc, FieldType type, Object[] options, Var<?> var) {

		Tag input;
		switch (type) {

		case TEXT:
			input = input().type("text").class_("form-control").name(name).placeholder(desc).bind(var);
			return input;

		case PASSWORD:
			input = input().type("password").class_("form-control").name(name).placeholder(desc).bind(var);
			return input;

		case EMAIL:
			input = input().type("email").class_("form-control").name(name).placeholder(desc).bind(var);
			return input;

		case TEXTAREA:
			TextareaTag textarea = textarea().class_("form-control").name(name).placeholder(desc).bind(var);
			return textarea;

		case CHECKBOX:
			input = input().type("checkbox").name(name).bind(var);
			return input;

		case DROPDOWN:
			U.notNull(options, "dropdown options");
			SelectTag dropdown = select().name(name).class_("form-control").multiple(false);
			for (Object opt : options) {
				Var<Boolean> optVar = Vars.eq(var, opt);
				OptionTag op = option(opt).value(str(opt)).bind(optVar);
				dropdown = dropdown.append(op);
			}
			return dropdown;

		case MULTI_SELECT:
			U.notNull(options, "multi-select options");
			SelectTag select = select().name(name).class_("form-control").multiple(true);
			for (Object opt : options) {
				Var<Boolean> optVar = Vars.has(var, opt);
				OptionTag op = option(opt).value(str(opt)).bind(optVar);
				select = select.append(op);
			}
			return select;

		case RADIOS:
			U.notNull(options, "radios options");
			Object[] radios = new Object[options.length];
			for (int i = 0; i < options.length; i++) {
				Object opt = options[i];
				Var<Boolean> optVar = Vars.eq(var, opt);
				InputTag radio = input().type("radio").name(name).value(str(opt)).bind(optVar);
				radios[i] = label(radio, opt).class_("radio-inline");
			}
			return radios;

		case CHECKBOXES:
			U.notNull(options, "checkboxes options");
			Object[] checkboxes = new Object[options.length];
			for (int i = 0; i < options.length; i++) {
				Object opt = options[i];
				Var<Boolean> optVar = Vars.has(var, opt);
				InputTag cc = input().type("checkbox").name(name).value(str(opt)).bind(optVar);
				checkboxes[i] = label(cc, opt).class_("radio-checkbox");
			}
			return checkboxes;

		case LABEL:
			input = span(var.get()).class_("form-control display-value");
			return input;

		default:
			throw U.notExpected();
		}
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

		if (primary) {
			cmd = cmd.substring(1);
		}

		ButtonTag btn = primary ? btnPrimary(U.capitalized(cmd)) : btn(U.capitalized(cmd));
		return btn.cmd(cmd, args);
	}

	public static ButtonTag[] cmds(String... commands) {
		ButtonTag[] cmds = new ButtonTag[commands.length];

		for (int i = 0; i < cmds.length; i++) {
			cmds[i] = cmd(commands[i]);
		}

		return cmds;
	}

}
