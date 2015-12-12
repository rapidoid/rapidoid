package org.rapidoid.web;

import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Config;
import org.rapidoid.ctx.Classes;
import org.rapidoid.dispatch.PojoDispatcher;
import org.rapidoid.http.fast.handler.FastHttpHandler;
import org.rapidoid.u.U;

/*
 * #%L
 * rapidoid-web
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

	private final PojoDispatcher dispatcher;

	private final Classes classes;

	private final Config config;

	private final FastHttpHandler handler;

	private volatile Object menu;

	public WebApp(String id, Set<String> owners, Set<String> hostnames, Set<String> uriContexts, AppMode mode,
			PojoDispatcher dispatcher, Classes classes, Config config, FastHttpHandler handler) {
		this.id = id;
		this.dispatcher = U.or(dispatcher, new WebPojoDispatcher(classes));
		this.owners = U.safe(owners);
		this.hostnames = U.safe(hostnames);
		this.uriContexts = U.safe(uriContexts);
		this.mode = U.or(mode, AppMode.DEVELOPMENT);
		this.classes = U.or(classes, new Classes());
		this.config = U.or(config, new Config());
		this.handler = handler;
	}

	public WebApp(String id, String uriPath, Classes classes, FastHttpHandler handler) {
		this(id, null, null, uriPath != null ? U.set(uriPath) : null, AppMode.DEVELOPMENT, null, classes, null, handler);
	}

	public WebApp(String id, FastHttpHandler handler) {
		this(id, "/", null, handler);
	}

	public WebApp(FastHttpHandler handler) {
		this("app-id", handler);
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

	public PojoDispatcher getDispatcher() {
		return dispatcher;
	}

	public Classes getClasses() {
		return classes;
	}

	public boolean dev() {
		return getMode().equals(AppMode.DEVELOPMENT);
	}

	public Config getConfig() {
		return config;
	}

	public void setMenu(Object menu) {
		this.menu = menu;
	}

	public Object getMenu() {
		return menu;
	}

	public FastHttpHandler getHandler() {
		return handler;
	}

}
