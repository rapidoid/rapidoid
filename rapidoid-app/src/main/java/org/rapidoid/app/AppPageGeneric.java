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

import java.util.LinkedHashMap;
import java.util.Map;
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
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class AppPageGeneric extends AppGUI {

	protected static final String[] themes = { "1", "2", "3", "4", "5" };

	protected static final Pattern ENTITY_VIEW = Pattern.compile("^/(\\w+?)/(\\d+)/?$");

	protected static final Pattern ENTITY_EDIT = Pattern.compile("^/edit(\\w+?)/(\\d+)/?$");

	protected static final AppScreens APP_SCREENS = U.customizable(AppScreens.class);

	protected final HttpExchange x;

	protected final AppClasses appCls;

	protected final Object app;

	protected final Object screen;

	public AppPageGeneric(HttpExchange x, AppClasses appCls) {
		this.x = x;
		this.appCls = appCls;
		this.app = appCls.main != null ? U.newInstance(appCls.main) : new Object();
		this.screen = getScreen();

		U.must(screen != null, "Cannot find a screen to process the request!");
		Pages.load(x, screen);
	}

	public String title() {
		return Pages.titleOf(x, app);
	}

	public Object head() {
		String theme = config("theme", null);
		return theme != null ? link().href(themeUrl(theme)).rel("stylesheet") : null;
	}

	protected String themeUrl(String theme) {
		if (theme.startsWith("bootswatch:")) {
			theme = theme.substring("bootswatch:".length());
			return "//maxcdn.bootstrapcdn.com/bootswatch/3.3.0/" + theme + "/bootstrap.min.css";
		} else {
			return "/bootstrap/css/theme-" + theme + ".css";
		}
	}

	public Object content() {

		Map<String, Class<?>> mainScreens = filterScreens();

		Object pageContent = null;
		int activeIndex = -1;

		pageContent = pageContent();

		Class<?>[] screens = APP_SCREENS.constructScreens(mainScreens);
		Object[] menuItems = new Object[screens.length];
		activeIndex = setupMenuItems(screen.getClass(), screens, menuItems);

		String theme = config("theme", null);

		ATag brand = a(Pages.titleOf(x, app)).href("/");
		Tag userMenu = userMenu();
		Tag themesMenu = theme == null ? themesMenu() : null;
		Tag debugMenu = x.devMode() ? debugMenu() : null;
		FormTag searchForm = searchForm();
		Tag navMenu = navbarMenu(true, activeIndex, menuItems);
		Object[] navbarContent = arr(navMenu, debugMenu, themesMenu, userMenu, searchForm);

		String modal = Cls.getPropValue(screen, "modal", null);
		Object modalContent = modal != null ? Cls.getPropValue(screen, modal, null) : null;
		Tag result = navbarPage(isFluid(), brand, navbarContent, arr(pageContent, modalContent));

		Pages.store(x, screen);

		return result;
	}

	protected Object genericScreen() {
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

			Object entity = DB.getIfExists(id);
			if (entity == null) {
				return null;
			}

			String entityClass = entity.getClass().getSimpleName();
			String reqType = U.capitalized(type);

			if (entityClass.equals(reqType)) {
				return new ViewEntityScreenGeneric();
			}
		}

		return null;
	}

	protected Object pageContent() {
		return U.or(Pages.contentOf(x, screen), "Page not found!");
	}

	protected FormTag searchForm() {
		return addon("search") ? navbarSearchForm("/search") : null;
	}

	protected boolean addon(String name) {
		return Apps.addon(app, name);
	}

	protected <T> T config(String name, T defaultValue) {
		return Apps.config(app, name, defaultValue);
	}

	protected int setupMenuItems(Class<?> screenClass, Class<?>[] screens, Object[] menuItems) {
		int activeIndex = -1;
		int k = 0;
		for (int i = 0; i < screens.length; i++) {
			Class<?> scr = screens[i];
			String name = Apps.screenName(scr);
			String title = Cls.getFieldValue(scr, "title", U.camelPhrase(name));
			menuItems[k++] = a(title).href(Apps.screenUrl(scr));

			if (scr.equals(screenClass)) {
				activeIndex = i;
			}
		}
		return activeIndex;
	}

	protected Tag userMenu() {
		Tag dropdownMenu = null;
		if (addon("auth") || addon("googleLogin") || addon("facebookLogin") || addon("linkedinLogin")
				|| addon("githubLogin")) {
			if (x.isLoggedIn()) {
				dropdownMenu = loggedInUserMenu();
			} else {
				dropdownMenu = loggedOutUserMenu();
			}
		}
		return dropdownMenu;
	}

	protected Tag themesMenu() {
		ATag theme = a_glyph("eye-open", "", caret());

		Object[] themess = new Object[themes.length];

		for (int i = 0; i < themes.length; i++) {
			String thm = themes[i];
			String js = U.format("document.cookie='THEME=%s; path=/'; location.reload();", thm);
			themess[i] = a_void(U.capitalized(thm)).onclick(js);
		}

		Tag themesMenu = addon("themes") ? navbarDropdown(false, theme, themess) : null;
		return themesMenu;
	}

	protected Tag loggedOutUserMenu() {
		Tag ga = null, fb = null, li = null, gh = null;

		if (addon("googleLogin")) {
			ga = a_awesome("google", "Sign in with Google").href(OAuth.getLoginURL(x, OAuthProvider.GOOGLE, null));
		}

		if (addon("facebookLogin")) {
			fb = a_awesome("facebook", "Sign in with Facebook")
					.href(OAuth.getLoginURL(x, OAuthProvider.FACEBOOK, null));
		}

		if (addon("linkedinLogin")) {
			li = a_awesome("linkedin", "Sign in with LinkedIn")
					.href(OAuth.getLoginURL(x, OAuthProvider.LINKEDIN, null));
		}

		if (addon("githubLogin")) {
			gh = a_awesome("github", "Sign in with GitHub").href(OAuth.getLoginURL(x, OAuthProvider.GITHUB, null));
		}

		return navbarDropdown(false, a_glyph("log-in", "Sign in", caret()), ga, fb, li, gh);
	}

	protected Tag loggedInUserMenu() {
		ATag profile = a_glyph("user", userDisplay(), caret());
		ATag settings = addon("settings") ? a_glyph("cog", " Settings").href("/settings") : null;
		ATag logout = a_glyph("log-out", "Logout").href("/_logout");

		return navbarDropdown(false, profile, settings, logout);
	}

	protected String userDisplay() {
		String username = x.user().username();
		int pos = username.indexOf('@');
		return pos > 0 ? username.substring(0, pos) : username;
	}

	protected Tag debugMenu() {
		ATag debug = a_awesome("bug", "Debug", caret());

		ATag userInfo = a_awesome("bug", "User info").href("/debuguserinfo");

		return navbarDropdown(false, debug, debugLoginUrl("admin"), debugLoginUrl("manager"),
				debugLoginUrl("moderator"), debugLoginUrl("foo"), debugLoginUrl("bar"), menuDivider(), userInfo);
	}

	protected ATag debugLoginUrl(String username) {
		return a_awesome("bug", "Sign in as " + U.capitalized(username)).href("/_debugLogin?user=" + username);
	}

	protected boolean isFluid() {
		return config("fluid", true);
	}

	protected Object getScreen() {
		// TODO use screens.get(...) instead of iteration
		for (Class<?> screen : appCls.screens.values()) {
			if (Apps.screenUrl(screen).equals(x.path())) {
				x.authorize(screen);
				return U.newInstance(screen);
			}
		}

		return U.or(genericScreen(), app);
	}

	protected Map<String, Class<?>> filterScreens() {
		Object[] screensConfig = config("screens", null);

		if (screensConfig == null) {
			return appCls.screens;
		}

		Map<String, Class<?>> filtered = new LinkedHashMap<String, Class<?>>();

		for (Object scr : screensConfig) {
			if (scr instanceof Class<?>) {
				Class<?> cls = (Class<?>) scr;
				filtered.put(cls.getSimpleName(), appCls.screens.get(cls.getSimpleName()));
			} else if (scr instanceof String) {
				String name = U.capitalized((String) scr);
				if (!name.endsWith("Screen")) {
					name += "Screen";
				}
				filtered.put(name, appCls.screens.get(name));
			} else {
				throw U.rte("Expected class or string to represent a screen, but found: %s", scr);
			}
		}

		return filtered;
	}

	public void on(String cmd, Object[] args) {
		Pages.callCmdHandler(x, screen, new Cmd(cmd, args));
	}

}
