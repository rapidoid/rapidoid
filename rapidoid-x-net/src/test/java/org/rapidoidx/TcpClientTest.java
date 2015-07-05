package org.rapidoidx;

/*
 * #%L
 * rapidoid-x-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import org.rapidoid.log.Log;
import org.rapidoid.log.LogLevel;
import org.rapidoid.util.UTILS;
import org.rapidoidx.net.Protocol;
import org.rapidoidx.net.TCP;
import org.rapidoidx.net.TCPClient;
import org.rapidoidx.net.TCPServer;
import org.rapidoidx.net.abstracts.Channel;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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

		TCPClient client = TCP.client().host("localhost").port(8080).connections(5).protocol(HI_CLIENT).build().start();

		// let the clients wait
		UTILS.sleep(3000);

		TCPServer server = TCP.server().port(8080).protocol(UPPERCASE_SERVER).build().start();

		// let the server serve the clients
		UTILS.sleep(3000);

		eq(client.info().messagesProcessed(), 5);
		eq(server.info().messagesProcessed(), 5);

		client.shutdown();
		server.shutdown();
	}

	@Test
	public void testTCPClientWithCustomConnections() {
		Log.setLogLevel(LogLevel.DEBUG);

		TCPClient client = TCP.client().build().start();
		client.connect("localhost", 8080, HI_CLIENT, 10, false, null);

		// let the clients wait
		UTILS.sleep(3000);

		TCPServer server = TCP.server().port(8080).protocol(UPPERCASE_SERVER).build().start();

		// let the server serve the clients
		UTILS.sleep(3000);

		eq(client.info().messagesProcessed(), 10);
		eq(server.info().messagesProcessed(), 10);

		client.shutdown();
		server.shutdown();
	}

	@Test
	public void testTCPClientWithDefaultAndCustomConnections() {
		Log.setLogLevel(LogLevel.DEBUG);

		TCPClient client = TCP.client().host("127.0.0.1").port(8080).connections(3).protocol(HI_CLIENT).build().start();
		client.connect("localhost", 9090, HI_CLIENT, 2, false, null);

		// let the clients wait
		UTILS.sleep(3000);

		TCPServer server1 = TCP.server().port(8080).protocol(UPPERCASE_SERVER).build().start();
		TCPServer server2 = TCP.server().port(9090).protocol(UPPERCASE_SERVER).build().start();

		// let the servers serve the clients
		UTILS.sleep(3000);

		eq(client.info().messagesProcessed(), 5);
		eq(server1.info().messagesProcessed(), 3);
		eq(server2.info().messagesProcessed(), 2);

		client.shutdown();
		server1.shutdown();
		server2.shutdown();
	}

}
