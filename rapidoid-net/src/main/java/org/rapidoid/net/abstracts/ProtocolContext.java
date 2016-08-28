package org.rapidoid.net.abstracts;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.net.impl.ConnState;
import org.rapidoid.net.impl.RapidoidHelper;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
@Since("2.0.0")
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

	long requestId();

	void setRequest(IRequest request);

	boolean onSameThread();

	/* PROTOCOL */

	boolean isInitial();

	/* WRITE */

	T write(String s);

	T writeln(String s);

	T write(byte[] bytes);

	T write(byte[] bytes, int offset, int length);

	T write(ByteBuffer buf);

	T write(File file);

	T writeJSON(Object value);

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

}
