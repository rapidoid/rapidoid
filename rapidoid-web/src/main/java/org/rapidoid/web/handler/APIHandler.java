package org.rapidoid.web.handler;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.u.U;
import org.rapidoid.web.config.bean.APIConfig;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class APIHandler extends RapidoidThing implements ReqRespHandler {

	private final APIConfig api;

	private final HttpVerb verb;

	public APIHandler(APIConfig api, HttpVerb verb) {
		this.api = api;
		this.verb = verb;
	}

	@Override
	public Object execute(Req req, Resp resp) {
		if (verb == HttpVerb.GET) {
			return JDBC.query(api.sql);
		} else {
			int changes = JDBC.execute(api.sql);
			return U.map("success", true, "changes", changes); // FIXME improve
		}
	}

}
