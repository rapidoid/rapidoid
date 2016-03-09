package org.rapidoid.goodies;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.Route;
import org.rapidoid.http.RouteConfig;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RoutesHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		List<Route> routes = U.list(On.routes().all());

		Collections.sort(routes, new Comparator<Route>() {
			@Override
			public int compare(Route a, Route b) {
				int cmpByPath = a.path().compareTo(b.path());

				return cmpByPath != 0 ? cmpByPath : a.verb().compareTo(b.verb());
			}
		});

		List<Object> rows = U.list();
		rows.add(tr(th("Verb"), th("Path"), th("Content type"), th("MVC"), th("View name"), th("Handler")));

		for (Route route : routes) {
			RouteConfig config = route.config();

			Tag verb = td(verb(route.verb()));
			Tag path = td(route.path());
			Tag hnd = td(route.handler());

			Tag ctype = td(config.contentType().info());

			String viewName = config.mvc() ? viewName(route, config) : "";
			Tag view = td(viewName);

			Tag mvc = td(config.mvc() ? "Yes" : "No");

			rows.add(tr(verb, path, ctype, mvc, view, hnd));
		}

		return div(h2("Application routes:"), table_(rows));
	}

	private String viewName(Route route, RouteConfig config) {
		return config.view() != null ? config.view() : HttpUtils.defaultView(route.path());
	}

}
