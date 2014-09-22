package org.rapidoid.bytes;

import java.nio.ByteBuffer;

public class ByteBufferBytes implements Bytes {

	private ByteBuffer buf;

	public ByteBufferBytes() {
	}

	public ByteBufferBytes(ByteBuffer buf) {
		this.buf = buf;
	}

	@Override
	public byte get(int position) {
		return buf.get(position);
	}

	@Override
	public int limit() {
		return buf.limit();
	}

	public void setBuf(ByteBuffer buf) {
		this.buf = buf;
	}

}
