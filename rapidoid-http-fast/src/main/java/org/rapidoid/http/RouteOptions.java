package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http-fast
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
import org.rapidoid.commons.Coll;
import org.rapidoid.commons.MediaType;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RouteOptions {

	public volatile MediaType contentType = MediaType.HTML_UTF_8;

	public volatile String view;

	public volatile String title;

	public volatile boolean mvc;

	public final Set<String> roles = Coll.synchronizedSet();

	public final List<HttpWrapper> wrappers = Coll.synchronizedList();

	public RouteOptions wrap(HttpWrapper[] wrappers) {
		if (U.notEmpty(wrappers)) {
			Collections.addAll(this.wrappers, wrappers);
		}

		return this;
	}

	public RouteOptions roles(Set<String> roles) {
		this.roles.addAll(roles);

		return this;
	}

	@Override
	public String toString() {
		String prefix = mvc ? "MVC" : "";
		return prefix + "{" +
				(contentType != null ? "contentType=" + contentType.info() : "") +
				(view != null ? ", view='" + view + '\'' : "") +
				(title != null ? ", title='" + title + '\'' : "") +
				(U.notEmpty(roles) ? ", roles=" + roles : "") +
				(U.notEmpty(wrappers) ? ", wrappers=" + wrappers : "") +
				'}';
	}

	public RouteOptions render() {
		this.mvc = true;
		return this;
	}
}
