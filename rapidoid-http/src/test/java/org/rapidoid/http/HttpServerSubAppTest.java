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
import org.rapidoid.appctx.Application;
import org.rapidoid.appctx.Applications;
import org.rapidoid.appctx.WebApp;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class HttpServerSubAppTest extends HttpTestCommons {

	@Test
	public void shouldHandleSubAppRequests() throws IOException, URISyntaxException {

		Application myapp = new WebApp("myapp", "My App", null, null, U.set("my"), null, null, null);
		Applications.main().register(myapp);

		server = HTTP.server().build();

		router.get("/ab", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return U.join(":", "special", x.uri(), x.uriContext(), x.path(), x.subpath());
			}
		});

		router.serve(new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return U.join(":", "generic", x.uri(), x.uriContext(), x.path(), x.subpath());
			}
		});

		start();

		eq(get("/"), "generic:/:/::");
		eq(get("/ab"), "special:/ab:/:/ab:");

		shutdown();
	}

}
