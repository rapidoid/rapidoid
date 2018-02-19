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

package org.rapidoid.net.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.net.NetworkingParams;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.TCPClient;
import org.rapidoid.net.TCPClientInfo;
import org.rapidoid.net.abstracts.ChannelHolder;
import org.rapidoid.u.U;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public class RapidoidClientLoop extends AbstractEventLoop<TCPClient> implements TCPClient, TCPClientInfo {

	private final NetworkingParams net;

	// initial number of connections to the default server
	private final int connections;

	private final boolean autoReconnect;

	private final SSLContext sslContext;

	private volatile ExtendedWorker[] ioWorkers;

	// round-robin workers for new connections
	private int currentWorkerInd = 0;

	public RapidoidClientLoop(NetworkingParams net, boolean autoReconnect, int connections, SSLContext sslContext) {
		super("client");

		this.net = net;
		this.autoReconnect = autoReconnect;
		this.connections = connections;
		this.sslContext = sslContext;
	}

	@Override
	protected void doProcessing() {
	}

	@Override
	protected final void beforeLoop() {
		openSockets();
	}

	private void openSockets() {
		ioWorkers = new ExtendedWorker[net.workers()];

		for (int i = 0; i < ioWorkers.length; i++) {
			RapidoidHelper helper = Cls.newInstance(net.helperClass(), net.exchangeClass());
			String workerName = "client" + (i + 1);
			ioWorkers[i] = new ExtendedWorker(workerName, helper, net, sslContext);

			new Thread(ioWorkers[i], workerName).start();
		}

		for (int i = 0; i < ioWorkers.length; i++) {
			ioWorkers[i].waitToStart();
		}
	}

	@Override
	public synchronized ChannelHolder connect(String serverHost, int serverPort, Protocol clientProtocol,
	                                          boolean autoreconnecting, ConnState state) {

		InetSocketAddress addr = new InetSocketAddress(serverHost, serverPort);
		SocketChannel socketChannel = openSocket();

		ChannelHolderImpl holder = new ChannelHolderImpl();

		try {
			ExtendedWorker targetWorker = ioWorkers[currentWorkerInd];
			ConnectionTarget target = new ConnectionTarget(socketChannel, addr, clientProtocol, holder, autoreconnecting, state);
			targetWorker.connect(target);

		} catch (IOException e) {
			throw U.rte("Cannot create a TCP client connection!", e);
		}

		switchToNextWorker();
		return holder;
	}

	@Override
	public synchronized ChannelHolder[] connect(String serverHost, int serverPort, Protocol clientProtocol,
	                                            int connectionsN, boolean autoreconnecting, ConnState state) {

		ChannelHolder[] holders = new ChannelHolder[connectionsN];

		for (int i = 0; i < connectionsN; i++) {
			holders[i] = connect(serverHost, serverPort, clientProtocol, autoreconnecting, state);
		}

		return holders;
	}

	private synchronized void switchToNextWorker() {
		currentWorkerInd++;
		if (currentWorkerInd == ioWorkers.length) {
			currentWorkerInd = 0;
		}
	}

	protected static SocketChannel openSocket() {
		try {
			SocketChannel socketChannel = SocketChannel.open();

			if (!socketChannel.isOpen()) {
				throw U.rte("Cannot open socket!");
			}

			return socketChannel;
		} catch (IOException e) {
			throw U.rte("Cannot open socket!", e);
		}
	}

	@Override
	public synchronized TCPClient start() {
		new Thread(this, "client").start();

		super.start();

		connect(net.address(), net.port(), net.protocol(), connections, autoReconnect, null);

		return this;
	}

	@Override
	public synchronized TCPClient shutdown() {
		stopLoop();

		for (ExtendedWorker worker : ioWorkers) {
			worker.stopLoop();
		}

		return super.shutdown();
	}

	@Override
	public TCPClientInfo info() {
		return this;
	}

	@Override
	public long messagesProcessed() {
		long total = 0;

		for (int i = 0; i < ioWorkers.length; i++) {
			total += ioWorkers[i].getMessagesProcessed();
		}

		return total;
	}

}
