package org.rapidoid.app.builtin;

/*
 * #%L
 * rapidoid-app
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Scaffold;
import org.rapidoid.annotation.Since;
import org.rapidoid.aop.AOP;
import org.rapidoid.app.AppClasses;
import org.rapidoid.app.AppGUI;
import org.rapidoid.app.AppScreens;
import org.rapidoid.app.Apps;
import org.rapidoid.app.Scaffolding;
import org.rapidoid.cls.Cls;
import org.rapidoid.html.Cmd;
import org.rapidoid.html.tag.FormTag;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.HttpNotFoundException;
import org.rapidoid.http.HttpSuccessException;
import org.rapidoid.pages.Pages;
import org.rapidoid.pages.impl.ComplexView;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.plugins.db.DB;
import org.rapidoid.plugins.languages.Languages;
import org.rapidoid.plugins.templates.ITemplate;
import org.rapidoid.plugins.templates.Templates;
import org.rapidoid.security.Secure;
import org.rapidoid.util.English;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.webapp.AppCtx;
import org.rapidoid.webapp.Scan;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class AppPageGeneric extends AppGUI implements ComplexView {

	protected static final String[] themes = { "default", "1", "2", "3", "4", "5", "none" };

	protected static final Pattern ENTITY_VIEW = Pattern.compile("^/(\\w+?)/(\\d+)/?$");

	protected static final Pattern ENTITY_EDIT = Pattern.compile("^/edit(\\w+?)/(\\d+)/?$");

	protected static final Pattern ENTITY_NEW = Pattern.compile("^/new(\\w+?)/?$");

	protected static final Pattern ENTITY_LIST = Pattern.compile("^/(\\w+?)/?$");

	protected static final AppScreens APP_SCREENS = Cls.customizable(AppScreens.class);

	protected static final Class<?>[] BUILT_IN_SCREENS = { SearchScreenBuiltIn.class, SettingsScreenBuiltIn.class,
			DebugUserInfoScreenBuiltIn.class, DeleteAllDataScreenBuiltIn.class };

	protected final HttpExchange x;

	protected final AppClasses appCls;

	protected final Object app;

	protected final Object screen;

	public AppPageGeneric(HttpExchange x, AppClasses appCls, Object app) {
		this.x = x;
		this.appCls = appCls;
		this.app = app;
		this.screen = Apps.wireExchange(getScreen(), x);

		if (appCls.main != null) {
			Method init = Cls.findMethod(appCls.main, "init");
			if (init != null) {
				AOP.invoke(x, init, app);
			}
		}

		U.notNull(screen, "Cannot find a screen to process the request!");
	}

	@Override
	public Object[] getSubViews() {
		return new Object[] { app, screen };
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

		pageContent = pageContent();

		Class<?>[] screens = APP_SCREENS.constructScreens(mainScreens);
		List<Class<?>> scaffolding = Scan.annotated(Scaffold.class);

		Object[] menuItems = new Object[screens.length + scaffolding.size()];
		int activeIndex = setupMenuItems(x.path(), screens, menuItems, scaffolding);

		boolean showNavbar = Apps.config(app, "navbar", true);

		String content = PageRenderer.get().toHTML(pageContent, x);

		Map<String, Object> model = U.map("navbar", showNavbar, "fluid", isFluid(), "title", title(), "content",
				content, "state", "{}", "screen", true);

		ITemplate page = Templates.fromFile("page.html");

		x.render(page, model);

		Pages.store(x, screen);

		return x;
	}

	protected Object genericScreen() {
		String path = x.path();

		if (path.equals("/")) {
			return appCls.main != null ? app : new Object();
		}

		for (Class<?> scr : BUILT_IN_SCREENS) {
			if (Apps.screenUrl(scr).equals(path)) {
				return Cls.newInstance(scr);
			}
		}

		if (!x.query().isEmpty()) {
			return null;
		}

		Matcher m = ENTITY_EDIT.matcher(path);

		if (m.find()) {
			String type = m.group(1);
			String id = m.group(2);

			Class<?> entityType = Scaffolding.getScaffoldingEntity(type);
			if (entityType == null) {
				return null;
			}

			Object entity = DB.getIfExists(entityType, id);

			String entityClass = Cls.entityName(entity);
			String reqType = U.capitalized(type);

			if (entityClass.equals(reqType)) {
				return new EditEntityScreenGeneric(entityType);
			}
		}

		m = ENTITY_NEW.matcher(path);

		if (m.find()) {
			String type = m.group(1);

			Class<?> entityType = Scaffolding.getScaffoldingEntity(type);
			if (entityType == null) {
				return null;
			}

			return new NewEntityScreenGeneric(entityType);
		}

		m = ENTITY_VIEW.matcher(path);

		if (m.find()) {
			String type = m.group(1);
			String id = m.group(2);

			Class<?> entityType = Scaffolding.getScaffoldingEntity(type);
			if (entityType == null) {
				return null;
			}

			Object entity = DB.getIfExists(entityType, id);

			String entityClass = Cls.entityName(entity);
			String reqType = U.capitalized(type);

			if (entityClass.equals(reqType)) {
				return new ViewEntityScreenGeneric(entityType);
			}
		}

		m = ENTITY_LIST.matcher(path);

		if (m.find()) {
			String type = m.group(1);
			String type2 = U.or(Languages.pluralToSingular(type), type);

			Class<?> entityType = Scaffolding.getScaffoldingEntity(type2);
			if (entityType == null) {
				return null;
			}

			return new ListEntityScreenGeneric(entityType);
		}

		return null;
	}

	protected Object pageContent() {
		Object cnt = Pages.contentOf(x, screen);
		if (cnt != null) {
			return cnt;
		} else {
			if (screen != null && (screen != app || x.uri().equals("/") || x.uri().equals("/index.html"))) {
				return "No content available!";
			} else {
				throw x.notFound();
			}
		}
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

	protected int setupMenuItems(String currentUrl, Class<?>[] screens, Object[] menuItems, List<Class<?>> scaffolding) {

		int activeIndex = -1;
		int k = 0;
		int i = 0;

		for (; i < screens.length; i++) {
			Class<?> scr = screens[i];
			String name = Apps.screenName(scr);

			if (name.equalsIgnoreCase("about") || name.equalsIgnoreCase("help") || name.equalsIgnoreCase("settings")
					|| name.equalsIgnoreCase("search") || name.equalsIgnoreCase("profile")) {
				break;
			}

			String title = Cls.getFieldValue(scr, "title", UTILS.camelPhrase(name));
			String url = Apps.screenUrl(scr);
			menuItems[k++] = a(title).href(url);

			if (url.equals(currentUrl)) {
				activeIndex = i;
			}
		}

		for (int j = 0; j < scaffolding.size(); j++) {
			Class<?> scaff = scaffolding.get(j);
			String name = English.plural(scaff.getSimpleName());
			String title = UTILS.camelSplit(name);
			String url = "/" + name.toLowerCase();
			menuItems[k++] = a(title).href(url);

			if (url.equals(currentUrl)) {
				activeIndex = i + j;
			}
		}

		for (; i < screens.length; i++) {
			Class<?> scr = screens[i];
			String name = Apps.screenName(scr);
			String title = Cls.getFieldValue(scr, "title", UTILS.camelPhrase(name));
			String url = Apps.screenUrl(scr);
			menuItems[k++] = a(title).href(url);

			if (url.equals(currentUrl)) {
				activeIndex = i + scaffolding.size();
			}
		}

		return activeIndex;
	}

	protected boolean isFluid() {
		return config("fluid", false);
	}

	protected Object getScreen() {
		// TODO use screens.get(...) instead of iteration
		for (Class<?> screen : appCls.screens.values()) {
			if (Apps.screenUrl(screen).equals(x.path())) {
				x.authorize(screen);
				return Cls.newInstance(screen);
			}
		}

		return U.or(genericScreen(), app);
	}

	protected Map<String, Class<?>> filterScreens() {
		Object[] screensConfig = config("screens", null);

		if (screensConfig == null) {
			screensConfig = appCls.screens.values().toArray();
		}

		Map<String, Class<?>> filtered = new LinkedHashMap<String, Class<?>>();

		for (Object scr : screensConfig) {
			if (scr instanceof Class<?>) {
				Class<?> cls = (Class<?>) scr;
				Class<?> screenCls = appCls.screens.get(cls.getSimpleName());
				if (isScreenAllowed(screenCls)) {
					filtered.put(cls.getSimpleName(), screenCls);
				}
			} else if (scr instanceof String) {
				String name = U.capitalized((String) scr);
				Class<?> screenCls = appCls.screens.get(name);
				if (isScreenAllowed(screenCls)) {
					filtered.put(name, screenCls);
				}
			} else {
				throw U.rte("Expected class or string to represent a screen, but found: %s", scr);
			}
		}

		return filtered;
	}

	protected boolean isScreenAllowed(Class<?> screenCls) {
		return Secure.canAccessClass(AppCtx.username(), screenCls);
	}

	public void on(String cmd, Object[] args) {
		try {
			Pages.callCmdHandler(x, screen, new Cmd(cmd, false, args));
		} catch (Exception e) {
			Throwable cause = UTILS.rootCause(e);
			if (cause instanceof HttpSuccessException || cause instanceof HttpNotFoundException) {
				Pages.store(x, screen);
			}
			throw U.rte(e);
		}
	}

	@Override
	public String toString() {
		return "AppPageGeneric [screen=" + screen + "]";
	}

}
