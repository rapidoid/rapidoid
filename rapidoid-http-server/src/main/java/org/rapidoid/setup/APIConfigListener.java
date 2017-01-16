package org.rapidoid.setup;

/*
 * #%L
 * rapidoid-http-server
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
import org.rapidoid.config.ConfigChanges;
import org.rapidoid.config.bean.APIConfig;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.lambda.Operation;
import org.rapidoid.u.U;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class APIConfigListener extends RapidoidThing implements Operation<ConfigChanges> {

	@Override
	public void execute(ConfigChanges changes) throws Exception {
		for (Map.Entry<String, APIConfig> e : changes.getAddedOrChangedAs(APIConfig.class).entrySet()) {

			String apiKey = e.getKey().trim();
			APIConfig api = e.getValue();

			applyAPIEntry(apiKey, api);
		}
	}

	private void applyAPIEntry(String apiKey, final APIConfig api) {
		String[] verbUri = apiKey.split("\\s+");

		final HttpVerb verb;
		String uri;

		if (verbUri.length == 1) {
			verb = HttpVerb.GET;
			uri = verbUri[0];

		} else if (verbUri.length == 2) {
			verb = HttpVerb.from(verbUri[0]);
			uri = verbUri[1];

		} else {
			throw U.rte("Invalid route!");
		}

		addApiHandler(api, verb, uri);
	}

	private void addApiHandler(final APIConfig api, final HttpVerb verb, String uri) {
		On.route(verb.name(), uri).json(new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				if (verb == HttpVerb.GET) {
					return JDBC.query(api.sql);
				} else {
					int changes = JDBC.execute(api.sql);
					return U.map("success", true, "changes", changes); // FIXME improve
				}
			}
		});
	}

}
