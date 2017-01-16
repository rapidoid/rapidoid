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
import org.rapidoid.gui.GUI;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;
import org.rapidoid.web.Screen;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public class HttpRenderTest extends IsolatedIntegrationTest {

	@Test
	public void testRender() {
		On.get("/view1").html((Req req, Resp resp) -> {
			return resp.mvc(true);
		});

		On.page("/view2").mvc((Req req, Resp resp) -> {
			resp.model().put("x", 12345);
			return req;
		});

		On.get("/view3").view("view1").mvc((Req req, Resp resp) -> {
			resp.model("msg", "custom view: 1");
			return req;
		});

		On.get("/views/sub").html((Req req, Resp resp) -> {
			resp.model("msg", "sub-view!");
			return resp.mvc(true);
		});

		On.get("/abc").html((Req req, Resp resp) -> {
			resp.model("a", 123);
			resp.model("b", "BBB");
			resp.model("req", req);

			return resp.view("view1").mvc(true);
		});

		On.get("/piece").mvc((Resp respo, Screen screen) -> {
			respo.screen().title("my-title");
			screen.brand(GUI.span(GUI.fa("cog"), "The Brand!"));
			respo.model("x", 12345);
			return U.map("screen", screen);
		});

		On.get("/defaults").mvc((Req req) -> new byte[0]);

		onlyGet("/view1");
		getAndPost("/view2");
		onlyGet("/view3");
		onlyGet("/abc");
		onlyGet("/views/sub");
		onlyGet("/views/sub");
		onlyGet("/piece");
		onlyGet("/defaults");

		verifyRoutes();
	}

}
