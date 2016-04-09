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
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpAdminAndDevServerTest extends HttpTestCommons {

	@Test
	public void testAdminOnAppServer() {
		On.get("/a").html((Req x) -> "default " + x.uri());
		Admin.get("/b").json((Req x) -> "admin " + x.uri());

		onlyGet("/a"); // app
		onlyGet("/b"); // admin
	}

	@Test
	public void testAdminServerConfig() {
		int port = 20000;

		Conf.section("admin").set("port", port);

		Admin.get("/myadmin").html((Req x) -> "admin " + x.uri());
		On.get("/c").html((Req x) -> "app " + x.uri());

		onlyGet("/c"); // app
		onlyGet(port, "/myadmin");
	}

}
