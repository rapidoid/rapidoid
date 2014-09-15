package org.rapidoid;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.rapidoid.buffer.Buf;
import org.rapidoid.net.ConnectionListener;
import org.rapidoid.net.RapidoidHelper;

public interface Connection {

	String address();

	int write(String s, Object tag, int kind);

	int write(byte[] bytes, Object tag, int kind);

	int write(byte[] bytes, int offset, int length, Object tag, int kind);

	int write(ByteBuffer buf, Object tag, int kind);

	void writeJSON(Object value);

	int writeTo(long connId, String s, Object tag, int kind);

	int writeTo(long connId, byte[] bytes, Object tag, int kind);

	int writeTo(long connId, byte[] bytes, int offset, int length, Object tag, int kind);

	int writeTo(long connId, ByteBuffer buf, Object tag, int kind);

	void complete(Object tag, boolean close);

	void close();

	Buf input();

	Buf output();

	String readln();

	String readN(int count);

	InetSocketAddress getAddress();

	RapidoidHelper helper();

	long connId();

	void fail(String msg);

	void failIf(boolean condition, String msg);

	void ensure(boolean expectedCondition, String msg);

	void close(boolean waitToWrite);

	boolean onSameThread();

	ConnectionListener listener();

	void setListener(ConnectionListener listener);

	void error();

}
