package org.rapidoid.pages.bootstrap;

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

import org.rapidoid.pages.Tag;
import org.rapidoid.pages.Widget;
import org.rapidoid.pages.html.ButtonTag;
import org.rapidoid.pages.html.DivTag;
import org.rapidoid.pages.html.FormTag;
import org.rapidoid.pages.html.InputTag;
import org.rapidoid.pages.html.LiTag;
import org.rapidoid.pages.html.NavTag;
import org.rapidoid.pages.html.SpanTag;
import org.rapidoid.pages.html.TableTag;
import org.rapidoid.pages.html.UlTag;

public abstract class BootstrapWidget extends Widget {

	@Override
	public TableTag table(Object... contents) {
		return super.table(contents).classs("table");
	}

	protected DivTag row(Object... contents) {
		return div(contents).classs("row");
	}

	protected DivTag container(Object... contents) {
		return div(contents).classs("container");
	}

	protected DivTag containerFluid(Object... contents) {
		return div(contents).classs("container-fluid");
	}

	protected SpanTag icon(String icon) {
		return span().classs("icon-" + icon);
	}

	protected ButtonTag btn(Object... contents) {
		return button(contents).classs("btn btn-default");
	}

	protected ButtonTag btnPrimary(Object... contents) {
		return button(contents).classs("btn btn-primary");
	}

	protected NavTag nav(boolean fluid, Tag<?> brand, Object[] navbarContent) {
		brand.classs("navbar-brand");
		DivTag hdr = div(btnCollapse(), brand).classs("navbar-header");

		DivTag collapsable = div(navbarContent).classs("collapse navbar-collapse").id("collapsable");

		DivTag cnt = div(hdr, collapsable).classs(containerMaybeFluid(fluid));
		return nav(cnt).classs("navbar navbar-default").attr("role", "navigation");
	}

	protected String containerMaybeFluid(boolean fluid) {
		return fluid ? "container-fluid" : "container";
	}

	protected ButtonTag btnCollapse() {
		ButtonTag btn = button(span("Toggle navigation").classs("sr-only"), icon("bar"), icon("bar"), icon("bar"));

		btn.attr("type", "button").classs("navbar-toggle collapsed").attr("data-toggle", "collapse")
				.attr("data-target", "#collapsable");

		return btn;
	}

	protected FormTag navbarForm(boolean onLeft, String buttonCaption, String... fields) {
		DivTag ctrls = div().classs("form-group");

		for (String fieldName : fields) {
			InputTag inp = input().attr("type", "text").classs("form-control").attr("placeholder", fieldName);
			ctrls.append(inp);
		}

		ButtonTag btn = button(buttonCaption).classs("btn btn-default").attr("type", "submit");
		return form(ctrls, btn).classs("navbar-form navbar-" + leftOrRight(onLeft));
	}

	protected UlTag navbarMenu(boolean onLeft, Object... menuItems) {
		return ul_li(menuItems).classs("nav navbar-nav navbar-" + leftOrRight(onLeft));
	}

	private String leftOrRight(boolean onLeft) {
		return onLeft ? "left" : "right";
	}

	protected SpanTag caret() {
		return span().classs("caret");
	}

	protected UlTag navbarDropdown(Tag<?> menu, Object... subItems) {
		UlTag ul1 = ul_li(subItems).classs("dropdown-menu").attr("role", "menu");
		menu.classs("dropdown-toggle").attr("data-toggle", "dropdown");
		LiTag drop1 = li(menu, ul1).classs("dropdown");
		return ul(drop1).classs("nav navbar-nav");
	}

	protected DivTag cols(int cols, Object... contents) {
		return div(contents).classs("col-md-" + cols);
	}

}
