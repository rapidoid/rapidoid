package org.rapidoid.net.impl;

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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Inject;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.config.Conf;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.TCPClient;
import org.rapidoid.util.Cls;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class RapidoidClientLoop extends AbstractEventLoop<TCPClient> implements TCPClient {

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
	public synchronized void connect(String serverHost, int serverPort, Protocol clientProtocol) {

		InetSocketAddress addr = new InetSocketAddress(serverHost, serverPort);
		SocketChannel socketChannel = openSocket();

		try {
			RapidoidWorker targetWorker = workers[currentWorkerInd];
			targetWorker.connect(new ConnectionTarget(socketChannel, addr, clientProtocol));
		} catch (IOException e) {
			throw U.rte("Cannot create a TCP client connection!", e);
		}

		switchToNextWorker();
	}

	@Override
	public synchronized void connect(String serverHost, int serverPort, Protocol clientProtocol, int connectionsN) {
		for (int i = 0; i < connectionsN; i++) {
			connect(serverHost, serverPort, clientProtocol);
		}
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

		connect(host, port, protocol, connections);

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

}
