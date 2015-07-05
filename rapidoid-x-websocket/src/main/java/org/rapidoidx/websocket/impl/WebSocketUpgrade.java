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

import org.apache.commons.codec.binary.Base64;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.http.HttpExchangeImpl;
import org.rapidoid.http.HttpUpgradeHandler;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
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

		byte[] hash = Crypto.sha1Bytes((wsKey + GUID).getBytes());

		x.output().append(SEC_WEBSOCKET_ACCEPT);
		x.output().append(Base64.encodeBase64(hash));
		x.output().append(CR_LF);

		x.output().append(CR_LF);
	}

}
