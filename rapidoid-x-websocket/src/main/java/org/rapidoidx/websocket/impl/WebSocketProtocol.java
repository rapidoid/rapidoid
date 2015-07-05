package org.rapidoidx.websocket.impl;

/*
 * #%L
 * rapidoid-x-websocket
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.util.U;
import org.rapidoidx.websocket.WSExchange;
import org.rapidoidx.websocket.WSHandler;

/*

 http://tools.ietf.org/html/rfc6455

 5.2. Base Framing Protocol

 0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 +-+-+-+-+-------+-+-------------+-------------------------------+
 |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
 |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
 |N|V|V|V|       |S|             |   (if payload len==126/127)   |
 | |1|2|3|       |K|             |                               |
 +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
 |     Extended payload length continued, if payload len == 127  |
 + - - - - - - - - - - - - - - - +-------------------------------+
 |                               | Masking-key, if MASK set to 1 |
 +-------------------------------+-------------------------------+
 | Masking-key (continued)       |          Payload Data         |
 +-------------------------------- - - - - - - - - - - - - - - - +
 :                     Payload Data continued ...                :
 + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
 |                     Payload Data continued ...                |
 +---------------------------------------------------------------+
 */

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class WebSocketProtocol implements Protocol {

	private static final byte[] NO_DATA = new byte[0];

	// OPCODES (http://tools.ietf.org/html/rfc6455#section-11.8)
	private static final byte OP_CONTINUATION = 0;
	private static final byte OP_TEXT = 1;
	private static final byte OP_BINARY = 2;
	private static final byte OP_CLOSE = 8;
	private static final byte OP_PING = 9;
	private static final byte OP_PONG = 10;

	private final WSHandler handler;

	public WebSocketProtocol(WSHandler handler) {
		this.handler = handler;
	}

	public void process(Channel ctx) {
		if (ctx.isInitial()) {
			Log.debug("WebSocket connection established");
			return;
		}

		byte[] msg = readMsg(ctx);
		if (msg == null) {
			return;
		}

		WSExchange x = new WSExchangeImpl(ctx, msg);

		Object result;
		try {
			result = handler.handle(x);
		} catch (Exception e) {
			throw U.rte("WebSocket message processing error!", e);
		}

		if (result != null) {
			if (result instanceof byte[]) {
				x.send((byte[]) result);
			} else if (result instanceof String) {
				x.send((String) result);
			}
		}
	}

	protected static byte[] readMsg(Channel ctx) {

		byte[] head = ctx.input().readNbytes(2);
		byte head0 = head[0];
		byte head1 = head[1];

		byte opcode = (byte) (0xF & head0); // bits 4 - 7

		U.must(1 == (0xFF & head0) >> 7, "FIN bit wasn't set!");

		switch (opcode) {
		case OP_CONTINUATION:
			throw U.rte("The CONTINUATION frame type is currently not supported!");

		case OP_TEXT:
			return readTextFrame(ctx, head1);

		case OP_BINARY:
			throw U.rte("The BINARY frame type is currently not supported!");

		case OP_CLOSE:
			ctx.close();
			return null;

		case OP_PING:
			throw U.rte("The PING frame type is currently not supported!");

		case OP_PONG:
			throw U.rte("The PONG frame type is currently not supported!");

		default:
			throw U.notExpected();
		}
	}

	protected static byte[] readTextFrame(Channel ctx, byte head1) {
		U.must(1 == (0xFF & head1) >> 7, "Data must be masked!");

		int datalen = (127 & head1);
		U.must(datalen < 126);

		if (datalen > 0) {
			byte[] maskKey = ctx.input().readNbytes(4);
			byte[] data = ctx.input().readNbytes(datalen);

			maskUnmask(maskKey, data);

			return data;
		} else {
			return NO_DATA;
		}
	}

	protected static void maskUnmask(byte[] maskKey, byte[] data) {
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (data[i] ^ maskKey[i % 4]);
		}
	}

	protected static void writeMsg(Channel ctx, byte[] msg) {

		U.must(msg.length < 126);

		// FIN bit set and text frame opcode
		ctx.output().append((byte) 0x81); // 10000001

		// set length, no mask
		ctx.output().append((byte) msg.length);

		// append data
		ctx.output().append(msg);
	}

}
