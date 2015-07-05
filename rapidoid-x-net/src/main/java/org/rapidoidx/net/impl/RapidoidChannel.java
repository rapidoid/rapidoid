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

import java.nio.channels.SocketChannel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoidx.net.Protocol;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class RapidoidChannel {

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
