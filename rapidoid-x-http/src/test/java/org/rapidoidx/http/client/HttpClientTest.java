package org.rapidoidx.http.client;

/*
 * #%L
 * rapidoid-x-http
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.lambda.ResultCounterCallback;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.U;
import org.rapidoidx.http.client.HttpClient;
import org.rapidoidx.http.client.HttpClientBodyCallback;
import org.rapidoidx.http.client.HttpClientCallback;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class HttpClientTest extends TestCommons {

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
