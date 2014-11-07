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
import org.rapidoid.html.tag.LiTag;
import org.rapidoid.html.tag.NavTag;
import org.rapidoid.html.tag.SpanTag;
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.html.tag.UlTag;
import org.rapidoid.util.U;

public class Bootstrap extends HTML {

	public static TableTag table(Object... contents) {
		return HTML.table(contents).classs("table table-default table-hover");
	}

	public static DivTag row(Object... contents) {
		return div(contents).classs("row");
	}

	public static DivTag rowFull(Object... contents) {
		return div(cols(12, contents)).classs("row");
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

	public static SpanTag a_awesome(String fontAwesomeIcon) {
		return span().classs("fa fa-" + fontAwesomeIcon);
	}

	public static ATag a_glyph(String glyphicon, Object... contents) {
		return a(glyphicon(glyphicon), contents);
	}

	public static ATag a_awesome(String fontAwesomeIcon, Object... contents) {
		return a(a_awesome(fontAwesomeIcon), contents);
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
		return HTML.nav(cnt).classs("navbar navbar-default").attr("role", "navigation");
	}

	public static String containerMaybeFluid(boolean fluid) {
		return fluid ? "container-fluid" : "container";
	}

	public static ButtonTag btnCollapse() {
		ButtonTag btn = button(span("Toggle navigation").classs("sr-only"), icon("bar"), icon("bar"), icon("bar"));

		btn.attr("type", "button").classs("navbar-toggle collapsed").attr("data-toggle", "collapse")
				.attr("data-target", "#collapsable");

		return btn;
	}

	public static FormTag navbarForm(boolean onLeft, String buttonCaption, String[] fields, String[] placeholders) {
		U.must(fields.length == placeholders.length, "");

		DivTag ctrls = div().classs("form-group");

		for (int i = 0; i < fields.length; i++) {
			InputTag inp = input().attr("type", "text").classs("form-control").attr("name", fields[i])
					.attr("placeholder", placeholders[i]);
			ctrls.append(inp);
		}

		ButtonTag btn = button(buttonCaption).classs("btn btn-default").attr("type", "submit");
		return form(ctrls, btn).classs("navbar-form navbar-" + leftOrRight(onLeft));
	}

	public static UlTag navbarMenu(boolean onLeft, Object... menuItems) {
		return ul_li(menuItems).classs("nav navbar-nav navbar-" + leftOrRight(onLeft));
	}

	public static UlTag navbarDropdown(boolean onLeft, Tag<?> menu, Object... subItems) {
		UlTag ul1 = ul_li(subItems).classs("dropdown-menu").attr("role", "menu");
		menu.classs("dropdown-toggle").attr("data-toggle", "dropdown");
		LiTag drop1 = li(menu, ul1).classs("dropdown");
		return ul(drop1).classs("nav navbar-nav navbar-" + leftOrRight(onLeft));
	}

	private static String leftOrRight(boolean onLeft) {
		return onLeft ? "left" : "right";
	}

	public static SpanTag caret() {
		return span().classs("caret");
	}

	public static DivTag cols(int cols, Object... contents) {
		return div(contents).classs("col-md-" + cols);
	}

	public static DivTag jumbotron(Object... contents) {
		return div(contents).classs("jumbotron");
	}

}
