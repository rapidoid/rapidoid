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
import org.rapidoid.commons.Err;
import org.rapidoid.commons.Rnd;
import org.rapidoid.ctx.Contextual;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.security.Role;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.List;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpLoginTest extends IsolatedIntegrationTest {

	private volatile boolean ready = false;

	@Test
	public void testLogin() {
		Log.setLogLevel(LogLevel.ERROR);

		On.get("/user").json(() -> U.list(Contextual.username(), Contextual.roles()));

		On.get("/profile").roles(Role.LOGGED_IN).json(Contextual::username);

		On.post("/mylogin").json((Resp resp, String user, String pass) -> {
			boolean success = resp.login(user, pass);
			return U.list(success, Contextual.username(), Contextual.roles());
		});

		On.post("/mylogout").json((Resp resp, String user, String pass) -> {
			resp.logout();
			return U.list(Contextual.username(), Contextual.roles());
		});

		ready = true;
		multiThreaded(Msc.normalOrHeavy(5, 150), Msc.normalOrHeavy(10, 15000), this::randomUserLogin);
	}

	private void randomUserLogin() {
		while (!ready) U.sleep(100); // wait

		switch (Rnd.rnd(4)) {
			case 0:
				loginFlow("foo", "bar", U.list());
				break;
			case 1:
				loginFlow("abc", "abc", U.list("guest"));
				break;
			case 2:
				loginFlow("chuck", "chuck", U.list("moderator", "restarter"));
				break;
			case 3:
				loginFlow("niko", "easy", U.list("owner", "administrator", "moderator"));
				break;
			default:
				throw Err.notExpected();
		}
	}

	private void loginFlow(String user, String pass, List<String> expectedRoles) {
		HttpClient anonymous = HTTP.client().reuseConnections(true);
		HttpClient client = HTTP.client().keepCookies(true).reuseConnections(true);

		List<Object> notLoggedIn = U.list(false, null, U.list());
		List<Object> loggedIn = U.list(true, user, expectedRoles);

		eq(client.get(localhost("/user")).parse(), U.list(null, U.list()));

		verifyAccessDenied(client);

		eq(client.post(localhost("/mylogin?user=a1&pass=b")).parse(), notLoggedIn);
		eq(client.post(localhost("/mylogin?user=a2&pass=b")).parse(), notLoggedIn);

		verifyAccessDenied(client);

		eq(client.post(localhost(U.frmt("/mylogin?user=%s&pass=%s", user, pass))).parse(), loggedIn);

		verifyAccessDenied(anonymous);
		verifyAccessGranted(user, client);

		eq(client.get(localhost("/user")).parse(), U.list(user, expectedRoles));

		verifyAccessDenied(anonymous);
		verifyAccessGranted(user, client);

		eq(client.post(localhost("/mylogin?user=a3&pass=b")).parse(), U.list(false, user, expectedRoles));

		verifyAccessDenied(anonymous);
		verifyAccessGranted(user, client);

		eq(client.get(localhost("/user")).parse(), U.list(user, expectedRoles));
		eq(client.post(localhost("/mylogout")).parse(), U.list(null, U.list()));

		verifyAccessDenied(anonymous);
		verifyLoggedOut(client);

		eq(client.get(localhost("/user")).parse(), U.list(null, U.list()));
		eq(client.get(localhost("/user")).parse(), U.list(null, U.list()));

		verifyLoggedOut(client);

		client.close();
		anonymous.close();
	}

	private void verifyAccessGranted(String user, HttpClient client) {
		verify("granted-" + user, fetch(client, "get", "/profile").replaceAll("_token=.*?;", "_token=...;"));
	}

	private void verifyAccessDenied(HttpClient client) {
		verify("denied", fetch(client, "get", "/profile"));
	}

	private void verifyLoggedOut(HttpClient client) {
		verify("logout", fetch(client, "get", "/profile"));
	}

}
