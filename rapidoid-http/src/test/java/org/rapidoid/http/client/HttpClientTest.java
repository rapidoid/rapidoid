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
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.5.0")
public class HttpClientTest extends HttpTestCommons {

	private static final String GET_LOCALHOST = "GET / HTTP/1.1\nHost:localhost\n\n";

	private static final String GET_RAPIDOID_IO = "GET / HTTP/1.1\nHost:rapidoid.io\n\n";

	protected static final String SIMPLE_RESPONSE = "AbC";

	@Test
	public void testHttpClient() {
		Log.setLogLevel(LogLevel.DEBUG);

		HTTPServer localServer = HTTP.serve(SIMPLE_RESPONSE);

		ResultCounterCallback<String> cb = new ResultCounterCallback<String>();

		HttpClient client = new HttpClient();

		client.get("rapidoid.io", 80, GET_RAPIDOID_IO, cb);
		client.get("localhost", 8080, GET_LOCALHOST, cb);

		waiting();
		while (cb.getResultCount() < 2) {
			timeout(5000);
		}

		client.shutdown();
		localServer.shutdown();
	}

}
