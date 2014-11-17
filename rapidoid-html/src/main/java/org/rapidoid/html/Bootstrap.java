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

import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.DivTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.InputTag;
import org.rapidoid.html.tag.LabelTag;
import org.rapidoid.html.tag.LiTag;
import org.rapidoid.html.tag.NavTag;
import org.rapidoid.html.tag.OptionTag;
import org.rapidoid.html.tag.SelectTag;
import org.rapidoid.html.tag.SpanTag;
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.html.tag.TextareaTag;
import org.rapidoid.html.tag.UlTag;
import org.rapidoid.reactive.Var;
import org.rapidoid.reactive.Vars;
import org.rapidoid.util.U;

public class Bootstrap extends HTML {

	public static TableTag table_(Object... contents) {
		return table(contents).class_("table table-striped table-hover");
	}

	public static DivTag row(Object... contents) {
		return div(contents).class_("row");
	}

	public static DivTag rowFull(Object... contents) {
		return div(col12(contents)).class_("row");
	}

	public static DivTag container(Object... contents) {
		return div(contents).class_("container");
	}

	public static DivTag containerFluid(Object... contents) {
		return div(contents).class_("container-fluid");
	}

	public static SpanTag icon(String icon) {
		return span().class_("icon-" + icon);
	}

	public static SpanTag glyphicon(String glyphicon) {
		return span().class_("glyphicon glyphicon-" + glyphicon);
	}

	public static SpanTag awesome(String fontAwesomeIcon) {
		return span().class_("fa fa-" + fontAwesomeIcon);
	}

	public static ATag a_glyph(String glyphicon, Object... contents) {
		return a(glyphicon(glyphicon), NBSP, contents);
	}

	public static ATag a_awesome(String fontAwesomeIcon, Object... contents) {
		return a(awesome(fontAwesomeIcon), NBSP, contents);
	}

	public static ButtonTag btn(Object... contents) {
		return button(contents).type("button").class_("btn btn-default");
	}

	public static ButtonTag btnPrimary(Object... contents) {
		return button(contents).type("button").class_("btn btn-primary");
	}

	public static NavTag nav_(boolean fluid, boolean inverse, Tag<?> brand, Object[] navbarContent) {
		brand.class_("navbar-brand");
		DivTag hdr = div(btnCollapse(), brand).class_("navbar-header");

		DivTag collapsable = div(navbarContent).class_("collapse navbar-collapse").id("collapsable");

		DivTag cnt = div(hdr, collapsable).class_(containerMaybeFluid(fluid));

		String navDefOrInv = inverse ? "navbar-inverse" : "navbar-default";
		return nav(cnt).class_("navbar " + navDefOrInv).role("navigation");
	}

	public static String containerMaybeFluid(boolean fluid) {
		return fluid ? "container-fluid" : "container";
	}

	public static ButtonTag btnCollapse() {
		ButtonTag btn = button(span("Toggle navigation").class_("sr-only"), icon("bar"), icon("bar"), icon("bar"));

		btn.type("button").class_("navbar-toggle collapsed").attr("data-toggle", "collapse")
				.attr("data-target", "#collapsable");

		return btn;
	}

	public static UlTag navbarMenu(boolean onLeft, Object... menuItems) {
		return ul_li(menuItems).class_("nav navbar-nav navbar-" + leftOrRight(onLeft));
	}

	public static UlTag navbarDropdown(boolean onLeft, Tag<?> menu, Object... subItems) {
		UlTag ul1 = ul_li(subItems).class_("dropdown-menu").role("menu");
		menu.class_("dropdown-toggle").attr("data-toggle", "dropdown");
		LiTag drop1 = li(menu, ul1).class_("dropdown");
		return ul(drop1).class_("nav navbar-nav navbar-" + leftOrRight(onLeft));
	}

	public static FormTag navbarForm(boolean onLeft, String buttonCaption, String[] fields, String[] placeholders) {
		U.must(fields.length == placeholders.length, "");

		DivTag ctrls = div().class_("form-group");

		for (int i = 0; i < fields.length; i++) {
			InputTag inp = input().type("text").class_("form-control").name(fields[i]).placeholder(placeholders[i]);
			ctrls.append(inp);
		}

		ButtonTag btn = button(buttonCaption).class_("btn btn-default").type("submit");
		return form(ctrls, btn).class_("navbar-form navbar-" + leftOrRight(onLeft));
	}

