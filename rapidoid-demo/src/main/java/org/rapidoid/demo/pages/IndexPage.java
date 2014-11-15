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

import org.rapidoid.demo.pojo.Person;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagEventHandler;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.ButtonTag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.H1Tag;
import org.rapidoid.html.tag.TrTag;
import org.rapidoid.html.tag.UlTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.model.Item;
import org.rapidoid.model.Items;
import org.rapidoid.model.Model;
import org.rapidoid.pages.DynamicContent;
import org.rapidoid.pages.bootstrap.NavbarBootstrapPage;
import org.rapidoid.pages.bootstrap.TableItemAction;
import org.rapidoid.pages.bootstrap.TableWidget;
import org.rapidoid.util.U;

@SuppressWarnings("serial")
public class IndexPage extends NavbarBootstrapPage {

	private ATag brand;
	private UlTag dropdownMenu;
	private UlTag menuL;
	private UlTag menuR;
	private FormTag formL;
	private FormTag formR;

	public IndexPage() {
		brand = a("Welcome to the Showcase page!").href("/");
		dropdownMenu = navbarDropdown(false, a("Profile", caret()).href("#"), a("Settings"), a("Logout"));
		menuL = navbarMenu(true, a("About us").href("#about"), a("Contact").href("#contact"));
		menuR = navbarMenu(false, a("Logout").href("/_logout"));
		formL = navbarForm(true, "Search", arr("query"), arr("Enter search phrase..."));
		formR = navbarForm(false, "Login", arr("user", "pass"), arr("Username", "Password"));

		setContent(page());
	}

	@Override
	protected Object pageContent() {
		ButtonTag abc = btnPrimary("abc", new TagEventHandler<ButtonTag>() {
			int n = 1;

			@Override
			public void handle(ButtonTag target) {
				System.out.println("clicked abc! " + n++);
				brand.content(glyphicon("cog"), n);
			}
		});

		ButtonTag xy = btnPrimary("xy", new TagEventHandler<ButtonTag>() {
			@Override
			public void handle(ButtonTag target) {
				System.out.println("clicked xy!");
				brand.content(target.content());
			}
		});

		Object dyn = dynamic(new DynamicContent() {
			private int n;

			@Override
			public Object eval(HttpExchange x) {
				return n++;
			}
		});

		Items items = Model.mockBeanItems(20, Person.class);

		H1Tag caption = h1("Manage persons");

		TableWidget table = new TableWidget(items, new TableItemAction() {
			@Override
			public void execute(TrTag row, Item item) {
				row.class_("bg-primary");
				Integer age = (Integer) U.or(item.get("age"), 0);
				item.set("age", age + 1);
			}
		});

		return arr(row(col6("Hello world!"), col3(abc, xy), col3(dyn)), arr(caption, rowFull(table)));
	}

	protected Object[] navbarContent() {
		return new Object[] { menuL, formL, dropdownMenu, menuR, formR };
	}

	@Override
	protected Tag<?> brand() {
		return brand;
	}

}
