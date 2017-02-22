package org.rapidoid.web;

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
import org.rapidoid.config.bean.APIConfig;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class APIConfigListener extends GenericRouteConfigListener<APIConfig> {

	public APIConfigListener() {
		super(APIConfig.class);
	}

	@Override
	protected void addHandler(final APIConfig api, final HttpVerb verb, String uri) {
		On.route(verb.name(), uri).json(new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				return handle(verb, api);
			}
		});
	}

	public Object handle(HttpVerb verb, APIConfig api) {
		if (verb == HttpVerb.GET) {
			return JDBC.query(api.sql);
		} else {
			int changes = JDBC.execute(api.sql);
			return U.map("success", true, "changes", changes); // FIXME improve
		}
	}
}
