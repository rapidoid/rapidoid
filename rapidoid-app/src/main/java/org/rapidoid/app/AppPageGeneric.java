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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.html.Cmd;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.html.tag.UlTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.oauth.OAuth;
import org.rapidoid.oauth.OAuthProvider;
import org.rapidoid.pages.BootstrapWidgets;
import org.rapidoid.pages.Pages;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class AppPageGeneric extends BootstrapWidgets implements Comparator<Class<?>> {

	private static final String SEARCH_SCREEN = "SearchScreen";

	private static final String SETTINGS_SCREEN = "SettingsScreen";

	private static final String[] themes = { "default", "cerulean", "cosmo", "cyborg", "darkly", "flatly", "journal",
			"lumen", "paper", "readable", "sandstone", "simplex", "slate", "spacelab", "superhero", "united", "yeti" };

	private static final String SESSION_CURRENT_SCREEN = "_current_screen_";

	private final AppClasses appCls;

	private final Object app;

	private final Map<String, Class<?>> mainScreens;

	public AppPageGeneric() {
		appCls = Apps.scanAppClasses();
		app = appCls.main != null ? U.newInstance(appCls.main) : new Object();
		mainScreens = filterScreens(app, appCls.screens);
	}

	public String title(HttpExchange x) {
		return Pages.titleOf(x, app);
	}

	public Object content(HttpExchange x) {

		Class<?> screenClass = getScreenClass(x);
		if (screenClass == null && x.path().equals("/")) {
			return Pages.contentOf(x, app);
		}

		x.sessionSet(SESSION_CURRENT_SCREEN, screenClass.getSimpleName());

		Object screen = U.newInstance(screenClass);
		Pages.load(x, screen);

		int screensN = mainScreens.containsKey(SEARCH_SCREEN) ? mainScreens.size() - 1 : mainScreens.size();

		if (mainScreens.containsKey(SETTINGS_SCREEN)) {
			screensN--;
		}

		Class<?>[] screens = new Class[screensN];
		int ind = 0;
		for (Entry<String, Class<?>> e : mainScreens.entrySet()) {
			if (!e.getKey().equals(SEARCH_SCREEN) && !e.getKey().equals(SETTINGS_SCREEN)) {
				screens[ind++] = e.getValue();
			}
		}

		Arrays.sort(screens, this);

		ATag brand = a(Pages.titleOf(x, app)).href("/");

		Tag<?> dropdownMenu = null;

		if (Apps.config(app, "auth", false)) {

			if (x.isLoggedIn()) {

				ATag profile = a_glyph("user", x.user().display, caret());
				ATag settings = Apps.config(app, "settings", false) ? a_glyph("cog", " Settings").href("/settings")
						: null;
				ATag logout = a_glyph("log-out", "Logout").href("/_logout");

				dropdownMenu = navbarDropdown(false, profile, settings, logout);

			} else {

				ATag ga = null, fb = null, li = null, gh = null;

				if (Apps.config(app, "googleLogin", false)) {
					ga = a_awesome("google", "Sign in with Google").href(
							OAuth.getLoginURL(x, OAuthProvider.GOOGLE, null));
				}

				if (Apps.config(app, "facebookLogin", false)) {
					fb = a_awesome("facebook", "Sign in with Facebook").href(
							OAuth.getLoginURL(x, OAuthProvider.FACEBOOK, null));
				}

				if (Apps.config(app, "linkedinLogin", false)) {
					li = a_awesome("linkedin", "Sign in with LinkedIn").href(
							OAuth.getLoginURL(x, OAuthProvider.LINKEDIN, null));
				}

				if (Apps.config(app, "githubLogin", false)) {
					gh = a_awesome("github", "Sign in with GitHub").href(
							OAuth.getLoginURL(x, OAuthProvider.GITHUB, null));
				}

				dropdownMenu = navbarDropdown(false, a_glyph("log-in", "Sign in", caret()), ga, fb, li, gh);
			}
		}

		ATag theme = a_glyph("eye-open", "", caret());

		Object[] themess = new Object[themes.length];

		for (int i = 0; i < themes.length; i++) {
			String thm = themes[i];
			String js = U.format("document.cookie='THEME=%s; path=/'; location.reload();", thm);
			themess[i] = a(U.capitalized(thm)).onclick(js);
		}

		UlTag themesMenu = Apps.config(app, "themes", false) ? navbarDropdown(false, theme, themess) : null;

		Object[] menuItems = new Object[screens.length];

		int activeIndex = -1;
		int k = 0;
		for (int i = 0; i < screens.length; i++) {
			Class<?> scr = screens[i];
			String name = Apps.screenName(scr);
			String title = U.or(titleOf(scr), U.camelPhrase(name));
			menuItems[k++] = a(title).href(Apps.screenUrl(scr));

			if (scr.equals(screenClass)) {
				activeIndex = i;
			}
		}

		UlTag navMenu = navbarMenu(true, activeIndex, menuItems);

		FormTag searchForm = null;
		if (Apps.config(app, "search", false)) {
			searchForm = navbarForm(false, "Find", arr("q"), arr("Search")).attr("action", "/search").attr("method",
					"GET");
		}

		Object[] navbarContent = arr(navMenu, themesMenu, dropdownMenu, searchForm);

		Object pageContent = Pages.contentOf(x, screen);
		if (pageContent == null) {
			pageContent = hardcoded("No content available!");
		}

		Tag<?> result = navbarPage(isFluid(app), brand, navbarContent, pageContent);

		Pages.store(x, screen);

		return result;
	}

	private Class<?> getScreenClass(HttpExchange x) {
		String path = x.path();

		Class<?> screenClass;
		if (path.startsWith("/_")) {
			screenClass = appCls.screens.get(x.session(SESSION_CURRENT_SCREEN));
		} else {
			screenClass = getScreen(path);
		}
		return screenClass;
	}

	protected boolean isFluid(Object app) {
		return Apps.config(app, "fluid", false);
	}

	public String appTitle(Object app) {
		return U.or(titleOf(app), "Untitled app");
	}

	private String titleOf(Object obj) {
		return Cls.getFieldValue(obj, "title", null);
	}

	@Override
	public int compare(Class<?> o1, Class<?> o2) {
		int cls1 = screenOrder(o1);
		int cls2 = screenOrder(o2);

		return cls1 - cls2;
	}

	private int screenOrder(Class<?> scrClass) {

		String cls = scrClass.getSimpleName();

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

	private Class<?> getScreen(String path) {
		// TODO use screens.get(...) instead of iteration
		for (Class<?> screen : appCls.screens.values()) {
			if (Apps.screenUrl(screen).equals(path)) {
				return screen;
			}
		}
		return null;
	}

	private static Map<String, Class<?>> filterScreens(Object app, Map<String, Class<?>> screenClasses) {
		Object[] screensConfig = Apps.config(app, "screens", null);

		if (screensConfig == null) {
			return screenClasses;
		}

		Map<String, Class<?>> filtered = new LinkedHashMap<String, Class<?>>();

		for (Object scr : screensConfig) {
			if (scr instanceof Class<?>) {
				Class<?> cls = (Class<?>) scr;
				filtered.put(cls.getSimpleName(), screenClasses.get(cls.getSimpleName()));
			} else if (scr instanceof String) {
				String name = U.capitalized((String) scr);
				if (!name.endsWith("Screen")) {
					name += "Screen";
				}
				filtered.put(name, screenClasses.get(name));
			} else {
				throw U.rte("Expected class or string to represent a screen, but found: %s", scr);
			}
		}

		return filtered;
	}

	public void on(HttpExchange x, String cmd, Object[] args) {

		Class<?> screenClass = getScreenClass(x);
		U.must(screenClass != null, "Cannot find a screen to process the command!");

		x.sessionSet(SESSION_CURRENT_SCREEN, screenClass.getSimpleName());

		Object screen = U.newInstance(screenClass);
		Pages.load(x, screen);

		Pages.callCmdHandler(x, screen, new Cmd(cmd, args));

		Pages.store(x, screen);
	}
}
