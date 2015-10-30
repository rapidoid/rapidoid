package org.rapidoid.webapp;

import java.util.Map;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.plugins.Plugins;
import org.rapidoid.u.U;

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
public class WebAppGroup {

	private static final WebAppGroup MAIN = new WebAppGroup("main");

	private final String name;

	private final Map<String, Map<String, WebApp>> appsByURL = U.mapOfMaps();

	private volatile WebApp defaultApp;

	public WebAppGroup(String name) {
		this.name = name;
	}

	public static WebAppGroup main() {
		return MAIN;
	}

	public synchronized void setDefaultApp(WebApp defaultApp) {
		this.defaultApp = defaultApp;
	}

	public synchronized void register(WebApp app) {
		Set<String> hosts = app.getHostnames();

		if (hosts.isEmpty()) {
			hosts = U.set("*");
		}

		for (String hostname : hosts) {
			for (String uriPath : app.getUriContexts()) {
				U.must(!appsByURL.get(hostname).containsKey(uriPath),
						"An WebApp has already been registered with the URI path: %s", uriPath);
				appsByURL.get(hostname).put(uriPath, app);
			}
		}
	}

	public synchronized void unregister(WebApp app) {
		throw U.notReady();
	}

	public WebApp get(String hostname, String uriContext) {
		if (appsByURL.containsKey(hostname)) {
			return U.or(appsByURL.get(hostname).get(uriContext), defaultApp);
		} else {
			return U.or(appsByURL.get("*").get(uriContext), defaultApp);
		}
	}

	@Override
	public String toString() {
		return "WebApps [name=" + name + ", appsByURL=" + appsByURL + ", defaultApp=" + defaultApp + "]";
	}

	public static RootWebApp root() {
		return new RootWebApp();
	}

	public static WebApp openRootContext() {
		WebApp app = WebAppGroup.root();
		WebAppGroup.main().setDefaultApp(app);

		Ctxs.open("root");
		Ctxs.ctx().setApp(app);

		Plugins.register(new AppClasspathEntitiesPlugin());

		return app;
	}

	public String getName() {
		return name;
	}

	public synchronized void clear() {
		appsByURL.clear();
		defaultApp = null;
	}

}
