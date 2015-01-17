package org.rapidoid;

/*
 * #%L
 * rapidoid-net
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

import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.TCP;
import org.rapidoid.net.TCPClient;
import org.rapidoid.net.TCPServer;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.impl.Protocol;
import org.rapidoid.util.U;
import org.testng.annotations.Test;

public class TcpClientTest extends NetTestCommons {

	@Test
	public void testTCPClient() {
		Log.setLogLevel(LogLevel.DEBUG);

		TCPClient client = TCP.client().host("localhost").port(8080).connections(1).protocol(new Protocol() {
			@Override
			public void process(Channel ctx) {
				if (ctx.isInitial()) {
					ctx.writeln("Hi there!");
					return;
				}

				eq(ctx.readln(), "HI THERE!");
			}
		}).build().start();

		U.sleep(3000);

		TCPServer server = TCP.server().port(8080).protocol(new Protocol() {
			@Override
			public void process(Channel ctx) {
				if (ctx.isInitial()) {
					return;
				}
				ctx.writeln(ctx.readln().toUpperCase());
			}
		}).build().start();

		U.sleep(3000);

		client.shutdown();
		server.shutdown();
	}

}
