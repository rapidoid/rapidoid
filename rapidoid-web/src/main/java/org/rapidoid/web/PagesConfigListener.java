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
import org.rapidoid.config.bean.PagesConfig;
import org.rapidoid.gui.GUI;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class PagesConfigListener extends GenericRouteConfigListener<PagesConfig> {

	public PagesConfigListener() {
		super(PagesConfig.class);
	}

	@Override
	protected void addHandler(final PagesConfig page, final HttpVerb verb, String uri) {
		U.must(verb == HttpVerb.GET || verb == HttpVerb.POST, "Only GET and POST verbs are supported for pages!");

		On.route(verb.name(), uri).roles(page.roles()).mvc(new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				return handle(verb, page);
			}
		});
	}

	public Object handle(HttpVerb verb, PagesConfig page) {
		return GUI.grid(JDBC.query(page.sql));
	}

}
