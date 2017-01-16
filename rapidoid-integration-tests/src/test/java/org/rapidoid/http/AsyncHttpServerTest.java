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
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;

import java.util.concurrent.TimeUnit;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class AsyncHttpServerTest extends IsolatedIntegrationTest {

	@Test
	public void testAsyncHttpServer() {
		Log.debugging();

		On.req(req -> {
			U.must(!req.isAsync());
			req.async();
			U.must(req.isAsync());

			Jobs.after(10, TimeUnit.MILLISECONDS).run(() -> {

				IO.write(req.out(), "O");

				Jobs.after(10, TimeUnit.MILLISECONDS).run(() -> {
					IO.write(req.out(), "K");
					req.done();
				});

			});

			return req;
		});

		Self.get("/").expect("OK").benchmark(1, 100, 10000);
		Self.post("/").expect("OK").benchmark(1, 100, 10000);
	}

	@Test
	public void testAsyncHttpServer2() {
		On.req(req -> Jobs.after(10, TimeUnit.MILLISECONDS).run(() -> {

			IO.write(req.out(), "A");

			Jobs.after(10, TimeUnit.MILLISECONDS).run(() -> {
				IO.write(req.out(), "SYNC");
				req.done();
			});
		}));

		Self.get("/").expect("ASYNC").benchmark(1, 100, 10000);
		Self.post("/").expect("ASYNC").benchmark(1, 100, 10000);
	}

}
