package org.rapidoid.buffer;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.bytes.ByteBufferBytes;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.bytes.BytesUtil;
import org.rapidoid.commons.Err;
import org.rapidoid.data.BufRange;
import org.rapidoid.data.BufRanges;
import org.rapidoid.pool.Pool;
import org.rapidoid.u.U;
import org.rapidoid.util.Constants;
import org.rapidoid.util.D;
import org.rapidoid.util.Msc;
import org.rapidoid.wrap.IntWrap;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
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
@Since("2.0.0")
public class MultiBuf extends OutputStream implements Buf, Constants {

	private final byte[] HELPER = new byte[20];

	private final ThreadLocal<ByteBuffer> tmpBufs = new ThreadLocal<ByteBuffer>() {
		@Override
		protected ByteBuffer initialValue() {
			return ByteBuffer.allocateDirect(20 * 1024);
		}
	};

	private final BufRange HELPER_RANGE = new BufRange();

	private static final int TO_BYTES = 1;

	private static final int TO_CHANNEL = 2;

	private static final int TO_BUFFER = 3;

	private static final int TO_SSL_DEST = 4;

	private static final int NOT_RELEVANT = Integer.MIN_VALUE;

	private final Pool<ByteBuffer> bufPool;

	private final int factor;

	private final int addrMask;

	private final int singleCap;

	private ByteBuffer[] bufs = new ByteBuffer[10];

	private int bufN;

	private int shrinkN;

	private final String name;

	private int _position;

	private int _limit;

	private int _checkpoint;

	private final ByteBufferBytes singleBytes = new ByteBufferBytes();

	private final Bytes multiBytes = new BufBytes(this);

	private Bytes _bytes = multiBytes;

	private int _size;

	private boolean readOnly = false;

	public MultiBuf(Pool<ByteBuffer> bufPool, int factor, String name) {
		this.bufPool = bufPool;
		this.name = name;
		this.singleCap = (int) Math.pow(2, factor);
		this.factor = factor;
		this.addrMask = Msc.bitMask(factor);

		assert invariant(true);
	}

	@Override
	public boolean isSingle() {
		assert invariant(false);
		return bufN == 1;
	}

	@Override
	public byte get(int position) {
		assert invariant(false);
		assert position >= 0;

		validatePos(position, 1);

		position += shrinkN;

		ByteBuffer buf = bufs[position >> factor];
		assert buf != null;

		assert invariant(false);
		return buf.get(position & addrMask);
	}

	private void validatePos(int pos, int space) {
		if (pos < 0) {
			throw U.rte("Invalid position: " + pos);
		}

		int least = pos + space;

		boolean hasEnough = least <= _size() && least <= _limit;

		if (!hasEnough) {
			throw INCOMPLETE_READ;
		}
	}

	@Override
	public void put(int position, byte value) {
		assert invariant(true);
		assert position >= 0;

		validatePos(position, 1);

		position += shrinkN;

		ByteBuffer buf = bufs[position >> factor];
		assert buf != null;

		buf.put(position & addrMask, value);

		assert invariant(true);
	}

	@Override
	public int size() {
		assert invariant(false);

		assert _size == _size();

		return _size;
	}

	private int _size() {
		return bufN > 0 ? (bufN - 1) * singleCap + bufs[bufN - 1].position() - shrinkN : 0;
	}

	private void expandUnit() {
		if (bufN == bufs.length) {
			bufs = Msc.expand(bufs, 2);
		}

		bufs[bufN] = bufPool.get();
		bufs[bufN].clear();

		bufN++;
	}

	@Override
	public void append(byte value) {
		assert invariant(true);

		writableBuf().put(value);

		sizeChanged();

		assert invariant(true);
	}

