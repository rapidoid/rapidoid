package org.rapidoid.websocket.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.websocket.WSExchange;

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

@Authors("Nikolche Mihajlovski")
@Since("2.3.0")
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
