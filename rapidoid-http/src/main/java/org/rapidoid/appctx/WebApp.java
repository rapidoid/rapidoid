package org.rapidoid.appctx;

import java.util.Collections;
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
public class WebApp implements Application {

	private final String id;

	private final String title;

	private final Set<String> owners;

	private final Set<String> hostnames;

	private final Set<String> uriContexts;

	private final AppMode mode;

	private final Router router;

	private final Classes classes;

	@SuppressWarnings("unchecked")
	public WebApp(String id, String title, Set<String> owners, Set<String> hostnames, Set<String> uriContexts,
			AppMode mode, Router router, Classes classes) {
		this.id = id;
		this.router = U.or(router, new HttpRouter());
		this.title = U.or(title, "App");
		this.owners = U.or(owners, Collections.EMPTY_SET);
		this.hostnames = U.or(hostnames, Collections.EMPTY_SET);
		this.uriContexts = U.or(uriContexts, Collections.EMPTY_SET);
		this.mode = U.or(mode, AppMode.DEVELOPMENT);
		this.classes = U.or(classes, new Classes());
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Set<String> getOwners() {
		return owners;
	}

	@Override
	public Set<String> getHostnames() {
		return hostnames;
	}

	@Override
	public Set<String> getUriContexts() {
		return uriContexts;
	}

	@Override
	public AppMode getMode() {
		return mode;
	}

	@Override
	public Router getRouter() {
		return router;
	}

	@Override
	public Classes getClasses() {
		return classes;
	}

	@Override
	public String toString() {
		return "Application [id=" + id + ", title=" + title + ", owners=" + owners + ", hostnames=" + hostnames
				+ ", uriPaths=" + uriContexts + ", mode=" + mode + ", classes #" + classes.size() + "]";
	}

	@Override
	public boolean dev() {
		return mode == AppMode.DEVELOPMENT;
	}

}
