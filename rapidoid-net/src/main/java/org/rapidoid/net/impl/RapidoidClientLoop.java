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

import org.rapidoid.annotation.Inject;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.net.TCPClient;
import org.rapidoid.util.U;

public class RapidoidClientLoop extends AbstractEventLoop<TCPClient> implements TCPClient {

	private RapidoidWorker[] workers;

	@Inject(optional = true)
	private String host = null;

	@Inject(optional = true)
	private int port = 80;

	@Inject(optional = true)
	private int workersN = U.cpus();

	@Inject(optional = true)
	private int bufSizeKB = 16;

	@Inject(optional = true)
	private boolean noDelay = false;

	@Inject(optional = true)
	private int connections = 1;

	private final Protocol protocol;

	private final Class<? extends RapidoidHelper> helperClass;

	private final Class<? extends DefaultExchange<?, ?>> exchangeClass;

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

		U.notNull(host, "host");

		InetSocketAddress addr = new InetSocketAddress(host, port);

		workers = new RapidoidWorker[workersN];

		for (int i = 0; i < workers.length; i++) {
			RapidoidHelper helper = U.newInstance(helperClass, exchangeClass);
			String workerName = "client" + (i + 1);
			workers[i] = new RapidoidWorker(workerName, new BufGroup(13), protocol, helper, bufSizeKB, noDelay);
			new Thread(workers[i], workerName).start();
		}

		int workerInd = 0;

		for (int c = 0; c < connections; c++) {

			SocketChannel socketChannel = SocketChannel.open();

			if ((socketChannel.isOpen())) {
				workers[workerInd++].connect(new ConnectionTarget(socketChannel, addr));

				if (workerInd == workers.length) {
					workerInd = 0;
				}

			} else {
				throw U.rte("Cannot open socket!");
			}
		}

	}

	@Override
	public synchronized TCPClient start() {
		new Thread(this, "client").start();

		return super.start();
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
