package org.rapidoid.http;

import java.io.IOException;
import java.net.URISyntaxException;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.fast.HttpUtils;
import org.rapidoid.http.fast.On;
import org.rapidoid.http.fast.ReqHandler;
import org.rapidoid.u.U;
import org.rapidoid.web.WebApp;
import org.rapidoid.web.WebAppGroup;

/*
 * #%L
 * rapidoid-integration-tests
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

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class HttpServerSubAppTest extends HttpTestCommons {

	// @Test
	public void shouldHandleSubAppRequests() throws IOException, URISyntaxException {

		WebAppGroup apps = new WebAppGroup("apps");

		WebApp myapp = new WebApp("myapp", "/my", null, null);
		apps.register(myapp);

		WebApp defaultApp = new WebApp("defapp", null, null, null);
		apps.setDefaultApp(defaultApp);

		On.get("/ab").plain(info("my-special"));
		On.req(info("my-generic"));

		On.get("/x").plain(info("def-special"));
		On.req(info("def-generic"));

		eq(get("/my"), "my-generic: id=myapp, host=localhost:8888, uri=/my, ctx=/my, path=/, segments=");
		eq(get("/my/"), "my-generic: id=myapp, host=localhost:8888, uri=/my, ctx=/my, path=/, segments=");

		eq(get("/my/ab"), "my-special: id=myapp, host=localhost:8888, uri=/my/ab, ctx=/my, path=/ab, segments=ab");
		eq(get("/my/ab/"), "my-special: id=myapp, host=localhost:8888, uri=/my/ab, ctx=/my, path=/ab, segments=ab");

		eq(get("/"), "def-generic: id=defapp, host=localhost:8888, uri=/, ctx=/, path=/, segments=");
		eq(get("/ab/"), "def-generic: id=defapp, host=localhost:8888, uri=/ab, ctx=/, path=/ab, segments=ab");
		eq(get("/p"), "def-generic: id=defapp, host=localhost:8888, uri=/p, ctx=/, path=/p, segments=p");

		eq(get("/xyz"), "def-generic: id=defapp, host=localhost:8888, uri=/xyz, ctx=/, path=/xyz, segments=xyz");
		eq(get("/xyz/"), "def-generic: id=defapp, host=localhost:8888, uri=/xyz, ctx=/, path=/xyz, segments=xyz");
		eq(get("/xyz/cd"),
				"def-generic: id=defapp, host=localhost:8888, uri=/xyz/cd, ctx=/, path=/xyz/cd, segments=xyz:cd");
		eq(get("/xyz/a/"),
				"def-generic: id=defapp, host=localhost:8888, uri=/xyz/a, ctx=/, path=/xyz/a, segments=xyz:a");

		eq(get("/x"), "def-special: id=defapp, host=localhost:8888, uri=/x, ctx=/, path=/x, segments=x");
		eq(get("/x/"), "def-special: id=defapp, host=localhost:8888, uri=/x, ctx=/, path=/x, segments=x");
		eq(get("/x/a"), "def-special: id=defapp, host=localhost:8888, uri=/x/a, ctx=/, path=/x/a, segments=x:a");
		eq(get("/x/a/bb/"),
				"def-special: id=defapp, host=localhost:8888, uri=/x/a/bb, ctx=/, path=/x/a/bb, segments=x:a:bb");
		eq(get("/x/a/bb/c"),
				"def-special: id=defapp, host=localhost:8888, uri=/x/a/bb/c, ctx=/, path=/x/a/bb/c, segments=x:a:bb:c");
	}

	private ReqHandler info(final String desc) {
		return new ReqHandler() {
			@Override
			public Object handle(Req x) throws Exception {
				org.rapidoid.web.WebApp app = Ctxs.ctx().app();
				String id = app != null ? app.getId() : "?";
				return U.frmt("%s: id=%s, host=%s, uri=%s, ctx=%s, path=%s, segments=%s", desc, id, x.host(), x.uri(),
						"/", x.path(), U.join(":", HttpUtils.pathSegments(x)));
			}
		};
	}
}
