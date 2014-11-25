package org.rapidoid.net.impl;

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

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufProvider;
import org.rapidoid.data.BinaryMultiData;
import org.rapidoid.data.Data;
import org.rapidoid.data.KeyValueRanges;
import org.rapidoid.data.MultiData;
import org.rapidoid.data.Range;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.abstracts.CtxFull;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Resetable;
import org.rapidoid.util.U;

public abstract class DefaultExchange<T, W> implements CtxFull<T, W>, BufProvider, Resetable, Constants {

	protected Channel conn;

	protected AtomicLong totalWritten = new AtomicLong();

	@Override
	public synchronized void reset() {
		conn = null;
		totalWritten.set(0);
	}

	public void setConnection(Channel conn) {
		this.conn = conn;
	}

	public String address() {
		return conn.address();
	}

	public W write(String s) {
		byte[] bytes = s.getBytes();
		conn.write(bytes);
		return wrote(bytes.length);
	}

	public W writeln(String s) {
		byte[] bytes = s.getBytes();
		conn.write(bytes);
		conn.write(CR_LF);
		return wrote(bytes.length + 2);
	}

	public W write(byte[] bytes) {
		conn.write(bytes);
		return wrote(bytes.length);
	}

	public W write(byte[] bytes, int offset, int length) {
		conn.write(bytes, offset, length);
		return wrote(length);
	}

	public W write(ByteBuffer buf) {
		int n = buf.remaining();
		conn.write(buf);
		return wrote(n);
	}

	public W write(File file) {
		long size = file.length();
		U.must(size < Integer.MAX_VALUE);
		conn.write(file);
		return wrote((int) size);
	}

	public W writeJSON(Object value) {
		conn.writeJSON(value);
		return meW();
	}

	private W wrote(int count) {
		totalWritten.addAndGet(count);
		return meW();
	}

	public T close() {
		conn.close();
		return meT();
	}

	@Override
	public T closeIf(boolean condition) {
		conn.closeIf(condition);
		return meT();
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

	protected Data data(Range range) {
		return new DefaultData(this, range);
	}

	protected Data decodedData(Range range) {
		return new DecodedData(this, range);
	}

	protected MultiData multiData(KeyValueRanges ranges) {
		return new DefaultMultiData(this, ranges);
	}

	protected BinaryMultiData binaryMultiData(KeyValueRanges ranges) {
		return new DefaultBinaryMultiData(this, ranges);
	}

	public Buf buffer() {
		return conn.input();
	}

	public long getTotalWritten() {
		return totalWritten.get();
	}

	public T restart() {
		conn.restart();
		return meT();
	}

	public W async() {
		conn.async();
		return meW();
	}

	public boolean isAsync() {
		return conn.isAsync();
	}

	public W done() {
		conn.done();
		return meW();
	}

	@SuppressWarnings("unchecked")
	protected T meT() {
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	protected W meW() {
		return (W) this;
	}

}
