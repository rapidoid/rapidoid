package org.rapidoid.pages;

/*
 * #%L
 * rapidoid-pages
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

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.rapidoid.html.Cmd;
import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.inject.IoC;
import org.rapidoid.json.JSON;
import org.rapidoid.pages.impl.BuiltInCmdHandler;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoHandlerNotFoundException;
import org.rapidoid.rest.WebPojoDispatcher;
import org.rapidoid.rest.WebReq;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class Pages {

	private static final String PAGE_RELOAD = "<h2>Reloading...</h2><script>location.reload();</script>";

	public static final String SESSION_CTX = "_ctx";

	public static final String SESSION_CURRENT_PAGE = "_current_page_";

	private static final Pattern STATIC_RESOURCE_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\.\\-/]+$");

	private static final BuiltInCmdHandler BUILT_IN_HANDLER = new BuiltInCmdHandler();

	public static void registerPages(HTTPServer server) {
		server.post("/_emit", new EmitHandler());
		server.serve(new PageHandler());
	}

	@SuppressWarnings("unchecked")
	protected static Map<Integer, Object> inputs(HttpExchange x) {
		String inputs = x.data("inputs");
		U.notNull(inputs, "inputs");

		Map<Integer, Object> inputsMap = U.map();

		Map<String, Object> inp = JSON.parse(inputs, Map.class);
		for (Entry<String, Object> e : inp.entrySet()) {
			inputsMap.put(U.num(e.getKey()), e.getValue());
		}

		return inputsMap;
	}

	public static String pageName(HttpExchange x) {
		String path = x.path();

		if (path.endsWith(".html")) {
			path = U.mid(path, 0, -5);
		}

		if (path.equals("/")) {
			path = "/index";
		}

		return U.capitalized(path.substring(1));
	}

	public static String defaultPageTitle(Class<?> pageClass) {
		String pageName = pageClass.getSimpleName();

		if (pageName.endsWith("Page")) {
			pageName = U.mid(pageName, 0, -4);
		}

		return U.camelPhrase(pageName);
	}

	public static String titleOf(HttpExchange x, Object target) {
		Method m = Cls.findMethod(target.getClass(), "title", HttpExchange.class);

		if (m != null) {
			return Cls.invoke(m, target, x);
		}

		try {
			return Cls.getPropValue(target, "title");
		} catch (Exception e) {
			return defaultPageTitle(target.getClass());
		}
	}

	public static Object contentOf(HttpExchange x, Object target) {
		Method m = Cls.findMethod(target.getClass(), "content", HttpExchange.class);
		return m != null ? Cls.invoke(m, target, x) : Cls.getPropValue(target, "content", null);
	}

	public static Object page(HttpExchange x, Object page) {

		String pageTitle = titleOf(x, page);
		Object content = contentOf(x, page);

		if (content == null) {
			return null;
		}

		if (content instanceof HttpExchange) {
			U.must(x == content, "Different HTTP exchange than expected!");
			return x;
		}

		return BootstrapWidgets.page(x.devMode(), pageTitle, content);
	}

	public static Object render(HttpExchange x, Object page) {

		Object fullPage = page(x, page);
		if (fullPage != null) {
			if (fullPage instanceof HttpExchange) {
				return x;
			} else {
				x.html();
				TagContext ctx = x.session(SESSION_CTX);
				PageRenderer.get().render(ctx, fullPage, x);
				return x;
			}
		} else {
			return x.notFound();
		}
	}

	public static Object dispatch(HttpExchange x, WebPojoDispatcher serviceDispatcher, Map<String, Class<?>> pages) {

		if (x.isGetReq()) {
			String filename = x.path().substring(1);

			if (filename.isEmpty()) {
				filename = "index.html";
			}

			if (!filename.contains("..") && STATIC_RESOURCE_PATTERN.matcher(filename).matches()) {
				URL res = U.resource("public/" + filename);
				if (res != null) {
					return x.sendFile(new File(res.getFile()));
				}
			}
		}

		if (serviceDispatcher != null) {
			try {
				return serviceDispatcher.dispatch(new WebReq(x));
			} catch (PojoHandlerNotFoundException e) {
				// / just ignore, will try to dispatch a page next...
			} catch (PojoDispatchException e) {
				return x.response(500, "Cannot initialize handler argument(s)!", e);
			}
		}

		String pageName = Pages.pageName(x);
		if (pageName == null) {
			return null;
		}

		String pageClassName = U.capitalized(pageName) + "Page";

		Class<?> pageClass = pages.get(pageClassName);
		if (pageClass == null) {
			return null;
		}

		return serve(x, pageClass);
	}

	public static Object serve(HttpExchange x, Class<?> pageClass) {
		x.sessionSet(Pages.SESSION_CURRENT_PAGE, pageClass);

		Object page = U.newInstance(pageClass);

		load(x, page);

		TagContext ctx = Tags.context();
		x.sessionSet(Pages.SESSION_CTX, ctx);

		Object result = render(x, page);

		store(x, page);

		return result;
	}

	public static void load(HttpExchange x, Object target) {
		IoC.autowire(target, U.mapper(x.session()));
	}

	public static void store(HttpExchange x, Object target) {
		for (Field field : IoC.getSessionFields(target)) {
			Object value = Cls.getFieldValue(field, target);
			x.sessionSet(field.getName(), value);
		}
	}

	public static Object emit(HttpExchange x) {
		int event = U.num(x.data("event"));

		TagContext ctx = x.session(SESSION_CTX, null);

		// if the context has been lost, reload the page
		if (ctx == null) {
			return changes(x, PAGE_RELOAD);
		}

		Cmd cmd = ctx.getEventCmd(event);

		if (cmd != null) {
			Map<Integer, Object> inp = Pages.inputs(x);
			ctx.emitValues(inp);
		} else {
			U.warn("Invalid event!", "event", event);
		}

		Object page = U.newInstance(currentPage(x));
		Pages.load(x, page);

		callCmdHandler(x, page, cmd);

		ctx = Tags.context();
		x.sessionSet(Pages.SESSION_CTX, ctx);

		Object content = Pages.contentOf(x, page);

		if (content == null || content instanceof HttpExchange) {
			return content;
		}

		String html = PageRenderer.get().toHTML(ctx, content, x);

		Pages.store(x, page);

		return changes(x, html);
	}

	private static Object changes(HttpExchange x, String html) {
		Map<String, String> changes = U.map();
		changes.put("body", html);
		x.json();
		return changes;
	}

	public static void callCmdHandler(HttpExchange x, Object target, Cmd cmd) {

		if (cmd.name.startsWith("_")) {
			target = BUILT_IN_HANDLER;
		}

		String handlerName = "on" + U.capitalized(cmd.name);
		Method m = Cls.findMethodByArgs(target.getClass(), handlerName, cmd.args);

		if (m != null) {
			Cls.invoke(m, target, cmd.args);
			return;
		}

		Method on = Cls.findMethod(target.getClass(), "on", String.class, Object[].class);
		if (on != null) {
			Cls.invoke(on, target, cmd.name, cmd.args);
			return;
		}

		on = Cls.findMethod(target.getClass(), "on", HttpExchange.class, String.class, Object[].class);
		if (on != null) {
			Cls.invoke(on, target, x, cmd.name, cmd.args);
			return;
		}

		throw U.rte("Cannot find handler '%s' for the command '%s' and args: %s", handlerName, cmd.name, cmd.args);
	}

	public static Class<?> currentPage(HttpExchange x) {
		return x.session(Pages.SESSION_CURRENT_PAGE);
	}

}
