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

import org.rapidoid.html.TagContext;
import org.rapidoid.html.impl.GuiContextImpl;
import org.rapidoid.util.U;

import com.rapidoid.http.HTTPServer;
import com.rapidoid.http.Handler;
import com.rapidoid.http.HttpExchange;

public class Pages {

	private static final String SESSION_CTX = "_ctx";

	public static TagContext context() {
		return new GuiContextImpl();
	}

	public static void registerPages(HTTPServer server) {
		server.post("/_emit", new Handler() {

			@Override
			public Object handle(HttpExchange x) throws Exception {
				String hnd = x.data("hnd");
				String event = x.data("event");

				U.notNull(hnd, "hnd");
				U.notNull(event, "event");

				TagContext ctx = x.session(SESSION_CTX);

				ctx.emit(hnd, event);

				x.json();
				return ctx.changedContent();
			}
		});
	}

}
