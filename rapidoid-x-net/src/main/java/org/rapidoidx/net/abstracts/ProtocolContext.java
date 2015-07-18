package org.rapidoidx.net.abstracts;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.net.impl.ConnState;
import org.rapidoidx.net.impl.RapidoidHelper;

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

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public interface ProtocolContext<T> {

	/* CONNECTION */

	String address();

	InetSocketAddress getAddress();

	long connId();

	boolean isAsync();

	T close();

	T closeIf(boolean condition);

	boolean isClosing();

	boolean isClosed();

	void waitUntilClosing();

	void log(String msg);

	Channel nextOp(int nextOp);

	Channel nextWrite();

	Channel mode(int mode);

	/* PROTOCOL */

	boolean isInitial();

	T restart(); // X-specific

	/* WRITE */

	T write(String s);

	T writeln(String s);

	T write(byte[] bytes);

	T write(byte[] bytes, int offset, int length);

	T write(ByteBuffer buf);

	T write(File file);

	T send();

	/* ASYNC */

	// due to async() web handling option, it ain't over till the fat lady sings "done"
	T async();

	T done();

	/* READ */

	String readln();

	String readN(int count);

	/* IO */

	Buf input();

	Buf output();

	RapidoidHelper helper();

	/* EXTRAS */

	ConnState state();

	ChannelHolder createHolder(); // X-specific

}
