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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.rapidoid.Connection;
import org.rapidoid.Ctx;
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.util.Resetable;
import org.rapidoid.util.U;

public class RapidoidConnection implements Connection, Resetable, Ctx {

	private static final ConnectionListener IGNORE = new IgnorantConnectionListener();

	public final RapidoidWorker worker;

	public final Buf input;

	public final Buf output;

	private boolean waitingToWrite = false;

	public SelectionKey key;

	private boolean closeAfterWrite = false;

	public boolean closed = false;

	int completedInputPos;

	private ConnectionListener listener;

	private long id;

	public RapidoidConnection(RapidoidWorker worker, BufGroup bufs) {
		this.worker = worker;
		this.input = bufs.newBuf("input");
		this.output = bufs.newBuf("output");
		reset();
	}

	@Override
	public synchronized void reset() {
		key = null;
		closed = false;
		input.clear();
		output.clear();
		closeAfterWrite = false;
		waitingToWrite = false;
		completedInputPos = 0;
		listener = IGNORE;
	}

	@Override
	public synchronized InetSocketAddress getAddress() {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		SocketAddress addr = socketChannel.socket().getRemoteSocketAddress();
		if (addr instanceof InetSocketAddress) {
			InetSocketAddress address = (InetSocketAddress) addr;
			return address;
		} else {
			throw new IllegalStateException("Cannot get remote address!");
		}
	}

	@Override
	public synchronized int write(String s, Object tag, int kind) {
		beforeWriting(tag, kind);
		int wrote = output.append(s);
		return wrote;
	}

	@Override
	public synchronized int write(byte[] bytes, Object tag, int kind) {
		return write(bytes, 0, bytes.length, tag, kind);
	}

	@Override
	public synchronized int write(byte[] bytes, int offset, int length, Object tag, int kind) {
		beforeWriting(tag, kind);
		output.append(bytes, offset, length);
		return length;
	}

	@Override
	public synchronized int write(ByteBuffer buf, Object tag, int kind) {
		beforeWriting(tag, kind);
		int count = buf.remaining();
		output.append(buf);
		return count;
	}

	@Override
	public synchronized int writeTo(long connId, String s, Object tag, int kind) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int writeTo(long connId, byte[] bytes, Object tag, int kind) {
		return writeTo(connId, bytes, 0, bytes.length, tag, kind);
	}

	@Override
	public synchronized int writeTo(long connId, byte[] bytes, int offset, int length, Object tag, int kind) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized int writeTo(long connId, ByteBuffer buf, Object tag, int kind) {
		// TODO Auto-generated method stub
		return 0;
	}

	public synchronized boolean closeAfterWrite() {
		return closeAfterWrite;
	}

	@Override
	public synchronized void complete(Object tag, boolean close) {
		listener().onComplete(this, tag);

		askToSend();

		listener().onDone(this, tag);

		if (close) {
			close(true);
		}
	}

	@Override
	public synchronized void error() {
		askToSend();
	}

	private void askToSend() {
		if (!waitingToWrite) {
			waitingToWrite = true;
			worker.wantToWrite(this);
		}
	}

	@Override
	public synchronized void close(boolean waitToWrite) {
		if (waitToWrite && waitingToWrite) {
			closeAfterWrite = true;
		} else {
			worker.close(this);
		}
	}

	public synchronized void wrote(boolean complete) {
		if (complete) {
			waitingToWrite = false;
		}

		input.deleteBefore(completedInputPos);
		completedInputPos = 0;
	}

	@Override
	public synchronized Buf input() {
		return input;
	}

	@Override
	public synchronized Buf output() {
		return output;
	}

	private void beforeWriting(Object tag, int kind) {
		listener().beforeWriting(this, tag, kind);
	}

	@Override
	public synchronized boolean onSameThread() {
		return worker.onSameThread();
	}

	@Override
	public synchronized RapidoidHelper helper() {
		return worker.helper;
	}

	@Override
	public synchronized ConnectionListener listener() {
		return listener;
	}

	@Override
	public synchronized void setListener(ConnectionListener listener) {
		this.listener = listener;
	}

	@Override
	public synchronized String address() {
		return getAddress().getAddress().getHostAddress();
	}

	@Override
	public synchronized void close() {
		close(true);
	}

	@Override
	public synchronized String readln() {
		return input().readLn();
	}

	@Override
	public synchronized String readN(int count) {
		return input().readN(count);
	}

	@Override
	public synchronized long connId() {
		return id;
	}

	@Override
	public synchronized void fail(String msg) {
		throw U.rte(ProtocolException.class, msg);
	}

	@Override
	public synchronized void failIf(boolean condition, String msg) {
		if (condition) {
			throw U.rte(ProtocolException.class, msg);
		}
	}

	@Override
	public synchronized void ensure(boolean expectedCondition, String msg) {
		if (!expectedCondition) {
			throw U.rte(ProtocolException.class, msg);
		}
	}

	@Override
	public int write(String s) {
		return write(s, null, 0);
	}

	@Override
	public int write(byte[] bytes) {
		return write(bytes, null, 0);
	}

	@Override
	public int write(byte[] bytes, int offset, int length) {
		return write(bytes, offset, length, null, 0);
	}

	@Override
	public int write(ByteBuffer buf) {
		return write(buf, null, 0);
	}

	@Override
	public int writeTo(long connId, String s) {
		return writeTo(connId, s, null, 0);
	}

	@Override
	public int writeTo(long connId, byte[] bytes) {
		return writeTo(connId, bytes, null, 0);
	}

	@Override
	public int writeTo(long connId, byte[] bytes, int offset, int length) {
		return writeTo(connId, bytes, offset, length, null, 0);
	}

	@Override
	public int writeTo(long connId, ByteBuffer buf) {
		return writeTo(connId, buf, null, 0);
	}

	@Override
	public void complete(boolean close) {
		complete(null, close);
	}

	@Override
	public Connection connection() {
		return this;
	}

}
