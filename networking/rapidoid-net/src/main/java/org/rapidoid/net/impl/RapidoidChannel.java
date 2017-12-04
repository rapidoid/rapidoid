/*-
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

package org.rapidoid.net.impl;


import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.net.Protocol;
import org.rapidoid.u.U;

import java.nio.channels.SocketChannel;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class RapidoidChannel extends RapidoidThing {

	final SocketChannel socketChannel;
	final boolean isClient;
	final Protocol protocol;
	final ChannelHolderImpl holder;
	final boolean autoreconnecting;
	final ConnState state;

	public RapidoidChannel(SocketChannel socketChannel, boolean isClient, Protocol protocol, ChannelHolderImpl holder,
	                       boolean autoreconnecting, ConnState state) {

		U.notNull(socketChannel, "socket channel");
		U.notNull(protocol, "channel protocol");
		U.notNull(holder, "channel holder");

		this.socketChannel = socketChannel;
		this.isClient = isClient;
		this.protocol = protocol;
		this.holder = holder;
		this.autoreconnecting = autoreconnecting;
		this.state = state;
	}

	public RapidoidChannel(SocketChannel socketChannel, boolean isClient, Protocol protocol) {

		U.notNull(socketChannel, "socket channel");
		U.notNull(protocol, "channel protocol");

		this.socketChannel = socketChannel;
		this.isClient = isClient;
		this.protocol = protocol;
		this.holder = null;
		this.autoreconnecting = false;
		this.state = null;
	}

}
