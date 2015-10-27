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
import org.rapidoid.util.U;
import org.rapidoid.webapp.AppCtx;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebAppGroup;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class HttpServerSubAppTest extends HttpTestCommons {

	@Test
	public void shouldHandleSubAppRequests() throws IOException, URISyntaxException {

		WebAppGroup apps = new WebAppGroup("apps");

		WebApp myapp = new WebApp("myapp", "/my", null);
		apps.register(myapp);

		WebApp defaultApp = new WebApp("defapp", null, null);
		apps.setDefaultApp(defaultApp);

		myapp.getRouter().get("/ab", info("my-special"));
		myapp.getRouter().generic(info("my-generic"));

		defaultApp.getRouter().get("/x", info("def-special"));
		defaultApp.getRouter().generic(info("def-generic"));

		server = WebServer.create().applications(apps).build();
		start();

		eq(get("/my"), "my-generic: id=myapp, host=localhost:8080, uri=/my, ctx=/my, path=/, subpath=/, segments=");
		eq(get("/my/"), "my-generic: id=myapp, host=localhost:8080, uri=/my, ctx=/my, path=/, subpath=/, segments=");

		eq(get("/my/ab"),
				"my-special: id=myapp, host=localhost:8080, uri=/my/ab, ctx=/my, path=/ab, subpath=/, segments=ab");
		eq(get("/my/ab/"),
				"my-special: id=myapp, host=localhost:8080, uri=/my/ab, ctx=/my, path=/ab, subpath=/, segments=ab");

		eq(get("/"), "def-generic: id=defapp, host=localhost:8080, uri=/, ctx=/, path=/, subpath=/, segments=");
		eq(get("/ab/"),
				"def-generic: id=defapp, host=localhost:8080, uri=/ab, ctx=/, path=/ab, subpath=/ab, segments=ab");
		eq(get("/p"), "def-generic: id=defapp, host=localhost:8080, uri=/p, ctx=/, path=/p, subpath=/p, segments=p");

		eq(get("/xyz"),
				"def-generic: id=defapp, host=localhost:8080, uri=/xyz, ctx=/, path=/xyz, subpath=/xyz, segments=xyz");
		eq(get("/xyz/"),
				"def-generic: id=defapp, host=localhost:8080, uri=/xyz, ctx=/, path=/xyz, subpath=/xyz, segments=xyz");
		eq(get("/xyz/cd"),
				"def-generic: id=defapp, host=localhost:8080, uri=/xyz/cd, ctx=/, path=/xyz/cd, subpath=/xyz/cd, segments=xyz:cd");
		eq(get("/xyz/a/"),
				"def-generic: id=defapp, host=localhost:8080, uri=/xyz/a, ctx=/, path=/xyz/a, subpath=/xyz/a, segments=xyz:a");

		eq(get("/x"), "def-special: id=defapp, host=localhost:8080, uri=/x, ctx=/, path=/x, subpath=/, segments=x");
		eq(get("/x/"), "def-special: id=defapp, host=localhost:8080, uri=/x, ctx=/, path=/x, subpath=/, segments=x");
		eq(get("/x/a"),
				"def-special: id=defapp, host=localhost:8080, uri=/x/a, ctx=/, path=/x/a, subpath=/a, segments=x:a");
		eq(get("/x/a/bb/"),
				"def-special: id=defapp, host=localhost:8080, uri=/x/a/bb, ctx=/, path=/x/a/bb, subpath=/a/bb, segments=x:a:bb");
		eq(get("/x/a/bb/c"),
				"def-special: id=defapp, host=localhost:8080, uri=/x/a/bb/c, ctx=/, path=/x/a/bb/c, subpath=/a/bb/c, segments=x:a:bb:c");

		shutdown();
	}

	private Handler info(final String desc) {
		return new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				String id = AppCtx.app().getId();
				x.plain();
				return U.frmt("%s: id=%s, host=%s, uri=%s, ctx=%s, path=%s, subpath=%s, segments=%s", desc, id,
						x.host(), x.uri(), x.home(), x.path(), x.subpath(), U.join(":", x.pathSegments()));
			}
		};
	}
}
