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
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Inject;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.log.Log;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.TCPServer;
import org.rapidoid.net.TCPServerInfo;
import org.rapidoid.util.Rnd;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class RapidoidServerLoop extends AbstractLoop<TCPServer> implements TCPServer, TCPServerInfo {

	private volatile RapidoidWorker[] workers;

	private RapidoidWorker currentWorker;

	@Inject(optional = true)
	private int port = 8080;

	@Inject(optional = true)
	private int workersN = Conf.cpus();

	@Inject(optional = true)
	private int bufSizeKB = 16;

	@Inject(optional = true)
	private boolean noDelay = false;

	@Inject(optional = true)
	private boolean blockingAccept = false;

	protected final Protocol protocol;

	private final Class<? extends RapidoidHelper> helperClass;

	private final Class<? extends DefaultExchange<?>> exchangeClass;

	private ServerSocketChannel serverSocketChannel;

	private final Selector selector;

	public RapidoidServerLoop(Protocol protocol, Class<? extends DefaultExchange<?>> exchangeClass,
			Class<? extends RapidoidHelper> helperClass) {
		super("server");
		this.protocol = protocol;
		this.exchangeClass = exchangeClass;
		this.helperClass = U.or(helperClass, RapidoidHelper.class);

		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			Log.severe("Cannot open selector!", e);
			throw new RuntimeException(e);
		}
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

		Log.info("Initializing server", "port", port, "accept", blockingAccept ? "blocking" : "non-blocking");

		serverSocketChannel = ServerSocketChannel.open();

		if ((serverSocketChannel.isOpen()) && (selector.isOpen())) {

			serverSocketChannel.configureBlocking(blockingAccept);

			ServerSocket socket = serverSocketChannel.socket();

			Log.info("Opening port to listen", "port", port);

			InetSocketAddress addr = new InetSocketAddress(port);

			socket.setReceiveBufferSize(16 * 1024);
			socket.setReuseAddress(true);
			socket.bind(addr, 1024);

			Log.info("Opened server socket", "address", addr);

			if (!blockingAccept) {
				Log.info("Registering accept selector");
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			}

			Log.info("Waiting for connections...");

			initWorkers();

		} else {
			throw U.rte("Cannot open socket!");
		}
	}

	private void initWorkers() {
		workers = new RapidoidWorker[workersN];

		for (int i = 0; i < workers.length; i++) {
			RapidoidHelper helper = Cls.newInstance(helperClass, exchangeClass);
			String workerName = "server" + (i + 1);
			BufGroup bufGroup = new BufGroup(14); // 2^14B (16 KB per buffer
													// segment)
			workers[i] = new RapidoidWorker(workerName, bufGroup, protocol, helper, bufSizeKB, noDelay);

			if (i > 0) {
				workers[i - 1].next = workers[i];
			}

			new Thread(workers[i], workerName).start();
		}

		workers[workers.length - 1].next = workers[0];
		currentWorker = workers[0];

		for (RapidoidWorker worker : workers) {
			worker.waitToStart();
		}
	}

	@Override
	public synchronized TCPServer start() {
		new Thread(this, "server").start();

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
		if (workers == null) {
			initWorkers();
		}

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

	@Override
	public TCPServerInfo info() {
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

	@Override
	protected void insideLoop() {
		if (blockingAccept) {
			processBlocking();
		} else {
			processNonBlocking();
		}
	}

	private void processNonBlocking() {
		try {
			selector.select(50);
		} catch (IOException e) {
			Log.error("Select failed!", e);
		}

		try {
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			synchronized (selectedKeys) {

				Iterator<?> iter = selectedKeys.iterator();

				while (iter.hasNext()) {
					SelectionKey key = (SelectionKey) iter.next();
					iter.remove();

					acceptChannel((ServerSocketChannel) key.channel());
				}
			}
		} catch (ClosedSelectorException e) {
			// do nothing
		}
	}

	private void processBlocking() {
		acceptChannel(serverSocketChannel);
	}

	private void acceptChannel(ServerSocketChannel serverChannel) {
		try {
			SocketChannel channel = serverSocketChannel.accept();
			currentWorker.accept(channel);
			currentWorker = currentWorker.next;
		} catch (IOException e) {
			Log.error("Acceptor error!", e);
		}
	}

}
