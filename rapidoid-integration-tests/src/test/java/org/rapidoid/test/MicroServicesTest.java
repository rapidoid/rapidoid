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
import org.rapidoid.http.HttpTestCommons;
import org.rapidoid.http.REST;
import org.rapidoid.http.Req;
import org.rapidoid.http.fast.On;
import org.rapidoid.http.fast.ReqHandler;
import org.rapidoid.u.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class MicroServicesTest extends HttpTestCommons {

	@Test
	public void testMicroserviceCommunication() {
		On.req(new ReqHandler() {
			@Override
			public Object handle(Req req) throws Exception {
				return U.num(req.param("n")) + 1;
			}
		});

		// a blocking call
		eq(REST.get("http://localhost:8888/?n=7", Integer.class).intValue(), 8);
		eq(REST.post("http://localhost:8888/?n=7", Integer.class).intValue(), 8);

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
				REST.get("http://localhost:8888/?n=" + i, Integer.class, callback);
			} else {
				REST.post("http://localhost:8888/?n=" + i, Integer.class, callback);
			}
		}

		U.wait(latch);
		UTILS.endMeasure(count, "calls");

		loop.interrupt();
	}

}
