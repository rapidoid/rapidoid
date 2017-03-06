package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-fast
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;
import org.rapidoid.http.customize.Customization;
import org.rapidoid.http.impl.HttpRoutesImpl;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.Date;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HttpRoutesGroup extends RapidoidThing {

	private static final int ROUTE_SETUP_WAITING_TIME_MS = Env.test() ? 300 : 500;

	private final HttpRoutesImpl[] routes;

	private volatile boolean initialized = false;

	public HttpRoutesGroup(HttpRoutesImpl... routes) {
		this.routes = routes;

		U.must(routes.length > 0, "Routes are missing!");
	}

	public HttpRoutesImpl[] all() {
		return routes;
	}

	public boolean hasRouteOrResource(HttpVerb verb, String uri) {
		for (HttpRoutesImpl route : routes) {
			if (route.hasRouteOrResource(verb, uri)) return true;
		}
		return false;
	}

	public Customization customization() {
		return routes[0].custom();
	}

	public Date lastChangedAt() {
		Date lastChangedAt = null;

		for (HttpRoutesImpl routes : routes) {
			Date changedAt = routes.lastChangedAt();

			if (lastChangedAt == null || U.compare(lastChangedAt, changedAt) < 0) {
				lastChangedAt = changedAt;
			}
		}

		return lastChangedAt;
	}

	public boolean isEmpty() {
		for (HttpRoutesImpl route : routes) {
			if (!route.isEmpty()) return false;
		}
		return true;
	}

	public boolean ready() {
		long lastChangedAt = lastChangedAt().getTime();
		return !isEmpty() && Msc.timedOut(lastChangedAt, ROUTE_SETUP_WAITING_TIME_MS);
	}

	public void reset() {
		for (HttpRoutesImpl route : routes) {
			route.reset();
			route.custom().reset();
		}

		initialized = false;
	}

	public void waitToInitialize() {
		while (!initialized) {
			U.sleep(1);
			if (ready()) {
				synchronized (this) {
					if (!initialized) {
						initialized = true;
						Log.info("Initialized HTTP routes");
					}
				}
			}
		}
	}

}
