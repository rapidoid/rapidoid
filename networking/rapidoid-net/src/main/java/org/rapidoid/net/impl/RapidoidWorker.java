package org.rapidoid.net.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.buffer.BufUtil;
import org.rapidoid.buffer.IncompleteReadException;
import org.rapidoid.collection.Coll;
import org.rapidoid.config.Conf;
import org.rapidoid.config.ConfigUtil;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.expire.ExpirationCrawlerThread;
import org.rapidoid.expire.Expire;
import org.rapidoid.log.Log;
import org.rapidoid.net.Protocol;
import org.rapidoid.pool.Pool;
import org.rapidoid.pool.Pools;
import org.rapidoid.u.U;
import org.rapidoid.util.SimpleList;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

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
public class RapidoidWorker extends AbstractEventLoop<RapidoidWorker> {

	public static int MAX_IO_WORKERS = 1024;

	public static boolean EXTRA_SAFE = false;

	private static final ExpirationCrawlerThread idleConnectionsCrawler;

	private static final int connTimeout;

	private final Queue<SocketChannel> connected;

	private final SimpleList<RapidoidConnection> done;

	private final Pool<RapidoidConnection> connections;

	private final Set<RapidoidConnection> allConnections = Coll.concurrentSet();

	private final int maxPipelineSize;

	final Protocol serverProtocol;

	final RapidoidHelper helper;

	private final int bufSize;

	private final boolean noDelay;

	private final BufGroup bufs;

	private volatile long messagesProcessed;

	private final SSLContext sslContext;

	RapidoidWorker next;

	static {
		int timeoutResolution = Conf.HTTP.entry("timeoutResolution").or(5000);
		connTimeout = Conf.HTTP.entry("timeout").or(30000);

		if (timeoutResolution > 0 && connTimeout > 0) {
			idleConnectionsCrawler = Expire.crawler("idleConnections", timeoutResolution);
		} else {
			idleConnectionsCrawler = null;
		}
	}

	public RapidoidWorker(String name, final Protocol protocol, final RapidoidHelper helper,
	                      int bufSizeKB, boolean noDelay, boolean syncBufs, SSLContext sslContext) {

		super(name);

		this.bufSize = bufSizeKB * 1024;
		this.noDelay = noDelay;
		this.bufs = new BufGroup(bufSize, syncBufs);

		this.serverProtocol = protocol;
		this.helper = helper;
		this.sslContext = sslContext;

		this.maxPipelineSize = Conf.HTTP.entry("maxPipeline").or(10);

		final int queueSize = ConfigUtil.micro() ? 1000 : 1000000;
		final int growFactor = ConfigUtil.micro() ? 2 : 10;

		this.connected = new ArrayBlockingQueue<SocketChannel>(queueSize);
		this.done = new SimpleList<RapidoidConnection>(queueSize / 10, growFactor);

		connections = Pools.create("connections", new Callable<RapidoidConnection>() {
			@Override
			public RapidoidConnection call() throws Exception {
				return newConnection();
			}
		}, 100000);

		if (idleConnectionsCrawler != null) {
			idleConnectionsCrawler.register(allConnections);
		}
	}

	public void accept(SocketChannel socketChannel) {
		connected.add(socketChannel);
		selector.wakeup();
	}

	private void configureSocket(SocketChannel socketChannel) throws IOException {
		socketChannel.configureBlocking(false);

		Socket socket = socketChannel.socket();
		socket.setTcpNoDelay(noDelay);
		socket.setReceiveBufferSize(bufSize);
		socket.setSendBufferSize(bufSize);
		socket.setReuseAddress(true);
	}

	@Override
	protected void readOP(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		RapidoidConnection conn = (RapidoidConnection) key.attachment();

		readInto(socketChannel, conn);

		process(conn);

		if (conn.closing) {
			close(key);
		}
	}

	private void readInto(SocketChannel socketChannel, RapidoidConnection conn) {
		int read;
		try {

			if (conn.hasTLS) {
				if (conn.tls.netIn.hasRemaining()) {
					read = socketChannel.read(conn.tls.netIn);

				} else {
					read = 0;
				}
			} else {
				read = conn.input.append(socketChannel);
			}

		} catch (Exception e) {
			Log.debug("Connection error", e);
			read = -1;
		}

		if (read == -1) {
			// the connection was closed
			Log.debug("The connection was closed!");
			conn.closing = true;

			if (conn.hasTLS) {
				conn.tls.closeInbound();
			}

		} else {

			if (conn.hasTLS) {
				if (read > 0) {
					boolean success = conn.tls.unwrapInput();
					if (success) wantToWrite(conn);
				}
			}
		}
	}

