package org.rapidoid.test;

/*
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HttpTestCommons;
import org.rapidoid.http.Req;
import org.rapidoid.http.fast.On;
import org.rapidoid.http.fast.ReqHandler;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class AsyncHttpServerTest extends HttpTestCommons {

	@Test
	public void testAsyncHttpServer() {
		Log.debugging();

		On.req(new ReqHandler() {
			@Override
			public Object handle(final Req req) throws Exception {
				req.async();
				U.must(req.isAsync());
				Jobs.schedule(new Runnable() {

					@Override
					public void run() {
						write(req.out(), "O");

						Jobs.schedule(new Runnable() {
							@Override
							public void run() {
								write(req.out(), "K");
								req.done();
							}
						}, 1, TimeUnit.SECONDS);

					}

				}, 1, TimeUnit.SECONDS);

				return req;
			}
		});

		eq(new String(HTTP.get("http://localhost:8888/")), "OK");
		eq(new String(HTTP.post("http://localhost:8888/", null, new byte[0], null)), "OK");
	}

	private static void write(OutputStream out, String s) {
		try {
			out.write(s.getBytes());
		} catch (IOException e) {
			throw U.rte(e);
		}
	}

}
