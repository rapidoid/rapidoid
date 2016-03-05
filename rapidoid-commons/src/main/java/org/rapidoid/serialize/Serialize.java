package org.rapidoid.serialize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

import java.nio.ByteBuffer;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Serialize {

	private static final TypeSpecificSerializer SERIALIZER = new TypeSpecificSerializer();

	private static final TypeSpecificDeserializer DESERIALIZER = new TypeSpecificDeserializer();

	public static int serialize(byte[] dest, Object value) {
		ByteBuffer buf = ByteBuffer.wrap(dest);
		return serialize(buf, value);
	}

	public static int serialize(ByteBuffer buf, Object value) {
		int pos = buf.position();
		SERIALIZER.serialize(buf, value);
		buf.put((byte) '!');
		return buf.position() - pos;
	}

	public static Object deserialize(ByteBuffer buf) {
		Object value = DESERIALIZER.deserialize(buf);
		U.must(buf.get() == '!');
		return value;
	}

	public static Object deserialize(byte[] bytes) {
		return deserialize(ByteBuffer.wrap(bytes));
	}

}