	public void process(RapidoidConnection conn) {
		messagesProcessed += processMsgs(conn);

		conn.completedInputPos = conn.input.position();
	}

	private long processMsgs(RapidoidConnection conn) {
		long reqN = 0;

		while (reqN < maxPipelineSize && conn.input().hasRemaining() && processNext(conn, false)) {
			reqN++;
		}

		touch(conn);

		return reqN;
	}

	private boolean processNext(RapidoidConnection conn, boolean initial) {

		long seq;

		if (initial) {
			// conn.log("<< INIT >>");

			seq = 0;
			conn.requestId = -1;
		} else {

			seq = conn.readSeq.incrementAndGet();

			// conn.log("<< PROCESS >>");
			U.must(conn.input().hasRemaining());

			conn.requestId = helper.requestIdGen;
			helper.requestIdGen += MAX_IO_WORKERS;
			helper.requestCounter++;
		}

		// prepare for a rollback in case the message isn't complete yet
		conn.input().checkpoint(conn.input().position());

		int limit = conn.input().limit();
		int osize = conn.output().size();

		BufUtil.doneWriting(conn.input());

		ConnState state = conn.state();
		long stateN = state.n;
		Object stateObj = state.obj;

		try {
			conn.done = false;
			conn.async = false;

			if (EXTRA_SAFE) {
				processNextExtraSafe(conn);
			} else {
				Protocol protocol = conn.getProtocol();

				if (protocol == null) {
					return false;
				}

				protocol.process(conn);
			}

			BufUtil.startWriting(conn.input());

			if (!conn.isAsync()) {
				if (!conn.closed) {
					conn.done();
				}

				conn.processedSeq(seq);
			}

			conn.input().deleteBefore(conn.input().checkpoint());

			// Log.debug("Completed message processing");
			return true;

		} catch (IncompleteReadException e) {

			// Log.debug("Incomplete message");
			conn.log("<< ROLLBACK >>");

			// input not complete, so rollback
			conn.input().position(conn.input().checkpoint());
			conn.input().limit(limit);
			BufUtil.startWriting(conn.input());

			state.n = stateN;
			state.obj = stateObj;

			boolean decreased = conn.readSeq.compareAndSet(seq, seq - 1);
			U.must(decreased, "Error in the request order control! Handle: %s", seq);

		} catch (ProtocolException e) {

			conn.log("<< PROTOCOL ERROR >>");
			Log.warn("Protocol error", "error", e);

			conn.output().deleteAfter(osize);
			conn.write(U.or(e.getMessage(), "Protocol error!"));
			conn.error();

			conn.processedSeq(seq);
			conn.close(true);

		} catch (Throwable e) {

			conn.log("<< ERROR >>");
			Log.error("Failed to process message!", e);

			conn.processedSeq(seq);
			conn.close(true);
		}

		return false;
	}

	private void processNextExtraSafe(RapidoidConnection conn) {
		if (Ctxs.hasContext()) {
			Log.warn("Detected unclosed context before processing message!");
			Ctxs.close();
		}

		try {
			conn.getProtocol().process(conn);
		} finally {
			if (Ctxs.hasContext()) {
				Log.warn("Detected unclosed context after processing message!");
				Ctxs.close();
			}
		}
	}

	public void close(RapidoidConnection conn) {
		close(conn.key);
	}

	private void close(SelectionKey key) {
		try {
			if (key != null) {

				Object attachment = key.attachment();

				clearKey(key);

				if (attachment instanceof RapidoidConnection) {
					RapidoidConnection conn = (RapidoidConnection) attachment;

					if (!conn.closed) {
						Log.trace("Closing connection", "connection", conn);
						assert conn.key == key;
						conn.reset();
						connections.release(conn);
					}
				}
			}
		} catch (IOException e) {
			Log.warn("Error while closing connection!", e);
		}
	}

	private void clearKey(SelectionKey key) throws IOException {
		if (key.isValid()) {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			socketChannel.close();
			key.attach(null);
			key.cancel();
		}
	}

	@Override
	protected void writeOP(SelectionKey key) throws IOException {
		RapidoidConnection conn = (RapidoidConnection) key.attachment();
		SocketChannel socketChannel = (SocketChannel) key.channel();

		checkOnSameThread();

		touch(conn);

		try {
			synchronized (conn) {
				synchronized (conn.outgoing) {

					if (conn.hasTLS) {
						synchronized (conn.output) {
							conn.tls.wrapToOutgoing();
						}
					}

					writeOp(key, conn, socketChannel);
				}
			}

		} catch (IOException e) {
			close(conn);

		} catch (CancelledKeyException cke) {
			Log.debug("Tried to write on canceled selector key!");
		}
	}

