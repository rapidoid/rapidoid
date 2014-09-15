package org.rapidoid.net;

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
import java.util.concurrent.atomic.AtomicLong;

import org.rapidoid.Connection;
import org.rapidoid.Ctx;
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufProvider;
import org.rapidoid.data.Data;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.MultiData;
import org.rapidoid.data.Range;
import org.rapidoid.util.Resetable;

public class Exchange implements BufProvider, Resetable, Ctx {

	protected Connection conn;

	protected AtomicLong totalWritten = new AtomicLong();

	@Override
	public synchronized void reset() {
		conn = null;
		totalWritten.set(0);
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	public String address() {
		return conn.address();
	}

	public int write(String s) {
		return wrote(conn.write(s, this, 0));
	}

	public int write(byte[] bytes) {
		return wrote(conn.write(bytes, this, 0));
	}

	public int write(byte[] bytes, int offset, int length) {
		return wrote(conn.write(bytes, offset, length, this, 0));
	}

	public int write(ByteBuffer buf) {
		return wrote(conn.write(buf, this, 0));
	}

	@Override
	public void writeJSON(Object value) {
		conn.writeJSON(value);
	}

	private int wrote(int count) {
		totalWritten.addAndGet(count);
		return count;
	}

	public int writeTo(long connId, String s) {
		return conn.writeTo(connId, s, this, 0);
	}

	public int writeTo(long connId, byte[] bytes) {
		return conn.writeTo(connId, bytes, this, 0);
	}

	public int writeTo(long connId, byte[] bytes, int offset, int length) {
		return conn.writeTo(connId, bytes, offset, length, this, 0);
	}

	public int writeTo(long connId, ByteBuffer buf) {
		return conn.writeTo(connId, buf, this, 0);
	}

	public void complete(boolean close) {
		conn.complete(this, close);
	}

	public void close() {
		conn.close(true);
	}

	public Buf input() {
		return conn.input();
	}

	public Buf output() {
		return conn.output();
	}

	public String readln() {
		return conn.readln();
	}

	public String readN(int count) {
		return conn.readN(count);
	}

	public InetSocketAddress getAddress() {
		return conn.getAddress();
	}

	public RapidoidHelper helper() {
		return conn.helper();
	}

	public long connId() {
		return conn.connId();
	}

	public void fail(String msg) {
		conn.fail(msg);
	}

	public void failIf(boolean condition, String msg) {
		conn.failIf(condition, msg);
	}

	public void ensure(boolean expectedCondition, String msg) {
		conn.ensure(expectedCondition, msg);
	}

	protected Data data(Range range) {
		return new DefaultData(this, range);
	}

	protected Data decodedData(Range range) {
		return new DecodedData(this, range);
	}

	protected MultiData multiData(KeyValueRanges ranges) {
		return new DefaultMultiData(this, ranges);
	}

	@Override
	public Buf buffer() {
		return conn.input();
	}

	public long getTotalWritten() {
		return totalWritten.get();
	}

	@Override
	public Connection connection() {
		return conn;
	}

}
