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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import org.rapidoid.Protocol;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.buffer.IncompleteReadException;
import org.rapidoid.config.ServerConfig;
import org.rapidoid.pool.ArrayPool;
import org.rapidoid.pool.Pool;
import org.rapidoid.util.SimpleList;
import org.rapidoid.util.U;

public class RapidoidWorker extends AbstractEventLoop {

	private static final int MAX_PIPELINED = Integer.MAX_VALUE;

	private final Queue<SocketChannel> accepted = new ArrayBlockingQueue<SocketChannel>(1000000);

	private final SimpleList<RapidoidConnection> done = new SimpleList<RapidoidConnection>(100000, 10);

	private final Pool<RapidoidConnection> connections;

	final Protocol protocol;

	final RapidoidHelper helper;

	private final boolean isProtocolListener;

	public RapidoidWorker(String name, final BufGroup bufs, final ServerConfig config, final Protocol protocol,
			final RapidoidHelper helper) {
		super(name, config);

		this.protocol = protocol;
		this.helper = helper;

		this.isProtocolListener = protocol instanceof ConnectionListener;

		connections = new ArrayPool<RapidoidConnection>(new Callable<RapidoidConnection>() {
			@Override
			public RapidoidConnection call() throws Exception {
				return new RapidoidConnection(RapidoidWorker.this, bufs);
			}
		}, 100000);
	}

	public void register(SocketChannel socketChannel) {
		accepted.add(socketChannel);
		selector.wakeup();
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
			close(key);
			return;
		}

		processMsgs(conn);

		conn.completedInputPos = conn.input.position();
	}

	private int processMsgs(RapidoidConnection conn) {
		int reqN = 0;

		while (reqN < MAX_PIPELINED && conn.input().hasRemaining() && processNext(conn)) {
			reqN++;
		}

		return reqN;
	}

	private boolean processNext(RapidoidConnection conn) {
		int pos = conn.input().position();
		int limit = conn.input().limit();

		try {
			protocol.process(conn);
			return true;
		} catch (IncompleteReadException e) {
			// input not ready, so rollback
			conn.input().position(pos);
			conn.input().limit(limit);
			// FIXME rollback output position
		} catch (ProtocolException e) {
			conn.write(U.or(e.getMessage(), "Protocol error!"));
			conn.error();
			conn.close(true);
		} catch (Throwable e) {
			U.error("Failed to process message!", e);
			conn.close(true);
		}

		return false;
	}

	public void close(RapidoidConnection conn) {
		close(conn.key);
	}

	private void close(SelectionKey key) {
		try {
			RapidoidConnection conn = (RapidoidConnection) key.attachment();

			if (key.isValid()) {
				SocketChannel socketChannel = (SocketChannel) key.channel();
				socketChannel.close();
				key.attach(null);
				key.cancel();
			}

			if (conn != null) {
				if (!conn.closed) {
					U.trace("Closing connection", "connection", conn);
					conn.closed = true;
					assert conn.key == key;
					conn.key = null;
					connections.release(conn);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
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

		SocketChannel socketChannel;
		while ((socketChannel = accepted.poll()) != null) {
			U.debug("incoming connection", "address", socketChannel.socket().getRemoteSocketAddress());

			try {
				SelectionKey newKey = socketChannel.register(selector, SelectionKey.OP_READ);
				RapidoidConnection conn = connections.get();
				conn.key = newKey;

				if (isProtocolListener) {
					conn.setListener((ConnectionListener) protocol);
				}

				newKey.attach(conn);

			} catch (ClosedChannelException e) {
				U.warn("Closed channel", e);
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

	public void closeAll() {
		// FIXME implement this
	}

}
