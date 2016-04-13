package org.rapidoid.http;

/*
 * #%L
 * rapidoid-web
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
import org.rapidoid.log.Log;
import org.rapidoid.setup.On;

@Authors("Nikolche Mihajlovski")
@Since("5.0.10")
public class HttpSessionTest extends HttpTestCommons {

	@Test
	public void testHttpSession() {
		On.req(new ReqHandler() {
			@Override
			public Object execute(Req req) throws Exception {
				Log.info("Session", "ID", req.sessionId(), "data", req.session());

				int n = req.session("n", 0) + 1;
				req.session().put("n", n);

				int m = req.session("m", 10) + 1;
				req.session().put("m", m);

				return n + ":" + m;
			}
		});

		HttpClient client = HTTP.keepCookies(true).dontClose();

		eq(client.get(localhost("/a")).fetch(), "1:11");
		eq(client.get(localhost("/b")).fetch(), "2:12");
		eq(client.get(localhost("/c")).fetch(), "3:13");

		client.close();

		client = HTTP.dontClose().keepCookies(true); // do it again

		eq(client.get(localhost("/a")).fetch(), "1:11");
		eq(client.get(localhost("/b")).fetch(), "2:12");
		eq(client.get(localhost("/c")).fetch(), "3:13");

		client.close();
	}

}
