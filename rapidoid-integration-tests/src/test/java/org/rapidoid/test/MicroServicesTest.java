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

import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.concurrent.Callback;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.http.Micro;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.webapp.WebApp;
import org.rapidoid.webapp.WebAppGroup;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class MicroServicesTest extends IntegrationTestCommons {

	@Test
	public void testMicroserviceCommunication() {
		HTTP.DEFAULT_CLIENT.reset();
		WebApp app = WebAppGroup.openRootContext();

		app.getRouter().generic(new Handler() {
			@Override
			public Object handle(HttpExchange x) throws Exception {
				// return x.async();
				return U.num(x.param("n")) + 1;
			}
		});
		HTTPServer server = HTTP.server().applications(WebAppGroup.main()).build().start();

		// a blocking call
		eq(Micro.get("http://localhost:8080/?n=7"), 8);
		eq(Micro.post("http://localhost:8080/?n=7"), 8);

		int count = 10000;
		final CountDownLatch latch = new CountDownLatch(count);
		UTILS.startMeasure();

		RapidoidThread loop = UTILS.loop(new Runnable() {
			@Override
			public void run() {
				System.out.println(latch);
				U.sleep(1000);
			}
		});

		for (int i = 0; i < count; i++) {
			final int expected = i + 1;

			Callback<Integer> callback = new Callback<Integer>() {
				@Override
				public void onDone(Integer result, Throwable error) throws Exception {
					if (result != null) {
						eq(result.intValue(), expected);
					} else {
						System.out.println(error);
					}
					latch.countDown();
				}
			};

			if (i % 2 == 0) {
				Micro.get("http://localhost:8080/?n=" + i, callback);
			} else {
				Micro.post("http://localhost:8080/?n=" + i, callback);
			}
		}

		U.wait(latch);
		UTILS.endMeasure(count, "calls");

		loop.interrupt();
		server.shutdown();
		Ctxs.close();
	}

}
