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

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.Resetable;
import org.rapidoidx.buffer.Buf;
import org.rapidoidx.buffer.BufProvider;
import org.rapidoidx.data.BinaryMultiData;
import org.rapidoidx.data.Data;
import org.rapidoidx.data.KeyValueRanges;
import org.rapidoidx.data.MultiData;
import org.rapidoidx.data.Range;
import org.rapidoidx.net.abstracts.Channel;
import org.rapidoidx.net.abstracts.ChannelHolder;
import org.rapidoidx.net.abstracts.ProtocolContext;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public abstract class DefaultExchange<T> implements ProtocolContext<T>, BufProvider, Resetable, Constants {

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
	public T restart() {
		conn.restart();
		return me();
	}

	@Override
	public T async() {
		conn.async();
		return me();
	}

	@Override
	public boolean isAsync() {
		return conn.isAsync();
	}

	@Override
	public T done() {
		conn.done();
		return me();
	}

	public T send() {
		conn.send();
		return me();
	}

	@Override
	public boolean isClosing() {
		return conn.isClosing();
	}

	@Override
	public boolean isClosed() {
		return conn.isClosed();
	}

	@Override
	public void waitUntilClosing() {
		conn.waitUntilClosing();
	}

	@Override
	public void log(String msg) {
		conn.log(msg);
	}

	@Override
	public synchronized boolean isInitial() {
		return conn.isInitial();
	}

	@Override
	public synchronized ConnState state() {
		return conn.state();
	}

	@Override
	public ChannelHolder createHolder() {
		return conn.createHolder();
	}

	@SuppressWarnings("unchecked")
	protected T me() {
		return (T) this;
	}

}
