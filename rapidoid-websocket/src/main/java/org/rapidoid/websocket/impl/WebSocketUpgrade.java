package org.rapidoid.websocket.impl;

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

import org.apache.commons.codec.binary.Base64;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.http.HttpUpgradeHandler;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
public class WebSocketUpgrade implements HttpUpgradeHandler, Constants {

	private static final byte[] UPGRADE_RESPONSE = ("HTTP/1.1 101 Switching Protocols\r\n" + "Upgrade: websocket\r\n"
			+ "Connection: Upgrade\r\n").getBytes();

	private static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

	private static final byte[] SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept: ".getBytes();

	@Override
	public void doUpgrade(HttpExchangeImpl x) {

		x.output().append(UPGRADE_RESPONSE);

		String wsKey = x.header("Sec-WebSocket-Key");
		String wsProtocol = x.header("Sec-WebSocket-Protocol", null);
		String wsVer = x.header("Sec-WebSocket-Version", "13");

		U.must(wsVer.equals("13"), "The WebSocket protocol version '%s' is not supported!", wsVer);
		U.must(wsProtocol == null, "The WebSocket sub-protocol(s) '%s' is/are not supported!", wsProtocol);

		byte[] hash = UTILS.sha1Bytes((wsKey + GUID).getBytes());

		x.output().append(SEC_WEBSOCKET_ACCEPT);
		x.output().append(Base64.encodeBase64(hash));
		x.output().append(CR_LF);

		x.output().append(CR_LF);
	}

}
