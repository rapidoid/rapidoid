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
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HttpResp;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqHandler;
import org.rapidoid.setup.On;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

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
		isFalse(Msc.hasRapidoidHTML());
		isFalse(Msc.hasRapidoidGUI());
	}

	@Test
	public void testRestAPI() {
		initAPI();

		String resp = HTTP.get("http://localhost:8888/inc/99").fetch();

		eq(resp, "100");
	}

	@Test
	public void testNotFound() {
		initAPI();

		HttpResp resp = HTTP.get("http://localhost:8888/foo/baz").execute();

		eq(resp.code(), 404);
		eq(resp.body(), JSON.stringify(U.map(
			"error", "The requested resource could not be found!",
			"code", 404,
			"status", "Not Found"
		)));
	}

	@Test
	public void testRuntimeError() {
		initAPI();

		HttpResp resp = HTTP.get("http://localhost:8888/inc/d9g").execute();

		eq(resp.code(), 500);
		eq(resp.body(), JSON.stringify(U.map(
			"error", "For input string: \"d9g\"",
			"code", 500,
			"status", "Internal Server Error"
		)));
	}

}
