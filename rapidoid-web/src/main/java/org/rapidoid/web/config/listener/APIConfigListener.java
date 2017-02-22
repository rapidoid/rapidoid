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
import org.rapidoid.web.config.bean.APIConfig;
import org.rapidoid.web.handler.APIHandler;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class APIConfigListener extends GenericRouteConfigListener<APIConfig> {

	public APIConfigListener() {
		super(APIConfig.class);
	}

	@Override
	protected OnRoute addRoute(HttpVerb verb, String uri) {
		verb = U.or(verb, HttpVerb.GET);
		return On.route(verb.name(), uri);
	}

	@Override
	protected void addHandler(APIConfig api, String uri, OnRoute route) {
		route.json(new APIHandler(api));
	}

}