	/**
	 * Reads data from the channel and appends it to the buffer.
	 * <p>
	 * Precondition: received event that the channel has data to be read.
	 */
	@Override
	public int append(ReadableByteChannel channel) throws IOException {
		assert invariant(true);

		int totalRead = 0;

		try {

			boolean done;

			// precondition: the channel has data

			do {
				ByteBuffer dest = writableBuf();

				int space = dest.remaining();
				assert space > 0;

				int read = channel.read(dest);
				if (read >= 0) {
					totalRead += read;
				} else {
					// end of stream (e.g. the other end closed the connection)
					removeLastBufferIfEmpty();
					sizeChanged();

					assert invariant(true);
					return -1;
				}

				// if buffer wasn't filled -> no data is available in channel
				done = read < space;
			} while (!done);

		} finally {
			removeLastBufferIfEmpty();
			sizeChanged();
			assert invariant(true);
		}

		return totalRead;
	}

	@Override
	public void append(ByteBuffer src) {
		assert invariant(true);

		int theLimit = src.limit();

		while (src.hasRemaining()) {
			ByteBuffer dest = writableBuf();

			int space = dest.remaining();
			assert space > 0;

			if (src.remaining() > space) {
				// set limit to match only available space in dest
				src.limit(src.position() + space);
			}

			dest.put(src);

			// restore original limit
			src.limit(theLimit);
		}

		sizeChanged();

		assert invariant(true);
	}

	@Override
	public void append(byte[] src, int offset, int length) {
		assert invariant(true);

		int sizeBefore = _size();

		if (length > 0) {
			ByteBuffer buf = writableBuf();

			if (length <= buf.remaining()) {
				buf.put(src, offset, length);
			} else {
				int partLen = buf.remaining();
				buf.put(src, offset, partLen);
				assert buf.remaining() == 0;
				append(src, offset + partLen, length - partLen);
			}
		}

		sizeChanged();

		assert _size() - sizeBefore == length;

		assert invariant(true);
	}

	private ByteBuffer writableBuf() {
		if (bufN == 0) {
			expandUnit();
			return last();
		}

		ByteBuffer cbuf = last();

		if (!cbuf.hasRemaining()) {
			expandUnit();
			cbuf = last();
		}

		assert cbuf.hasRemaining();
		return cbuf;
	}

	private ByteBuffer last() {
		assert bufN > 0;
		return bufs[bufN - 1];
	}

	@Override
	public ByteBuffer first() {
		assert invariant(false);
		assert bufN > 0;
		return bufs[0];
	}

	@Override
	public ByteBuffer bufAt(int index) {
		assert invariant(false);
		assert bufN > index;
		return bufs[index];
	}

	@Override
	public int append(String s) {
		assert invariant(true);

		byte[] bytes = s.getBytes();
		append(bytes);

		sizeChanged();

		assert invariant(true);
		return bytes.length;
	}

	@Override
	public String toString() {
		return String.format("Buf " + name + " [size=" + _size() + ", units=" + unitCount() + ", trash=" + shrinkN
			+ ", pos=" + position() + ", limit=" + limit() + "] " + super.toString());
	}

	@Override
	public String data() {
		assert invariant(false);

		byte[] bytes = new byte[_size()];
		int total = readAll(bytes, 0, 0, bytes.length);

		assert total == bytes.length;

		assert invariant(false);
		return new String(bytes);
	}

	@Override
	public String get(BufRange range) {
		assert invariant(false);

		if (range.isEmpty()) {
			return "";
		}

		byte[] bytes = new byte[range.length];
		int total = readAll(bytes, 0, range.start, range.length);

		assert total == bytes.length;

		assert invariant(false);
		return new String(bytes);
	}

	@Override
	public void get(BufRange range, byte[] dest, int offset) {
		assert invariant(false);

		int total = readAll(dest, offset, range.start, range.length);

		assert total == range.length;
		assert invariant(false);
	}

	private int writeToHelper(BufRange range) {
		assert invariant(false);
		return readAll(HELPER, 0, range.start, range.length);
	}

