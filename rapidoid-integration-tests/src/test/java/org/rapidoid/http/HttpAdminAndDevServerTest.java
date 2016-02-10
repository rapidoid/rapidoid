package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpAdminAndDevServerTest extends HttpTestCommons {

	@Test
	public void testAdminAndDevServer() {
		On.get("/a").html(x -> "default " + x.uri());
		On.admin().get("/a").html(x -> "admin " + x.uri());
		On.dev().get("/a").json((req, resp) -> "dev " + req.uri());

		onlyGet("/a"); // default server
		onlyGet(8889, "/a"); // admin server
		onlyGet(8887, "/a"); // dev server
	}

	@Test
	public void testAdminServerConfig() {
		int port = 19999;

		Conf.set("admin", "port", port);

		On.admin().get("/myadmin").html(x -> "admin " + x.uri());

		onlyGet(port, "/myadmin");
	}

	@Test
	public void testDevServerConfig() {
		int port = 17777;

		Conf.set("dev", "port", port);

		On.dev().get("/mydev").html(x -> "dev " + x.uri());

		onlyGet(port, "/mydev");
	}

}
