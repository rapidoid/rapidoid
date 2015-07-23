package org.rapidoid.apps;

import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Classes;

/*
 * #%L
 * rapidoid-appctx
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
public class Application {

	private final String id;

	private final String title;

	private final Set<String> owners;

	private final Set<String> hostnames;

	private final Set<String> uriPaths;

	private final AppMode mode;

	private final Classes classes;

	public Application(String id, String title, Set<String> owners, Set<String> hostnames, Set<String> uriPaths,
			AppMode mode, Classes classes) {
		this.id = id;
		this.title = title;
		this.owners = owners;
		this.hostnames = hostnames;
		this.uriPaths = uriPaths;
		this.mode = mode;
		this.classes = classes;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Set<String> getOwners() {
		return owners;
	}

	public Set<String> getHostnames() {
		return hostnames;
	}

	public Set<String> getUriPaths() {
		return uriPaths;
	}

	public AppMode getMode() {
		return mode;
	}

	public Classes getClasses() {
		return classes;
	}

	@Override
	public String toString() {
		return "Application [id=" + id + ", title=" + title + ", owners=" + owners + ", hostnames=" + hostnames
				+ ", uriPaths=" + uriPaths + ", mode=" + mode + ", classes #" + classes.size() + "]";
	}

}
