package org.rapidoidx.net.impl;

/*
 * #%L
 * rapidoid-x-net
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Inject;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.util.U;
import org.rapidoidx.buffer.BufGroup;
import org.rapidoidx.net.Protocol;
import org.rapidoidx.net.TCPClient;
import org.rapidoidx.net.TCPClientInfo;
import org.rapidoidx.net.abstracts.ChannelHolder;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class RapidoidClientLoop extends AbstractEventLoop<TCPClient> implements TCPClient, TCPClientInfo {

	private RapidoidWorker[] workers;

	@Inject(optional = true)
	private String host = null;

	@Inject(optional = true)
	private int port = 80;

	@Inject(optional = true)
	private int workersN = Conf.cpus();

	@Inject(optional = true)
	private int bufSizeKB = 16;

	@Inject(optional = true)
	private boolean noDelay = false;

	@Inject(optional = true)
	private boolean autoreconnecting = false;

	// initial number of connections to the default server
	@Inject(optional = true)
	private int connections = 0;

	private final Protocol protocol;

	private final Class<? extends RapidoidHelper> helperClass;

	private final Class<? extends DefaultExchange<?, ?>> exchangeClass;

	// round-robin workers for new connections
	private int currentWorkerInd = 0;

	public RapidoidClientLoop(Protocol protocol, Class<? extends DefaultExchange<?, ?>> exchangeClass,
			Class<? extends RapidoidHelper> helperClass) {
		super("client");

		this.protocol = protocol;
		this.exchangeClass = exchangeClass;
		this.helperClass = U.or(helperClass, RapidoidHelper.class);
	}

	@Override
	protected void doProcessing() {}

	@Override
	protected final void beforeLoop() {
		try {
			openSockets();
		} catch (IOException e) {
			throw U.rte("Cannot open socket!", e);
		}
	}

	private void openSockets() throws IOException {
		workers = new RapidoidWorker[workersN];

		for (int i = 0; i < workers.length; i++) {
			RapidoidHelper helper = Cls.newInstance(helperClass, exchangeClass);
			String workerName = "client" + (i + 1);
			workers[i] = new RapidoidWorker(workerName, new BufGroup(13), null, helper, bufSizeKB, noDelay);
			new Thread(workers[i], workerName).start();
		}

		for (int i = 0; i < workers.length; i++) {
			workers[i].waitToStart();
		}
	}

	@Override
	public synchronized ChannelHolder connect(String serverHost, int serverPort, Protocol clientProtocol,
			boolean autoreconnecting, ConnState state) {

		InetSocketAddress addr = new InetSocketAddress(serverHost, serverPort);
		SocketChannel socketChannel = openSocket();

		ChannelHolderImpl holder = new ChannelHolderImpl();

		try {
			RapidoidWorker targetWorker = workers[currentWorkerInd];
			targetWorker.connect(new ConnectionTarget(socketChannel, addr, clientProtocol, holder, autoreconnecting,
					state));
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
		if (currentWorkerInd == workers.length) {
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

		for (RapidoidWorker worker : workers) {
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

		for (int i = 0; i < workers.length; i++) {
			total += workers[i].getMessagesProcessed();
		}

		return total;
	}

}
