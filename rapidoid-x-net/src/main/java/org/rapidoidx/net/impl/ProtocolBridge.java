package org.rapidoidx.net.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.util.U;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.net.Protocol;
import org.rapidoidx.net.abstracts.Channel;
import org.rapidoidx.net.abstracts.ChannelHolder;

/*
 * #%L
 * rapidoid-x-net
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
public abstract class ProtocolBridge {

	abstract protected void peer1(Channel ctx);

	abstract protected void peer2(Channel ctx);

	public Protocol peer1() {
		return new Protocol() {
			@Override
			public void process(Channel ctx) {
				peer1(ctx);
			}
		};
	}

	public Protocol peer2() {
		return new Protocol() {
			@Override
			public void process(Channel ctx) {
				peer2(ctx);
			}
		};
	}

	protected Channel peer(Channel ctx) {
		ChannelHolder holder = (ChannelHolder) ctx.state().obj;
		U.notNull(holder, "channel holder");

		if (holder.channel() == null) {
			// client not connected, so maybe later...
			throw Buf.INCOMPLETE_READ;
		}

		return holder.channel();
	}

}