	private int readAll(byte[] bytes, int destOffset, int offset, int length) {
		assert invariant(false);

		if (offset + length > _size()) {
			throw new IllegalArgumentException("offset + length > buffer size!");
		}

		int wrote;
		try {
			wrote = writeTo(TO_BYTES, offset, length, bytes, null, null, null, destOffset);
		} catch (IOException e) {
			throw U.rte(e);
		}

		assert invariant(false);
		return wrote;
	}

	@Override
	public int writeTo(WritableByteChannel channel) throws IOException {
		return writeTo(channel, 0, _size());
	}

	@Override
	public int writeTo(WritableByteChannel channel, int srcOffset, int length) throws IOException {
		assert invariant(false);

		int wrote = writeTo(TO_CHANNEL, srcOffset, length, null, channel, null, null, NOT_RELEVANT);
		assert U.must(wrote <= _size(), "Incorrect write to channel!");

		assert invariant(false);
		return wrote;
	}

	@Override
	public int writeTo(ByteBuffer buffer) {
		return writeTo(buffer, 0, _size());
	}

	@Override
	public int writeTo(ByteBuffer buffer, int srcOffset, int length) {
		assert invariant(false);

		try {
			int wrote = writeTo(TO_BUFFER, srcOffset, length, null, null, buffer, null, NOT_RELEVANT);
			assert wrote == length;
			assert invariant(false);
			return wrote;
		} catch (IOException e) {
			assert invariant(false);
			throw U.rte(e);
		}
	}

	private int writeTo(int mode, int offset, int length, byte[] bytes, WritableByteChannel channel, ByteBuffer buffer,
	                    SSLDestination sslDest, int destOffset) throws IOException {

		if (_size() == 0) {
			assert length == 0;
			return 0;
		}

		int fromPos = (offset + shrinkN);
		int toPos = fromPos + length - 1;

		int fromInd = fromPos >> factor;
		int toInd = toPos >> factor;

		int fromAddr = fromPos & addrMask;
		int toAddr = toPos & addrMask;

		assert fromInd <= toInd;

		if (fromInd == toInd) {
			return writePart(bufs[fromInd], fromAddr, toAddr + 1, mode, bytes, channel, buffer, sslDest, destOffset, -1);
		} else {
			return multiWriteTo(mode, fromInd, toInd, fromAddr, toAddr, bytes, channel, buffer, sslDest, destOffset);
		}
	}

	private int multiWriteTo(int mode, int fromIndex, int toIndex, int fromAddr, int toAddr, byte[] bytes,
	                         WritableByteChannel channel, ByteBuffer buffer, SSLDestination sslDest, int destOffset) throws IOException {

		ByteBuffer first = bufs[fromIndex];
		int len = singleCap - fromAddr;

		int wrote = writePart(first, fromAddr, singleCap, mode, bytes, channel, buffer, sslDest, destOffset, len);
		if (wrote < len) {
			return wrote;
		}

		int wroteTotal = wrote;

		for (int i = fromIndex + 1; i < toIndex; i++) {

			wrote = writePart(bufs[i], 0, singleCap, mode, bytes, channel, buffer, sslDest, destOffset + wroteTotal, singleCap);

			wroteTotal += wrote;

			if (wrote < singleCap) {
				return wroteTotal;
			}
		}

		ByteBuffer last = bufs[toIndex];
		wroteTotal += writePart(last, 0, toAddr + 1, mode, bytes, channel, buffer, sslDest, destOffset + wroteTotal, toAddr + 1);

		return wroteTotal;
	}

