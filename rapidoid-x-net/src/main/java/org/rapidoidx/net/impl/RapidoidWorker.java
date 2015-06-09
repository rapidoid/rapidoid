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
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.log.Log;
import org.rapidoid.pool.ArrayPool;
import org.rapidoid.pool.Pool;
import org.rapidoid.util.SimpleList;
import org.rapidoid.util.U;
import org.rapidoidx.buffer.BufGroup;
import org.rapidoidx.buffer.IncompleteReadException;
import org.rapidoidx.net.Protocol;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class RapidoidWorker extends AbstractEventLoop<RapidoidWorker> {

	private final Queue<RapidoidConnection> restarting;

	private final Queue<ConnectionTarget> connecting;

	private final Queue<RapidoidChannel> connected;

	private final SimpleList<RapidoidConnection> done;

	private final Pool<RapidoidConnection> connections;

	private final int maxPipelineSize;

	final Protocol serverProtocol;

	final RapidoidHelper helper;

	private final int bufSize;

	private final boolean noDelay;

	private final BufGroup bufs;

	private volatile long messagesProcessed;

	public RapidoidWorker(String name, final BufGroup bufs, final Protocol protocol, final RapidoidHelper helper,
			int bufSizeKB, boolean noNelay) {
		super(name);
		this.bufs = bufs;

		this.serverProtocol = protocol;
		this.helper = helper;
		this.maxPipelineSize = Conf.option("pipeline-max", Integer.MAX_VALUE);

		final int queueSize = Conf.micro() ? 1000 : 1000000;
		final int growFactor = Conf.micro() ? 2 : 10;

		this.restarting = new ArrayBlockingQueue<RapidoidConnection>(queueSize);
		this.connecting = new ArrayBlockingQueue<ConnectionTarget>(queueSize);
		this.connected = new ArrayBlockingQueue<RapidoidChannel>(queueSize);
		this.done = new SimpleList<RapidoidConnection>(queueSize / 10, growFactor);

		connections = new ArrayPool<RapidoidConnection>(new Callable<RapidoidConnection>() {
			@Override
			public RapidoidConnection call() throws Exception {
				return newConnection();
			}
		}, 100000);

		this.bufSize = bufSizeKB * 1024;
		this.noDelay = noNelay;
	}

	public void accept(SocketChannel socketChannel) throws IOException {

		configureSocket(socketChannel);

		connected.add(new RapidoidChannel(socketChannel, false, serverProtocol));
		selector.wakeup();
	}

	public void connect(ConnectionTarget target) throws IOException {

		configureSocket(target.socketChannel);

		connecting.add(target);

		if (target.socketChannel.connect(target.addr)) {
			Log.debug("Opened socket, connected", "address", target.addr);
		} else {
			Log.debug("Opened socket, connecting...", "address", target.addr);
		}

		selector.wakeup();
	}

	private void configureSocket(SocketChannel socketChannel) throws IOException, SocketException {
		socketChannel.configureBlocking(false);

		Socket socket = socketChannel.socket();
		socket.setTcpNoDelay(noDelay);
		socket.setReceiveBufferSize(bufSize);
		socket.setSendBufferSize(bufSize);
		socket.setReuseAddress(true);
	}

	@Override
	protected void connectOP(SelectionKey key) throws IOException {
		U.must(key.isConnectable());

		SocketChannel socketChannel = (SocketChannel) key.channel();
		if (!socketChannel.isConnectionPending()) {
			// not ready to retrieve the connection status
			return;
		}

		ConnectionTarget target = (ConnectionTarget) key.attachment();

		boolean ready;
		try {
			ready = socketChannel.finishConnect();
			U.rteIf(!ready, "Expected an established connection!");
			connected.add(new RapidoidChannel(socketChannel, true, target.protocol, target.holder,
					target.autoreconnecting, target.state));
		} catch (ConnectException e) {
			retryConnecting(target);
		}
	}

	private void retryConnecting(ConnectionTarget target) throws IOException {
		Log.warn("Reconnecting...", "address", target.addr);
		target.socketChannel = SocketChannel.open();
		target.retryAfter = U.time() + 1000;
		connect(target);
	}

	@Override
	protected void readOP(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		RapidoidConnection conn = (RapidoidConnection) key.attachment();

		int read;
		try {
			read = conn.input.append(socketChannel);
		} catch (Exception e) {
			read = -1;
		}

		if (read == -1) {
			// the other end closed the connection
			Log.debug("The other end closed the connection!");
			conn.closing = true;
		}

		process(conn);

		if (conn.closing) {
			if (conn.shouldReconnect()) {
				reconnect(conn);
			} else {
				close(key);
			}
		}
	}

	private void reconnect(RapidoidConnection conn) throws IOException {
		SelectionKey key = conn.key;
		InetSocketAddress addr = conn.getAddress();
		Protocol protocol = conn.getProtocol();
		ChannelHolderImpl holder = conn.getHolder();
		ConnState state = conn.state().copy();

		holder.closed();
		close(key);

		retryConnecting(new ConnectionTarget(null, addr, protocol, holder, true, state));
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

		return reqN;
	}

	private boolean processNext(RapidoidConnection conn, boolean initial) {

		conn.log(initial ? "<< INIT >>" : "<< PROCESS >>");

		U.must(initial || conn.input().hasRemaining());

		int pos = conn.input().position();
		int limit = conn.input().limit();
		int osize = conn.output().size();

		ConnState state = conn.state();
		long stateN = state.n;
		Object stateObj = state.obj;

		try {
			conn.done = false;

			if (Ctx.hasContext()) {
				Ctx.delExchange();
				Ctx.delUser();
			}

			conn.getProtocol().process(conn);

			if (Ctx.hasContext()) {
				Ctx.delExchange();
				Ctx.delUser();
			}

			if (!conn.closed && !conn.isAsync()) {
				conn.done();
			}

			Log.debug("Completed message processing");
			return true;

		} catch (IncompleteReadException e) {

			Log.debug("Incomplete message");
			conn.log("<< ROLLBACK >>");

			// input not complete, so rollback
			conn.input().position(pos);
			conn.input().limit(limit);

			conn.output().deleteAfter(osize);

			state.n = stateN;
			state.obj = stateObj;

		} catch (ProtocolException e) {

			conn.log("<< PROTOCOL ERROR >>");
			Log.warn("Protocol error", "error", e);
			conn.output().deleteAfter(osize);
			conn.write(U.or(e.getMessage(), "Protocol error!"));
			conn.error();
			conn.close(true);

		} catch (Throwable e) {

			conn.log("<< ERROR >>");
			Log.error("Failed to process message!", e);
			conn.close(true);
		}

		return false;
	}

	public void close(RapidoidConnection conn) {
		close(conn.key);
	}

	private void close(SelectionKey key) {
		try {
			Object attachment = key.attachment();

			clearKey(key);

			if (attachment instanceof RapidoidConnection) {
				RapidoidConnection conn = (RapidoidConnection) attachment;

				if (conn != null) {
					if (!conn.closed) {
						Log.trace("Closing connection", "connection", conn);
						assert conn.key == key;
						conn.reset();
						connections.release(conn);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
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

		try {
			int wrote = conn.output.writeTo(socketChannel);
			conn.output.deleteBefore(wrote);

			boolean complete = conn.output.size() == 0;

			if (conn.closeAfterWrite() && complete) {
				close(conn);
			} else {
				if (complete) {
					key.interestOps(SelectionKey.OP_READ);
				} else {
					key.interestOps(SelectionKey.OP_READ + SelectionKey.OP_WRITE);
				}
				conn.wrote(complete);
			}
		} catch (IOException e) {
			close(conn);
		}
	}

	public void wantToWrite(RapidoidConnection conn) {
		if (onSameThread()) {
			conn.key.interestOps(SelectionKey.OP_WRITE);
		} else {
			wantToWriteAsync(conn);
		}
	}

	private void wantToWriteAsync(RapidoidConnection conn) {
		synchronized (done) {
			done.add(conn);
		}

		selector.wakeup();
	}

	@Override
	protected void doProcessing() {

		long now = U.time();
		int connectingN = connecting.size();

		for (int i = 0; i < connectingN; i++) {
			ConnectionTarget target = connecting.poll();
			assert target != null;

			if (target.retryAfter < now) {
				Log.debug("connecting", "address", target.addr);

				try {
					SelectionKey newKey = target.socketChannel.register(selector, SelectionKey.OP_CONNECT);
					newKey.attach(target);
				} catch (ClosedChannelException e) {
					Log.warn("Closed channel", e);
				}
			} else {
				connecting.add(target);
			}
		}

		RapidoidChannel channel;

		while ((channel = connected.poll()) != null) {

			SocketChannel socketChannel = channel.socketChannel;
			Log.debug("connected", "address", socketChannel.socket().getRemoteSocketAddress());

			try {
				SelectionKey newKey = socketChannel.register(selector, SelectionKey.OP_READ);
				U.notNull(channel.protocol, "protocol");
				RapidoidConnection conn = attachConn(newKey, channel.protocol);

				conn.setClient(channel.isClient);
				conn.setAutoReconnect(channel.autoreconnecting);

				bindChannelToHolder(conn, channel.holder);

				if (channel.state != null) {
					conn.state().copyFrom(channel.state);
				}

				try {
					processNext(conn, true);
				} finally {
					conn.setInitial(false);
				}

			} catch (ClosedChannelException e) {
				Log.warn("Closed channel", e);
			}
		}

		RapidoidConnection restartedConn;
		while ((restartedConn = restarting.poll()) != null) {
			Log.debug("restarting", "connection", restartedConn);

			processNext(restartedConn, true);
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

	private void bindChannelToHolder(RapidoidConnection conn, ChannelHolderImpl holder) {
		conn.setHolder(holder);
		if (holder != null) {
			holder.setChannel(conn);
		}
	}

	private RapidoidConnection attachConn(SelectionKey key, Protocol protocol) {
		U.notNull(key, "protocol");
		U.notNull(protocol, "protocol");

		Object attachment = key.attachment();
		assert attachment == null || attachment instanceof ConnectionTarget;

		RapidoidConnection conn = connections.get();

		// the connection is reset when closed
		// but a protocol can modify the connection after closing it
		// so it is reset again before reuse
		conn.reset();

		U.must(conn.closed);
		conn.closed = false;

		conn.key = key;
		conn.setProtocol(protocol);

		if (protocol instanceof CtxListener) {
			conn.setListener((CtxListener) protocol);
		}

		key.attach(conn);

		return conn;
	}

	@Override
	protected void failedOP(SelectionKey key, Throwable e) {
		Log.error("Network error", e);
		close(key);
	}

	public void restart(RapidoidConnection conn) {
		restarting.add(conn);
	}

	public RapidoidConnection newConnection() {
		return new RapidoidConnection(RapidoidWorker.this, bufs);
	}

	public long getMessagesProcessed() {
		return messagesProcessed;
	}

}
