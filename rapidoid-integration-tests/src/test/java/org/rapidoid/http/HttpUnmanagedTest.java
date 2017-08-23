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
import org.rapidoid.u.U;

import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.4.3")
public class HttpUnmanagedTest extends IsolatedIntegrationTest {

	private static final Map<String, Integer> DATA = U.map("x", 123);

	private static final byte[] PRE_RENDERED = "{\"xyz\": 12345}".getBytes();

	@Test
	public void testUnmanagedHandlersWithPrerenderedJSON1() {

		On.post("/json").managed(false).json((Req req, Resp resp) -> {
			return resp.body(PRE_RENDERED);
		});

		onlyPost("/json");
	}

	@Test
	public void testUnmanagedHandlersWithPrerenderedJSON2() {

		On.get("/json").managed(false).serve((Req req, Resp resp) -> {
			return resp.contentType(MediaType.JSON).body(PRE_RENDERED);
		});

		onlyGet("/json");
	}

	@Test
	public void testUnmanagedHandlersWithPrerenderedJSON3() {

		On.post("/json").managed(false).serve((Req req, Resp resp) -> {

			resp.header("hdr1", "val1")
				.cookie("cook1", "the-cookie");

			return resp.contentType(MediaType.JSON).code(500).body(PRE_RENDERED);
		});

		onlyPost("/json");
	}

	@Test
	public void testUnmanagedHandlersWithPrerenderedJSON4() {

		On.get("/json/{num}").managed(false).json((Integer num, Req req, Resp resp) -> {

			resp.header("hdr1", "val1")
				.cookie("cook1", "the-cookie");

			return num < 0 ? resp.code(201).json(U.map("neg", num)) : U.map("pos", num);
		});

		onlyGet("/json/102030");
		onlyGet("/json/-7");
	}

	@Test
	public void testUnmanagedHandlersWithNormalJSON1() {

		On.get("/json").managed(false).json((Req req, Resp resp) -> DATA);

		onlyGet("/json");
	}

	@Test
	public void testUnmanagedHandlersWithNormalJSON2() {

		On.post("/json").managed(false).serve((Req req, Resp resp) -> {
			resp.contentType(MediaType.JSON)
				.code(500)
				.header("hdr1", "val1")
				.cookie("cook1", "the-cookie");

			return DATA;
		});

		onlyPost("/json");
	}

	@Test
	public void testUnmanagedHandlersWithHtml() {

		On.post("/").managed(false).serve((Req req, Resp resp) -> {
			return resp.html("denied!").code(403);
		});

		onlyPost("/");
	}

	@Test
	public void testUnmanagedHandlersWithPlainText() {

		On.post("/").managed(false).json((Req req, Resp resp) -> {
			resp.code(404)
				.contentType(MediaType.PLAIN_TEXT_UTF_8)
				.header("hdr1", "val1")
				.cookie("cook1", "the-cookie");

			return "NOT found!";
		});

		onlyPost("/");
	}

	@Test
	public void testUnmanagedHandlersWithPlainTextSimple() {

		On.get("/").managed(false).plain(() -> "hi!");

		onlyGet("/");
	}

	@Test
	public void testUnmanagedHandlersWithErrors() {

		On.get("/").managed(false).json(() -> {
			throw U.rte("INTENTIONAL ERROR!");
		});

		onlyGet("/");
	}

}
