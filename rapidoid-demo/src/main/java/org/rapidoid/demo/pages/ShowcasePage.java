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

import org.rapidoid.pages.Tag;
import org.rapidoid.pages.bootstrap.NavbarBootstrapPage;
import org.rapidoid.pages.html.FormTag;
import org.rapidoid.pages.html.UlTag;

public class ShowcasePage extends NavbarBootstrapPage {

	@Override
	protected Object pageContent() {
		return row(cols(6, "the content"), cols(3, btnPrimary("abc")), cols(3, btn("some", " button")));
	}

	protected Tag<?> brand() {
		return a("Welcome to the Showcase page!").href("/");
	}

	protected Object[] navbarContent() {
		UlTag dropdownMenu = navbarDropdown(a("Profile", caret()).href("#"), a("Settings"), a("Logout"));
		UlTag menuL = navbarMenu(true, a("About us").href("#about"), a("Contact").href("#contact"));
		UlTag menuR = navbarMenu(false, a("RAbout us").href("#about"), a("RContact").href("#contact"));
		FormTag formL = navbarForm(true, "Search", "Enter search phrase...");
		FormTag formR = navbarForm(false, "Login", "Username", "Password");

		return new Object[] { menuL, formL, dropdownMenu, menuR, formR };
	}

}
