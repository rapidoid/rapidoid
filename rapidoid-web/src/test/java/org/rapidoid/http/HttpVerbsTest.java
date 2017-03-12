package org.rapidoid.http;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("5.0.5")
public class HttpVerbsTest extends HttpTestCommons {

	@Test
	public void testHttpVerbs() {
		On.get("/testGet").html("get:success");
		On.post("/testPost").html("post:success");
		On.put("/testPut").html("put:success");
		On.delete("/testDelete").html("delete:success");
		On.patch("/testPatch").html("patch:success");
		On.options("/testOptions").html("options:success");
		On.head("/testHead").html(""); // no body for the HEAD verb
		On.trace("/testTrace").html("trace:success");

		eq(HTTP.get(localhost("/testGet")).fetch(), "get:success");
		eq(HTTP.post(localhost("/testPost")).fetch(), "post:success");
		eq(HTTP.put(localhost("/testPut")).fetch(), "put:success");
		eq(HTTP.delete(localhost("/testDelete")).fetch(), "delete:success");
		eq(HTTP.patch(localhost("/testPatch")).fetch(), "patch:success");
		eq(HTTP.options(localhost("/testOptions")).fetch(), "options:success");
		eq(HTTP.head(localhost("/testHead")).fetch(), ""); // no body for the HEAD verb
		eq(HTTP.trace(localhost("/testTrace")).fetch(), "trace:success");
	}

}