	private int writePart(ByteBuffer src, int pos, int limit, int mode, byte[] bytes, WritableByteChannel channel,
	                      ByteBuffer buffer, SSLDestination sslDest, int destOffset, int len) throws IOException {

		// backup buf positions
		int posBackup = src.position();
		int limitBackup = src.limit();

		src.position(pos);
		src.limit(limit);

		assert src.remaining() == len || len < 0;

		int count;

		switch (mode) {
			case TO_BYTES:
				if (len >= 0) {
					src.get(bytes, destOffset, len);
					count = len;
				} else {
					count = src.remaining();
					src.get(bytes, destOffset, count);
				}
				break;

			case TO_CHANNEL:
				count = 0;
				while (src.hasRemaining()) {
					int wrote = channel.write(src);
					count += wrote;
					if (wrote == 0) {
						break;
					}
				}
				break;

			case TO_BUFFER:
				count = src.remaining();
				buffer.put(src); // FIXME does the buffer have enough space?
				break;

			case TO_SSL_DEST:

				count = 0;
				ByteBuffer tmpBuf = tmpBufs.get();

				while (src.hasRemaining()) {

					SSLEngineResult result;
					tmpBuf.clear();

					try {
						result = sslDest.engine.wrap(src, tmpBuf);
					} catch (SSLException e) {
						throw U.rte(e);
					}

					tmpBuf.flip();
					sslDest.dest.append(tmpBuf);

					count += result.bytesConsumed();
				}

				break;

			default:
				throw Err.notExpected();
		}

		// restore buf positions
		src.limit(limitBackup);
		src.position(posBackup);

		return count;
	}

	private boolean invariant(boolean writing) {
		if (this.readOnly) {
			assert !writing;
		}

		try {

			assert bufN >= 0;

			for (int i = 0; i < bufN - 1; i++) {
				ByteBuffer buf = bufs[i];
				assert buf.position() == singleCap;
				assert buf.limit() == singleCap;
				assert buf.capacity() == singleCap;
			}

			if (bufN > 0) {
				ByteBuffer buf = bufs[bufN - 1];
				assert buf == last();
				assert buf.position() > 0;
				assert buf.capacity() == singleCap;
			}

			return true;

		} catch (AssertionError e) {
			dumpBuffers();
			throw e;
		}
	}

	private void dumpBuffers() {
		U.print(">> BUFFER " + name + " HAS " + bufN + " PARTS:");

		for (int i = 0; i < bufN - 1; i++) {
			ByteBuffer buf = bufs[i];
			D.print(i + "]" + buf);
		}

		if (bufN > 0) {
			ByteBuffer buf = bufs[bufN - 1];
			D.print("LAST]" + buf);
		}
	}

	@Override
	public void deleteBefore(int count) {
		assert invariant(true);

		if (count == _size()) {
			clear();
			return;
		}

		shrinkN += count;

		while (shrinkN >= singleCap) {
			removeFirstBuf();
			shrinkN -= singleCap;
		}

		_position -= count;
		if (_position < 0) {
			_position = 0;
		}

		sizeChanged();

		assert invariant(true);
	}

	private void removeFirstBuf() {
		bufs[0].clear();
		bufPool.release(bufs[0]);

		for (int i = 0; i < bufN - 1; i++) {
			bufs[i] = bufs[i + 1];
		}

		bufN--;
	}

	private void removeLastBuf() {
		bufs[bufN - 1].clear();
		bufPool.release(bufs[bufN - 1]);
		bufN--;
		if (bufN == 0) {
			shrinkN = 0;
		}
	}

	private void removeLastBufferIfEmpty() {
		if (bufN > 0) {
			if (last().position() == 0) {
				removeLastBuf();
			}
		}
	}

	@Override
	public int unitCount() {
		assert invariant(false);
		return bufN;
	}

	@Override
	public int unitSize() {
		assert invariant(false);
		return singleCap;
	}

	@Override
	public void put(int position, byte[] bytes, int offset, int length) {
		assert invariant(true);

		// TODO optimize
		int pos = position;
		for (int i = offset; i < offset + length; i++) {
			put(pos++, bytes[i]);
		}

		assert invariant(true);
	}

	@Override
	public void append(byte[] bytes) {
		assert invariant(true);

		append(bytes, 0, bytes.length);

		assert invariant(true);
	}

