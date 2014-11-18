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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.inject.IoC;
import org.rapidoid.json.JSON;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.pojo.PojoDispatchException;
import org.rapidoid.pojo.PojoHandlerNotFoundException;
import org.rapidoid.rest.WebPojoDispatcher;
import org.rapidoid.rest.WebReq;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class Pages {

	public static final String SESSION_CTX = "_ctx";

	public static final String SESSION_CURRENT_PAGE = "_current_page_";

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

	public static TagContext ctx(HttpExchange x) {
		return x.session(SESSION_CTX);
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
		return m != null ? Cls.invoke(m, target, x) : Cls.getPropValue(target, "content");
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

		return BootstrapWidgets.page(pageTitle, content);
	}

	public static Object render(HttpExchange x, Object page) {

		Object fullPage = page(x, page);
		if (fullPage != null) {
			if (fullPage instanceof HttpExchange) {
				return x;
			} else {
				x.html();
				PageRenderer.get().render(ctx(x), fullPage, x);
				return x;
			}
		} else {
			return x.notFound();
		}
	}

	public static Object dispatch(HttpExchange x, WebPojoDispatcher serviceDispatcher, Map<String, Class<?>> pages) {

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
		x.setSession(Pages.SESSION_CURRENT_PAGE, pageClass);

		Object page = U.newInstance(pageClass);

		load(x, page);

		TagContext ctx = Tags.context();
		x.setSession(Pages.SESSION_CTX, ctx);

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
			x.setSession(field.getName(), value);
		}
	}

	public static Object emit(HttpExchange x) {
		int event = U.num(x.data("event"));

		TagContext ctx = Pages.ctx(x);

		Map<Integer, Object> inp = Pages.inputs(x);
		ctx.emit(inp, event);

		Object page = U.newInstance(currentPage(x));
		Pages.load(x, page);

		ctx = Tags.context();
		x.setSession(Pages.SESSION_CTX, ctx);

		Object content = Pages.contentOf(x, page);

		if (content == null || content instanceof HttpExchange) {
			return content;
		}

		String html = PageRenderer.get().toHTML(ctx, content, x);

		Pages.store(x, page);

		Map<String, String> changes = U.map();
		changes.put("body", html);

		x.json();
		return changes;
	}

	public static Class<?> currentPage(HttpExchange x) {
		return x.session(Pages.SESSION_CURRENT_PAGE);
	}

}
