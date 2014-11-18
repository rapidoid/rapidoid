package org.rapidoid.demo.pages;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.annotation.Session;
import org.rapidoid.demo.pojo.Person;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.H1Tag;
import org.rapidoid.html.tag.UlTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Model;
import org.rapidoid.pages.BootstrapWidgets;
import org.rapidoid.util.U;

public class IndexPage extends BootstrapWidgets {

	@Session
	private int n;

	public Tag<?> content(HttpExchange x) {

		final ATag brand = a("Welcome to the Showcase page!").href("/");

		UlTag dropdownMenu = navbarDropdown(false, a("Profile", caret()).href("#"), a("Settings"), a("Logout"));
		UlTag menuL = navbarMenu(true, a("About us").href("#about"), a("Contact").href("#contact"));
		UlTag menuR = navbarMenu(false, a("Logout").href("/_logout"));

		FormTag formL = navbarForm(true, "Search", arr("query"), arr("Enter search phrase..."));
		FormTag formR = navbarForm(false, "Login", arr("user", "pass"), arr("Username", "Password"));

		ButtonTag abc = btnPrimary("abc");

		ButtonTag xy = btnPrimary("X Z Y").cmd("xyz");

		Items items = Model.mockBeanItems(20, Person.class);

		H1Tag caption = h1("Manage persons");

		Tag<?> table = grid(items, 10);

		Object[] pageContent = arr(row(col6("Hello world!"), col3(abc, xy)), arr(caption, rowFull(table)));

		Tag<?>[] navbarContent = arr(menuL, formL, dropdownMenu, menuR, formR);

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
