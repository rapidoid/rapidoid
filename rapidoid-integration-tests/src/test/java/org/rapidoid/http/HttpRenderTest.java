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
import org.rapidoid.annotation.Since;
import org.rapidoid.setup.On;
import org.rapidoid.web.MustacheViewRenderer;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public class HttpRenderTest extends HttpTestCommons {

	@Test
	public void testRender() {

		On.custom().viewRenderer((req, resp, out) -> resp.content("this will be overwritten"));

		On.custom().viewRenderer(new MustacheViewRenderer());

		On.get("/view1").html((Req req, Resp resp) -> {
			return resp.mvc(true);
		});

		On.page("/view2").render((Req req, Resp resp) -> {
			resp.model().put("x", 12345);
			return req;
		});

		On.get("/view3").view("view1").render((Req req, Resp resp) -> {
			resp.model().put("msg", "custom view: 1");
			return req;
		});

		On.get("/views/sub").html((Req req, Resp resp) -> {
			resp.model().put("msg", "sub-view!");
			return resp.mvc(true);
		});

		On.get("/abc").html((Req req, Resp resp) -> {
			resp.model().put("a", 123);
			resp.model().put("b", "BBB");
			resp.model().put("req", req);

			return resp.view("view1").mvc(true);
		});

		onlyGet("/view1");
		getAndPost("/view2");
		onlyGet("/view3");
		onlyGet("/abc");
		onlyGet("/views/sub");
	}

}
