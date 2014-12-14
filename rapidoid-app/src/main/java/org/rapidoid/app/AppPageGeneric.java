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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.db.DB;
import org.rapidoid.html.Cmd;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.ATag;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.oauth.OAuth;
import org.rapidoid.oauth.OAuthProvider;
import org.rapidoid.pages.Pages;
import org.rapidoid.util.Arr;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class AppPageGeneric extends AppGUI implements Comparator<Class<?>> {

	private static final String SEARCH_SCREEN = "SearchScreen";

	private static final String SETTINGS_SCREEN = "SettingsScreen";

	private static final String ADMIN_SCREEN = "AdminScreen";

	private static final String[] SPECIAL_SCREENS = { SEARCH_SCREEN, SETTINGS_SCREEN, ADMIN_SCREEN };

	private static final String[] themes = { "default", "cerulean", "cosmo", "cyborg", "darkly", "flatly", "journal",
			"lumen", "paper", "readable", "sandstone", "simplex", "slate", "spacelab", "superhero", "united", "yeti" };

	private static final Pattern ENTITY_VIEW = Pattern.compile("^/(\\w+?)/(\\d+)/?$");

	private static final Pattern ENTITY_EDIT = Pattern.compile("^/edit(\\w+?)/(\\d+)/?$");

	public String title(HttpExchange x) {
		Object app = app(x);
		return Pages.titleOf(x, app);
	}

	public Object head(HttpExchange x) {
		Object app = app(x);
		String theme = Apps.config(app, "theme", null);
		return theme != null ? link()
				.href("//maxcdn.bootstrapcdn.com/bootswatch/3.3.0/" + theme + "/bootstrap.min.css").rel("stylesheet")
				: null;
	}

	public Object content(HttpExchange x) {

		AppClasses appCls = Apps.scanAppClasses(x);
		Object app = app(x);
		Map<String, Class<?>> mainScreens = filterScreens(app, appCls.screens);

		Object pageContent = null;
		int activeIndex = -1;
		Object screen = getScreen(x, appCls, app);
		U.notNull(screen, "screen");

		Pages.load(x, screen);
		pageContent = pageContent(x, screen);

		Class<?>[] screens = constructScreens(mainScreens);
		Object[] menuItems = new Object[screens.length];
		activeIndex = setupMenuItems(screen.getClass(), screens, menuItems);

		String theme = Apps.config(app, "theme", null);

		ATag brand = a(Pages.titleOf(x, app)).href("/");
		Tag userMenu = userMenu(x, app);
		Tag themesMenu = theme == null ? themesMenu(app) : null;
		FormTag searchForm = searchForm(app);
		Tag navMenu = navbarMenu(true, activeIndex, menuItems);
		Object[] navbarContent = arr(navMenu, themesMenu, userMenu, searchForm);

		Tag result = navbarPage(isFluid(app), brand, navbarContent, pageContent);

		Pages.store(x, screen);

		return result;
	}

	private Object genericScreen(HttpExchange x, Object app) {
		if (!x.query_().range().isEmpty()) {
			return null;
		}

		String path = x.path();

		Matcher m = ENTITY_EDIT.matcher(path);
		if (m.find()) {
			String type = m.group(1);
			long id = Long.parseLong(m.group(2));

			Object entity = DB.get(id);

			String entityClass = entity.getClass().getSimpleName();
			String reqType = U.capitalized(type);

			if (entityClass.equals(reqType)) {
				return new EditEntityScreenGeneric();
			}
		}

		m = ENTITY_VIEW.matcher(path);

		if (m.find()) {
			String type = m.group(1);
			long id = Long.parseLong(m.group(2));

			Object entity = DB.get(id);

			String entityClass = entity.getClass().getSimpleName();
			String reqType = U.capitalized(type);

			if (entityClass.equals(reqType)) {
				return new ViewEntityScreenGeneric();
			}
		}

		return null;
	}

	private Object pageContent(HttpExchange x, Object screen) {
		Object pageContent = Pages.contentOf(x, screen);
		if (pageContent == null) {
			pageContent = hardcoded("No content available!");
		}
		return pageContent;
	}

	private FormTag searchForm(Object app) {
		FormTag searchForm = null;
		if (Apps.addon(app, "search")) {
			searchForm = navbarForm(false, "Find", arr("q"), arr("Search")).attr("action", "/search").attr("method",
					"GET");
		}
		return searchForm;
	}

	private int setupMenuItems(Class<?> screenClass, Class<?>[] screens, Object[] menuItems) {
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
		return activeIndex;
	}

	private Tag userMenu(HttpExchange x, Object app) {
		Tag dropdownMenu = null;
		if (Apps.addon(app, "auth") || Apps.addon(app, "googleLogin") || Apps.addon(app, "facebookLogin")
				|| Apps.addon(app, "linkedinLogin") || Apps.addon(app, "githubLogin")) {
			if (x.isLoggedIn()) {
				dropdownMenu = loggedInUserMenu(x, app);
			} else {
				dropdownMenu = loggedOutUserMenu(x, app);
			}
		}
		return dropdownMenu;
	}

	private Class<?>[] constructScreens(Map<String, Class<?>> mainScreens) {

		int screensN = mainScreens.size();
		for (String scr : SPECIAL_SCREENS) {
			if (mainScreens.containsKey(scr)) {
				screensN--;
			}
		}

		Class<?>[] screens = new Class[screensN];
		int ind = 0;
		for (Entry<String, Class<?>> e : mainScreens.entrySet()) {

			if (Arr.indexOf(SPECIAL_SCREENS, e.getKey()) < 0) {
				screens[ind++] = e.getValue();
			}
		}

		Arrays.sort(screens, this);
		return screens;
	}

	private Tag themesMenu(Object app) {
		ATag theme = a_glyph("eye-open", "", caret());

		Object[] themess = new Object[themes.length];

		for (int i = 0; i < themes.length; i++) {
			String thm = themes[i];
			String js = U.format("document.cookie='THEME=%s; path=/'; location.reload();", thm);
			themess[i] = a_void(U.capitalized(thm)).onclick(js);
		}

		Tag themesMenu = Apps.addon(app, "themes") ? navbarDropdown(false, theme, themess) : null;
		return themesMenu;
	}

	private Tag loggedOutUserMenu(HttpExchange x, Object app) {
		Tag dropdownMenu;
		ATag ga = null, fb = null, li = null, gh = null;

		if (Apps.addon(app, "googleLogin")) {
			ga = a_awesome("google", "Sign in with Google").href(OAuth.getLoginURL(x, OAuthProvider.GOOGLE, null));
		}

		if (Apps.addon(app, "facebookLogin")) {
			fb = a_awesome("facebook", "Sign in with Facebook")
					.href(OAuth.getLoginURL(x, OAuthProvider.FACEBOOK, null));
		}

		if (Apps.addon(app, "linkedinLogin")) {
			li = a_awesome("linkedin", "Sign in with LinkedIn")
					.href(OAuth.getLoginURL(x, OAuthProvider.LINKEDIN, null));
		}

		if (Apps.addon(app, "githubLogin")) {
			gh = a_awesome("github", "Sign in with GitHub").href(OAuth.getLoginURL(x, OAuthProvider.GITHUB, null));
		}

		dropdownMenu = navbarDropdown(false, a_glyph("log-in", "Sign in", caret()), ga, fb, li, gh);
		return dropdownMenu;
	}

	private Tag loggedInUserMenu(HttpExchange x, Object app) {
		Tag dropdownMenu;
		ATag profile = a_glyph("user", x.user().display, caret());
		ATag settings = Apps.addon(app, "settings") ? a_glyph("cog", " Settings").href("/settings") : null;
		ATag logout = a_glyph("log-out", "Logout").href("/_logout");

		dropdownMenu = navbarDropdown(false, profile, settings, logout);
		return dropdownMenu;
	}

	protected boolean isFluid(Object app) {
		return Apps.config(app, "fluid", true);
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

	private Object getScreen(HttpExchange x, AppClasses appCls, Object app) {
		// TODO use screens.get(...) instead of iteration
		for (Class<?> screen : appCls.screens.values()) {
			if (Apps.screenUrl(screen).equals(x.path())) {
				x.authorize(screen);
				return U.newInstance(screen);
			}
		}

		return U.or(genericScreen(x, app), app);
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

		AppClasses appCls = Apps.scanAppClasses(x);

		Object app = appCls.main != null ? U.newInstance(appCls.main) : new Object();

		Object screen = getScreen(x, appCls, app);
		U.must(screen != null, "Cannot find a screen to process the command!");

		Pages.load(x, screen);

		Pages.callCmdHandler(x, screen, new Cmd(cmd, args));

		Pages.store(x, screen);
	}

	private Object app(HttpExchange x) {
		AppClasses appCls = Apps.scanAppClasses(x);
		Object app = appCls.main != null ? U.newInstance(appCls.main) : new Object();
		return app;
	}

}
