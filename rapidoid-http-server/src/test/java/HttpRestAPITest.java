/*
 * #%L
 * rapidoid-http-server
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
import org.rapidoid.data.JSON;
import org.rapidoid.http.HttpResp;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.http.Self;
import org.rapidoid.setup.On;
import org.rapidoid.test.ExpectErrors;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.MscOpts;

@Authors("Nikolche Mihajlovski")
@Since("5.2.9")
public class HttpRestAPITest extends TestCommons {

	private void initAPI() {
		On.get("/inc/{x}").json(new ReqHandler() {
			@Override
			public Object execute(Req req) {
				return U.num(req.param("x")) + 1;
			}
		});
	}

	@Test
	public void testIncludedModules() {
		isFalse(MscOpts.hasRapidoidHTML());
		isFalse(MscOpts.hasRapidoidGUI());
	}

	@Test
	public void testRestAPI() {
		initAPI();

		Self.get("/inc/99").expect("100");
	}

	@Test
	public void testNotFound() {
		initAPI();

		HttpResp resp = Self.get("/foo/baz").execute();

		eq(resp.code(), 404);
		eq(resp.body(), JSON.stringify(U.map(
			"error", "The requested resource could not be found!",
			"code", 404,
			"status", "Not Found"
		)));
	}

	@Test
	@ExpectErrors
	public void testRuntimeError() {
		initAPI();

		HttpResp resp = Self.get("/inc/d9g").execute();

		eq(resp.code(), 500);
		eq(resp.body(), JSON.stringify(U.map(
			"error", "For input string: \"d9g\"",
			"code", 500,
			"status", "Internal Server Error"
		)));
	}

}
