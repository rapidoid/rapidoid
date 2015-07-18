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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicLong;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.json.JSON;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Resetable;
import org.rapidoid.util.U;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.buffer.BufGroup;
import org.rapidoidx.net.Protocol;
import org.rapidoidx.net.abstracts.Channel;
import org.rapidoidx.net.abstracts.ChannelHolder;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class RapidoidConnection implements Resetable, Channel, Constants {

	private static final CtxListener IGNORE = new IgnorantConnectionListener();

	private static final AtomicLong ID_N = new AtomicLong();

	public final RapidoidWorker worker;

	public final Buf input;

	public final Buf output;

	private final ConnState state = new ConnState();

	private boolean waitingToWrite = false;

	public volatile SelectionKey key;

	private boolean closeAfterWrite = false;

	public volatile boolean closed = true;

	public volatile boolean closing = false;

	volatile long completedInputPos;

	private CtxListener listener;

	private long id = ID_N.incrementAndGet();

	private boolean initial;

	private boolean async;

	volatile boolean done;

	private boolean isClient;

	private boolean autoReconnect;

	private Protocol protocol;

	private ChannelHolderImpl holder;

	public RapidoidConnection(RapidoidWorker worker, BufGroup bufs) {
		this.worker = worker;
		this.input = bufs.newBuf("input#" + connId());
		this.output = bufs.newBuf("output#" + connId());
		reset();
	}

	@Override
	public synchronized void reset() {
		key = null;
		closed = true;
		closing = false;
		input.clear();
		output.clear();
		closeAfterWrite = false;
		waitingToWrite = false;
		completedInputPos = 0;
		listener = IGNORE;
		initial = true;
		async = false;
		done = false;
		isClient = false;
		autoReconnect = false;
		protocol = null;
		holder = null;
		state.reset();
	}

	public void log(String msg) {
		state().log(msg);
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
	public synchronized Channel write(String s) {
		output.append(s);
		return this;
	}

	@Override
	public synchronized Channel writeln(String s) {
		output.append(s);
		output.append(CR_LF);
		return this;
	}

	@Override
	public synchronized Channel write(byte[] bytes) {
		return write(bytes, 0, bytes.length);
	}

	@Override
	public synchronized Channel write(byte[] bytes, int offset, int length) {
		output.append(bytes, offset, length);
		return this;
	}

	@Override
	public synchronized Channel write(ByteBuffer buf) {
		output.append(buf);
		return this;
	}

	@Override
	public synchronized Channel write(File file) {
		try {
			FileInputStream stream = new FileInputStream(file);
			FileChannel fileChannel = stream.getChannel();
			output.append(fileChannel);
			stream.close();
		} catch (IOException e) {
			throw U.rte(e);
		}

		return this;
	}

	@Override
	public synchronized Channel writeJSON(Object value) {
		JSON.stringify(value, output.asOutputStream());
		return this;
	}

	public synchronized boolean closeAfterWrite() {
		return closeAfterWrite;
	}

	@Override
	public synchronized Channel done() {
		done(null);
		return this;
	}

	public synchronized void done(Object tag) {
		async = false;
		// TODO done might be obsolete, is async enough?
		if (!done) {
			done = true;
			askToSend();
			if (tag != null) {
				listener().onDone(this, tag);
			}
		}
	}

	@Override
	public synchronized Channel send() {
		askToSend();
		return this;
	}

	public synchronized void error() {
		askToSend();
	}

	private void askToSend() {
		if (!waitingToWrite && output.size() > 0) {
			waitingToWrite = true;
			worker.wantToWrite(this);
		}
	}

	public synchronized void close(boolean waitToWrite) {
		if (waitToWrite) {
			done();
		}

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

	public synchronized boolean onSameThread() {
		return worker.onSameThread();
	}

	@Override
	public synchronized RapidoidHelper helper() {
		return worker.helper;
	}

	public synchronized CtxListener listener() {
		return listener;
	}

	public synchronized void setListener(CtxListener listener) {
		this.listener = listener;
	}

	@Override
	public synchronized String address() {
		return getAddress().getAddress().getHostAddress();
	}

	@Override
	public synchronized Channel close() {
		close(true);
		return this;
	}

	@Override
	public synchronized Channel closeIf(boolean condition) {
		if (condition) {
			close();
		}
		return this;
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

	public synchronized ConnState state() {
		return state;
	}

	@Override
	public synchronized boolean isInitial() {
		return initial;
	}

	@Override
	public synchronized String toString() {
		return "conn#" + connId();
	}

	public synchronized void setInitial(boolean initial) {
		this.initial = initial;
	}

	@Override
	public synchronized Channel restart() {
		worker.restart(this);
		return this;
	}

	@Override
	public synchronized Channel async() {
		this.async = true;
		this.done = false;
		return this;
	}

	@Override
	public synchronized boolean isAsync() {
		return async;
	}

	public synchronized boolean isClient() {
		return isClient;
	}

	public synchronized void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	public synchronized void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public synchronized Protocol getProtocol() {
		return protocol;
	}

	@Override
	public synchronized boolean isClosing() {
		return closing;
	}

	@Override
	public synchronized boolean isClosed() {
		return closed;
	}

	@Override
	public void waitUntilClosing() {
		if (!isClosing()) {
			throw Buf.INCOMPLETE_READ;
		}
	}

	public synchronized ChannelHolderImpl getHolder() {
		return holder;
	}

	public synchronized void setHolder(ChannelHolderImpl holder) {
		this.holder = holder;
	}

	public synchronized boolean shouldReconnect() {
		return isClient && autoReconnect;
	}

	public synchronized void setAutoReconnect(boolean autoReconnect) {
		this.autoReconnect = autoReconnect;
	}

	@Override
	public ChannelHolder createHolder() {
		return new ChannelHolderImpl(this);
	}

}