	@Override
	public void deleteAfter(int position) {
		assert invariant(true);

		if (bufN == 0 || position == _size()) {
			assert invariant(true);
			return;
		}

		assert validPosition(position);

		if (bufN == 1) {
			int newPos = position + shrinkN;
			assert newPos <= singleCap;
			first().position(newPos);
			if (newPos == 0) {
				removeLastBuf();
			}
		} else {
			position += shrinkN;
			int index = position >> factor;
			int addr = position & addrMask;

			// make it the last buffer
			while (index < bufN - 1) {
				removeLastBuf();
			}

			ByteBuffer last = bufs[index];
			assert last() == last;

			if (addr > 0) {
				last.position(addr);
			} else {
				removeLastBuf();
				if (bufN > 0) {
					last().position(singleCap);
				}
			}
		}

		removeLastBufferIfEmpty();
		sizeChanged();

		assert invariant(true);
	}

	@Override
	public void deleteLast(int count) {
		assert invariant(true);

		deleteAfter(_size() - count);

		assert invariant(true);
	}

	private boolean validPosition(int position) {
		assert U.must(position >= 0 && position < _size(), "Invalid position: %s", position);
		return true;
	}

	@Override
	public void clear() {
		// don't assert invariant() here, invalid state is allowed before clear/reset

		for (int i = 0; i < bufN; i++) {
			bufs[i].clear();
			bufPool.release(bufs[i]);
		}

		readOnly = false;
		shrinkN = 0;
		bufN = 0;
		_position = 0;

		sizeChanged();

		assert invariant(true);
	}

	@Override
	public long getN(BufRange range) {
		assert invariant(false);

		assert range.length >= 1;

		if (range.length > 20) {
			assert invariant(false);
			throw U.rte("Too many digits!");
		}

		int count = writeToHelper(range);

		int value = 0;

		boolean negative = HELPER[0] == '-';
		int start = negative ? 1 : 0;

		for (int i = start; i < count; i++) {
			byte b = HELPER[i];
			if (b >= '0' && b <= '9') {
				int digit = b - '0';
				value = value * 10 + digit;
			} else {
				assert invariant(false);
				throw U.rte("Invalid number: '%s'", get(range));
			}
		}

		assert invariant(false);
		return negative ? -value : value;
	}

	@Override
	public ByteBuffer getSingle() {
		assert invariant(false);
		return isSingle() ? first() : null;
	}

	@Override
	public int putNumAsText(int position, long n, boolean forward) {
		assert invariant(true);

		boolean appending;
		int direction;

		if (forward) {
			direction = 0;
			appending = position == size();
		} else {
			direction = -1;
			appending = false;
		}

		int space;

		if (n >= 0) {
			if (n < 10) {
				if (appending) {
					append((byte) (n + '0'));
				} else {
					put(position, (byte) (n + '0'));
				}
				space = 1;

			} else if (n < 100) {
				long dig1 = n / 10;
				long dig2 = n % 10;
				if (appending) {
					append((byte) (dig1 + '0'));
					append((byte) (dig2 + '0'));
				} else {
					put(position + direction, (byte) (dig1 + '0'));
					put(position + direction + 1, (byte) (dig2 + '0'));
				}
				space = 2;

			} else {
				if (appending) {
					String nums = "" + n;
					append(nums.getBytes());
					space = nums.length();
				} else {

					int digitsN = (int) Math.ceil(Math.log10(n + 1));

					int pos = position + digitsN - 1 + direction * digitsN;
					if (!forward) {
						pos++;
					}

					while (true) {
						long digit = n % 10;
						byte dig = (byte) (digit + 48);
						put(pos--, dig);
						if (n < 10) {
							break;
						}
						n = n / 10;
					}
					space = digitsN;
				}
			}
		} else {
			if (forward) {
				put(position, (byte) ('-'));
				space = putNumAsText(position + 1, -n, forward) + 1;
			} else {
				int digits = putNumAsText(position, -n, forward);
				put(position - digits, (byte) ('-'));
				space = digits + 1;
			}
		}

		assert invariant(true);
		return space;
	}

