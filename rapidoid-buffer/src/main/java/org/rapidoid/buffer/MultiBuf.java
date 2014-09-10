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
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.rapidoid.data.Range;
import org.rapidoid.data.Ranges;
import org.rapidoid.pool.Pool;
import org.rapidoid.util.Constants;
import org.rapidoid.util.U;
import org.rapidoid.wrap.Int;

public class MultiBuf implements Buf, Constants {

	public static final byte[] CHARS_SWITCH_CASE = new byte[128];

	private final byte[] HELPER = new byte[20];

	private final ByteBuffer HELPER_BUF = ByteBuffer.wrap(HELPER);

	private final Range HELPER_RANGE = new Range();

	static {
		for (int ch = 0; ch < 128; ch++) {
			if (ch >= 'a' && ch <= 'z') {
				CHARS_SWITCH_CASE[ch] = (byte) (ch - 32);
			} else if (ch >= 'A' && ch <= 'Z') {
				CHARS_SWITCH_CASE[ch] = (byte) (ch + 32);
			} else {
				CHARS_SWITCH_CASE[ch] = (byte) ch;
			}
		}
	}

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

	private IncompleteReadException ERRR;

	int ccc = BufScanner.MORE;

	int[] cccc = new int[300];

	private int scanFrom;

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

		boolean hasEnough = least <= size() && least <= _limit;

		if (!hasEnough) {
			throw incomplete();
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
	 * Reads data from the channel and appends to the buffer.
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
			cbuf.flip();
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
		return String.format("Buf " + name + " [size=" + size() + ", units=" + unitCount() + ", trash=" + shrinkN
				+ "] " + super.toString());
	}

