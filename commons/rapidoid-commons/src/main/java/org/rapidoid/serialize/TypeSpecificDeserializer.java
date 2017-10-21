package org.rapidoid.serialize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.AbstractTypeSpecificFactory;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.TUUID;

import java.nio.ByteBuffer;
import java.util.*;

/*
 * #%L
 * rapidoid-commons
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
@Since("5.1.0")
public class TypeSpecificDeserializer extends AbstractTypeSpecificFactory<ByteBuffer> {

	public Object deserialize(ByteBuffer buf) {
		return create(buf);
	}

	@Override
	public Object create(ByteBuffer buf) {
		TypeKind kind = Ser.kind(Msc.ubyte(buf.get()));
		return super.create(buf, kind);
	}

	@Override
	public Object objectValue(ByteBuffer buf) {
		throw U.rte("?");
	}

	@Override
	public boolean booleanValue(ByteBuffer buf) {
		return Ser.byte2bool(buf.get());
	}

	@Override
	public byte byteValue(ByteBuffer buf) {
		return buf.get();
	}

	@Override
	public short shortValue(ByteBuffer buf) {
		return buf.getShort();
	}

	@Override
	public char charValue(ByteBuffer buf) {
		return buf.getChar();
	}

	@Override
	public int intValue(ByteBuffer buf) {
		return buf.getInt();
	}

	@Override
	public long longValue(ByteBuffer buf) {
		return buf.getLong();
	}

	@Override
	public float floatValue(ByteBuffer buf) {
		return buf.getFloat();
	}

	@Override
	public double doubleValue(ByteBuffer buf) {
		return buf.getDouble();
	}

	@Override
	public String string(ByteBuffer buf) {
		return new String(Ser.readBytes(buf));
	}

	@Override
	public Date date(ByteBuffer buf) {
		return new Date(longValue(buf));
	}

	@Override
	public UUID uuid(ByteBuffer buf) {
		return new UUID(longValue(buf), longValue(buf));
	}

	@Override
	public TUUID tuuid(ByteBuffer buf) {
		return new TUUID(longValue(buf), longValue(buf), longValue(buf));
	}

	@Override
	public boolean[] booleanArr(ByteBuffer buf) {
		int len = Ser.readNum(buf);
		boolean[] arr = new boolean[len];

		for (int i = 0; i < len; i++) {
			arr[i] = booleanValue(buf);
		}

		return arr;
	}

	@Override
	public byte[] byteArr(ByteBuffer buf) {
		int len = Ser.readNum(buf);

		byte[] bytes = new byte[len];
		buf.get(bytes);

		return bytes;
	}

	@Override
	public short[] shortArr(ByteBuffer buf) {
		int len = Ser.readNum(buf);
		short[] arr = new short[len];

		for (int i = 0; i < len; i++) {
			arr[i] = shortValue(buf);
		}

		return arr;
	}

	@Override
	public char[] charArr(ByteBuffer buf) {
		int len = Ser.readNum(buf);
		char[] arr = new char[len];

		for (int i = 0; i < len; i++) {
			arr[i] = charValue(buf);
		}

		return arr;
	}

	@Override
	public int[] intArr(ByteBuffer buf) {
		int len = Ser.readNum(buf);
		int[] arr = new int[len];

		for (int i = 0; i < len; i++) {
			arr[i] = intValue(buf);
		}

		return arr;
	}

	@Override
	public long[] longArr(ByteBuffer buf) {
		int len = Ser.readNum(buf);
		long[] arr = new long[len];

		for (int i = 0; i < len; i++) {
			arr[i] = longValue(buf);
		}

		return arr;
	}

	@Override
	public float[] floatArr(ByteBuffer buf) {
		int len = Ser.readNum(buf);
		float[] arr = new float[len];

		for (int i = 0; i < len; i++) {
			arr[i] = floatValue(buf);
		}

		return arr;
	}

	@Override
	public double[] doubleArr(ByteBuffer buf) {
		int len = Ser.readNum(buf);
		double[] arr = new double[len];

		for (int i = 0; i < len; i++) {
			arr[i] = doubleValue(buf);
		}

		return arr;
	}

	@Override
	public List<?> list(ByteBuffer buf) {
		List<Object> list = U.list();
		int size = Ser.readNum(buf);

		for (int i = 0; i < size; i++) {
			list.add(create(buf));
		}

		return list;
	}

	@Override
	public Set<?> set(ByteBuffer buf) {
		Set<Object> set = U.set();
		int size = Ser.readNum(buf);

		for (int i = 0; i < size; i++) {
			set.add(create(buf));
		}

		return set;
	}

	@Override
	public Map<?, ?> map(ByteBuffer buf) {
		Map<Object, Object> map = U.map();
		int size = Ser.readNum(buf);

		for (int i = 0; i < size; i++) {
			Object key = create(buf);
			Object value = create(buf);
			map.put(key, value);
		}

		return map;
	}

	@Override
	public Object[] objectArr(ByteBuffer buf) {
		int size = Ser.readNum(buf);
		Object[] arr = new Object[size];

		for (int i = 0; i < size; i++) {
			arr[i] = create(buf);
		}

		return arr;
	}

}
