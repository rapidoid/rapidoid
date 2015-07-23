package org.rapidoid.appctx;

import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.util.U;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
@Since("4.1.0")
public class Applications {

	private static final Applications MAIN = new Applications("main");

	private final String name;

	private final Map<String, Map<String, Application>> appsByURL = U.mapOfMaps();

	private Application defaultApp;

	public Applications(String name) {
		this.name = name;
	}

	public static Applications main() {
		return MAIN;
	}

	public synchronized void setDefaultApp(Application defaultApp) {
		this.defaultApp = defaultApp;
	}

	public synchronized void register(Application app) {
		Set<String> hosts = app.getHostnames();

		if (hosts.isEmpty()) {
			hosts = U.set("*");
		}

		for (String hostname : hosts) {
			for (String uriPath : app.getUriContexts()) {
				U.must(!appsByURL.get(hostname).containsKey(uriPath),
						"An application has already been registered with the URI path: %s", uriPath);
				appsByURL.get(hostname).put(uriPath, app);
			}
		}
	}

	public synchronized void unregister(Application app) {
		throw U.notReady();
	}

	public Application get(String hostname, String uriContext) {
		if (appsByURL.containsKey(hostname)) {
			return U.or(appsByURL.get(hostname).get(uriContext), defaultApp);
		} else {
			return U.or(appsByURL.get("*").get(uriContext), defaultApp);
		}
	}

	@Override
	public String toString() {
		return "Applications [name=" + name + ", appsByURL=" + appsByURL + ", defaultApp=" + defaultApp + "]";
	}

	public static RootApplication root() {
		return new RootApplication();
	}

	public static Application openRootContext() {
		Application app = Applications.root();
		Applications.main().setDefaultApp(app);

		Ctxs.open();
		Ctxs.ctx().setApp(app);

		Plugins.register(new AppClasspathEntitiesPlugin());

		return app;
	}

	public String getName() {
		return name;
	}

}
