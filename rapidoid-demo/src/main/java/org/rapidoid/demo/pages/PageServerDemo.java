package org.rapidoid.demo.pages;

/*
 * #%L
 * rapidoid-demo
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

import org.rapidoid.util.U;

import com.rapidoid.http.HTTP;
import com.rapidoid.http.HTTPServer;
import com.rapidoid.http.Handler;
import com.rapidoid.http.HttpExchange;

public class PageServerDemo {

	public static void main(String[] args) {
		U.args(args);

		HTTPServer server = HTTP.server().build();

		server.get("/", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return x.sessionGetOrCreate("page", ShowcasePage.class, U.rndStr(11));
			}
		});

		server.get("/del", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				x.closeSession();
				return x.html().write(x.session().toString());
			}
		});

		server.post("/_emit", new Handler() {

			@Override
			public Object handle(HttpExchange x) throws Exception {
				String hnd = x.data("hnd");
				String event = x.data("event");

				U.notNull(hnd, "hnd");
				U.notNull(event, "event");

				ShowcasePage page = x.session("page");
				page.emit(event, hnd);

				Map<String, String> diff = page.changedContent();
				x.json();
				return diff;
			}
		});

		server.start();

	}
}