	@Override
	public String data() {
		assert invariant();

		byte[] bytes = new byte[size()];
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

		if (offset + length > size()) {
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

		int wrote = writeTo(TO_CHANNEL, 0, size(), null, channel, null, 0);
		U.ensure(wrote <= size(), "Incorrect write to channel!");

		assert invariant();
		return wrote;
	}

	@Override
	public int writeTo(ByteBuffer buffer) {
		assert invariant();

		try {
			int wrote = writeTo(TO_BUFFER, 0, size(), null, null, buffer, 0);
			assert wrote == size();
			assert invariant();
			return wrote;
		} catch (IOException e) {
			assert invariant();
			throw U.rte(e);
		}
	}

	private int writeTo(int mode, int offset, int length, byte[] bytes, WritableByteChannel channel, ByteBuffer buffer,
			int destOffset) throws IOException {
		if (size() == 0) {
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
		assert bufN >= 0;

		for (int i = 0; i < bufN - 1; i++) {
			ByteBuffer buf = bufs[i];
			assert buf.position() == 0;
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
	}

	@Override
	public void deleteBefore(int count) {
		assert invariant();

		if (count == size()) {
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

		// FIXME optimize
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

		if (bufN == 0 || position == size()) {
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

		deleteAfter(size() - count);

		assert invariant();
	}

	private boolean validPosition(int position) {
		U.ensure(position >= 0 && position < size(), "Invalid position: %s", position);
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
	public void putNumAsText(int position, long n) {
		assert invariant();

		assert n >= 0;

		if (n >= 0) {
			if (n < 10) {
				put(position, (byte) (n + '0'));
			} else if (n < 100) {
				long dig1 = n / 10;
				long dig2 = n % 10;
				put(position, (byte) (dig1 + '0'));
				put(position + 1, (byte) (dig2 + '0'));
			} else {
				int digitsN = (int) Math.ceil(Math.log10(n + 1));

				int pos = position + digitsN - 1;

				while (true) {
					long digit = n % 10;
					byte dig = (byte) (digit + 48);
					put(pos--, dig);

					if (n < 10) {
						break;
					}
					n = n / 10;
				}
			}
		} else {
			throw U.notReady();
		}

		assert invariant();
	}

	protected int scan(int start, int last, byte valB, short valS, int valI, long valL, int n) {

		int fromPos = (start + shrinkN);
		int toPos = (last + shrinkN);

		int fromInd = fromPos >> factor;
		int toInd = toPos >> factor;

		int fromAddr = fromPos & addrMask;
		int toAddr = toPos & addrMask;

		U.ensure(fromInd >= 0, "bad start: %s", start);
		U.ensure(toInd >= 0, "bad end: %s", last);

		int res;
		if (fromInd == toInd) {
			res = scanBuf(bufs[fromInd], fromAddr, toAddr, valB, valS, valI, valL, n);
			if (res >= 0) {
				return rebase(res, fromInd);
			}
		} else {
			res = scanBuf(bufs[fromInd], fromAddr, singleCap - 1, valB, valS, valI, valL, n);
			if (res >= 0) {
				return rebase(res, fromInd);
			}

			for (int i = fromInd + 1; i < toInd; i++) {
				res = scanBuf(bufs[i], 0, singleCap - 1, valB, valS, valI, valL, n);
				if (res >= 0) {
					return rebase(res, i);
				}
			}

			res = scanBuf(bufs[toInd], 0, toAddr, valB, valS, valI, valL, n);
			if (res >= 0) {
				return rebase(res, toInd);
			}
		}

		return -1;
	}

	private int rebase(int pos, int bufInd) {
		return (bufInd << factor) + pos - shrinkN;
	}

	private int scanBuf(ByteBuffer buf, int from, int to, byte valB, short valS, int valI, long valL, int n) {
		switch (n) {
		case 1:
			return scanBuf(buf, from, to, valB);

		case 2:
			for (int i = from; i <= to - 1; i++) {
				if (buf.getShort(i) == valS) {
					return i;
				}
			}

			return scanBuf(buf, Math.max(to, from), to, valB);

		case 4:
			for (int i = from; i <= to - 3; i++) {
				if (buf.getInt(i) == valI) {
					return i;
				}
			}

			return scanBuf(buf, Math.max(to - 2, from), to, valB);

		case 8:
			for (int i = from; i <= to - 7; i++) {
				if (buf.getLong(i) == valL) {
					return i;
				}
			}

			return scanBuf(buf, Math.max(to - 6, from), to, valB);

		default:
			throw U.notExpected();
		}
	}

	private int scanBuf(ByteBuffer buf, int from, int to, byte value) {
		for (int i = from; i <= to; i++) {
			if (buf.get(i) == value) {
				return i;
			}
		}

		return -1;
	}

	private int scanBufLn(Range range, ByteBuffer buf, int from, int to, int search, Int result, int lineInd) {

		int p = from;
		byte b0 = buf.get(p);
		if (b0 == LF) {
			return p;
		}

		p++;
		byte b1 = buf.get(p);
		if (b1 == LF) {
			return p;
		}

		p++;
		byte b2 = buf.get(p);
		if (b2 == LF) {
			return p;
		}

		p++;
		byte b3 = buf.get(p);
		if (b3 == LF) {
			return p;
		}

		int prefix = U.intFrom(b0, b1, b2, b3);

		if (prefix == search) {
			result.value = lineInd;
		}

		for (int i = p; i <= to; i++) {
			if (buf.get(i) == LF) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public boolean match(int start, byte[] match, int offset, int length, boolean caseSensitive) {
		assert invariant();

		boolean result;
		if (caseSensitive) {
			result = matchSensitive(start, match, offset, length);
		} else {
			result = matchNoCase(start, match, offset, length);
		}

		assert invariant();
		return result;
	}

	private boolean matchNoCase(int start, byte[] match, int offset, int length) {
		for (int i = 0; i < length; i++) {
			byte b = get(start + i);
			if (b != match[offset + i] && (b < 'A' || CHARS_SWITCH_CASE[b] != match[offset + i])) {
				return false;
			}
		}
		return true;
	}

	private boolean matchSensitive(int start, byte[] match, int offset, int length) {
		for (int i = 0; i < length; i++) {
			if (get(start + i) != match[offset + i]) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int find(int start, int limit, byte[] match, int offset, int length, boolean caseSensitive) {
		assert invariant();

		assert start >= 0;
		assert limit >= 0;
		assert offset >= 0;
		assert length >= 0;

		int result;
		if (caseSensitive) {
			result = findSensitive(start, limit, match, offset, length);
		} else {
			result = findNoCase(start, limit, match, offset, length);
		}

		assert invariant();
		return result;
	}

	private int findNoCase(int start, int limit, byte[] match, int offset, int length) {
		throw U.notReady();
	}

	private int findSensitive(int start, int limit, byte[] match, int offset, int length) {
		if (limit - start < length) {
			return -1;
		}

		int pos = start;
		int last = limit - length;

		int len = matchPartLen(length);

		HELPER_BUF.position(0);
		HELPER_BUF.put(match, offset, len);

		byte valB = match[offset];
		short valS = 0;
		int valI = 0;
		long valL = 0;

		switch (len) {
		case 8:
			valL = HELPER_BUF.getLong(0);
		case 4:
			valI = HELPER_BUF.getInt(0);
		case 2:
			valS = HELPER_BUF.getShort(0);
			break;

		default:
			break;
		}

		while ((pos = scan(pos, last, valB, valS, valI, valL, len)) >= 0) {
			if (matchSensitive(pos, match, offset, length)) {
				return pos;
			}
			pos++;
		}

		return -1;
	}

	private int matchPartLen(int length) {
		switch (length) {
		case 0:
			throw U.notExpected();

		case 1:
			return 1;

		case 2:
		case 3:
			return 2;

		case 4:
		case 5:
		case 6:
		case 7:
			return 4;
		}

		return 8;
	}

	private boolean match(int start, byte[] match, boolean caseSensitive) {
		return match(start, match, 0, match.length, caseSensitive);
	}

	@Override
	public int find(int start, int limit, byte match, boolean caseSensitive) {
		assert invariant();

		assert start >= 0;
		assert limit >= 0;

		if (limit - start < 1) {
			assert invariant();
			return -1;
		}

		if (caseSensitive) {
			assert invariant();
			return scan(start, limit - 1, match, (short) 0, 0, 0, 1);
		} else {
			assert invariant();
			throw U.notReady(); // FIXME
		}
	}

	@Override
	public int find(int start, int limit, byte[] match, boolean caseSensitive) {
		assert invariant();

		int pos = find(start, limit, match, 0, match.length, caseSensitive);

		assert invariant();
		return pos;
	}

	@Override
	public boolean matches(Range target, byte[] match, boolean caseSensitive) {
		assert invariant();

		if (target.length != match.length || target.start < 0 || target.last() >= size()) {
			assert invariant();
			return false;
		}

		boolean result = match(target.start, match, caseSensitive);

		assert invariant();
		return result;
	}

	@Override
	public boolean startsWith(Range target, byte[] match, boolean caseSensitive) {
		assert invariant();

		if (target.length < match.length || target.start < 0 || target.last() >= size()) {
			assert invariant();
			return false;
		}

		boolean result = match(target.start, match, caseSensitive);
		assert invariant();

		return result;
	}

	@Override
	public boolean containsAt(Range target, int offset, byte[] match, boolean caseSensitive) {
		assert invariant();

		if (offset < 0 || target.length < offset + match.length || target.start < 0 || target.last() >= size()) {
			assert invariant();
			return false;
		}

		boolean result = match(target.start + offset, match, caseSensitive);

		assert invariant();
		return result;
	}

	@Override
	public void trim(Range target) {
		assert invariant();

		int start = target.start;
		int len = target.length;
		int finish = start + len - 1;

		if (start < 0 || len == 0) {
			assert invariant();
			return;
		}

		while (start < finish && get(start) == ' ') {
			start++;
		}

		while (start < finish && get(finish) == ' ') {
			finish--;
		}

		target.start = start;
		target.length = finish - start + 1;

		assert invariant();
	}

	private void consumeAndSkip(int toPos, Range range, int skip) {
		range.setInterval(_position, toPos);
		_position = toPos + skip;
	}

	@Override
	public void scanTo(byte sep, Range range, boolean failOnLimit) {
		assert invariant();

		int pos = find(_position, _limit, sep, true);

		if (pos >= 0) {
			consumeAndSkip(pos, range, 1);
		} else {
			if (failOnLimit) {
				assert invariant();
				throw incomplete();
			} else {
				consumeAndSkip(_limit, range, 0);
			}
		}

		assert invariant();
	}

	@Override
	public void scanTo(byte[] sep, Range range, boolean failOnLimit) {
		assert invariant();

		int pos = find(_position, _limit, sep, true);

		if (pos >= 0) {
			consumeAndSkip(pos, range, sep.length);
		} else {
			if (failOnLimit) {
				assert invariant();
				throw incomplete();
			} else {
				consumeAndSkip(_limit, range, 0);
			}
		}

		assert invariant();
	}

	@Override
	public int scanTo(byte sep1, byte sep2, Range range, boolean failOnLimit) {
		assert invariant();

		int pos1 = find(_position, _limit, sep1, true);
		int pos2 = find(_position, _limit, sep2, true);

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
				throw incomplete();
			} else {
				consumeAndSkip(_limit, range, 0);
				assert invariant();
				return 0;
			}
		}
	}

	@Override
	public int scanTo(byte[] sep1, byte[] sep2, Range range, boolean failOnLimit) {
		assert invariant();

		int pos1 = find(_position, _limit, sep1, true);
		int pos2 = find(_position, _limit, sep2, true);

		boolean found1 = pos1 >= 0;
		boolean found2 = pos2 >= 0;

		if (found1 && found2) {
			if (pos1 <= pos2) {
				consumeAndSkip(pos1, range, sep1.length);
				assert invariant();
				return 1;
			} else {
				consumeAndSkip(pos2, range, sep2.length);
				assert invariant();
				return 2;
			}
		} else if (found1 && !found2) {
			consumeAndSkip(pos1, range, sep1.length);
			assert invariant();
			return 1;
		} else if (!found1 && found2) {
			consumeAndSkip(pos2, range, sep2.length);
			assert invariant();
			return 2;
		} else {
			if (failOnLimit) {
				assert invariant();
				throw incomplete();
			} else {
				consumeAndSkip(_limit, range, 0);
				assert invariant();
				return 0;
			}
		}
	}

	@Override
	public IncompleteReadException incomplete() {
		assert invariant();

		if (ERRR == null) {
			ERRR = U.rte(IncompleteReadException.class);
		}

		assert invariant();
		return ERRR;
	}

	@Override
	public void scanLn(Range range, boolean failOnLimit) {
		assert invariant();

		scanTo(LF_, CR_LF, range, failOnLimit);

		// TODO: this is faster, but buggy:
		// scanTo(LF, range, failOnLimit);
		// if (range.start > 0 && get(range.last()) == CR) {
		// range.length--;
		// }

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
	public String readLn() {
		assert invariant();

		scanLn(HELPER_RANGE, true);
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
		_limit = size();
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
	public void scanUntil(byte value, Range range, boolean failOnLimit) {
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

		U.ensure(fromInd >= 0, "bad start: %s", start);
		U.ensure(toInd >= 0, "bad end: %s", last);

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

		if (failOnLimit) {
			assert invariant();
			throw incomplete();
		} else {
			range.setInterval(start, absPos - 1);
		}

		assert invariant();
	}

	@Override
	public void scanWhile(byte value, Range range, boolean failOnLimit) {
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

		U.ensure(fromInd >= 0, "bad start: %s", start);
		U.ensure(toInd >= 0, "bad end: %s", last);

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

		if (failOnLimit) {
			assert invariant();
			throw incomplete();
		} else {
			range.setInterval(start, absPos - 1);
		}

		assert invariant();
	}

	@Override
	public void skip(int count) {
		assert invariant();

		_position += count;

		assert invariant();
	}

	@Override
	public void scanLnLn(Ranges ranges, int search, Int result) {
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

		U.ensure(fromInd >= 0, "bad start: %s", start);
		U.ensure(toInd >= 0, "bad limit: %s", limit);

		result.value = -1;
		scanFrom = position();

		if (fromInd == toInd) {
			ranges.count = simpleScanLines(ranges.ranges, fromInd, fromAddr, toAddr, search, result);
		} else {
			ranges.count = complexScanLines(ranges.ranges, fromAddr, fromInd, toAddr, toInd, search, result);
		}

		assert invariant();
	}

	private int simpleScanLines(Range[] ranges, int ind, int fromAddr, int toAddr, int search, Int result) {
		int rangeInd = scanRegionLines(ranges, ind, fromAddr, toAddr, 0, search, result);
		if (rangeInd < 0) {
			return -rangeInd - 1;
		}

		position(limit());
		throw incomplete();
	}

	/**
	 * @return the index of the range used to the scanned line
	 */
	private int scanRegionLines(Range[] ranges, int ind, int fromAddr, int toAddr, int rangeInd, int search, Int result) {
		int base = rebase(0, ind);
		ByteBuffer src = bufs[ind];

		int pos = fromAddr;
		while ((pos = scanBufLn(ranges[rangeInd], src, pos, toAddr, search, result, rangeInd)) > 0) {
			boolean withCR = src.get(pos - 1) == CR;
			int to = base + pos - (withCR ? 1 : 0);
			ranges[rangeInd++].setInterval(scanFrom, to);

			position(base + pos + 1);

			if (scanFrom == to) {
				// return negative number to mark that an empty line was found
				return -rangeInd;
			}

			scanFrom = position();
			pos++;
		}

		return rangeInd;
	}

	private int complexScanLines(Range[] ranges, int fromAddr, int fromInd, int toAddr, int toInd, int search,
			Int result) {

		int rangeInd = 0;

		rangeInd = scanRegionLines(ranges, fromInd, fromAddr, singleCap - 1, rangeInd, search, result);
		if (rangeInd < 0) {
			return -rangeInd - 1;
		}

		for (int ind = fromInd + 1; ind < toInd; ind++) {
			rangeInd = scanRegionLines(ranges, ind, 0, singleCap - 1, rangeInd, search, result);
			if (rangeInd < 0) {
				return -rangeInd - 1;
			}
		}

		rangeInd = scanRegionLines(ranges, toInd, 0, toAddr, rangeInd, search, result);
		if (rangeInd < 0) {
			return -rangeInd - 1;
		}

		position(limit());
		throw incomplete();
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
	public boolean split(Range target, byte sep, Range before, Range after) {
		assert invariant();
		int pos = find(target.start, target.limit(), sep, true);

		if (pos >= 0) {
			before.setInterval(target.start, pos);
			after.setInterval(pos + 1, target.limit());
			assert invariant();
			return true;
		} else {
			assert invariant();
			return false;
		}
	}

}
