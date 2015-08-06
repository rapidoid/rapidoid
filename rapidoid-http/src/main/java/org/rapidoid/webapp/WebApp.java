package org.rapidoid.webapp;

import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Classes;
import org.rapidoid.http.HttpRouter;
import org.rapidoid.http.Router;
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
public class WebApp {

	private final String id;

	private final Set<String> owners;

	private final Set<String> hostnames;

	private final Set<String> uriContexts;

	private final AppMode mode;

	private final Router router;

	private final Classes classes;

	private volatile String title;

	private volatile AppMenu menu;

	public WebApp(String id, String title, Set<String> owners, Set<String> hostnames, Set<String> uriContexts,
			AppMode mode, Router router, Classes classes) {
		this.id = id;
		this.router = U.or(router, new HttpRouter());
		this.title = U.or(title, "App");
		this.owners = U.safe(owners);
		this.hostnames = U.safe(hostnames);
		this.uriContexts = U.safe(uriContexts);
		this.mode = U.or(mode, AppMode.DEVELOPMENT);
		this.classes = U.or(classes, new Classes());
	}

	public WebApp(String id, String title, String uriPath, Classes classes) {
		this(id, title, null, null, uriPath != null ? U.set(uriPath) : null, AppMode.DEVELOPMENT, null, classes);
	}

	public WebApp(String id, String uriPath, Classes classes) {
		this(id, id, uriPath, classes);
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
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

}
