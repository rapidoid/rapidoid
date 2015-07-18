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

	public synchronized byte get(long position) {
		return buf.get(position);
	}

	public synchronized void put(long position, byte value) {
		buf.put(position, value);
	}

	public synchronized void append(byte value) {
		buf.append(value);
	}

	public synchronized void put(long position, byte[] bytes, int offset, int length) {
		buf.put(position, bytes, offset, length);
	}

	public synchronized long size() {
		return buf.size();
	}

	public synchronized void append(ByteBuffer wrap) {
		buf.append(wrap);
	}

	public synchronized long append(ReadableByteChannel channel) throws IOException {
		return buf.append(channel);
	}

	public synchronized long append(String s) {
		return buf.append(s);
	}

	public synchronized void append(byte[] bytes) {
		buf.append(bytes);
	}

	public synchronized void append(byte[] bytes, int offset, int length) {
		buf.append(bytes, offset, length);
	}

	public synchronized String data() {
		return buf.data();
	}

	public synchronized long writeTo(WritableByteChannel channel) throws IOException {
		return buf.writeTo(channel);
	}

	public synchronized long writeTo(ByteBuffer buffer) {
		return buf.writeTo(buffer);
	}

	public synchronized void deleteBefore(long position) {
		buf.deleteBefore(position);
	}

	public synchronized void deleteAfter(long position) {
		buf.deleteAfter(position);
	}

	public synchronized void deleteLast(long count) {
		buf.deleteLast(count);
	}

	public synchronized int unitCount() {
		return buf.unitCount();
	}

	public synchronized int unitSize() {
		return buf.unitSize();
	}

	public synchronized void clear() {
		buf.clear();
	}

	public synchronized String get(Range range) {
		return buf.get(range);
	}

	public synchronized long getN(Range range) {
		return buf.getN(range);
	}

	public synchronized boolean isSingle() {
		return buf.isSingle();
	}

	public synchronized ByteBuffer getSingle() {
		return buf.getSingle();
	}

	public synchronized ByteBuffer first() {
		return buf.first();
	}

	public synchronized long putNumAsText(long position, long num, boolean forward) {
		return buf.putNumAsText(position, num, forward);
	}

	public synchronized void get(Range range, byte[] dest, int offset) {
		buf.get(range, dest, offset);
	}

	public synchronized byte next() {
		return buf.next();
	}

	public synchronized void back(long count) {
		buf.back(count);
	}

	public synchronized byte peek() {
		return buf.peek();
	}

	public synchronized boolean hasRemaining() {
		return buf.hasRemaining();
	}

	public synchronized long remaining() {
		return buf.remaining();
	}

	public synchronized long position() {
		return buf.position();
	}

	public synchronized long limit() {
		return buf.limit();
	}

	public synchronized void position(long position) {
		buf.position(position);
	}

	public synchronized void limit(long limit) {
		buf.limit(limit);
	}

	public synchronized void upto(byte value, Range range) {
		buf.upto(value, range);
	}

	public synchronized ByteBuffer exposed() {
		return buf.exposed();
	}

	public synchronized void scanUntil(byte value, Range range) {
		buf.scanUntil(value, range);
	}

	public synchronized void scanWhile(byte value, Range range) {
		buf.scanWhile(value, range);
	}

	public synchronized void skip(long count) {
		buf.skip(count);
	}

	public synchronized ByteBuffer bufAt(int index) {
		return buf.bufAt(index);
	}

	public synchronized int bufCount() {
		return buf.bufCount();
	}

	public synchronized int bufferIndexOf(long position) {
		return buf.bufferIndexOf(position);
	}

	public synchronized int bufferOffsetOf(long position) {
		return buf.bufferOffsetOf(position);
	}

	public synchronized OutputStream asOutputStream() {
		return buf.asOutputStream();
	}

	public synchronized String asText() {
		return buf.asText();
	}

	public synchronized Bytes bytes() {
		return buf.bytes();
	}

	public synchronized void scanLn(Range range) {
		buf.scanLn(range);
	}

	public synchronized void scanLnLn(Ranges ranges) {
		buf.scanLnLn(ranges);
	}

	public synchronized void scanN(long count, Range range) {
		buf.scanN(count, range);
	}

	public synchronized String readLn() {
		return buf.readLn();
	}

	public synchronized String readN(long count) {
		return buf.readN(count);
	}

	public synchronized byte[] readNbytes(int count) {
		return buf.readNbytes(count);
	}

	public synchronized void scanTo(byte sep, Range range, boolean failOnLimit) {
		buf.scanTo(sep, range, failOnLimit);
	}

	public synchronized long scanTo(byte sep1, byte sep2, Range range, boolean failOnLimit) {
		return buf.scanTo(sep1, sep2, range, failOnLimit);
	}

	public synchronized void scanLnLn(Ranges ranges, LongWrap result, byte end1, byte end2) {
		buf.scanLnLn(ranges, result, end1, end2);
	}

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
