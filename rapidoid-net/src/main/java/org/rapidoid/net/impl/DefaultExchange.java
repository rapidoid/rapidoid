package org.rapidoid.net.impl;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
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

	@Override
	public String address() {
		return conn.address();
	}

	@Override
	public W write(String s) {
		byte[] bytes = s.getBytes();
		conn.write(bytes);
		return wrote(bytes.length);
	}

	@Override
	public W writeln(String s) {
		byte[] bytes = s.getBytes();
		conn.write(bytes);
		conn.write(CR_LF);
		return wrote(bytes.length + 2);
	}

	@Override
	public W write(byte[] bytes) {
		conn.write(bytes);
		return wrote(bytes.length);
	}

	@Override
	public W write(byte[] bytes, int offset, int length) {
		conn.write(bytes, offset, length);
		return wrote(length);
	}

	@Override
	public W write(ByteBuffer buf) {
		int n = buf.remaining();
		conn.write(buf);
		return wrote(n);
	}

	@Override
	public W write(File file) {
		long size = file.length();
		U.must(size < Integer.MAX_VALUE);
		conn.write(file);
		return wrote((int) size);
	}

	@Override
	public W writeJSON(Object value) {
		conn.writeJSON(value);
		return meW();
	}

	private W wrote(int count) {
		totalWritten.addAndGet(count);
		return meW();
	}

	@Override
	public T close() {
		conn.close();
		return meT();
	}

	@Override
	public T closeIf(boolean condition) {
		conn.closeIf(condition);
		return meT();
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

	protected BinaryMultiData binaryMultiData(KeyValueRanges ranges) {
		return new DefaultBinaryMultiData(this, ranges);
	}

	@Override
	public Buf buffer() {
		return conn.input();
	}

	public long getTotalWritten() {
		return totalWritten.get();
	}

	@Override
	public W async() {
		conn.async();
		return meW();
	}

	@Override
	public boolean isAsync() {
		return conn.isAsync();
	}

	@Override
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
