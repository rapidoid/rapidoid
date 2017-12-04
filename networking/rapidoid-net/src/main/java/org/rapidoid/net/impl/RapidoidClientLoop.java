package org.rapidoid.net.impl;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
import org.rapidoid.cls.Cls;
import org.rapidoid.config.ConfigUtil;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.TCPClient;
import org.rapidoid.net.TCPClientInfo;
import org.rapidoid.net.abstracts.ChannelHolder;
import org.rapidoid.u.U;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

@Authors("Nikolche Mihajlovski")
@Since("5.5.0")
public class RapidoidClientLoop extends AbstractEventLoop<TCPClient> implements TCPClient, TCPClientInfo {

	private volatile ExtendedWorker[] ioWorkers;

	private ExtendedWorker currentWorker;

	private final String host;

	private final int port;

	private int workers = ConfigUtil.cpus();

	private boolean blockingAccept = false;

	protected final Protocol protocol;

	private final Class<? extends RapidoidHelper> helperClass;

	private final Class<? extends DefaultExchange<?>> exchangeClass;

	private ServerSocketChannel serverSocketChannel;

	private final int bufSizeKB;

	private final boolean noDelay;

	private final boolean syncBufs;

	private final boolean autoreconnecting;

	// initial number of connections to the default server
	private final int connections;

	// round-robin workers for new connections
	private int currentWorkerInd = 0;

	private final SSLContext sslContext;

	public RapidoidClientLoop(Protocol protocol, Class<? extends DefaultExchange<?>> exchangeClass,
	                          Class<? extends RapidoidHelper> helperClass, String host, int port,
	                          int workers, int bufSizeKB, boolean noDelay, boolean syncBufs,
	                          boolean autoreconnecting, int connections, SSLContext sslContext) {
		super("client");

		this.protocol = protocol;
		this.exchangeClass = exchangeClass;
		this.host = host;
		this.port = port;
		this.workers = workers;
		this.bufSizeKB = bufSizeKB;
		this.noDelay = noDelay;
		this.syncBufs = syncBufs;
		this.helperClass = U.or(helperClass, RapidoidHelper.class);
		this.autoreconnecting = autoreconnecting;
		this.connections = connections;
		this.sslContext = sslContext;
	}

	@Override
	protected void doProcessing() {
	}

	@Override
	protected final void beforeLoop() {
		try {
			openSockets();
		} catch (IOException e) {
			throw U.rte("Cannot open socket!", e);
		}
	}

	private void openSockets() throws IOException {
		ioWorkers = new ExtendedWorker[workers];

		for (int i = 0; i < ioWorkers.length; i++) {
			RapidoidHelper helper = Cls.newInstance(helperClass, exchangeClass);
			String workerName = "client" + (i + 1);
			ioWorkers[i] = new ExtendedWorker(workerName, protocol, helper, bufSizeKB, noDelay, syncBufs, sslContext);

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

		connect(host, port, protocol, connections, autoreconnecting, null);

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
