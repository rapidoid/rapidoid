package org.rapidoid.net.tls;

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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.BufUtil;
import org.rapidoid.commons.Err;
import org.rapidoid.log.Log;
import org.rapidoid.net.impl.RapidoidConnection;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;

import javax.net.ssl.*;
import java.nio.ByteBuffer;

@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class RapidoidTLS extends RapidoidThing {

	private static boolean debugging = false;

	private final SSLContext sslContext;
	private final RapidoidConnection conn;

	private volatile SSLEngine engine;

	private final ByteBuffer appIn;
	public final ByteBuffer netIn;
	final ByteBuffer netOut;

	public RapidoidTLS(SSLContext sslContext, RapidoidConnection conn) {

		this.sslContext = sslContext;
		this.conn = conn;
		this.engine = createServerEngine();

		SSLSession session = engine.getSession();

		int appBufferMax = session.getApplicationBufferSize();
		int netBufferMax = session.getPacketBufferSize();

		appIn = ByteBuffer.allocateDirect(appBufferMax + 64);
		netIn = ByteBuffer.allocateDirect(netBufferMax);
		netOut = ByteBuffer.allocateDirect(netBufferMax);
	}

	private SSLEngine createServerEngine() {
		SSLEngine engine = sslContext.createSSLEngine();
		engine.setUseClientMode(false);
		return engine;
	}

	private void reactToHandshakeStatus(SSLEngineResult.HandshakeStatus status) {
		debug("HANDSHAKE STATUS = " + status);

		switch (status) {
			case FINISHED:
				break;

			case NEED_TASK:
				status = executeTasks();
				reactToHandshakeStatus(status);
				break;

			case NEED_UNWRAP:
				unwrapInput();
				break;

			case NEED_WRAP:
				wrapOutput();
				break;

			case NOT_HANDSHAKING:
				break;

			default:
				throw Err.notExpected();
		}
	}

	private SSLEngineResult.HandshakeStatus executeTasks() {
		Runnable runnable;
		while ((runnable = engine.getDelegatedTask()) != null) {
			runnable.run();
		}

		SSLEngineResult.HandshakeStatus hsStatus = engine.getHandshakeStatus();
		U.must(hsStatus != SSLEngineResult.HandshakeStatus.NEED_TASK, "handshake shouldn't need additional tasks!");

		debug("after tasks: " + hsStatus);

		return hsStatus;
	}

	public boolean isClosed() {
		return (engine.isOutboundDone() && engine.isInboundDone());
	}

	public SSLSession getSession() {
		return engine.getSession();
	}

	private void debug(String msg, SSLEngineResult result) {
		if (debugging) {
			SSLEngineResult.HandshakeStatus handshakeStatus = result.getHandshakeStatus();

			debug(U.frmt("%s (status = %s:%s, consumed=%s, produced=%s)",
				msg, result.getStatus(), handshakeStatus, result.bytesConsumed(), result.bytesProduced()));

			if (handshakeStatus.equals(SSLEngineResult.HandshakeStatus.FINISHED)) {
				debug("\n<<< HANDSHAKE FINISHED >>>\n");
			}
		}
	}

	private void debug(String msg) {
		if (debugging) {
			U.print(conn + " :: " + msg);
		}
	}

	public synchronized boolean unwrapInput() {
		boolean success = false;
		boolean shouldUnwrap = true;

		while (!isClosed() && shouldUnwrap && netIn.position() > 0) {
			debug("- UNWRAP");

			netIn.flip(); // prepare for reading
			netIn.mark(); // backup, to rollback in case of underflow

			SSLEngineResult result = unwrap(netIn, appIn);

			boolean underflow = result.getStatus().equals(SSLEngineResult.Status.BUFFER_UNDERFLOW);

			if (!underflow) {
				// prepare for writing
				netIn.compact();
			} else {
				// rollback
				netIn.reset();
				netIn.compact();
			}

			appIn.flip();
			conn.input.append(appIn);
			appIn.clear();

			reactToResult(result);

			shouldUnwrap = !result.getStatus().equals(SSLEngineResult.Status.BUFFER_UNDERFLOW)
				&& !result.getHandshakeStatus().equals(SSLEngineResult.HandshakeStatus.NEED_TASK)
				&& !result.getHandshakeStatus().equals(SSLEngineResult.HandshakeStatus.NEED_WRAP);

			success = true;
		}

		return success;
	}

	public synchronized boolean wrapToOutgoing() {
		boolean success = false;

		if (conn.output.hasRemaining()) {
			debug("- WRAP TO OUTGOING " + conn);

			BufUtil.startWriting(conn.output);
			BufUtil.startWriting(conn.outgoing);

			int bytesConsumed = conn.output.sslWrap(engine, conn.outgoing);
			success = bytesConsumed > 0;

			BufUtil.doneWriting(conn.outgoing);
			BufUtil.doneWriting(conn.output);
		}

		return success;
	}

	private synchronized void wrapOutput() {
		if (!isClosed()) {
			debug("- WRAP");
			SSLEngineResult result;

			try {
				result = engine.wrap(new ByteBuffer[]{}, 0, 0, netOut);
			} catch (SSLException e) {
				throw U.rte(e);
			}

			debug("wrap: ", result);

			debug("OUT " + netOut);

			netOut.flip();

			synchronized (conn.outgoing) {
				conn.outgoing.append(netOut);
			}

			netOut.compact();

			reactToResult(result);
		}
	}

	private void reactToResult(SSLEngineResult result) {
		switch (result.getStatus()) {
			case BUFFER_OVERFLOW:
				debug("@@@ BUFFER_OVERFLOW " + conn);
				// destination is out of capacity
				break;
			case BUFFER_UNDERFLOW:
				debug("@@@ BUFFER_UNDERFLOW " + conn);
				// source doesn't have enough data
				break;
			case CLOSED:
				debug("@@@ CLOSED " + conn);
				conn.closeAfterWrite();
				break;
			case OK:
				debug("@@@ OK " + conn);
				reactToHandshakeStatus(result.getHandshakeStatus());
				break;
			default:
				throw Err.notExpected();
		}
	}

	public synchronized SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst) {
		try {
			SSLEngineResult result = engine.wrap(src, dst);

			debug("wrap: ", result);

			return result;

		} catch (SSLException e) {
			throw U.rte(e);
		}
	}

	public synchronized SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst) {
		try {
			SSLEngineResult result = engine.unwrap(src, dst);

			debug("unwrap: ", result);

			return result;

		} catch (SSLException e) {
			throw U.rte(e);
		}
	}

	public SSLEngine engine() {
		return engine;
	}

	public synchronized void closeInbound() {
		try {
			engine.closeInbound();
		} catch (SSLException e) {
			Log.warn("SSL error while closing connection", "message", Msc.errorMsg(e));
		}
	}

	public void reset() {
		this.appIn.clear();
		this.netIn.clear();
		this.netOut.clear();
		this.engine = createServerEngine();
	}

}
