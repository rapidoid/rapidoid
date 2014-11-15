package org.rapidoid.app;

/*
 * #%L
 * rapidoid-app
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

import java.util.Arrays;
import java.util.Comparator;

import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.UlTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.oauth.OAuth;
import org.rapidoid.oauth.OAuthProvider;
import org.rapidoid.pages.DynamicContent;
import org.rapidoid.pages.bootstrap.NavbarBootstrapPage;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class AppPage extends NavbarBootstrapPage implements Comparator<Object> {

	private static final long serialVersionUID = 5633741993098185900L;

	private static final String[] themes = { "default", "cerulean", "cosmo", "cyborg", "darkly", "flatly", "journal",
			"lumen", "paper", "readable", "sandstone", "simplex", "slate", "spacelab", "superhero", "united", "yeti" };

	final Object app;
	final Object[] screens;
	Object screen;

	private int searchScreenIndex;

	private ATag brand;
	private Object dropdownMenu;
	private UlTag navMenu;
	private FormTag searchForm;

	private Object themesMenu;

	public AppPage(Object app, Object[] screens, Object screen) {
		this.app = app;
		this.screens = screens;
		this.screen = screen;

		Arrays.sort(screens, this);
		this.searchScreenIndex = findSearchScreen();

		brand = a(pageTitle()).href("/");

		dropdownMenu = dynamic(new DynamicContent() {

			private static final long serialVersionUID = -2512806659030800594L;

			@Override
			public Object eval(HttpExchange x) {
				if (x.isLoggedIn()) {

					ATag profile = a_glyph("user", x.user().display, caret());
					ATag settings = a_glyph("cog", " Settings");
					ATag logout = a_glyph("log-out", "Logout").href("/_logout");

					return navbarDropdown(false, profile, settings, logout);
				} else {

					ATag ga = a_awesome("google", "Sign in with Google").href(
							OAuth.getLoginURL(x, OAuthProvider.GOOGLE));

					ATag fb = a_awesome("facebook", "Sign in with Facebook").href(
							OAuth.getLoginURL(x, OAuthProvider.FACEBOOK));

					ATag li = a_awesome("linkedin", "Sign in with LinkedIn").href(
							OAuth.getLoginURL(x, OAuthProvider.LINKEDIN));

					ATag gh = a_awesome("github", "Sign in with GitHub").href(
							OAuth.getLoginURL(x, OAuthProvider.GITHUB));

					return navbarDropdown(false, a_glyph("log-in", "Sign in", caret()), ga, fb, li, gh);
				}
			}
		});

		ATag theme = a_glyph("eye-open", "", caret());

		Object[] themess = new Object[themes.length];

		for (int i = 0; i < themes.length; i++) {
			String thm = themes[i];
			String js = U.format("document.cookie='THEME=%s; path=/'; location.reload();", thm);
			themess[i] = a(U.capitalized(thm)).onclick(js);
		}

		themesMenu = Apps.config(app, "themes", false) ? navbarDropdown(false, theme, themess) : null;

		Object[] menuItems = new Object[searchScreenIndex < 0 ? screens.length : screens.length - 1];

		int k = 0;
		for (int i = 0; i < screens.length; i++) {
			if (i != searchScreenIndex) {
				Object scr = screens[i];
				String name = Apps.screenName(scr);
				String title = U.or(titleOf(scr), U.camelPhrase(name));
				menuItems[k++] = a(title).href(Apps.screenUrl(scr));
			}
		}

		navMenu = navbarMenu(true, menuItems);

		if (Apps.config(app, "search", false)) {
			searchForm = navbarForm(false, "Find", arr("q"), arr("Search")).attr("action", "/search").attr("method",
					"GET");
		}

		setContent(page());
	}

	private int findSearchScreen() {
		for (int i = 0; i < screens.length; i++) {
			if (Apps.screenName(screens[i]).equals("Search")) {
				return i;
			}
		}

		return -1;
	}

	@Override
	protected Object pageContent() {
		Object content = Cls.getPropValue(screen, "content", null);

		if (content == null) {
			content = hardcoded("Cannot find/execute method: <b>Object content() { }</b> in screen: <b>"
					+ screen.getClass().getSimpleName() + "</b>");
		}

		return content;
	}

	protected Object[] navbarContent() {
		if (searchForm != null) {
			return new Object[] { navMenu, themesMenu, dropdownMenu, searchForm };
		} else {
			return new Object[] { navMenu, themesMenu, dropdownMenu };
		}
	}

	@Override
	protected Tag<?> brand() {
		return brand;
	}

	@Override
	protected String pageTitle() {
		return U.or(titleOf(app), "Untitled app");
	}

	private String titleOf(Object obj) {
		return Cls.getFieldValue(obj, "title", null);
	}

	@Override
	public int compare(Object o1, Object o2) {
		int cls1 = screenOrder(o1);
		int cls2 = screenOrder(o2);

		return cls1 - cls2;
	}

	private int screenOrder(Object obj) {

		String cls = obj.getClass().getSimpleName();

		if (cls.equals("HomeScreen")) {
			return -1000;
		}

		if (cls.equals("AboutScreen")) {
			return 1000;
		}

		if (cls.equals("HelpScreen")) {
			return 2000;
		}

		return cls.charAt(0);
	}

	public void setScreen(Object screen) {
		this.screen = screen;
	}

}