	@SuppressWarnings("unused")
	private int rebase(int pos, int bufInd) {
		return (bufInd << factor) + pos - shrinkN;
	}

	@Override
	public byte next() {
		assert invariant(false);

		byte b = get(_position++);

		assert invariant(false);
		return b;
	}

	@Override
	public void back(int count) {
		assert invariant(false);

		_position--;

		assert invariant(false);
	}

	@Override
	public byte peek() {
		assert invariant(false);

		byte b = get(_position);

		assert invariant(false);
		return b;
	}

	@Override
	public boolean hasRemaining() {
		assert invariant(false);

		boolean result = remaining() > 0;

		assert invariant(false);
		return result;
	}

	@Override
	public int remaining() {
		assert invariant(false);
		return _limit - _position;
	}

	@Override
	public int position() {
		assert invariant(false);
		return _position;
	}

	@Override
	public int limit() {
		assert invariant(false);
		return _limit;
	}

	private void sizeChanged() {
		_size = _size();
		_limit = _size();

		if (bufN == 1) {
			singleBytes.setTarget(bufs[0], shrinkN, _limit);
			_bytes = singleBytes;

		} else {
			_bytes = multiBytes;
		}
	}

	@Override
	public void position(int position) {
		assert invariant(false);
		_position = position;
		assert invariant(false);
	}

	@Override
	public void limit(int limit) {
		assert invariant(false);
		_limit = limit;
		assert invariant(false);
	}

	@Override
	public void upto(byte value, BufRange range) {
		assert invariant(false);

		range.starts(_position);

		while (get(_position) != value) {
			_position++;
		}

		range.ends(_position);

		_position++;

		assert invariant(false);
	}

	@Override
	public ByteBuffer exposed() {
		assert invariant(false);

		ByteBuffer first = first();

		assert invariant(false);
		return first;
	}

	@Override
	public void scanUntil(byte value, BufRange range) {
		assert invariant(false);

		requireRemaining(1);

		int start = position();
		int limit = limit();
		int last = limit - 1;

		int fromPos = (start + shrinkN);
		int toPos = (last + shrinkN);

		int fromInd = fromPos >> factor;
		int toInd = toPos >> factor;

		int fromAddr = fromPos & addrMask;
		int toAddr = toPos & addrMask;

		assert U.must(fromInd >= 0, "bad start: %s", start);
		assert U.must(toInd >= 0, "bad end: %s", last);

		ByteBuffer src = bufs[fromInd];

		int absPos = start;

		for (int pos = fromAddr; pos < singleCap; pos++) {
			byte b = src.get(pos);

			if (b == value) {
				range.setInterval(start, absPos);
				position(absPos + 1);
				assert invariant(false);
				return;
			}

			absPos++;
		}

		for (int i = fromInd + 1; i < toInd; i++) {
			src = bufs[i];

			for (int pos = 0; pos < singleCap; pos++) {
				byte b = src.get(pos);

				if (b == value) {
					range.setInterval(start, absPos);
					position(absPos + 1);
					assert invariant(false);
					return;
				}

				absPos++;
			}
		}

		if (fromInd < toInd) {
			src = bufs[toInd];

			for (int pos = 0; pos <= toAddr; pos++) {
				byte b = src.get(pos);

				if (b == value) {
					range.setInterval(start, absPos);
					position(absPos + 1);
					assert invariant(false);
					return;
				}

				absPos++;
			}
		}

		position(limit);

		assert invariant(false);
		throw INCOMPLETE_READ;
	}

