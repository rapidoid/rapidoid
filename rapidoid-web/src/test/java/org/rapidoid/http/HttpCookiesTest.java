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
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.2.5")
public class HttpCookiesTest extends HttpTestCommons {

	private static final int THREADS = Msc.normalOrHeavy(5, 100);
	private static final int ROUNDS = Msc.normalOrHeavy(10, 1000);

	@Test
	public void testHttpCookies() {

		On.req(new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				resp.cookie(req.uri(), "" + req.cookies().size());
				return resp.json(req.cookies());
			}
		});

		multiThreaded(THREADS, ROUNDS, new Runnable() {
			@Override
			public void run() {
				checkCookies();
			}
		});
	}

	private void checkCookies() {
		HttpClient client = HTTP.client().keepCookies(true).timeout(10000);

		eq(client.get(localhost("/a")).parse(), U.map());
		eq(client.get(localhost("/b")).parse(), U.map("/a", "0"));
		eq(client.get(localhost("/c")).parse(), U.map("/a", "0", "/b", "1"));
		eq(client.get(localhost("/d")).parse(), U.map("/a", "0", "/b", "1", "/c", "2"));

		client.close();
	}

	@Test
	public void testNoCookies() {

		On.req(new ReqRespHandler() {
			@Override
			public Object execute(Req req, Resp resp) throws Exception {
				isTrue(req.cookies().isEmpty());
				return req.cookies().size();
			}
		});

		multiThreaded(THREADS, ROUNDS, new Runnable() {
			@Override
			public void run() {
				checkNoCookies(true);
				checkNoCookies(false);
			}
		});
	}

	private void checkNoCookies(boolean keepCookies) {
		HttpClient client;
		client = HTTP.client().keepCookies(keepCookies).timeout(10000);

		eq(client.get(localhost("/a")).fetch(), "0");
		eq(client.get(localhost("/b")).fetch(), "0");
		eq(client.get(localhost("/c")).fetch(), "0");

		client.close();
	}

}
