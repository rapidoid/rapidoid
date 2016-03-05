package org.rapidoid.serialize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.util.UTILS;

import java.nio.ByteBuffer;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Ser {

	protected static void writeNum(ByteBuffer buf, int len) {
		if (len < 255) {
			buf.put(UTILS.sbyte(len));

		} else {
			buf.put(UTILS.sbyte(255));
			buf.putInt(len);
		}
	}

	protected static int readNum(ByteBuffer buf) {
		int len = UTILS.ubyte(buf.get());

		if (len == 255) {
			len = buf.getInt();
		}

		return len;
	}

	protected static void writeBytes(ByteBuffer buf, byte[] bytes) {
		writeNum(buf, bytes.length);
		buf.put(bytes);
	}

	protected static byte[] readBytes(ByteBuffer buf) {
		byte[] bytes = new byte[readNum(buf)];
		buf.get(bytes);
		return bytes;
	}

	protected static byte bool2byte(boolean val) {
		return (byte) (val ? 1 : 0);
	}

	protected static boolean byte2bool(byte b) {
		return b != 0;
	}

	protected static TypeKind kind(int kindCode) {
		return TypeKind.values()[kindCode];
	}

	protected static byte code(TypeKind kind) {
		int ordinal = kind.ordinal();
		assert ordinal < 128;

		return (byte) ordinal;
	}

}
