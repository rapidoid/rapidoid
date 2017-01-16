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
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class HttpWrappersTest extends IsolatedIntegrationTest {

	@Test
	public void testWrappers() {
		HttpWrapper hey = wrapper("hey");
		My.wrappers(hey);

		On.defaults().wrappers(wrapper("on-def"));

		On.get("/def").plain("D");

		On.defaults().wrappers((HttpWrapper[]) null); // reset the default wrappers

		HttpWrapper[] wrappers = On.custom().wrappers();
		eq(U.array(hey), wrappers);

		On.get("/").wrappers(wrapper("index")).plain("home");
		On.post("/x").wrappers(wrapper("x"), wrapper("x2")).json("X");
		On.get("/y").html("YYY");

		On.custom().wrappers(wrapper("on"));

		onlyGet("/");
		onlyPost("/x");
		onlyGet("/y");
		onlyGet("/def");
	}

	@Test
	public void testDefaultWrappers() {
		My.wrappers(wrapper("def"));

		On.post("/z").plain("Zzz");

		onlyPost("/z");
	}

	private static HttpWrapper wrapper(String msg) {
		return (req, next) -> next.invokeAndTransformResult(result -> msg + "(" + req.uri() + ":" + result + ")");
	}

}
