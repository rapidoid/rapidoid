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

import org.rapidoid.pages.DynamicContent;
import org.rapidoid.pages.Handler;
import org.rapidoid.pages.Tag;
import org.rapidoid.pages.bootstrap.NavbarBootstrapPage;
import org.rapidoid.pages.html.ATag;
import org.rapidoid.pages.html.ButtonTag;
import org.rapidoid.pages.html.FormTag;
import org.rapidoid.pages.html.UlTag;

public class ShowcasePage extends NavbarBootstrapPage {

	private final String info;

	private ATag brand;
	private UlTag dropdownMenu;
	private UlTag menuL;
	private UlTag menuR;
	private FormTag formL;
	private FormTag formR;

	public ShowcasePage(String info) {
		this.info = info;

		brand = a("Welcome to the Showcase page!").href("/");
		dropdownMenu = navbarDropdown(a("Profile", caret()).href("#"), a("Settings"), a("Logout"));
		menuL = navbarMenu(true, a("About us").href("#about"), a("Contact").href("#contact"));
		menuR = navbarMenu(false, a("Logout").href("/_logout"));
		formL = navbarForm(true, "Search", "Enter search phrase...");
		formR = navbarForm(false, "Login", "Username", "Password");
	}

	@Override
	protected Object pageContent() {
		ButtonTag abc = btnPrimary("abc", new Handler<ButtonTag>() {
			@Override
			public void handle(ButtonTag target) {
				System.out.println("clicked abc!");
				brand.content(glyphicon("cog"));
			}
		});

		ButtonTag xy = btnPrimary("xy", new Handler<ButtonTag>() {
			@Override
			public void handle(ButtonTag target) {
				System.out.println("clicked xy!");
				brand.content(target.content());
			}
		});

		Object dyn = dynamic(new DynamicContent() {
			private int n;

			@Override
			public Object eval() {
				return n++;
			}
		});

		return row(cols(6, info), cols(3, abc, xy), cols(3, dyn));
	}

	protected Object[] navbarContent() {
		return new Object[] { menuL, formL, dropdownMenu, menuR, formR };
	}

	@Override
	protected Tag<?> brand() {
		return brand;
	}

}
