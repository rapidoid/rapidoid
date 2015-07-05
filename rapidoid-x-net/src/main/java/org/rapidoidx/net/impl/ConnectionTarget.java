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

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoidx.net.Protocol;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class ConnectionTarget {

	volatile SocketChannel socketChannel;

	final InetSocketAddress addr;

	volatile long retryAfter;

	final Protocol protocol;

	final ChannelHolderImpl holder;

	final boolean autoreconnecting;

	final ConnState state;

	public ConnectionTarget(SocketChannel socketChannel, InetSocketAddress addr, Protocol protocol,
			ChannelHolderImpl holder, boolean autoreconnecting, ConnState state) {

		U.notNull(protocol, "connection protocol");
		U.notNull(holder, "connection holder");

		this.socketChannel = socketChannel;
		this.addr = addr;
		this.protocol = protocol;
		this.retryAfter = U.time();
		this.holder = holder;
		this.autoreconnecting = autoreconnecting;
		this.state = state;
	}

}
