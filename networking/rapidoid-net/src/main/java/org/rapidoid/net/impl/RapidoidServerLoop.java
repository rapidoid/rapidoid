package org.rapidoid.net.impl;

import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Rnd;
import org.rapidoid.log.Log;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.Server;
import org.rapidoid.net.TCPServerInfo;
import org.rapidoid.u.U;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class RapidoidServerLoop extends AbstractLoop<Server> implements Server, TCPServerInfo {

	private static final int MAX_PENDING_CONNECTIONS = 16 * 1024;

	private volatile RapidoidWorker[] ioWorkers;

	private RapidoidWorker currentWorker;

	private final String address;

	private final int port;

	private final int workers;

	private final boolean blockingAccept;

	protected final Protocol protocol;

	private final Class<? extends RapidoidHelper> helperClass;

	private final Class<? extends DefaultExchange<?>> exchangeClass;

	private ServerSocketChannel serverSocketChannel;

	private final Selector selector;

	private final int bufSizeKB;

	private final boolean noDelay;

	private final boolean syncBufs;

	private final SSLContext sslContext;

	public RapidoidServerLoop(Protocol protocol, Class<? extends DefaultExchange<?>> exchangeClass,
	                          Class<? extends RapidoidHelper> helperClass, String address, int port,
	                          int workers, int bufSizeKB, boolean noDelay, boolean syncBufs,
	                          boolean blockingAccept, SSLContext sslContext) {
		super("server");

		this.protocol = protocol;
		this.exchangeClass = exchangeClass;
		this.address = address;
		this.port = port;
		this.workers = workers;
		this.bufSizeKB = bufSizeKB;
		this.noDelay = noDelay;
		this.syncBufs = syncBufs;
		this.blockingAccept = blockingAccept;
		this.helperClass = U.or(helperClass, RapidoidHelper.class);
		this.sslContext = sslContext;

		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			Log.error("Cannot open selector!", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	protected final void beforeLoop() {
		validate();

		try {
			openSocket();
		} catch (IOException e) {
			throw U.rte("Cannot open socket!", e);
		}
	}

	private void validate() {
		U.must(workers <= RapidoidWorker.MAX_IO_WORKERS, "Too many workers! Maximum = %s",
			RapidoidWorker.MAX_IO_WORKERS);
	}

	private void openSocket() throws IOException {
		U.notNull(protocol, "protocol");
		U.notNull(helperClass, "helperClass");

		String blockingInfo = blockingAccept ? "blocking" : "non-blocking";
		Log.debug("Initializing server", "address", address, "port", port, "sync", syncBufs, "accept", blockingInfo);

		serverSocketChannel = ServerSocketChannel.open();

		if ((serverSocketChannel.isOpen()) && (selector.isOpen())) {

			serverSocketChannel.configureBlocking(blockingAccept);

			ServerSocket socket = serverSocketChannel.socket();

			Log.info("!Starting server", "!address", address, "!port", port, "I/O workers", workers, "sync", syncBufs, "accept", blockingInfo);

			InetSocketAddress addr = new InetSocketAddress(address, port);

			socket.setReceiveBufferSize(16 * 1024);
			socket.setReuseAddress(true);
			socket.bind(addr, MAX_PENDING_CONNECTIONS);

			Log.debug("Opened server socket", "address", addr);

			if (!blockingAccept) {
				Log.debug("Registering accept selector");
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			}

			initWorkers();

		} else {
			throw U.rte("Cannot open socket!");
		}
	}

	private void initWorkers() {
		ioWorkers = new RapidoidWorker[workers];

		for (int i = 0; i < ioWorkers.length; i++) {

			RapidoidWorkerThread workerThread = new RapidoidWorkerThread(i, protocol, exchangeClass,
				helperClass, bufSizeKB, noDelay, syncBufs, sslContext);

			workerThread.start();

			ioWorkers[i] = workerThread.getWorker();

			if (i > 0) {
				ioWorkers[i - 1].next = ioWorkers[i];
			}
		}

		ioWorkers[ioWorkers.length - 1].next = ioWorkers[0];
		currentWorker = ioWorkers[0];

		for (RapidoidWorker worker : ioWorkers) {
			worker.waitToStart();
		}
	}

	@Override
	public synchronized Server start() {
		new RapidoidThread(this, "server").start();

		waitForStatusOtherThan(LoopStatus.INIT, LoopStatus.BEFORE_LOOP);

		if (status == LoopStatus.FAILED) {
			throw U.rte("Server start-up failed!");
		}

		return super.start();
	}

	@Override
	public synchronized Server shutdown() {
		Log.info("Shutting down the server...");
		stopLoop();

		if (ioWorkers != null) {
			for (RapidoidWorker worker : ioWorkers) {
				worker.shutdown();
			}
		}

		if (serverSocketChannel != null && selector != null && serverSocketChannel.isOpen() && selector.isOpen()) {
			try {
				selector.close();
				serverSocketChannel.close();
			} catch (IOException e) {
				Log.warn("Cannot close socket or selector!", e);
			}
		}

		super.shutdown();
		Log.info("!The server is down.");
		return this;
	}

	public synchronized RapidoidConnection newConnection() {
		int rndWorker = Rnd.rnd(ioWorkers.length);
		return ioWorkers[rndWorker].newConnection();
	}

	public synchronized void process(RapidoidConnection conn) {
		conn.worker.process(conn);
	}

	@Override
	public synchronized String process(String input) {
		if (ioWorkers == null) {
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

		for (int i = 0; i < ioWorkers.length; i++) {
			total += ioWorkers[i].getMessagesProcessed();
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
			SocketChannel channel = serverChannel.accept();
			currentWorker.accept(channel);
			currentWorker = currentWorker.next;

		} catch (IOException e) {
			Log.error("Acceptor error!", e);
		}
	}

}
