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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.fast.On;
import org.rapidoid.http.fast.ViewRenderer;

import java.io.PrintWriter;

@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public class HttpRenderTest extends HttpTestCommons {

	@Test
	public void testRender() {

		MustacheFactory mf = new DefaultMustacheFactory();

		On.render((req, resp) -> resp.content("this will be overwritten"));

		On.render(new ViewRenderer() {
			@Override
			public void render(Req req, Resp resp) throws Exception {
				Mustache mustache = mf.compile(resp.view() + ".html");
				mustache.execute(new PrintWriter(resp.out()), resp.model()).flush();
			}
		});

		On.get("/view1").html((req, resp) -> {
			return resp.render();
		});

		On.get("/view2").html((req, resp) -> {
			resp.model().put("x", 12345);
			return resp.render();
		});

		On.get("/view3").html((req, resp) -> {
			resp.model().put("msg", "custom view: 1");
			return resp.view("view1").render();
		});

		On.get("/views/sub").html((req, resp) -> {
			resp.model().put("msg", "sub-view!");
			return resp.render();
		});

		On.get("/abc").html((req, resp) -> {
			resp.model().put("a", 123);
			resp.model().put("b", "BBB");
			resp.model().put("req", req);

			return resp.view("view1").render();
		});

		onlyGet("/view1");
		onlyGet("/view2");
		onlyGet("/view3");
		onlyGet("/abc");
		onlyGet("/views/sub");
	}

}
