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
import org.rapidoid.annotation.*;
import org.rapidoid.security.Role;
import org.rapidoid.security.annotation.*;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpRolesTest extends IsolatedIntegrationTest {

	@Test
	public void testRoles() {
		App.scan(path());

		On.defaults().roles("aa", "bb");

		On.get("/a").json(() -> "ok");
		On.get("/ok").roles().json(() -> "ok");

		onlyGet("/a");
		onlyGet("/ok");

		verifyRoutes();
	}

}

@Controller
class Ctrl {

	@GET
	@Administrator
	@Roles({"manager", Role.MODERATOR})
	public Object hi() {
		return "hi";
	}

	@POST
	@Administrator
	public Object p1() {
		return "";
	}

	@PUT
	@Moderator
	public Object p2() {
		return "";
	}

	@DELETE
	@Administrator
	@Roles("eraser")
	@Manager
	public Object del() {
		return "";
	}

	@PATCH
	@Roles("patcher")
	public Object p3() {
		return "";
	}

	@HEAD
	@Roles(Role.LOGGED_IN)
	public Object h() {
		return "";
	}

	@OPTIONS
	@LoggedIn
	@Roles({"a", "b", Role.ANYBODY})
	public Object opt() {
		return "";
	}

}
