package org.rapidoid.buffer;

/*
 * #%L
 * rapidoid-buffer
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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.rapidoid.bytes.BYTES;
import org.rapidoid.bytes.ByteBufferBytes;
import org.rapidoid.bytes.Bytes;
import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.pool.Pool;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;

public class MultiBuf implements Buf, Constants {

	private final byte[] HELPER = new byte[20];

	private final Range HELPER_RANGE = new Range();

	private static final int TO_BYTES = 1;

	private static final int TO_CHANNEL = 2;

	private static final int TO_BUFFER = 3;

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

	int ccc = BufScanner.MORE;

	int[] cccc = new int[300];

	private OutputStream outputStream;

	private final ByteBufferBytes singleBytes = new ByteBufferBytes();

	private final Bytes multiBytes = new BufBytes(this);

	public MultiBuf(Pool<ByteBuffer> bufPool, int factor, String name) {
		this.bufPool = bufPool;
		this.name = name;
		this.singleCap = (int) Math.pow(2, factor);
		this.factor = factor;
		this.addrMask = addrMask();
	}

	private int addrMask() {
		int mask = 1;

		for (int i = 0; i < factor - 1; i++) {
			mask <<= 1;
			mask |= 1;
		}

		return mask;
	}

	@Override
	public boolean isSingle() {
		assert invariant();
		return bufN == 1;
	}

	@Override
	public byte get(int position) {
		assert invariant();
		assert position >= 0;

		validatePos(position, 1);

		position += shrinkN;

		ByteBuffer buf = bufs[position >> factor];
		assert buf != null;

		assert invariant();
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
		assert invariant();
		assert position >= 0;

		validatePos(position, 1);

		position += shrinkN;

		ByteBuffer buf = bufs[position >> factor];
		assert buf != null;

		buf.put(position & addrMask, value);

		assert invariant();
	}

	@Override
	public int size() {
		assert invariant();

		int result = _size();

		assert invariant();
		return result;
	}

	private int _size() {
		return bufN > 0 ? (bufN - 1) * singleCap + bufs[bufN - 1].position() - shrinkN : 0;
	}

	private void expandUnit() {
		if (bufN == bufs.length) {
			bufs = U.expand(bufs, 2);
		}

		bufs[bufN] = bufPool.get();
		bufs[bufN].clear();

		bufN++;
	}

	@Override
	public void append(byte value) {
		assert invariant();

		writableBuf().put(value);

		sizeChanged();

		assert invariant();
	}

	/**
	 * Reads data from the channel and appends it to the buffer.
	 * 
	 * Precondition: received event that the channel has data to be read.
	 */
	@Override
	public int append(ReadableByteChannel channel) throws IOException {
		assert invariant();

		int totalRead = 0;
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

				assert invariant();
				return -1;
			}

			// if buffer wasn't filled -> no data is available in channel
			done = read < space;
		} while (!done);

		removeLastBufferIfEmpty();
		sizeChanged();

		assert invariant();
		return totalRead;
	}

	@Override
	public void append(ByteBuffer src) {
		assert invariant();

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

		assert invariant();
	}

	@Override
	public void append(byte[] src, int offset, int length) {
		assert invariant();

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

		assert invariant();
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
		assert invariant();
		assert bufN > 0;
		return bufs[0];
	}

	@Override
	public ByteBuffer bufAt(int index) {
		assert invariant();
		assert bufN > index;
		return bufs[index];
	}

	@Override
	public int append(String s) {
		assert invariant();

		byte[] bytes = s.getBytes();
		append(bytes);

		sizeChanged();

		assert invariant();
		return bytes.length;
	}

	@Override
	public String toString() {
		return String.format("Buf " + name + " [size=" + _size() + ", units=" + unitCount() + ", trash=" + shrinkN
				+ ", pos=" + position() + ", limit=" + limit() + "] " + super.toString());
	}

	@Override
	public String data() {
		assert invariant();

		byte[] bytes = new byte[_size()];
		int total = readAll(bytes, 0, 0, bytes.length);

		assert total == bytes.length;

		assert invariant();
		return new String(bytes);
	}

	@Override
	public String get(Range range) {
		assert invariant();

		if (range.isEmpty()) {
			return "";
		}

		byte[] bytes = new byte[range.length];
		int total = readAll(bytes, 0, range.start, range.length);

		assert total == bytes.length;

		assert invariant();
		return new String(bytes);
	}

	@Override
	public void get(Range range, byte[] dest, int offset) {
		assert invariant();

		int total = readAll(dest, offset, range.start, range.length);

		assert total == range.length;
		assert invariant();
	}

	private int writeToHelper(Range range) {
		assert invariant();
		return readAll(HELPER, 0, range.start, range.length);
	}

	private int readAll(byte[] bytes, int destOffset, int offset, int length) {
		assert invariant();

		if (offset + length > _size()) {
			throw new IllegalArgumentException("offset + length > buffer size!");
		}

		int wrote;
		try {
			wrote = writeTo(TO_BYTES, offset, length, bytes, null, null, destOffset);
		} catch (IOException e) {
			throw U.rte(e);
		}

		assert invariant();
		return wrote;
	}

	@Override
	public int writeTo(WritableByteChannel channel) throws IOException {
		assert invariant();

		int wrote = writeTo(TO_CHANNEL, 0, _size(), null, channel, null, 0);
		assert U.must(wrote <= _size(), "Incorrect write to channel!");

		assert invariant();
		return wrote;
	}

	@Override
	public int writeTo(ByteBuffer buffer) {
		assert invariant();

		try {
			int wrote = writeTo(TO_BUFFER, 0, _size(), null, null, buffer, 0);
			assert wrote == _size();
			assert invariant();
			return wrote;
		} catch (IOException e) {
			assert invariant();
			throw U.rte(e);
		}
	}

	private int writeTo(int mode, int offset, int length, byte[] bytes, WritableByteChannel channel, ByteBuffer buffer,
			int destOffset) throws IOException {
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
			return writePart(bufs[fromInd], fromAddr, toAddr + 1, mode, bytes, channel, buffer, destOffset, -1);
		} else {
			return multiWriteTo(mode, fromInd, toInd, fromAddr, toAddr, bytes, channel, buffer, destOffset);
		}
	}

	private int multiWriteTo(int mode, int fromIndex, int toIndex, int fromAddr, int toAddr, byte[] bytes,
			WritableByteChannel channel, ByteBuffer buffer, int destOffset) throws IOException {
		int wrote = 0;

		ByteBuffer first = bufs[fromIndex];
		int len = singleCap - fromAddr;
		wrote += writePart(first, fromAddr, singleCap, mode, bytes, channel, buffer, destOffset, len);

		for (int i = fromIndex + 1; i < toIndex; i++) {
			wrote += writePart(bufs[i], 0, singleCap, mode, bytes, channel, buffer, destOffset + wrote, singleCap);
		}

		ByteBuffer last = bufs[toIndex];
		wrote += writePart(last, 0, toAddr + 1, mode, bytes, channel, buffer, destOffset + wrote, toAddr + 1);

		return wrote;
	}

	private int writePart(ByteBuffer src, int pos, int limit, int mode, byte[] bytes, WritableByteChannel channel,
			ByteBuffer buffer, int destOffset, int len) throws IOException {

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
			buffer.put(src);
			break;

		default:
			throw U.notExpected();
		}

		// restore buf positions
		src.limit(limitBackup);
		src.position(posBackup);

		return count;
	}

	private boolean invariant() {
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
		U.print(">>" + bufN + " BUFFERS:");

		for (int i = 0; i < bufN - 1; i++) {
			ByteBuffer buf = bufs[i];
			U.show(i + "]" + buf);
		}

		if (bufN > 0) {
			ByteBuffer buf = bufs[bufN - 1];
			U.show("LAST]" + buf);
		}
	}

	@Override
	public void deleteBefore(int count) {
		assert invariant();

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

		assert invariant();
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
		assert invariant();
		return bufN;
	}

	@Override
	public int unitSize() {
		assert invariant();
		return singleCap;
	}

	@Override
	public void put(int position, byte[] bytes, int offset, int length) {
		assert invariant();

		// TODO optimize
		int pos = position;
		for (int i = offset; i < offset + length; i++) {
			put(pos++, bytes[i]);
		}

		assert invariant();
	}

	@Override
	public void append(byte[] bytes) {
		assert invariant();

		append(bytes, 0, bytes.length);

		assert invariant();
	}

	@Override
	public void deleteAfter(int position) {
		assert invariant();

		if (bufN == 0 || position == _size()) {
			assert invariant();
			return;
		}

		assert validPosition(position);

		if (bufN == 1) {
			int newPos = position + shrinkN;
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

		sizeChanged();

		assert invariant();
	}

	@Override
	public void deleteLast(int count) {
		assert invariant();

		deleteAfter(_size() - count);

		assert invariant();
	}

	private boolean validPosition(int position) {
		assert U.must(position >= 0 && position < _size(), "Invalid position: %s", position);
		return true;
	}

	@Override
	public void clear() {
		assert invariant();

		for (int i = 0; i < bufN; i++) {
			bufs[i].clear();
			bufPool.release(bufs[i]);
		}

		shrinkN = 0;
		bufN = 0;

		_position = 0;

		sizeChanged();

		assert invariant();
	}

	@Override
	public long getN(Range range) {
		assert invariant();

		assert range.length >= 1;

		if (range.length > 20) {
			assert invariant();
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
				assert invariant();
				throw U.rte("Invalid number!");
			}
		}

		assert invariant();
		return negative ? -value : value;
	}

	@Override
	public ByteBuffer getSingle() {
		assert invariant();
		return isSingle() ? first() : null;
	}

	@Override
	public int putNumAsText(int position, long n, boolean forward) {
		assert invariant();

		int direction = forward ? 0 : -1;

		int space;

		if (n >= 0) {
			if (n < 10) {
				put(position, (byte) (n + '0'));
				space = 1;
			} else if (n < 100) {
				long dig1 = n / 10;
				long dig2 = n % 10;
				put(position + direction, (byte) (dig1 + '0'));
				put(position + direction + 1, (byte) (dig2 + '0'));
				space = 2;
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

		assert invariant();
		return space;
	}

	@SuppressWarnings("unused")
	private int rebase(int pos, int bufInd) {
		return (bufInd << factor) + pos - shrinkN;
	}

	@Override
	public byte next() {
		assert invariant();

		byte b = get(_position++);

		assert invariant();
		return b;
	}

	@Override
	public void back(int count) {
		assert invariant();

		_position--;

		assert invariant();
	}

	@Override
	public byte peek() {
		assert invariant();

		byte b = get(_position);

		assert invariant();
		return b;
	}

	@Override
	public boolean hasRemaining() {
		assert invariant();

		boolean result = remaining() > 0;

		assert invariant();
		return result;
	}

	@Override
	public int remaining() {
		assert invariant();
		return _limit - _position;
	}

	@Override
	public int position() {
		assert invariant();
		return _position;
	}

	@Override
	public int limit() {
		assert invariant();
		return _limit;
	}

	private void sizeChanged() {
		_limit = _size();
	}

	@Override
	public void position(int position) {
		assert invariant();
		_position = position;
		assert invariant();
	}

	@Override
	public void limit(int limit) {
		assert invariant();
		_limit = limit;
		assert invariant();
	}

	@Override
	public void upto(byte value, Range range) {
		assert invariant();

		range.starts(_position);

		while (get(_position) != value) {
			_position++;
		}

		range.ends(_position);

		_position++;

		assert invariant();
	}

	@Override
	public ByteBuffer exposed() {
		assert invariant();

		ByteBuffer first = first();

		assert invariant();
		return first;
	}

	@Override
	public void scanUntil(byte value, Range range) {
		assert invariant();

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
				assert invariant();
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
					assert invariant();
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
					assert invariant();
					return;
				}

				absPos++;
			}
		}

		position(limit);

		assert invariant();
		throw INCOMPLETE_READ;
	}

	@Override
	public void scanWhile(byte value, Range range) {
		assert invariant();

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
				assert invariant();
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
					assert invariant();
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
					assert invariant();
					return;
				}

				absPos++;
			}
		}

		position(limit);

		assert invariant();
		throw INCOMPLETE_READ;
	}

	@Override
	public void skip(int count) {
		assert invariant();

		_position += count;

		assert invariant();
	}

	@Override
	public int bufferIndexOf(int position) {
		assert invariant();

		assert position >= 0;

		validatePos(position, 1);

		position += shrinkN;

		int index = position >> factor;
		assert bufs[index] != null;

		assert invariant();
		return index;
	}

	@Override
	public int bufferOffsetOf(int position) {
		assert invariant();

		assert position >= 0;

		validatePos(position, 1);

		position += shrinkN;

		assert invariant();
		return position & addrMask;
	}

	@Override
	public int bufCount() {
		assert invariant();
		return bufN;
	}

	@Override
	public OutputStream asOutputStream() {
		if (outputStream == null) {
			outputStream = new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					append((byte) b);
				}
			};
		}
		return outputStream;
	}

	@Override
	public String asText() {
		return get(new Range(0, size()));
	}

	@Override
	public Bytes bytes() {
		assert invariant();
		if (isSingle()) {
			singleBytes.setBuf(bufs[0]);
			return singleBytes;
		} else {
			return multiBytes;
		}
	}

	@Override
	public void scanLn(Range line) {
		assert invariant();
		int pos = BYTES.parseLine(bytes(), line, position(), size());

		if (pos < 0) {
			assert invariant();
			throw INCOMPLETE_READ;
		}

		_position = pos;
		assert invariant();
	}

	@Override
	public void scanLnLn(Ranges lines) {
		assert invariant();

		int pos = BYTES.parseLines(bytes(), lines, position(), size());

		if (pos < 0) {
			assert invariant();
			throw INCOMPLETE_READ;
		}

		_position = pos;
		assert invariant();
	}

	@Override
	public void scanN(int count, Range range) {
		assert invariant();

		get(_position + count - 1);
		range.set(_position, count);
		_position += count;

		assert invariant();
	}

	@Override
	public String readLn() {
		assert invariant();

		scanLn(HELPER_RANGE);
		String result = get(HELPER_RANGE);

		assert invariant();
		return result;
	}

	@Override
	public String readN(int count) {
		assert invariant();

		scanN(count, HELPER_RANGE);
		String result = get(HELPER_RANGE);
		assert invariant();
		return result;
	}

	@Override
	public void scanTo(byte sep, Range range, boolean failOnLimit) {
		assert invariant();

		int pos = BYTES.find(bytes(), _position, _limit, sep, true);

		if (pos >= 0) {
			consumeAndSkip(pos, range, 1);
		} else {
			if (failOnLimit) {
				assert invariant();
				throw INCOMPLETE_READ;
			} else {
				consumeAndSkip(_limit, range, 0);
			}
		}

		assert invariant();
	}

	@Override
	public int scanTo(byte sep1, byte sep2, Range range, boolean failOnLimit) {
		assert invariant();

		int pos1 = BYTES.find(bytes(), _position, _limit, sep1, true);
		int pos2 = BYTES.find(bytes(), _position, _limit, sep2, true);

		boolean found1 = pos1 >= 0;
		boolean found2 = pos2 >= 0;

		if (found1 && found2) {
			if (pos1 <= pos2) {
				consumeAndSkip(pos1, range, 1);
				assert invariant();
				return 1;
			} else {
				consumeAndSkip(pos2, range, 1);
				assert invariant();
				return 2;
			}
		} else if (found1 && !found2) {
			consumeAndSkip(pos1, range, 1);
			assert invariant();
			return 1;
		} else if (!found1 && found2) {
			consumeAndSkip(pos2, range, 1);
			assert invariant();
			return 2;
		} else {
			if (failOnLimit) {
				assert invariant();
				throw INCOMPLETE_READ;
			} else {
				consumeAndSkip(_limit, range, 0);
				assert invariant();
				return 0;
			}
		}
	}

	private void consumeAndSkip(int toPos, Range range, int skip) {
		range.setInterval(_position, toPos);
		_position = toPos + skip;
	}

}
