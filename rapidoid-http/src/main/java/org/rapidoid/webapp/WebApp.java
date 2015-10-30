package org.rapidoid.webapp;

import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Config;
import org.rapidoid.ctx.Classes;
import org.rapidoid.dispatch.PojoDispatcher;
import org.rapidoid.http.HttpRouter;
import org.rapidoid.http.Router;
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
public class WebApp {

	private final String id;

	private final Set<String> owners;

	private final Set<String> hostnames;

	private final Set<String> uriContexts;

	private final AppMode mode;

	private final Router router;

	private final PojoDispatcher dispatcher;

	private final Classes classes;

	private final Config config;

	private volatile AppMenu menu;

	public WebApp(String id, Set<String> owners, Set<String> hostnames, Set<String> uriContexts, AppMode mode,
			Router router, PojoDispatcher dispatcher, Classes classes, Config config) {
		this.id = id;
		this.router = U.or(router, new HttpRouter());
		this.dispatcher = U.or(dispatcher, new WebPojoDispatcher(classes));
		this.owners = U.safe(owners);
		this.hostnames = U.safe(hostnames);
		this.uriContexts = U.safe(uriContexts);
		this.mode = U.or(mode, AppMode.DEVELOPMENT);
		this.classes = U.or(classes, new Classes());
		this.config = U.or(config, new Config());
	}

	public WebApp(String id, String uriPath, Classes classes) {
		this(id, null, null, uriPath != null ? U.set(uriPath) : null, AppMode.DEVELOPMENT, null, null, classes, null);
	}

	public WebApp(String id) {
		this(id, "/", null);
	}

	public WebApp() {
		this("app-id");
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return config.option("title", null);
	}

	public void setTitle(String title) {
		config.put("title", title);
	}

	public Set<String> getOwners() {
		return owners;
	}

	public Set<String> getHostnames() {
		return hostnames;
	}

	public Set<String> getUriContexts() {
		return uriContexts;
	}

	public AppMode getMode() {
		return mode;
	}

	public Router getRouter() {
		return router;
	}

	public PojoDispatcher getDispatcher() {
		return dispatcher;
	}

	public Classes getClasses() {
		return classes;
	}

	public boolean dev() {
		return getMode().equals(AppMode.DEVELOPMENT);
	}

	public AppMenu getMenu() {
		return menu;
	}

	public void setMenu(AppMenu menu) {
		this.menu = menu;
	}

	public Config getConfig() {
		return config;
	}

}
