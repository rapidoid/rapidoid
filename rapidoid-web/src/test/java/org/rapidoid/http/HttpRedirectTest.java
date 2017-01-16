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
@Since("5.0.10")
public class HttpRedirectTest extends HttpTestCommons {

	@Test
	public void testSimpleRedirect() {
		On.get("/redir").html(new ReqHandler() {
			@Override
			public Object execute(Req req) throws Exception {
				return req.response().redirect("/target");
			}
		});

		onlyGet("/redir");
	}

	@Test
	public void testRedirectWithCustomCode() {
		On.get("/redir2").json(new ReqHandler() {
			@Override
			public Object execute(Req req) throws Exception {
				return req.response().redirect("/target2").code(302);
			}
		});

		onlyGet("/redir2");
	}

}
