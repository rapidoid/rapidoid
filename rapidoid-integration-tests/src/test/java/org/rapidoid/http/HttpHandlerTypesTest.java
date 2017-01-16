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
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpHandlerTypesTest extends IsolatedIntegrationTest {

	@Test
	public void testHandlerTypes() {
		On.get("/a").html(req -> {
			return "a";
		});

		On.get("/b").html((req, resp) -> {
			return "b";
		});

		On.get("/c").html(new ReqHandler() {
			@Override
			public Object execute(Req req) throws Exception {
				return "c";
			}
		});

		On.get("/d").html(new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				return "d";
			}
		});

		On.get("/e").html((Req req) -> {
			return "e";
		});

		On.get("/f").html((Req req, Integer x) -> {
			return "f";
		});

		On.get("/g").html((Req req, Resp resp) -> {
			return "g";
		});

		On.get("/h").html((Resp yy, Integer tt, Resp xx, Req rrr, Boolean b) -> {
			return "h";
		});

		onlyGet("/a");
		onlyGet("/b");
		onlyGet("/c");
		onlyGet("/d");
		onlyGet("/e");
		onlyGet("/f");
		onlyGet("/g");
		onlyGet("/h");
	}

}