	private void writeOp(SelectionKey key, RapidoidConnection conn, SocketChannel socketChannel) throws IOException {

		synchronized (conn.outgoing) {
			if (conn.outgoing.hasRemaining()) {
				conn.log("WRITING");
//					conn.log(conn.outgoing.asText());
				BufUtil.startWriting(conn.outgoing);
				int wrote = conn.outgoing.writeTo(socketChannel);
				conn.outgoing.deleteBefore(wrote);
				BufUtil.doneWriting(conn.outgoing);
				conn.log("DONE WRITING");
			}
		}

		boolean finishedWriting, closeAfterWrite;
		synchronized (conn) {
			finishedWriting = conn.finishedWriting();
			closeAfterWrite = conn.closeAfterWrite();
		}

		if (finishedWriting && closeAfterWrite) {
			close(conn);

		} else {
			if (finishedWriting) {
				key.interestOps(SelectionKey.OP_READ);
			} else {
				key.interestOps(SelectionKey.OP_READ + SelectionKey.OP_WRITE);
			}
			conn.wrote(finishedWriting);
		}
	}

	public void wantToWrite(RapidoidConnection conn) {
		touch(conn);

		if (onSameThread()) {
			conn.key.interestOps(SelectionKey.OP_WRITE);
		} else {
			wantToWriteAsync(conn);
		}
	}

	private void wantToWriteAsync(RapidoidConnection conn) {
		touch(conn);

		synchronized (done) {
			done.add(conn);
		}

		selector.wakeup();
	}

	@Override
	protected void doProcessing() {

		SocketChannel schannel;

		while ((schannel = connected.poll()) != null) {

			try {
				configureSocket(schannel);
			} catch (IOException e) {
				Log.error("Cannot configure channel!", e);
				continue;
			}

			RapidoidChannel channel = new RapidoidChannel(schannel, false, serverProtocol);

			SocketChannel socketChannel = channel.socketChannel;
			Log.debug("connected", "address", socketChannel.socket().getRemoteSocketAddress());

			try {
				SelectionKey newKey = socketChannel.register(selector, SelectionKey.OP_READ);
				U.notNull(channel.protocol, "protocol");
				RapidoidConnection conn = attachConn(newKey, channel.protocol);

				conn.setClient(channel.isClient);

				try {
					processNext(conn, true);
				} finally {
					conn.setInitial(false);
				}

			} catch (ClosedChannelException e) {
				Log.warn("Closed channel", e);
			}
		}

		synchronized (done) {
			for (int i = 0; i < done.size(); i++) {
				RapidoidConnection conn = done.get(i);
				if (conn.key != null && conn.key.isValid()) {
					conn.key.interestOps(SelectionKey.OP_WRITE);
				}
			}
			done.clear();
		}
	}

	private RapidoidConnection attachConn(SelectionKey key, Protocol protocol) {
		U.notNull(key, "protocol");
		U.notNull(protocol, "protocol");

		assert key.attachment() == null;

		RapidoidConnection conn = connections.get();

		// the connection is reset when closed
		// but a protocol can modify the connection after closing it
		// so it is reset again before reuse
		conn.reset();

		U.must(conn.closed);
		conn.closed = false;

		conn.key = key;
		conn.setProtocol(protocol);

//		if (protocol instanceof CtxListener) {
//			conn.setListener((CtxListener) protocol);
//		}

		key.attach(conn);

		touch(conn);

		return conn;
	}

	private void touch(RapidoidConnection conn) {
		conn.setExpiresAt(approxTime + connTimeout);
	}

	@Override
	protected void failedOP(SelectionKey key, Throwable e) {
		Log.error("Network error", e);
		close(key);
	}

	public RapidoidConnection newConnection() {
		RapidoidConnection conn = new RapidoidConnection(RapidoidWorker.this, bufs);
		allConnections.add(conn);
		return conn;
	}

	public long getMessagesProcessed() {
		return messagesProcessed;
	}

	@Override
	protected synchronized void stopLoop() {
		super.stopLoop();

		done.clear();
		connected.clear();
		connections.clear();
		bufs.clear();
	}

	@Override
	public synchronized RapidoidWorker shutdown() {
		stopLoop();
		waitToStop();
		return this;
	}

	public SSLContext sslContext() {
		return sslContext;
	}
}
