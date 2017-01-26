package org.rapidoid.http;

/*
 * #%L
 * rapidoid-integration-tests
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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.setup.Admin;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpAdminAndDevServerTest extends IsolatedIntegrationTest {

	@Test
	public void testAdminOnAppServer() {
		sameSetup();
		sameRequests(8080);
	}

	@Test
	public void testAdminServerConfig() {
		int port = 8881;
		Conf.section("admin").set("port", port);

		sameSetup();
		sameRequests(port);
	}

	private void sameSetup() {
		On.get("/a").html((Req x) -> "default " + U.join(":", x.uri(), x.zone(), x.contextPath()));
		Admin.get("/b").roles().json((Req x) -> "admin " + U.join(":", x.uri(), x.zone(), x.contextPath()));
		Admin.get("/c").json((Req x) -> "unauthorized");
		Admin.get("/d").html((Req x) -> "unauthorized");
	}

	private void sameRequests(int port) {
		onlyGet("/a"); // app
		onlyGet(port, "/b"); // admin
		onlyGet(port, "/c"); // admin - unauthorized
		onlyGet(port, "/d"); // admin - unauthorized
	}

}
