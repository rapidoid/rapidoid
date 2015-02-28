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
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Inject;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.config.Conf;
import org.rapidoid.log.Log;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.TCPServer;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Rnd;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class RapidoidServerLoop extends AbstractEventLoop<TCPServer> implements TCPServer {

	private volatile RapidoidWorker[] workers;

	private int workerIndex = 0;

	@Inject(optional = true)
	private int port = 8080;

	@Inject(optional = true)
	private int workersN = Conf.cpus();

	@Inject(optional = true)
	private int bufSizeKB = 16;

	@Inject(optional = true)
	private boolean noDelay = false;

	protected final Protocol protocol;

	private final Class<? extends RapidoidHelper> helperClass;

	private final Class<? extends DefaultExchange<?, ?>> exchangeClass;

	private ServerSocketChannel serverSocketChannel;

	public RapidoidServerLoop(Protocol protocol, Class<? extends DefaultExchange<?, ?>> exchangeClass,
			Class<? extends RapidoidHelper> helperClass) {
		super("server");
		this.protocol = protocol;
		this.exchangeClass = exchangeClass;
		this.helperClass = U.or(helperClass, RapidoidHelper.class);
	}

	@Override
	protected void acceptOP(SelectionKey key) throws IOException {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();

		SocketChannel socketChannel = serverChannel.accept();

		RapidoidWorker worker = workers[workerIndex];
		workerIndex++;
		if (workerIndex >= workers.length) {
			workerIndex = 0;
		}

		worker.accept(socketChannel);
	}

	@Override
	protected void doProcessing() {
	}

	@Override
	protected final void beforeLoop() {
		try {
			openSocket();
		} catch (IOException e) {
			throw U.rte("Cannot open socket!", e);
		}
	}

	private void openSocket() throws IOException {
		U.notNull(protocol, "protocol");
		U.notNull(helperClass, "helperClass");

		serverSocketChannel = ServerSocketChannel.open();

		if ((serverSocketChannel.isOpen()) && (selector.isOpen())) {

			serverSocketChannel.configureBlocking(false);
			ServerSocket socket = serverSocketChannel.socket();

			InetSocketAddress addr = new InetSocketAddress(port);
			socket.bind(addr);

			Log.info("Opened socket", "address", addr);

			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			Log.info("Waiting for connections...");

			workers = new RapidoidWorker[workersN];
			for (int i = 0; i < workers.length; i++) {
				RapidoidHelper helper = Cls.newInstance(helperClass, exchangeClass);
				String workerName = "server" + (i + 1);
				BufGroup bufGroup = new BufGroup(14); // 2^14B (16 KB per buffer segment)
				workers[i] = new RapidoidWorker(workerName, bufGroup, protocol, helper, bufSizeKB, noDelay);
				new Thread(workers[i], workerName).start();
			}
		} else {
			throw U.rte("Cannot open socket!");
		}
	}

	@Override
	public synchronized TCPServer start() {
		new Thread(this, "server").start();

		// wait for the server thread to activate
		while (status == LoopStatus.INIT || status == LoopStatus.BEFORE_LOOP) {
			UTILS.sleep(50);
		}

		return super.start();
	}

	@Override
	public synchronized TCPServer shutdown() {
		stopLoop();

		for (RapidoidWorker worker : workers) {
			worker.stopLoop();
		}

		if ((serverSocketChannel.isOpen()) && (selector.isOpen())) {
			try {
				selector.close();
				serverSocketChannel.close();
			} catch (IOException e) {
				Log.warn("Cannot close socket or selector!", e);
			}
		}

		return super.shutdown();
	}

	public synchronized RapidoidConnection newConnection() {
		int rndWorker = Rnd.rnd(workers.length);
		return workers[rndWorker].newConnection();
	}

	public synchronized void process(RapidoidConnection conn) {
		conn.worker.process(conn);
	}

	@Override
	public synchronized String process(String input) {
		RapidoidConnection conn = newConnection();
		conn.setInitial(false);
		conn.input.append(input);
		conn.setProtocol(protocol);
		process(conn);
		return conn.output.asText();
	}

	public Protocol getProtocol() {
		return protocol;
	}

}
