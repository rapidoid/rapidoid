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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.html.Tag;
import org.rapidoid.html.TagContext;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.json.JSON;
import org.rapidoid.pages.impl.PageRenderer;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

public class Pages {

	public static final String SESSION_CTX = "_ctx";

	public static final String SESSION_PAGE_NAME = "_page_name";

	public static void registerPages(HTTPServer server) {

		Map<String, Class<?>> pages = U.map();
		List<Class<?>> pageClasses = U.classpathClassesBySuffix("Page", null, null);

		for (Class<?> cls : pageClasses) {
			pages.put(cls.getSimpleName(), cls);
		}

		server.post("/_emit", new EmitHandler(pages));
		server.serve(new PageHandler(pages));
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

	public static String pageTitle(Class<?> pageClass) {
		String pageName = pageClass.getSimpleName();

		if (pageName.endsWith("Page")) {
			pageName = U.mid(pageName, 0, -4);
		}

		return U.camelPhrase(pageName);
	}

	public static String titleOf(Object page) {
		if (page.getClass().getSimpleName().endsWith("Page")) {
			return pageTitle(page.getClass());
		} else {
			return Cls.getPropValue(page, "title");
		}
	}

	public static Tag<?> contentOf(HttpExchange x, Object page) {
		Method m = Cls.findMethod(page.getClass(), "content", HttpExchange.class);
		return (Tag<?>) (m != null ? Cls.invoke(m, page, x) : Cls.getPropValue(page, "content"));
	}

	public static Tag<?> page(HttpExchange x, Object page) {
		String pageTitle = titleOf(page);
		Tag<?> body = contentOf(x, page);

		return BootstrapWidgets.page(pageTitle, body);
	}

	public static HttpExchange render(HttpExchange x, Object page) {

		Object fullPage = page(x, page);

		x.html();
		PageRenderer.get().render(ctx(x), fullPage, x);
		return x;
	}

}
