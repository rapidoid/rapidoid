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
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.util.U;

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

	public static Tag titleBox(Object... contents) {
		return div(contents).class_("title-box");
	}

}
