package org.rapidoidx.net.impl;

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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Inject;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.log.Log;
import org.rapidoid.util.Rnd;
import org.rapidoid.util.U;
import org.rapidoidx.buffer.BufGroup;
import org.rapidoidx.net.Protocol;
import org.rapidoidx.net.TCPServer;
import org.rapidoidx.net.TCPServerInfo;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class RapidoidServerLoop extends AbstractEventLoop<TCPServer> implements TCPServer, TCPServerInfo {

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
	protected void doProcessing() {}

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

			Log.info("Opening port to listen", "port", port);

			InetSocketAddress addr = new InetSocketAddress(port);

			socket.bind(addr);

			Log.info("Opened socket", "address", addr);

			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			Log.info("Waiting for connections...");

			workers = new RapidoidWorker[workersN];

			for (int i = 0; i < workers.length; i++) {
				RapidoidHelper helper = Cls.newInstance(helperClass, exchangeClass);
				String workerName = "server" + (i + 1);
				BufGroup bufGroup = new BufGroup(14); // 2^14B (16 KB per buffer
														// segment)
				workers[i] = new RapidoidWorker(workerName, bufGroup, protocol, helper, bufSizeKB, noDelay);
				new Thread(workers[i], workerName).start();
			}

			for (RapidoidWorker worker : workers) {
				worker.waitToStart();
			}

		} else {
			throw U.rte("Cannot open socket!");
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

}
