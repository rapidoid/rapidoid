package org.rapidoidx.http;

/*
 * #%L
 * rapidoid-x-http-client
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
import org.rapidoid.config.Conf;
import org.rapidoid.http.HTTP;
import org.rapidoid.http.HTTPServer;
import org.rapidoid.http.Handler;
import org.rapidoid.http.HttpExchange;
import org.rapidoid.test.TestCommons;
import org.rapidoid.util.D;
import org.rapidoid.util.UTILS;
import org.rapidoid.wrap.BoolWrap;
import org.rapidoid.wrap.IntWrap;
import org.rapidoidx.bytes.BytesUtil;
import org.rapidoidx.data.Range;
import org.rapidoidx.data.Ranges;
import org.rapidoidx.net.TCP;
import org.rapidoidx.net.abstracts.Channel;
import org.rapidoidx.net.impl.FiniteStateProtocol;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class HttpPipeliningTest extends TestCommons {

	protected static final byte[] REQ = "GET /hello H\r\nasf:asf\r\n\r\n".getBytes();

	protected static final byte[] RESP = "Hello".getBytes();

	@Test
	public void testHttpServerPipelining() {
		Conf.args("workers=1");

		HTTPServer server = HTTP.server().build();

		server.get("/hello", new Handler() {
			@Override
			public Object handle(HttpExchange x) {
				return "Hello";
			}
		});

		server.start();

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

		server.shutdown();

		isFalse(err.value);
		D.print(counter.value, counter.value / sec);
	}

}
