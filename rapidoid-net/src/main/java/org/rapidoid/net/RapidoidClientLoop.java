package org.rapidoid.net;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import org.rapidoid.Protocol;
import org.rapidoid.RapidoidClient;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.config.ServerConfig;
import org.rapidoid.util.U;

public class RapidoidClientLoop extends AbstractEventLoop implements RapidoidClient {

	private RapidoidWorker[] workers;

	private final int workersN;

	private final ServerConfig config;

	private final Protocol protocol;

	private final Class<? extends RapidoidHelper> helperClass;

	private final Class<? extends Exchange> exchangeClass;

	private final String host;

	private final int port;

	private final int connections;

	public RapidoidClientLoop(int connections, ServerConfig config, Protocol protocol,
			Class<? extends Exchange> exchangeClass, Class<? extends RapidoidHelper> helperClass, String host, int port) {
		super("main", config);
		this.connections = connections;
		this.protocol = protocol;
		this.exchangeClass = exchangeClass;
		this.helperClass = helperClass;
		this.host = host;
		this.port = port;
		this.workersN = config.workers();
		this.config = config;
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

		InetSocketAddress addr = new InetSocketAddress(host, port);

		workers = new RapidoidWorker[workersN];

		for (int i = 0; i < workers.length; i++) {
			RapidoidHelper helper = U.newInstance(helperClass, exchangeClass);
			String workerName = "client" + (i + 1);
			workers[i] = new RapidoidWorker(workerName, new BufGroup(13), config, protocol, helper);
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
	public void start() {
		new Thread(this, "client").start();
	}

	@Override
	public synchronized void stop() {
		stopLoop();

		for (RapidoidWorker worker : workers) {
			worker.close();
			worker.stopLoop();
		}

		// TODO: selector is not used here!
	}

}
