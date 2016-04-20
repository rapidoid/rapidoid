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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.POST;
import org.rapidoid.annotation.Since;
import org.rapidoid.data.JSON;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class CustomizationTest extends IntegrationTestCommons {

	@Test
	public void testSerializationConfig() {
		On.custom().jsonResponseRenderer(JSON::prettify);

		On.get("/").json(() -> U.map("foo", 12, "bar", 345));
		On.get("/a").json(() -> U.map("foo", 12, "bar", 345));

		onlyGet("/");

		On.custom().jsonResponseRenderer(JSON::stringify);

		onlyGet("/a");
	}

	@Test
	public void testAuthConfig() {
		On.custom().loginProvider((username, password) -> password.equals(username + "!"));
		On.custom().rolesProvider(username -> username.equals("root") ? U.set("admin") : U.set());
		// FIXME complete the test
	}

	@Test
	public void testBeanParamFactoryConfig() {
		On.beans(new Object() {
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

}
