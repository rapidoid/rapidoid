package org.rapidoid.goodies;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.GUI;
import org.rapidoid.html.Tag;
import org.rapidoid.html.tag.TableTag;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Route;
import org.rapidoid.http.RouteConfig;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.*;
import java.util.concurrent.Callable;

/*
 * #%L
 * rapidoid-web
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RoutesHandler extends GUI implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		List<Object> routes = U.list();

		Set<Route> appRoutes = On.setup().routes().allNonAdmin();

		Set<Route> adminRoutes = On.setup().routes().allAdmin();
		adminRoutes.addAll(Admin.setup().routes().allAdmin());

		routes.add(div(h3("Application routes:"), routesOf(appRoutes, true)));
		routes.add(div(h3("Admin routes:"), routesOf(adminRoutes, true)));

		return multi(routes);
	}

	public static TableTag routesOf(Set<Route> httpRoutes, boolean withHandler) {
		List<Route> routes = U.list(httpRoutes);
		sortRoutes(routes);

		List<Object> rows = U.list();
		rows.add(tr(th("Verb"), th("Path"), th("Zone"), th("Content type"), th("MVC"), th("View name"), th("Roles"), withHandler ? th("Handler") : null));

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

			rows.add(routeRow(route, verbs, withHandler));
		}


		return table_(rows);
	}

	private static boolean sameTarget(Route a, Route b) {
		return !a.verb().equals(b.verb())
			&& a.path().equals(b.path())
			&& a.handler() == b.handler()
			&& a.config().equals(b.config());
	}

	private static void sortRoutes(List<Route> routes) {
		Collections.sort(routes, new Comparator<Route>() {

			@Override
			public int compare(Route a, Route b) {
				int cmpByPath = a.path().compareTo(b.path());
				return cmpByPath != 0 ? cmpByPath : a.verb().compareTo(b.verb());
			}

		});
	}

	private static Tag routeRow(Route route, List<HttpVerb> verbs, boolean withHandler) {
		RouteConfig config = route.config();

		Tag verb = td();
		for (HttpVerb vrb : verbs) {
			verb = verb.append(verb(vrb));
		}

		Tag path = td(route.path());
		Tag zone = td(config.zone());
		Tag roles = td(display(config.roles().isEmpty() ? "" : config.roles()));
		Tag hnd = td(route.handler());

		Tag ctype = td(config.contentType().info());

		String viewName = config.mvc() ? viewName(route, config) : "";
		Tag view = td(viewName);

		Tag mvc = td(config.mvc() ? fa("check") : "");

		return tr(verb, path, zone, ctype, mvc, view, roles, withHandler ? hnd : null);
	}

	private static String viewName(Route route, RouteConfig config) {
		return config.view() != null ? config.view() : route.path().substring(1) + " (AUTO)";
	}

}
