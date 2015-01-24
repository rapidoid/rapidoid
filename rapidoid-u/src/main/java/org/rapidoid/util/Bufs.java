package org.rapidoid.util;

import java.nio.ByteBuffer;

public class Bufs {

	public static ByteBuffer expand(ByteBuffer buf, int newSize) {
		ByteBuffer buf2 = ByteBuffer.allocate(newSize);

		ByteBuffer buff = buf.duplicate();
		buff.rewind();
		buff.limit(buff.capacity());

		buf2.put(buff);

		return buf2;
	}

	public static ByteBuffer expand(ByteBuffer buf) {
		int cap = buf.capacity();

		if (cap <= 1000) {
			cap *= 10;
		} else if (cap <= 10000) {
			cap *= 5;
		} else {
			cap *= 2;
		}

		return expand(buf, cap);
	}

	public static String buf2str(ByteBuffer buf) {
		ByteBuffer buf2 = buf.duplicate();

		buf2.rewind();
		buf2.limit(buf2.capacity());

		byte[] bytes = new byte[buf2.capacity()];
		buf2.get(bytes);

		return new String(bytes);
	}

	public static ByteBuffer buf(String s) {
		byte[] bytes = s.getBytes();

		ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);
		buf.put(bytes);
		buf.rewind();

		return buf;
	}

}