	@Override
	public void scanWhile(byte value, BufRange range) {
		assert invariant(false);

		requireRemaining(1);

		int start = position();
		int limit = limit();
		int last = limit - 1;

		int fromPos = (start + shrinkN);
		int toPos = (last + shrinkN);

		int fromInd = fromPos >> factor;
		int toInd = toPos >> factor;

		int fromAddr = fromPos & addrMask;
		int toAddr = toPos & addrMask;

		assert U.must(fromInd >= 0, "bad start: %s", start);
		assert U.must(toInd >= 0, "bad end: %s", last);

		ByteBuffer src = bufs[fromInd];

		int absPos = start;

		for (int pos = fromAddr; pos < singleCap; pos++) {
			byte b = src.get(pos);

			if (b != value) {
				range.setInterval(start, absPos);
				position(absPos);
				assert invariant(false);
				return;
			}

			absPos++;
		}

		for (int i = fromInd + 1; i < toInd; i++) {
			src = bufs[i];

			for (int pos = 0; pos < singleCap; pos++) {
				byte b = src.get(pos);

				if (b != value) {
					range.setInterval(start, absPos);
					position(absPos);
					assert invariant(false);
					return;
				}

				absPos++;
			}
		}

		if (fromInd < toInd) {
			src = bufs[toInd];

			for (int pos = 0; pos <= toAddr; pos++) {
				byte b = src.get(pos);

				if (b != value) {
					range.setInterval(start, absPos);
					position(absPos);
					assert invariant(false);
					return;
				}

				absPos++;
			}
		}

		position(limit);

		assert invariant(false);
		throw INCOMPLETE_READ;
	}

	private void requireRemaining(int n) {
		if (remaining() < n) {
			throw Buf.INCOMPLETE_READ;
		}
	}

	@Override
	public void skip(int count) {
		assert invariant(false);

		requireRemaining(count);
		_position += count;

		assert invariant(false);
	}

	@Override
	public int bufferIndexOf(int position) {
		assert invariant(false);

		assert position >= 0;

		validatePos(position, 1);

		position += shrinkN;

		int index = position >> factor;
		assert bufs[index] != null;

		assert invariant(false);
		return index;
	}

	@Override
	public int bufferOffsetOf(int position) {
		assert invariant(false);

		assert position >= 0;

		validatePos(position, 1);

		position += shrinkN;

		assert invariant(false);
		return position & addrMask;
	}

	@Override
	public int bufCount() {
		assert invariant(false);
		return bufN;
	}

	@Override
	public OutputStream asOutputStream() {
		return this;
	}

	@Override
	public String asText() {
		return get(new BufRange(0, size()));
	}

	@Override
	public Bytes bytes() {
		assert invariant(false);
		return _bytes;
	}

	@Override
	public void scanLn(BufRange line) {
		assert invariant(false);
		int pos = BytesUtil.parseLine(bytes(), line, position(), size());

		if (pos < 0) {
			assert invariant(false);
			throw INCOMPLETE_READ;
		}

		_position = pos;
		assert invariant(false);
	}

	@Override
	public void scanLnLn(BufRanges lines) {
		assert invariant(false);

		int pos = BytesUtil.parseLines(bytes(), lines, position(), size());

		if (pos < 0) {
			assert invariant(false);
			throw INCOMPLETE_READ;
		}

		_position = pos;
		assert invariant(false);
	}

	@Override
	public void scanN(int count, BufRange range) {
		assert invariant(false);

		get(_position + count - 1);
		range.set(_position, count);
		_position += count;

		assert invariant(false);
	}

	@Override
	public String readLn() {
		assert invariant(false);

		scanLn(HELPER_RANGE);
		String result = get(HELPER_RANGE);

		assert invariant(false);
		return result;
	}

	@Override
	public String readN(int count) {
		assert invariant(false);

		scanN(count, HELPER_RANGE);
		String result = get(HELPER_RANGE);

		assert invariant(false);
		return result;
	}

