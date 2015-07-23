package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.appctx.AppCtx;
import org.rapidoid.appctx.Application;
import org.rapidoid.appctx.Applications;
import org.rapidoid.appctx.WebApp;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class HttpServerSubAppTest extends HttpTestCommons {

	@Test
	public void shouldHandleSubAppRequests() throws IOException, URISyntaxException {

		Applications apps = new Applications("apps");

		Application myapp = new WebApp("myapp", "My App", null, null, U.set("/my"), null, null, null);
		apps.register(myapp);

		Application defaultApp = new WebApp("defapp", "Default", null, null, null, null, null, null);
		apps.setDefaultApp(defaultApp);

		myapp.getRouter().get("/ab", info("my-special"));
		myapp.getRouter().generic(info("my-generic"));

		defaultApp.getRouter().get("/cd", info("def-special"));
		defaultApp.getRouter().generic(info("def-generic"));

		server = HTTP.server().applications(apps).build();
		start();

		eq(get("/my/ab"), "my-special: id=myapp, uri=/my/ab, uriContext=/my, path=/ab, subpath=");

		// TODO add more tests here

		shutdown();
	}

	private Handler info(final String desc) {
		return new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				String id = AppCtx.app().getId();
				return U.format("%s: id=%s, uri=%s, uriContext=%s, path=%s, subpath=%s", desc, id, x.uri(),
						x.uriContext(), x.path(), x.subpath());
			}
		};
	}
}
