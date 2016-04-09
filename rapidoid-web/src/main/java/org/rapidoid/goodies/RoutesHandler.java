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
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.http.HttpUtils;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Route;
import org.rapidoid.http.RouteConfig;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.On;
import org.rapidoid.setup.Setup;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RoutesHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		List<Object> routes = U.list();

		routes.add(div(h2("Application routes:"), routesOf(On.setup())));
		routes.add(div(h2("Admin routes:"), routesOf(Admin.setup())));

		return multi(routes);
	}

	private TableTag routesOf(Setup setup) {
		List<Route> routes = U.list(setup.getRoutes().all());

		sortRoutes(routes);

		List<Object> rows = U.list();
		rows.add(tr(th("Verb"), th("Path"), th("Sector"), th("Content type"), th("MVC"), th("View name"), th("Roles"), th("Handler")));

		while (!routes.isEmpty()) {
			Route route = U.first(routes);
			List<HttpVerb> verbs = U.list(route.verb());

			Iterator<Route> it = routes.iterator();
			while (it.hasNext()) {
				Route other = it.next();

				if (route == other) {
					it.remove();
				} else if (sameTarget(route, other)) {
					verbs.add(other.verb());
					it.remove();
				}
			}

			rows.add(routeRow(route, verbs));
		}


		return table_(rows);
	}

	private boolean sameTarget(Route a, Route b) {
		return !a.verb().equals(b.verb())
				&& a.path().equals(b.path())
				&& a.handler() == b.handler()
				&& a.config().equals(b.config());
	}

	private void sortRoutes(List<Route> routes) {
		Collections.sort(routes, new Comparator<Route>() {
			@Override
			public int compare(Route a, Route b) {
				int cmpByPath = a.path().compareTo(b.path());

				return cmpByPath != 0 ? cmpByPath : a.verb().compareTo(b.verb());
			}
		});
	}

	private Tag routeRow(Route route, List<HttpVerb> verbs) {
		RouteConfig config = route.config();

		Tag verb = td();
		for (HttpVerb vrb : verbs) {
			verb = verb.append(verb(vrb));
		}

		Tag path = td(route.path());
		Tag sector = td(config.sector());
		Tag roles = td(config.roles());
		Tag hnd = td(route.handler());

		Tag ctype = td(config.contentType().info());

		String viewName = config.mvc() ? viewName(route, config) : "";
		Tag view = td(viewName);

		Tag mvc = td(config.mvc() ? "Yes" : "No");

		return tr(verb, path, sector, ctype, mvc, view, roles, hnd);
	}

	private String viewName(Route route, RouteConfig config) {
		return config.view() != null ? config.view() : HttpUtils.defaultView(route.path());
	}

}