	@Override
	public byte[] readNbytes(int count) {
		assert invariant(false);

		scanN(count, HELPER_RANGE);
		byte[] bytes = new byte[count];
		get(HELPER_RANGE, bytes, 0);

		assert invariant(false);
		return bytes;
	}

	@Override
	public void scanTo(byte sep, BufRange range, boolean failOnLimit) {
		assert invariant(false);

		int pos = BytesUtil.find(bytes(), _position, _limit, sep, true);

		if (pos >= 0) {
			consumeAndSkip(pos, range, 1);
		} else {
			if (failOnLimit) {
				assert invariant(false);
				throw INCOMPLETE_READ;
			} else {
				consumeAndSkip(_limit, range, 0);
			}
		}

		assert invariant(false);
	}

	@Override
	public int scanTo(byte sep1, byte sep2, BufRange range, boolean failOnLimit) {
		assert invariant(false);

		int pos1 = BytesUtil.find(bytes(), _position, _limit, sep1, true);
		int pos2 = BytesUtil.find(bytes(), _position, _limit, sep2, true);

		boolean found1 = pos1 >= 0;
		boolean found2 = pos2 >= 0;

		if (found1 && found2) {
			if (pos1 <= pos2) {
				consumeAndSkip(pos1, range, 1);
				assert invariant(false);
				return 1;
			} else {
				consumeAndSkip(pos2, range, 1);
				assert invariant(false);
				return 2;
			}
		} else if (found1 && !found2) {
			consumeAndSkip(pos1, range, 1);
			assert invariant(false);
			return 1;
		} else if (!found1 && found2) {
			consumeAndSkip(pos2, range, 1);
			assert invariant(false);
			return 2;
		} else {
			if (failOnLimit) {
				assert invariant(false);
				throw INCOMPLETE_READ;
			} else {
				consumeAndSkip(_limit, range, 0);
				assert invariant(false);
				return 0;
			}
		}
	}

	private void consumeAndSkip(int toPos, BufRange range, int skip) {
		range.setInterval(_position, toPos);
		_position = toPos + skip;
	}

	@Override
	public void scanLnLn(BufRanges ranges, IntWrap result, byte end1, byte end2) {
		assert invariant(false);

		int nextPos = BytesUtil.parseLines(bytes(), ranges, result, _position, _limit, end1, end2);

		if (nextPos < 0) {
			throw Buf.INCOMPLETE_READ;
		}

		_position = nextPos;

		assert invariant(false);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public int checkpoint() {
		return _checkpoint;
	}

	@Override
	public void checkpoint(int checkpoint) {
		this._checkpoint = checkpoint;
	}

	@Override
	public void write(int byteValue) throws IOException {
		// used as OutputStream
		append((byte) byteValue);
	}

	@Override
	public void write(byte[] src, int off, int len) {
		// used as OutputStream
		append(src, off, len);
	}

	@Override
	public Buf unwrap() {
		return this;
	}

	@Override
	public void append(ByteArrayOutputStream src) {
		try {
			src.writeTo(asOutputStream());
		} catch (IOException e) {
			throw U.rte(e);
		}
	}

	@Override
	public int sslWrap(SSLEngine engine, Buf dest) {
		assert invariant(false);

		SSLDestination sslDest = new SSLDestination(engine, dest);

		int consumed;
		try {
			consumed = writeTo(TO_SSL_DEST, 0, _size(), null, null, null, sslDest, NOT_RELEVANT);

		} catch (IOException e) {
			throw U.rte(e);
		}

		assert U.must(consumed <= _size(), "Incorrect write to channel!");

		deleteBefore(consumed);

		assert invariant(false);

		return consumed;
	}

	@Override
	public void writeByte(byte byteValue) {
		append(byteValue);
	}

	@Override
	public void writeBytes(byte[] src) {
		append(src);
	}

	@Override
	public void writeBytes(byte[] src, int offset, int length) {
		append(src, offset, length);
	}
}
