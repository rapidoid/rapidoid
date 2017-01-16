package org.rapidoid.net.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufProvider;
import org.rapidoid.data.*;
import org.rapidoid.net.abstracts.Channel;
import org.rapidoid.net.abstracts.ProtocolContext;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Resetable;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/*
 * #%L
 * rapidoid-net
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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
public abstract class DefaultExchange<T> extends RapidoidThing implements ProtocolContext<T>, BufProvider, Resetable, Constants {

	protected Channel conn;

	protected AtomicLong totalWritten = new AtomicLong();

	protected long requestId = 0;

	@Override
	public synchronized void reset() {
		this.conn = null;
		this.totalWritten.set(0);
		this.requestId = 0;
	}

	public synchronized void setConnection(Channel conn) {
		this.conn = conn;
		this.requestId = conn.requestId();
	}

	@Override
	public String address() {
		return conn.address();
	}

	@Override
	public T write(String s) {
		byte[] bytes = s.getBytes();
		conn.write(bytes);
		return wrote(bytes.length);
	}

	@Override
	public T writeln(String s) {
		byte[] bytes = s.getBytes();
		conn.write(bytes);
		conn.write(CR_LF);
		return wrote(bytes.length + 2);
	}

	@Override
	public T write(byte[] bytes) {
		conn.write(bytes);
		return wrote(bytes.length);
	}

	@Override
	public T write(byte[] bytes, int offset, int length) {
		conn.write(bytes, offset, length);
		return wrote(length);
	}

	@Override
	public T write(ByteBuffer buf) {
		int n = buf.remaining();
		conn.write(buf);
		return wrote(n);
	}

	@Override
	public T write(File file) {
		long size = file.length();
		U.must(size < Integer.MAX_VALUE);
		conn.write(file);
		return wrote((int) size);
	}

	@Override
	public T writeJSON(Object value) {
		conn.writeJSON(value);
		return me();
	}

	private T wrote(int count) {
		totalWritten.addAndGet(count);
		return me();
	}

	@Override
	public T close() {
		conn.close();
		return me();
	}

	@Override
	public T closeIf(boolean condition) {
		conn.closeIf(condition);
		return me();
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

	protected Data data(BufRange range) {
		return new DefaultData(this, range);
	}

	protected Data decodedData(BufRange range) {
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
	public long async() {
		return conn.async();
	}

	@Override
	public boolean isAsync() {
		return conn.isAsync();
	}

	@SuppressWarnings("unchecked")
	protected T me() {
		return (T) this;
	}

	@Override
	public long requestId() {
		return requestId;
	}

}
