package org.rapidoid.net.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.buffer.BufUtil;
import org.rapidoid.data.JSON;
import org.rapidoid.expire.Expiring;
import org.rapidoid.job.Jobs;
import org.rapidoid.log.Log;
import org.rapidoid.net.AsyncLogic;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.abstracts.IRequest;
import org.rapidoid.net.tls.RapidoidTLS;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Resetable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicLong;

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
public class RapidoidConnection extends RapidoidThing implements Resetable, Channel, Expiring, Constants {

	private static final CtxListener IGNORE = new IgnorantConnectionListener();

	private static final AtomicLong ID_N = new AtomicLong();

	private static final AtomicLong SERIAL_N = new AtomicLong();

	final boolean hasTLS;

	final RapidoidTLS tls;

	final RapidoidWorker worker;

	public final Buf input;

	public final Buf output;

	public final Buf outgoing;

	private final ConnState state = new ConnState();

	private volatile boolean waitingToWrite = false;

	public volatile SelectionKey key;

	private volatile boolean closeAfterWrite = false;

	volatile boolean closed = true;

	volatile boolean closing = false;

	volatile int completedInputPos;

	private volatile CtxListener listener;

	private final long serialN = SERIAL_N.incrementAndGet();

	private volatile long id;

	private volatile boolean initial;

	volatile boolean async;

	volatile boolean done;

	private volatile boolean isClient;

	private volatile Protocol protocol;

	volatile long requestId;

	final AtomicLong readSeq = new AtomicLong();

	final AtomicLong writeSeq = new AtomicLong();

	volatile boolean resumeInProgress = false;

	volatile IRequest request;

	private volatile long expiresAt;

	public RapidoidConnection(RapidoidWorker worker, BufGroup bufs) {
		this.worker = worker;

		this.hasTLS = worker.sslContext() != null;
		this.tls = hasTLS ? new RapidoidTLS(worker.sslContext(), this) : null;

		this.input = bufs.newBuf("input#" + serialN);
		this.output = bufs.newBuf("output#" + serialN);
		this.outgoing = hasTLS ? bufs.newBuf("outgoing#" + serialN) : this.output;

		reset();
	}

	@Override
	public synchronized void reset() {
		IRequest req = request;
		if (req != null) {
			req.stop();
			request = null;
		}

		id = ID_N.incrementAndGet();
		key = null;
		closed = true;
		closing = false;
		input.clear();
		output.clear();
		outgoing.clear();
		closeAfterWrite = false;
		waitingToWrite = false;
		completedInputPos = 0;
		listener = IGNORE;
		initial = true;
		async = false;
		done = false;
		isClient = false;
		protocol = null;
		requestId = 0;
		readSeq.set(0);
		writeSeq.set(0);
		expiresAt = 0;
		state.reset();

		if (tls != null) tls.reset();
	}

	@Override
	public void log(String msg) {
		state().log(msg);
	}

