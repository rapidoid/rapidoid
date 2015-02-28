package org.rapidoid.websocket;

/*
 * #%L
 * rapidoid-websocket
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.log.Log;
import org.rapidoid.net.Protocol;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.util.U;

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
@Since("2.?")
public class WebSocketProtocol implements Protocol {

	public void process(Channel ctx) {
		if (ctx.isInitial()) {
			Log.debug("WebSocket connection established");
			return;
		}

		byte[] msg = readMsg(ctx);
		U.show(msg);
		writeMsg(ctx, msg);
	}

	private byte[] readMsg(Channel ctx) {
		// FIXME implement this properly
		int toRead = ctx.input().size();
		return ctx.input().readNbytes(toRead);
	}

	private void writeMsg(Channel ctx, byte[] msg) {
		ctx.output().append(msg);
	}

}
