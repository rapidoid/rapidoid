package org.rapidoid.http;

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
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.config.Conf;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.net.TCP;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.FiniteStateProtocol;
import org.rapidoid.util.D;
import org.rapidoid.util.UTILS;
import org.rapidoid.wrap.BoolWrap;
import org.rapidoid.wrap.IntWrap;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpPipeliningTest extends HttpTestCommons {

	protected static final byte[] REQ = "GET /hello H\r\nasf:asf\r\n\r\n".getBytes();

	protected static final byte[] RESP = "Hello".getBytes();

	@Test
	public void testHttpServerPipelining() {
		Conf.args("workers=1");

		defaultServerSetup();

		final int connections = 1000;
		final int pipelining = 10;

		final IntWrap counter = new IntWrap();
		final BoolWrap err = new BoolWrap();

		TCP.client().host("localhost").port(8080).connections(connections).protocol(new FiniteStateProtocol(2) {

			@Override
			protected int state0(Channel ctx) {
				for (int i = 0; i < pipelining; i++) {
					ctx.write(REQ);
				}
				return 1;
			}

			@Override
			protected int state1(Channel ctx) {
				final Ranges lines = ctx.helper().ranges1;
				final Range resp = ctx.helper().ranges2.ranges[0];

				for (int i = 0; i < pipelining; i++) {
					ctx.input().scanLnLn(lines.reset());
					ctx.input().scanN(5, resp); // response body: "Hello"

					if (!BytesUtil.matches(ctx.input().bytes(), resp, RESP, true)) {
						err.value = true;
					}

					counter.value++;
				}

				for (int i = 0; i < pipelining; i++) {
					ctx.write(REQ);
				}

				return 1;
			}

		}).build().start();

		int sec = 5;
		UTILS.sleep(sec * 1000);
		shutdown();

		isFalse(err.value);
		D.print(counter.value, counter.value / sec);
	}

}
