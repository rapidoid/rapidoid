package org.rapidoid.standalone;

/*
 * #%L
 * rapidoid-standalone
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.gui.reqinfo.ReqInfoUtils;
import org.rapidoid.http.HttpVerb;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Main extends RapidoidThing {

	public static void main(String[] args) {
		App.bootstrap(args).jpa().adminCenter();

		if (!On.setup().routes().hasRouteOrResource(HttpVerb.GET, "/")) {
			On.get("/").view("_welcome").mvc(welcome());
		}

		if (!Admin.setup().routes().hasRouteOrResource(HttpVerb.GET, "/")) {
			Admin.get("/").view("_welcome").mvc(welcome());
		}
	}

	private static ReqRespHandler welcome() {
		return new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				return U.map("adminUrl", ReqInfoUtils.adminUrl());
			}
		};
	}

}
