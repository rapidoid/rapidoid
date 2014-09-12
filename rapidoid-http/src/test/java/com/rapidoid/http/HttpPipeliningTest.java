package com.rapidoid.http;

/*
 * #%L
 * rapidoid-http
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import org.rapidoid.util.F2;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class HttpPipeliningTest extends HttpTestCommons {

	protected static final byte[] REQ = "GET /hello H\nasf:asf\n\n".getBytes();

	@Test
	public void testHttpServerPipelining() {
		server();

		final int K = 10000;
		final int N = 10;

		U.connect("localhost", 8080, new F2<Void, BufferedReader, DataOutputStream>() {
			@Override
			public Void execute(final BufferedReader in, final DataOutputStream out) throws Exception {
				U.benchmark("10-req batch", K, new Runnable() {
					@Override
					public void run() {
						try {
							for (int i = 1; i <= N; i++) {
								out.write(REQ);
							}
							for (int i = 1; i <= N; i++) {
								readLn(in);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
				return null;
			}

			private void readLn(BufferedReader in) throws IOException {
				int a = 0, b = 0, c = 0;
				boolean done;
				do {
					c = b;
					b = a;
					a = in.read();
					done = a == 10 && b == 13 && c == 10;
				} while (!done);
			}
		});

		shutdown();
	}
	
}
