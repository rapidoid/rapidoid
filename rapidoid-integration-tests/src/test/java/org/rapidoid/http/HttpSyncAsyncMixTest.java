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
import org.rapidoid.job.Jobs;
import org.rapidoid.setup.On;
import org.rapidoid.util.Msc;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class HttpSyncAsyncMixTest extends IsolatedIntegrationTest {

	private static final int ROUNDS = Msc.normalOrHeavy(100, 1000);

	@Test
	public void testSyncAsyncMix() {

		On.get("/").plain((Resp resp, Integer n) -> {
			if (n % 2 == 0) return n;

			return Jobs.after(3).milliseconds(() -> resp.result(n * 10).done());
		});

		// it is important to use only 1 connection
		HttpClient client = HTTP.client().reuseConnections(true).keepAlive(true).maxConnTotal(1);

		for (int i = 0; i < ROUNDS; i++) {
			int expected = i % 2 == 0 ? i : i * 10;
			client.get(localhost("/?n=" + i)).expect("" + expected);
			client.get(localhost("/abcd.txt")).expect("ABCD");
		}

		client.close();
	}

}
