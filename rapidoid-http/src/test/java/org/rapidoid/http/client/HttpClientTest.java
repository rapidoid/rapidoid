package org.rapidoid.http.client;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.HttpTestCommons;
import org.rapidoid.lambda.ResultCounterCallback;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class HttpClientTest extends HttpTestCommons {

	private static final String GET_LOCALHOST = "GET / HTTP/1.1\nHost: localhost\n\n";

	private static final String GET_RAPIDOID_ORG = "GET / HTTP/1.1\nHost: www.rapidoid.org\n\n";

	protected static final String SIMPLE_RESPONSE = "AbC";

	@Test
	public void testHttpClient() {
		for (int k = 0; k < 3; k++) {

			HTTPServer localServer = HTTP.serve(SIMPLE_RESPONSE);

			ResultCounterCallback<String> cb1 = new ResultCounterCallback<String>();
			ResultCounterCallback<String> cb2 = new ResultCounterCallback<String>();

			HttpClientCallback hcb1 = new HttpClientBodyCallback(cb1);
			HttpClientCallback hcb2 = new HttpClientBodyCallback(cb2);

			HttpClient client = new HttpClient();

			int count1 = 1000;
			for (int i = 0; i < count1; i++) {
				client.get("localhost", 8080, GET_LOCALHOST, hcb1);
			}

			int count2 = 10;
			for (int i = 0; i < count2; i++) {
				client.get("www.rapidoid.org", 80, GET_RAPIDOID_ORG, hcb2);
			}

			waiting();
			while (cb1.getResultCount() < count1) {
				timeout(50000);
			}

			waiting();
			while (cb2.getResultCount() < count2) {
				timeout(60000);
			}

			eq(cb1.getResults(), U.set(SIMPLE_RESPONSE));
			eq(cb2.getResults().size(), 1);

			isTrue(cb2.getResults().iterator().next().length() > 5000);

			client.shutdown();
			localServer.shutdown();
		}
	}

}
