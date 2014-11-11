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

import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.html.TagContext;
import org.rapidoid.html.Tags;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.json.JSON;
import org.rapidoid.util.U;

public class Pages {

	public static final String SESSION_CTX = "_ctx";

	static final String SESSION_PAGE_PREFIX = "_page_";

	public static final String SESSION_PAGE = "_page";

	public static void registerPages(HTTPServer server) {
		registerEmitHandler(server);

		server.serve(new PageHandler());
	}

	public static void registerEmitHandler(HTTPServer server) {
		server.post("/_emit", new Handler() {

			@Override
			public Object handle(HttpExchange x) throws Exception {

				int hnd = U.num(x.data("hnd"));
				String event = x.data("event");

				U.notNull(hnd, "hnd");
				U.notNull(event, "event");

				TagContext ctx = x.session(SESSION_CTX);
				Page page = x.session(SESSION_PAGE);


				Map<Integer, Object> inp = inputs(x);
				ctx.emit(page.content(), inp, hnd, event);

				Map<Integer, String> changes = ctx.changes();
				x.json();
				return changes;
			}
		});
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
		TagContext ctx = x.session(SESSION_CTX, null);

		if (ctx == null) {
			ctx = Tags.context();
			x.setSession(SESSION_CTX, ctx);
		}

		return ctx;
	}

	public static String pageName(HttpExchange x) {
		String path = x.path();

		if (path.equals("/")) {
			path = "/index.html";
		}

		if (path.length() > 6 && path.endsWith(".html")) {
			return U.capitalized(U.mid(path, 1, -5));
		} else {
			return null;
		}
	}

	public static String pageTitle(Class<? extends Page> pageClass) {
		String pageName = pageClass.getSimpleName();

		if (pageName.endsWith("Page")) {
			pageName = U.mid(pageName, 0, -4);
		}

		return pageName;
	}

}
