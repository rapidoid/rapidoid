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
import org.rapidoid.html.tag.SpanTag;
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.html.tag.UlTag;
import org.rapidoid.util.U;

public class Bootstrap extends HTML {

	public static TableTag table_(Object... contents) {
		return HTML.table(contents).classs("table table-default table-hover");
	}

	public static DivTag row(Object... contents) {
		return div(contents).classs("row");
	}

	public static DivTag rowFull(Object... contents) {
		return div(col12(contents)).classs("row");
	}

	public static DivTag container(Object... contents) {
		return div(contents).classs("container");
	}

	public static DivTag containerFluid(Object... contents) {
		return div(contents).classs("container-fluid");
	}

	public static SpanTag icon(String icon) {
		return span().classs("icon-" + icon);
	}

	public static SpanTag glyphicon(String glyphicon) {
		return span().classs("glyphicon glyphicon-" + glyphicon);
	}

	public static SpanTag awesome(String fontAwesomeIcon) {
		return span().classs("fa fa-" + fontAwesomeIcon);
	}

	public static ATag a_glyph(String glyphicon, Object... contents) {
		return a(glyphicon(glyphicon), contents);
	}

	public static ATag a_awesome(String fontAwesomeIcon, Object... contents) {
		return a(awesome(fontAwesomeIcon), contents);
	}

	public static ButtonTag btn(Object... contents) {
		return button(contents).classs("btn btn-default");
	}

	public static ButtonTag btnPrimary(Object... contents) {
		return button(contents).classs("btn btn-primary");
	}

	public static NavTag nav(boolean fluid, Tag<?> brand, Object[] navbarContent) {
		brand.classs("navbar-brand");
		DivTag hdr = div(btnCollapse(), brand).classs("navbar-header");

		DivTag collapsable = div(navbarContent).classs("collapse navbar-collapse").id("collapsable");

		DivTag cnt = div(hdr, collapsable).classs(containerMaybeFluid(fluid));
		return HTML.nav(cnt).classs("navbar navbar-default").role("navigation");
	}

	public static String containerMaybeFluid(boolean fluid) {
		return fluid ? "container-fluid" : "container";
	}

	public static ButtonTag btnCollapse() {
		ButtonTag btn = button(span("Toggle navigation").classs("sr-only"), icon("bar"), icon("bar"), icon("bar"));

		btn.type("button").classs("navbar-toggle collapsed").attr("data-toggle", "collapse")
				.attr("data-target", "#collapsable");

		return btn;
	}

	public static UlTag navbarMenu(boolean onLeft, Object... menuItems) {
		return ul_li(menuItems).classs("nav navbar-nav navbar-" + leftOrRight(onLeft));
	}

	public static UlTag navbarDropdown(boolean onLeft, Tag<?> menu, Object... subItems) {
		UlTag ul1 = ul_li(subItems).classs("dropdown-menu").role("menu");
		menu.classs("dropdown-toggle").attr("data-toggle", "dropdown");
		LiTag drop1 = li(menu, ul1).classs("dropdown");
		return ul(drop1).classs("nav navbar-nav navbar-" + leftOrRight(onLeft));
	}

	public static FormTag navbarForm(boolean onLeft, String buttonCaption, String[] fields, String[] placeholders) {
		U.must(fields.length == placeholders.length, "");

		DivTag ctrls = div().classs("form-group");

		for (int i = 0; i < fields.length; i++) {
			InputTag inp = input().type("text").classs("form-control").name(fields[i]).placeholder(placeholders[i]);
			ctrls.append(inp);
		}

		ButtonTag btn = button(buttonCaption).classs("btn btn-default").type("submit");
		return form(ctrls, btn).classs("navbar-form navbar-" + leftOrRight(onLeft));
	}

	public static FormTag form_(FormLayout layout, String[] fieldsNames, String[] fieldsDesc, FieldType[] fieldTypes,
			Object[][] options, String... commands) {

		U.notNull(fieldsNames, "field names");
		fieldsDesc = U.or(fieldsDesc, fieldsNames);
		U.must(fieldsNames.length == fieldsDesc.length, "");

		FormTag form = form().classs(formLayoutClass(layout)).role("form");

		for (int i = 0; i < fieldsNames.length; i++) {
			form.append(field(layout, fieldsNames[i], fieldsDesc[i], fieldTypes[i], options[i], null));
		}

		form.append(formBtns(layout, commands));

		return form;
	}

	public static Tag<?> formBtns(FormLayout layout, String[] commands) {
		Tag<?> wrap, btns;

		if (layout == FormLayout.HORIZONTAL) {
			btns = div().classs("col-sm-offset-4 col-sm-8");
			wrap = div(btns).classs("form-group");
		} else {
			wrap = div().classs("form-group");
			btns = wrap;
		}

		for (String cmd : commands) {
			btns.append(button(cmd).classs("btn btn-default"));
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

	public static DivTag field(FormLayout layout, String name, String desc, FieldType type, Object[] options,
			Object value) {
		desc = U.or(desc, name);

		String inputId = "_" + name; // FIXME

		Object inp = input_(inputId, name, desc, type, options, value);
		LabelTag label;
		Object inputWrap;

		if (type == FieldType.RADIOS || type == FieldType.CHECKBOXES) {
			inp = layout == FormLayout.VERTICAL ? div(inp) : span(inp);
		}

		if (type == FieldType.CHECKBOX) {
			label = null;

			inp = div(label(inp, desc)).classs("checkbox");

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).classs("col-sm-offset-4 col-sm-8") : inp;

		} else {
			if (layout != FormLayout.INLINE) {
				label = label(desc).for_(inputId);
			} else {
				if (type == FieldType.RADIOS) {
					label = label(desc);
				} else {
					label = null;
				}
			}

			if (layout == FormLayout.HORIZONTAL) {
				label.classs("col-sm-4 control-label");
			}

			inputWrap = layout == FormLayout.HORIZONTAL ? div(inp).classs("col-sm-8") : inp;
		}

		DivTag group = label != null ? div(label, inputWrap) : div(inputWrap);
		group.classs("form-group");
		return group;
	}

	public static Object input_(String id, String name, String desc, FieldType type, Object[] options, Object value) {
		switch (type) {

		case TEXT:
			return input().type("text").classs("form-control").id(id).name(name).placeholder(desc);

		case PASSWORD:
			return input().type("password").classs("form-control").id(id).name(name).placeholder(desc);

		case EMAIL:
			return input().type("email").classs("form-control").id(id).name(name).placeholder(desc);

		case CHECKBOX:
			return input().type("checkbox").id(id).name(name);

		case DROPDOWN:
			return select(foreach(options, option($value))).id(id).name(name).classs("form-control").multiple(false);

		case MULTI_SELECT:
			return select(foreach(options, option($value))).id(id).name(name).classs("form-control").multiple(true);

		case RADIOS:
			return foreach(options, label(input().type("radio").name(name).value($value), $value)
					.classs("radio-inline"));

		case CHECKBOXES:
			return foreach(options,
					label(input().type("checkbox").name(name).value($value), $value).classs("radio-checkbox"));

		default:
			throw U.notExpected();
		}
	}

	private static String leftOrRight(boolean onLeft) {
		return onLeft ? "left" : "right";
	}

	public static SpanTag caret() {
		return span().classs("caret");
	}

	public static DivTag jumbotron(Object... contents) {
		return div(contents).classs("jumbotron");
	}

	public static DivTag col_(int cols, Object... contents) {
		return div(contents).classs("col-md-" + cols);
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

}
