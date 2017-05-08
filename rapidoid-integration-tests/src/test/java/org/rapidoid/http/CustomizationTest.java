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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.Since;
import org.rapidoid.data.JSON;
import org.rapidoid.setup.App;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class CustomizationTest extends IsolatedIntegrationTest {

	@Test
	public void testSerializationConfig() {
		On.custom().jsonResponseRenderer((req, value, out) -> JSON.prettify(value, out));

		On.get("/").json(() -> U.map("foo", 12, "bar", 345));
		On.get("/a").json(() -> U.map("foo", 12, "bar", 345));

		onlyGet("/");

		On.custom().jsonResponseRenderer((req, value, out) -> JSON.stringify(value, out));

		onlyGet("/a");
	}

	@Test
	public void testAuthConfig() {
		On.custom().loginProvider((req, username, password) -> password.equals(username + "!"));
		On.custom().rolesProvider((req, username) -> username.equals("root") ? U.set("admin") : U.set());
		// FIXME complete the test
	}

	@Test
	public void testBeanParamFactoryConfig() {
		App.beans(new Object() {
			@POST
			Object aa(Num num) {
				return num;
			}
		});

		On.put("/bb").json((Num f) -> f);

		// before customization
		onlyPost("/aa?id=1", U.map("the-name", "one"));
		onlyPut("/bb?id=2", U.map("the-name", "two"));

		// customization
		ObjectMapper mapper = new ObjectMapper();
		On.custom().beanParameterFactory((req, type, name, props) -> mapper.convertValue(req.posted(), type));

		// after customization
		onlyPost("/aa?id=3", U.map("the-name", "three"));
		onlyPut("/bb?id=4", U.map("the-name", "four"));
	}

	static class Num {
		public long id = -1;

		@JsonProperty("the-name")
		public String name;
	}

	@Test
	public void customJsonBodyParser() {
		My.jsonRequestBodyParser((req, body) -> U.map("uri", req.uri(), "parsed", JSON.parse(body)));

		On.post("/abc").json(req -> req.data());
		On.req(req -> req.posted());

		postData("/abc?multipart", U.map("x", 13579, "foo", "bar"));
		postData("/abc2?multipart", U.map("x", 13579, "foo", "bar"));

		postJson("/abc?custom", U.map("x", 13579, "foo", "bar"));
		postJson("/abc2?custom", U.map("x", 13579, "foo", "bar"));
	}

	@Test
	public void customErrorHandlerByType() {
		My.error(NullPointerException.class).handler((req1, resp, e) -> "MY NPE");
		My.error(RuntimeException.class).handler((req1, resp, e) -> e instanceof NotFound ? null : "MY RTE");

		On.error(SecurityException.class).handler((req1, resp, e) -> "ON SEC");
		My.error(SecurityException.class).handler((req1, resp, e) -> "MY SEC");

		On.get("/err1").json(req -> {
			throw new NullPointerException();
		});

		On.post("/err2").json(req -> {
			throw new RuntimeException();
		});

		On.get("/err3").json(req -> {
			throw new SecurityException("INTENTIONAL - Access denied!");
		});

		On.get("/err4").json(req -> {
			throw new OutOfMemoryError("INTENTIONAL - Out of memory!");
		});

		onlyGet("/err1");
		onlyPost("/err2");
		onlyGet("/err3");
		onlyGet("/err4");
	}

}
