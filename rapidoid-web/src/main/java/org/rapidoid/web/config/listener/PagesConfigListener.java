package org.rapidoid.web.config.listener;

/*
 * #%L
 * rapidoid-web
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.setup.On;
import org.rapidoid.setup.OnRoute;
import org.rapidoid.u.U;
import org.rapidoid.web.config.bean.PageConfig;
import org.rapidoid.web.handler.PageHandler;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class PagesConfigListener extends GenericRouteConfigListener<PageConfig> {

	public PagesConfigListener() {
		super(PageConfig.class);
	}

	@Override
	protected OnRoute addRoute(HttpVerb verb, String uri) {
		if (verb == null) {
			return On.page(uri);
		} else {
			U.must(verb == HttpVerb.GET || verb == HttpVerb.POST, "Only GET and POST verbs are supported for pages!");
			return On.route(verb.name(), uri);
		}
	}

	@Override
	protected void addHandler(PageConfig page, String uri, OnRoute route) {

		if (page.view != null) route.view(page.view);
		if (page.zone != null) route.zone(page.zone);

		PageHandler handler = new PageHandler(page);

		// MVC by default
		if (Boolean.FALSE.equals(page.mvc)) {
			route.html(handler);
		} else {
			route.mvc(handler);
		}
	}

}
