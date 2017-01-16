package org.rapidoid.http.handler;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpWrapper;
import org.rapidoid.http.MediaType;
import org.rapidoid.http.Route;
import org.rapidoid.http.impl.RouteOptions;
import org.rapidoid.u.U;

import java.util.Map;

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

@Authors("Nikolche Mihajlovski")
@Since("4.3.0")
public abstract class AbstractHttpHandler extends RapidoidThing implements HttpHandler {

	protected final RouteOptions options;

	protected final MediaType contentType;

	protected final HttpWrapper[] httpWrappers;

	protected volatile Route route;

	public AbstractHttpHandler(RouteOptions options) {
		this.options = options;
		this.contentType = options.contentType();
		this.httpWrappers = options.wrappers();
	}

	@Override
	public boolean needsParams() {
		return false;
	}

	@Override
	public MediaType contentType() {
		return contentType;
	}

	@Override
	public Map<String, String> getParams() {
		return null;
	}

	@Override
	public HttpHandler getHandler() {
		return this;
	}

	@Override
	public RouteOptions options() {
		return options;
	}

	protected String contentTypeInfo(String inside) {
		String type;
		if (contentType == MediaType.HTML_UTF_8) {
			type = options.mvc() ? "mvc" : "html";

		} else if (contentType == MediaType.JSON) {
			type = "json";

		} else if (contentType == MediaType.PLAIN_TEXT_UTF_8) {
			type = "plain";

		} else if (contentType == MediaType.BINARY) {
			type = "binary";

		} else {
			return inside;
		}

		return U.frmt("%s(%s)", type, inside);
	}

	@Override
	public void setRoute(Route route) {
		this.route = route;
	}

	@Override
	public Route getRoute() {
		return route;
	}

}
