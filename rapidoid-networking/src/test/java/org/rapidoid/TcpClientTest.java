/*-
 * #%L
 * rapidoid-networking
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
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

package org.rapidoid;


import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.Server;
import org.rapidoid.net.TCP;
import org.rapidoid.net.TCPClient;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public class TcpClientTest extends NetTestCommons {

	private final Protocol UPPERCASE_SERVER = new Protocol() {
		@Override
		public void process(Channel ctx) {
			if (ctx.isInitial()) {
				return;
			}
			ctx.writeln(ctx.readln().toUpperCase());
		}
	};

	private final Protocol HI_CLIENT = new Protocol() {
		@Override
		public void process(Channel ctx) {
			if (ctx.isInitial()) {
				ctx.writeln("Hi there!");
				return;
			}

			eq(ctx.readln(), "HI THERE!");
		}
	};

	@Test
	public void testTCPClientWithDefaultConnections() {
		Log.setLogLevel(LogLevel.DEBUG);

		TCPClient client = TCP.client().host("localhost").port(8888).connections(5).protocol(HI_CLIENT).build().start();

		// let the clients wait
		U.sleep(3000);

		Server server = TCP.server().port(8888).protocol(UPPERCASE_SERVER).build().start();

		// let the server serve the clients
		U.sleep(3000);

		eq(client.info().messagesProcessed(), 5);
		eq(server.info().messagesProcessed(), 5);

		client.shutdown();
		server.shutdown();
	}

	@Test
	public void testTCPClientWithCustomConnections() {
		Log.setLogLevel(LogLevel.DEBUG);

		TCPClient client = TCP.client().build().start();
		client.connect("localhost", 8888, HI_CLIENT, 10, false, null);

		// let the clients wait
		U.sleep(3000);

		Server server = TCP.server().port(8888).protocol(UPPERCASE_SERVER).build().start();

		// let the server serve the clients
		U.sleep(3000);

		eq(client.info().messagesProcessed(), 10);
		eq(server.info().messagesProcessed(), 10);

		client.shutdown();
		server.shutdown();
	}

	@Test
	public void testTCPClientWithDefaultAndCustomConnections() {
		Log.setLogLevel(LogLevel.DEBUG);

		TCPClient client = TCP.client().host("127.0.0.1").port(8888).connections(3).protocol(HI_CLIENT).build().start();
		client.connect("localhost", 9090, HI_CLIENT, 2, false, null);

		// let the clients wait
		U.sleep(3000);

		Server server1 = TCP.server().port(8888).protocol(UPPERCASE_SERVER).build().start();
		Server server2 = TCP.server().port(9090).protocol(UPPERCASE_SERVER).build().start();

		// let the servers serve the clients
		U.sleep(3000);

		eq(client.info().messagesProcessed(), 5);
		eq(server1.info().messagesProcessed(), 3);
		eq(server2.info().messagesProcessed(), 2);

		client.shutdown();
		server1.shutdown();
		server2.shutdown();
	}

}
