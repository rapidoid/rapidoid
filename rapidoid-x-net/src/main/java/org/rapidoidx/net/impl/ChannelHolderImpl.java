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

import java.util.concurrent.atomic.AtomicLong;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.net.abstracts.Channel;
import org.rapidoidx.net.abstracts.ChannelHolder;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class ChannelHolderImpl implements ChannelHolder {

	private static final AtomicLong COUNTER = new AtomicLong();

	private volatile Channel channel;

	private final long id;

	public ChannelHolderImpl() {
		this.id = COUNTER.incrementAndGet();
	}

	public ChannelHolderImpl(Channel channel) {
		this();
		this.channel = channel;
	}

	@Override
	public long id() {
		return id;
	}

	@Override
	public synchronized Channel channel() {
		return channel;
	}

	public synchronized void setChannel(Channel channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "#" + id + ":" + channel;
	}

	public synchronized void closed() {
		setChannel(null);
	}

}