	public static Tag<?> navbarPage(boolean fluid, Tag<?> brand, Object[] navbarContent, Object pageContent) {
		Object cont = div(pageContent).class_(containerMaybeFluid(fluid));
		return body(nav_(fluid, false, brand, navbarContent), cont);
	}

	public static FormTag form_(FormLayout layout, String[] fieldsNames, String[] fieldsDesc, FieldType[] fieldTypes,
			Object[][] options, Var<?>[] vars, Object[] buttons) {

		U.notNull(fieldsNames, "field names");
		fieldsDesc = U.or(fieldsDesc, fieldsNames);
		U.must(fieldsNames.length == fieldsDesc.length, "");

		FormTag form = form().class_(formLayoutClass(layout)).role("form");

		for (int i = 0; i < fieldsNames.length; i++) {
			form.append(field(layout, fieldsNames[i], fieldsDesc[i], fieldTypes[i], options[i], vars[i]));
		}

		form.append(formBtns(layout, buttons));

		return form;
	}

	public static Tag<?> formBtns(FormLayout layout, Object[] buttons) {
		Tag<?> wrap, btns;

		if (layout == FormLayout.HORIZONTAL) {
			btns = div().class_("col-sm-offset-4 col-sm-8");
			wrap = div(btns).class_("form-group");
		} else {
			wrap = div().class_("form-group");
			btns = wrap;
		}

		for (Object btn : buttons) {
			btns.append(btn);
		}

		return wrap;
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

	public static DivTag field(FormLayout layout, String name, String desc, FieldType type, Object[] options, Var<?> var) {
		desc = U.or(desc, name);

		Object inp = input_(name, desc, type, options, var);
		LabelTag label;
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
				label.class_("col-sm-4 control-label");
			}

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).class_("col-sm-8") : inp;
		}

		DivTag group = label != null ? div(label, inputWrap) : div(inputWrap);
		group.class_("form-group");
		return group;
	}

	public static Object input_(String name, String desc, FieldType type, Object[] options, Var<?> var) {

		InputTag input;
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
				dropdown.append(op);
			}
			return dropdown;

		case MULTI_SELECT:
			U.notNull(options, "multi-select options");
			SelectTag select = select().name(name).class_("form-control").multiple(true);
			for (Object opt : options) {
				Var<Boolean> optVar = Vars.has(var, opt);
				OptionTag op = option(opt).value(str(opt)).bind(optVar);
				select.append(op);
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

		default:
			throw U.notExpected();
		}
	}

	private static String leftOrRight(boolean onLeft) {
		return onLeft ? "left" : "right";
	}

	public static SpanTag caret() {
		return span().class_("caret");
	}

	public static DivTag jumbotron(Object... contents) {
		return div(contents).class_("jumbotron");
	}

	public static DivTag col_(int cols, Object... contents) {
		return div(contents).class_("col-md-" + cols);
	}

	public static DivTag col1(Object... contents) {
		return col_(1, contents);
	}

	public static DivTag col2(Object... contents) {
		return col_(2, contents);
	}

	public static DivTag col3(Object... contents) {
		return col_(3, contents);
	}

	public static DivTag col4(Object... contents) {
		return col_(4, contents);
	}

	public static DivTag col5(Object... contents) {
		return col_(5, contents);
	}

	public static DivTag col6(Object... contents) {
		return col_(6, contents);
	}

	public static DivTag col7(Object... contents) {
		return col_(7, contents);
	}

	public static DivTag col8(Object... contents) {
		return col_(8, contents);
	}

	public static DivTag col9(Object... contents) {
		return col_(9, contents);
	}

	public static DivTag col10(Object... contents) {
		return col_(10, contents);
	}

	public static DivTag col11(Object... contents) {
		return col_(11, contents);
	}

	public static DivTag col12(Object... contents) {
		return col_(12, contents);
	}

	public static ButtonTag cmd(String cmd) {
		return btn(U.capitalized(cmd)).cmd(cmd);
	}

}
