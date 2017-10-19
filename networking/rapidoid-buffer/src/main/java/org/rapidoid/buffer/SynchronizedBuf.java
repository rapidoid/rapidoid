package org.rapidoid.buffer;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.wrap.IntWrap;

import javax.net.ssl.SSLEngine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/*
 * #%L
 * rapidoid-buffer
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
@Since("4.1.0")
public class SynchronizedBuf extends OutputStream implements Buf {

	private final Buf buf;

	public SynchronizedBuf(Buf buf) {
		this.buf = buf;
	}

	@Override
	public synchronized byte get(int position) {
		return buf.get(position);
	}

	@Override
	public synchronized void put(int position, byte value) {
		buf.put(position, value);
	}

	@Override
	public synchronized void append(byte value) {
		buf.append(value);
	}

	@Override
	public synchronized void put(int position, byte[] bytes, int offset, int length) {
		buf.put(position, bytes, offset, length);
	}

	@Override
	public synchronized int size() {
		return buf.size();
	}

	@Override
	public synchronized void append(ByteBuffer wrap) {
		buf.append(wrap);
	}

	@Override
	public synchronized int append(ReadableByteChannel channel) throws IOException {
		return buf.append(channel);
	}

	@Override
	public synchronized int append(String s) {
		return buf.append(s);
	}

	@Override
	public synchronized void append(byte[] bytes) {
		buf.append(bytes);
	}

	@Override
	public synchronized void append(byte[] bytes, int offset, int length) {
		buf.append(bytes, offset, length);
	}

	@Override
	public synchronized String data() {
		return buf.data();
	}

	@Override
	public synchronized int writeTo(WritableByteChannel channel) throws IOException {
		return buf.writeTo(channel);
	}

	@Override
	public int writeTo(WritableByteChannel channel, int srcOffset, int length) throws IOException {
		return buf.writeTo(channel, srcOffset, length);
	}

	@Override
	public synchronized int writeTo(ByteBuffer buffer) {
		return buf.writeTo(buffer);
	}

	@Override
	public int writeTo(ByteBuffer buffer, int srcOffset, int length) {
		return buf.writeTo(buffer, srcOffset, length);
	}

	@Override
	public synchronized void deleteBefore(int position) {
		buf.deleteBefore(position);
	}

	@Override
	public synchronized void deleteAfter(int position) {
		buf.deleteAfter(position);
	}

	@Override
	public synchronized void deleteLast(int count) {
		buf.deleteLast(count);
	}

	@Override
	public synchronized int unitCount() {
		return buf.unitCount();
	}

	@Override
	public synchronized int unitSize() {
		return buf.unitSize();
	}

	@Override
	public synchronized void clear() {
		buf.clear();
	}

	@Override
	public synchronized String get(BufRange range) {
		return buf.get(range);
	}

	@Override
	public synchronized long getN(BufRange range) {
		return buf.getN(range);
	}

	@Override
	public synchronized boolean isSingle() {
		return buf.isSingle();
	}

	@Override
	public synchronized ByteBuffer getSingle() {
		return buf.getSingle();
	}

	@Override
	public synchronized ByteBuffer first() {
		return buf.first();
	}

	@Override
	public synchronized int putNumAsText(int position, long num, boolean forward) {
		return buf.putNumAsText(position, num, forward);
	}

	@Override
	public synchronized void get(BufRange range, byte[] dest, int offset) {
		buf.get(range, dest, offset);
	}

	@Override
	public synchronized byte next() {
		return buf.next();
	}

	@Override
	public synchronized void back(int count) {
		buf.back(count);
	}

	@Override
	public synchronized byte peek() {
		return buf.peek();
	}

	@Override
	public synchronized boolean hasRemaining() {
		return buf.hasRemaining();
	}

	@Override
	public synchronized int remaining() {
		return buf.remaining();
	}

	@Override
	public synchronized int position() {
		return buf.position();
	}

	@Override
	public synchronized int limit() {
		return buf.limit();
	}

	@Override
	public synchronized void position(int position) {
		buf.position(position);
	}

	@Override
	public synchronized void limit(int limit) {
		buf.limit(limit);
	}

	@Override
	public synchronized void upto(byte value, BufRange range) {
		buf.upto(value, range);
	}

	@Override
	public synchronized ByteBuffer exposed() {
		return buf.exposed();
	}

	@Override
	public synchronized void scanUntil(byte value, BufRange range) {
		buf.scanUntil(value, range);
	}

	@Override
	public synchronized void scanWhile(byte value, BufRange range) {
		buf.scanWhile(value, range);
	}

	@Override
	public synchronized void skip(int count) {
		buf.skip(count);
	}

	@Override
	public synchronized ByteBuffer bufAt(int index) {
		return buf.bufAt(index);
	}

	@Override
	public synchronized int bufCount() {
		return buf.bufCount();
	}

	@Override
	public synchronized int bufferIndexOf(int position) {
		return buf.bufferIndexOf(position);
	}

	@Override
	public synchronized int bufferOffsetOf(int position) {
		return buf.bufferOffsetOf(position);
	}

	@Override
	public synchronized String asText() {
		return buf.asText();
	}

	@Override
	public synchronized Bytes bytes() {
		return buf.bytes();
	}

	@Override
	public synchronized void scanLn(BufRange range) {
		buf.scanLn(range);
	}

	@Override
	public synchronized void scanLnLn(BufRanges ranges) {
		buf.scanLnLn(ranges);
	}

	@Override
	public synchronized void scanN(int count, BufRange range) {
		buf.scanN(count, range);
	}

	@Override
	public synchronized String readLn() {
		return buf.readLn();
	}

	@Override
	public synchronized String readN(int count) {
		return buf.readN(count);
	}

	@Override
	public synchronized byte[] readNbytes(int count) {
		return buf.readNbytes(count);
	}

	@Override
	public synchronized void scanTo(byte sep, BufRange range, boolean failOnLimit) {
		buf.scanTo(sep, range, failOnLimit);
	}

	@Override
	public synchronized int scanTo(byte sep1, byte sep2, BufRange range, boolean failOnLimit) {
		return buf.scanTo(sep1, sep2, range, failOnLimit);
	}

	@Override
	public synchronized void scanLnLn(BufRanges ranges, IntWrap result, byte end1, byte end2) {
		buf.scanLnLn(ranges, result, end1, end2);
	}

	@Override
	public synchronized void setReadOnly(boolean readOnly) {
		buf.setReadOnly(readOnly);
	}

	@Override
	public synchronized int checkpoint() {
		return buf.checkpoint();
	}

	@Override
	public synchronized void checkpoint(int checkpoint) {
		buf.checkpoint(checkpoint);
	}

	@Override
	public synchronized void writeByte(byte byteValue) {
		buf.writeByte(byteValue);
	}

	@Override
	public synchronized void writeBytes(byte[] src) {
		buf.writeBytes(src);
	}

	@Override
	public synchronized void writeBytes(byte[] src, int offset, int length) {
		buf.writeBytes(src, offset, length);
	}

	@Override
	public synchronized void write(int byteValue) throws IOException {
		buf.write(byteValue);
	}

	@Override
	public synchronized void write(byte[] src, int off, int len) throws IOException {
		buf.write(src, off, len);
	}

	@Override
	public OutputStream asOutputStream() {
		return this;
	}

	@Override
	public Buf unwrap() {
		return buf;
	}

	@Override
	public synchronized int sslWrap(SSLEngine engine, Buf dest) {
		return buf.sslWrap(engine, dest);
	}

	@Override
	public synchronized void append(ByteArrayOutputStream src) {
		buf.append(src);
	}
}
