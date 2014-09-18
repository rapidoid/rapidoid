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

	@Override
	public String address() {
		return conn.address();
	}

	@Override
	public int write(String s) {
		return wrote(conn.write(s));
	}

	@Override
	public int write(byte[] bytes) {
		return wrote(conn.write(bytes));
	}

	@Override
	public int write(byte[] bytes, int offset, int length) {
		return wrote(conn.write(bytes, offset, length));
	}

	@Override
	public int write(ByteBuffer buf) {
		return wrote(conn.write(buf));
	}

	@Override
	public void writeJSON(Object value) {
		conn.writeJSON(value);
	}

	private int wrote(int count) {
		totalWritten.addAndGet(count);
		return count;
	}

	@Override
	public int writeTo(long connId, String s) {
		return conn.writeTo(connId, s);
	}

	@Override
	public int writeTo(long connId, byte[] bytes) {
		return conn.writeTo(connId, bytes);
	}

	@Override
	public int writeTo(long connId, byte[] bytes, int offset, int length) {
		return conn.writeTo(connId, bytes, offset, length);
	}

	@Override
	public int writeTo(long connId, ByteBuffer buf) {
		return conn.writeTo(connId, buf);
	}

	@Override
	public void writeJSONTo(long connId, Object value) {
		conn.writeJSONTo(connId, value);
	}

	@Override
	public void complete(boolean close) {
		conn.complete(this, close);
	}

	@Override
	public void close() {
		conn.close(true);
	}

	@Override
	public Buf input() {
		return conn.input();
	}

	@Override
	public Buf output() {
		return conn.output();
	}

	@Override
	public String readln() {
		return conn.readln();
	}

	@Override
	public String readN(int count) {
		return conn.readN(count);
	}

	@Override
	public InetSocketAddress getAddress() {
		return conn.getAddress();
	}

	@Override
	public RapidoidHelper helper() {
		return conn.helper();
	}

	@Override
	public long connId() {
		return conn.connId();
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
