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
import org.rapidoid.annotation.GET;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.fast.On;
import org.rapidoid.http.fast.ReqHandler;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public class HttpMultiControllerTest extends HttpTestCommons {

	@Test
	public void testSequentialControllerRegistration() {

		On.req(new Object() {
			@GET
			public String foo(Req req) {
				return "FOO";
			}
		});

		On.req(new Object() {
			@GET
			public String bar(Req req, Resp resp) {
				return "BAR";
			}
		});

		On.req(new Object() {
			@GET
			public String foo() {
				return "FOO2";
			}
		});

		On.req(new ReqHandler() {
			@Override
			public Object handle(Req req) throws Exception {
				return req.verb().equals("GET") ? "generic:" + req.uri() : null;
			}
		});

		On.req(new Object() {
			@GET
			public String baz() {
				return "BAZZZZZZ";
			}
		});

		onlyGet("/foo");
		onlyGet("/bar");
		onlyGet("/baz");
	}

}
