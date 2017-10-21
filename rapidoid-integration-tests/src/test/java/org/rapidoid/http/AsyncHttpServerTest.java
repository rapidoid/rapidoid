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
import org.rapidoid.io.IO;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.io.IOException;
import java.io.OutputStream;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class AsyncHttpServerTest extends IsolatedIntegrationTest {

	private static final int REQUESTS = Msc.normalOrHeavy(100, 10000);

	@Test(timeout = 20000)
	public void testAsyncHttpServer() {
		On.req(req -> {
			U.must(!req.isAsync());
			req.async();
			U.must(req.isAsync());

			async(() -> {
				IO.write(req.out(), "O");

				async(() -> {
					IO.write(req.out(), "K");

					try {
						req.out().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					req.done();
				});
			});

			return req;
		});

		Self.get("/").expect("OK").execute();
		Self.get("/").expect("OK").benchmark(1, 100, REQUESTS);
		Self.post("/").expect("OK").benchmark(1, 100, REQUESTS);
	}

	@Test(timeout = 20000)
	public void testAsyncHttpServerNested() {
		On.req(req -> {
			U.must(!req.isAsync());
			req.async();
			U.must(req.isAsync());

			async(() -> {
				appendTo(req, "O", false, () -> {
					async(() -> {
						appendTo(req, "K", true, null);
						// req.done() is in "appendTo"
					});
				});
			});

			return req;
		});

		Self.get("/").expect("OK").execute();
		Self.get("/").expect("OK").benchmark(1, 100, REQUESTS);
		Self.post("/").expect("OK").benchmark(1, 100, REQUESTS);
	}


	@Test(timeout = 20000)
	public void testAsyncHttpServer2() {
		On.req(req -> async(() -> {
			Resp resp = req.response();

			resp.chunk("A".getBytes());

			async(() -> {
				resp.chunk("SYNC".getBytes());
				IO.close(resp.out(), false);
				req.done();
			});
		}));

		Self.get("/").expect("ASYNC").execute();
		Self.get("/").expect("ASYNC").benchmark(1, 100, REQUESTS);
		Self.post("/").expect("ASYNC").benchmark(1, 100, REQUESTS);
	}

	private void appendTo(Req req, String data, boolean complete, Runnable then) {
		req.response().resume(() -> {
			OutputStream out = req.out();

			IO.write(out, data);

			try {
				out.flush();
			} catch (IOException e) {
				throw U.rte(e);
			}

			if (then != null) {
				then.run();
			}

			if (complete) {
				IO.close(out, false);
				req.done();
			}

			return complete;
		});
	}

}