	@Override
	public synchronized InetSocketAddress getAddress() {
		if (key == null) return null;

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
	public Channel writeJSON(Object value) {
		JSON.stringify(value, output.asOutputStream());
		return this;
	}

	public boolean closeAfterWrite() {
		return closeAfterWrite;
	}

	Channel done() {
		async = false;

		if (!done) {
			done = true;
			askToSend();
		}

		return this;
	}

	void processedSeq(long processedHandle) {

		if (processedHandle == 0) return; // a new connection

		U.must(processedHandle > 0);

		boolean increased = writeSeq.compareAndSet(processedHandle - 1, processedHandle);

		if (!increased) {
			// the current response might be already marked as processed (e.g. in non-async handlers)
			long writeSeqN = writeSeq.get();
			if (writeSeqN != processedHandle) {
				throw U.rte("Error in the response order control! Expected handle: %s, real: %s", processedHandle - 1, writeSeqN);
			}
		}
	}

	@Override
	public Channel send() {
		askToSend();
		return this;
	}

	public void error() {
		askToSend();
	}

	private synchronized void askToSend() {
		synchronized (outgoing) {
			if (hasTLS) {
				synchronized (output) {
					tls.wrapToOutgoing();
				}
			}

			if (!waitingToWrite && outgoing.size() > 0) {
				waitingToWrite = true;
				worker.wantToWrite(this);
			}
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

	synchronized void wrote(boolean complete) {
		if (complete) {
			waitingToWrite = false;
		}

		input.deleteBefore(completedInputPos);
		completedInputPos = 0;
	}

	@Override
	public void resume(final long expectedConnId, final long handle, final AsyncLogic asyncLogic) {

		if (expectedConnId != connId()) return;

		long seq = writeSeq.get();

		if (seq < handle - 1) {
			// too early

			Jobs.execute(new Runnable() {
				@Override
				public void run() {
					resume(expectedConnId, handle, asyncLogic);
				}
			});

		} else if (seq == handle - 1) {

			synchronized (this) {

				if (expectedConnId != connId()) {
					return;
				}

//				TODO investigate options for stricter flow control:
//				U.must(!resumeInProgress, "Resume is already in progress!");

				resumeInProgress = true;

				try {
					doResume(handle, asyncLogic, seq);

				} finally {
					resumeInProgress = false;
				}
			}

		} else {
			Log.error("Tried to resume a job that already has finished!", "handle", handle, "currentHandle", seq, "job", asyncLogic);
			throw U.rte("Tried to resume a job that already has finished!");
		}
	}

	private void doResume(long handle, AsyncLogic asyncLogic, long seq) {
		U.must(seq == writeSeq.get());

		// execute the logic
		boolean finished = false;

		synchronized (output) {
			BufUtil.startWriting(output);

			try {
				finished = asyncLogic.resumeAsync();
			} catch (Throwable e) {
				Log.error("Error while resuming an asynchronous operation!", e);
			}

			BufUtil.doneWriting(output);
		}

		if (finished) {
			processedSeq(handle);
		}
	}

	@Override
	public Buf input() {
		return input;
	}

	@Override
	public Buf output() {
		return output;
	}

	@Override
	public OutputStream outputStream() {
		return output.asOutputStream();
	}

	@Override
	public boolean onSameThread() {
		return worker.onSameThread();
	}

	@Override
	public RapidoidHelper helper() {
		return worker.helper;
	}

	public CtxListener listener() {
		return listener;
	}

	public void setListener(CtxListener listener) {
		this.listener = listener;
	}

	@Override
	public String address() {
		InetSocketAddress inetSocketAddress = getAddress();
		return inetSocketAddress != null ? inetSocketAddress.getAddress().getHostAddress() : null;
	}

	@Override
	public Channel close() {
		close(true);
		return this;
	}

	@Override
	public Channel closeIf(boolean condition) {
		if (condition) {
			close();
		}
		return this;
	}

	@Override
	public String readln() {
		return input().readLn();
	}

	@Override
	public String readN(int count) {
		return input().readN(count);
	}

	@Override
	public ConnState state() {
		return state;
	}

	@Override
	public long handle() {
		return readSeq.get();
	}

	@Override
	public boolean isInitial() {
		return initial;
	}

	@Override
	public String toString() {
		return "conn#" + connId();
	}

	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	@Override
	public synchronized long async() {
		U.must(onSameThread(), "The connection can be marked as 'async' only on its I/O worker thread!");

		this.async = true;
		this.done = false;
		return handle();
	}

	@Override
	public synchronized boolean isAsync() {
		return async;
	}

	public boolean isClient() {
		return isClient;
	}

	public void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	@Override
	public boolean isClosing() {
		return closing;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void waitUntilClosing() {
		if (!isClosing()) {
			throw Buf.INCOMPLETE_READ;
		}
	}

	@Override
	public long connId() {
		return id;
	}

	@Override
	public long requestId() {
		return requestId;
	}

	@Override
	public void setRequest(IRequest request) {
		this.request = request;
	}

	@Override
	public void setExpiresAt(long expiresAt) {
		this.expiresAt = expiresAt;
	}

	@Override
	public long getExpiresAt() {
		return expiresAt;
	}

	@Override
	public void expire() {
		close(false);
	}

	public boolean finishedWriting() {
		return outgoing.size() == 0;
	}

}
