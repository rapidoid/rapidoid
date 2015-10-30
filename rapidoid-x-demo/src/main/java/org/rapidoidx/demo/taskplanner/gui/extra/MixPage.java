package org.rapidoidx.demo.taskplanner.gui.extra;

/*
 * #%L
 * rapidoid-x-demo
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Session;
import org.rapidoid.annotation.Since;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Models;
import org.rapidoid.u.U;
import org.rapidoid.widget.BootstrapWidgets;
import org.rapidoid.widget.ButtonWidget;
import org.rapidoid.widget.GridWidget;
import org.rapidoidx.demo.taskplanner.model.Person;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class MixPage extends BootstrapWidgets {

	@Session
	private int n;

	public Object content(HttpExchange x) {

		final ATag brand = a("Welcome to the Mix!").href("/mix");

		Tag dropdownMenu = navbarDropdown(false, a_void("Profile", caret()), a_void("Settings"), a_void("Logout"));
		Tag menuL = navbarMenu(true, 1, a("Forms").href("/forms"), a_void("Contact"));
		Tag menuR = navbarMenu(false, -1, a("Logout").href("/_logout"));

		FormTag formR = navbarForm(false, "Login", arr("user", "pass"), arr("Username", "Password"));

		ButtonWidget abc = btn("abc").primary();

		ButtonWidget xy = btn("X Z Y").command("xyz").info();

		Items items = Models.mockBeanItems(20, Person.class);

		Tag caption = h1("Manage persons");

		GridWidget table = grid(items, null, 10);

		Object[] pageContent = arr(row(col6("Hello world!"), col3(abc, xy)), arr(caption, row(table)));

		Tag[] navbarContent = arr(menuL, dropdownMenu, menuR, formR);

		return navbarPage(true, brand, navbarContent, pageContent);
	}

	public void onXyz() {
		n++;
	}

	public void onEnter(Item item) {
		Integer age = (Integer) U.or(item.get("age"), 0);
		item.set("age", age + 1);
	}

}
