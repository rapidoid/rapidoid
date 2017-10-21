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
import org.rapidoid.commons.Str;
import org.rapidoid.io.IO;
import org.rapidoid.log.Log;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import java.util.concurrent.atomic.AtomicInteger;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HttpPipeliningTest extends IsolatedIntegrationTest {

	private static final int ROUNDS = Msc.normalOrHeavy(1, 100);

	private static final int REQ_COUNT = 100;

	private static final AtomicInteger COUNTER = new AtomicInteger();

	@Test
	public void testSyncPipeline() {

		On.get("/").json((Req req, Integer n) -> {
			COUNTER.incrementAndGet();
			return U.frmt("n=%s, handle=%s", n, req.handle());
		});

		String req = "GET /?n=%s _\r\n\r\n";
		testReqResp(req, REQ_COUNT);
	}

	private void testReqResp(String req, int multiply) {
		Msc.startMeasure();

		for (int i = 0; i < ROUNDS; i++) {
			Log.info("ROUND " + i);
			COUNTER.set(0);

			connect((in, reader, out) -> {

				for (int n = 1; n <= multiply; n++) {
					out.writeBytes(String.format(req, n));
				}

				String resp = new String(IO.readWithTimeoutUntil(in, this::isComplete));

				eq(COUNTER.get(), REQ_COUNT);
				verify(maskHttpResponse(resp));

				return null;
			});
		}

		Msc.endMeasure(ROUNDS, "rounds");
	}

	private boolean isComplete(byte[] bytes) {
		String resp = new String(bytes);

		int count = Str.find(resp, "HTTP/1.1 200 OK", found -> {
		});

		return count == REQ_COUNT;
	}

}
