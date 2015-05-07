package org.rapidoidx.websocket.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoidx.websocket.WSExchange;

/*
 * #%L
 * rapidoid-x-websocket
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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class WSExchangeImpl implements WSExchange {

	private final Channel ctx;
	private final byte[] data;

	public WSExchangeImpl(Channel ctx, byte[] data) {
		this.ctx = ctx;
		this.data = data;
	}

	@Override
	public byte[] data() {
		return data;
	}

	@Override
	public String msg() {
		return new String(data);
	}

	@Override
	public void send(byte[] msg) {
		WebSocketProtocol.writeMsg(ctx, msg);
	}

	public void send(String msg) {
		WebSocketProtocol.writeMsg(ctx, msg.getBytes());
	}

}
