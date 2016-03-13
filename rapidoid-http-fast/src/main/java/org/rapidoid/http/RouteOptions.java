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
import org.rapidoid.annotation.TransactionMode;
import org.rapidoid.commons.Coll;
import org.rapidoid.commons.MediaType;
import org.rapidoid.u.U;

import java.util.List;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class RouteOptions implements RouteConfig {

	private volatile MediaType contentType = MediaType.HTML_UTF_8;

	private volatile String view;

	private volatile boolean mvc;

	private volatile TransactionMode transactionMode = TransactionMode.NONE;

	private final Set<String> roles = Coll.synchronizedSet();

	private final List<HttpWrapper> wrappers = Coll.synchronizedList();

	@Override
	public String toString() {
		String prefix = mvc ? "MVC" : "";
		return prefix + "{" +
				(contentType != null ? "contentType=" + contentType.info() : "") +
				(view != null ? ", view='" + view + '\'' : "") +
				(transactionMode != null ? ", transactionMode='" + transactionMode + '\'' : "") +
				(U.notEmpty(roles) ? ", roles=" + roles : "") +
				(U.notEmpty(wrappers) ? ", wrappers=" + wrappers : "") +
				'}';
	}

	@Override
	public MediaType contentType() {
		return contentType;
	}

	@Override
	public RouteOptions contentType(MediaType contentType) {
		this.contentType = contentType;
		return this;
	}

	@Override
	public String view() {
		return view;
	}

	@Override
	public RouteOptions view(String view) {
		this.view = view;
		return this;
	}

	@Override
	public boolean mvc() {
		return mvc;
	}

	@Override
	public RouteOptions mvc(boolean mvc) {
		this.mvc = mvc;
		return this;
	}

	@Override
	public TransactionMode transactionMode() {
		return transactionMode;
	}

	@Override
	public RouteOptions transactionMode(TransactionMode transactionMode) {
		this.transactionMode = transactionMode;
		return this;
	}

	@Override
	public Set<String> roles() {
		return roles;
	}

	@Override
	public RouteOptions roles(String... roles) {
		Coll.assign(this.roles, roles);
		return this;
	}

	@Override
	public HttpWrapper[] wrappers() {
		return wrappers.toArray(new HttpWrapper[wrappers.size()]);
	}

	@Override
	public RouteOptions wrap(HttpWrapper... wrappers) {
		Coll.assign(this.wrappers, wrappers);
		return this;
	}

	public RouteOptions copy() {
		RouteOptions copy = new RouteOptions();

		copy.contentType(contentType());
		copy.view(view());
		copy.mvc(mvc());
		copy.transactionMode(transactionMode());
		copy.roles(roles.toArray(new String[roles.size()]));
		copy.wrap(wrappers());

		return copy;
	}
}
