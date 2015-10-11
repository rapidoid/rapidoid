package org.rapidoidx.buffer;

/*
 * #%L
 * rapidoid-x-buffer
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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.wrap.LongWrap;
import org.rapidoidx.bytes.Bytes;
import org.rapidoidx.data.Range;
import org.rapidoidx.data.Ranges;

@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class SynchronizedBuf implements Buf {

	private final Buf buf;

	public SynchronizedBuf(Buf buf) {
		this.buf = buf;
	}

	@Override
	public synchronized byte get(long position) {
		return buf.get(position);
	}

	@Override
	public synchronized void put(long position, byte value) {
		buf.put(position, value);
	}

	@Override
	public synchronized void append(byte value) {
		buf.append(value);
	}

	@Override
	public synchronized void put(long position, byte[] bytes, int offset, int length) {
		buf.put(position, bytes, offset, length);
	}

	@Override
	public synchronized long size() {
		return buf.size();
	}

	@Override
	public synchronized void append(ByteBuffer wrap) {
		buf.append(wrap);
	}

	@Override
	public synchronized long append(ReadableByteChannel channel) throws IOException {
		return buf.append(channel);
	}

	@Override
	public synchronized long append(String s) {
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
	public synchronized long writeTo(WritableByteChannel channel) throws IOException {
		return buf.writeTo(channel);
	}

	@Override
	public synchronized long writeTo(ByteBuffer buffer) {
		return buf.writeTo(buffer);
	}

	@Override
	public synchronized void deleteBefore(long position) {
		buf.deleteBefore(position);
	}

	@Override
	public synchronized void deleteAfter(long position) {
		buf.deleteAfter(position);
	}

	@Override
	public synchronized void deleteLast(long count) {
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
	public synchronized byte[] getBytes(Range range) {
		return buf.getBytes(range);
	}

	@Override
	public synchronized String get(Range range) {
		return buf.get(range);
	}

	@Override
	public synchronized long getN(Range range) {
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
	public synchronized long putNumAsText(long position, long num, boolean forward) {
		return buf.putNumAsText(position, num, forward);
	}

	@Override
	public synchronized void get(Range range, byte[] dest, int offset) {
		buf.get(range, dest, offset);
	}

	@Override
	public synchronized byte next() {
		return buf.next();
	}

	@Override
	public synchronized void back(long count) {
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
	public synchronized long remaining() {
		return buf.remaining();
	}

	@Override
	public synchronized long position() {
		return buf.position();
	}

	@Override
	public synchronized long limit() {
		return buf.limit();
	}

	@Override
	public synchronized void position(long position) {
		buf.position(position);
	}

	@Override
	public synchronized void limit(long limit) {
		buf.limit(limit);
	}

	@Override
	public synchronized void upto(byte value, Range range) {
		buf.upto(value, range);
	}

	@Override
	public synchronized ByteBuffer exposed() {
		return buf.exposed();
	}

	@Override
	public synchronized void scanUntil(byte value, Range range) {
		buf.scanUntil(value, range);
	}

	@Override
	public synchronized void scanWhile(byte value, Range range) {
		buf.scanWhile(value, range);
	}

	@Override
	public synchronized void skip(long count) {
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
	public synchronized int bufferIndexOf(long position) {
		return buf.bufferIndexOf(position);
	}

	@Override
	public synchronized int bufferOffsetOf(long position) {
		return buf.bufferOffsetOf(position);
	}

	@Override
	public synchronized OutputStream asOutputStream() {
		return buf.asOutputStream();
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
	public synchronized void scanLn(Range range) {
		buf.scanLn(range);
	}

	@Override
	public synchronized void scanLnLn(Ranges ranges) {
		buf.scanLnLn(ranges);
	}

	@Override
	public synchronized void scanN(long count, Range range) {
		buf.scanN(count, range);
	}

	@Override
	public synchronized String readLn() {
		return buf.readLn();
	}

	@Override
	public synchronized String readN(long count) {
		return buf.readN(count);
	}

	@Override
	public synchronized byte[] readNbytes(int count) {
		return buf.readNbytes(count);
	}

	@Override
	public synchronized void scanTo(byte sep, Range range, boolean failOnLimit) {
		buf.scanTo(sep, range, failOnLimit);
	}

	@Override
	public synchronized long scanTo(byte sep1, byte sep2, Range range, boolean failOnLimit) {
		return buf.scanTo(sep1, sep2, range, failOnLimit);
	}

	@Override
	public synchronized void scanLnLn(Ranges ranges, LongWrap result, byte end1, byte end2) {
		buf.scanLnLn(ranges, result, end1, end2);
	}

	@Override
	public synchronized void setReadOnly(boolean readOnly) {
		buf.setReadOnly(readOnly);
	}

	@Override
	public synchronized long checkpoint() {
		return buf.checkpoint();
	}

	@Override
	public synchronized void checkpoint(long checkpoint) {
		buf.checkpoint(checkpoint);
	}

}
