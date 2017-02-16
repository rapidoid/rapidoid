package org.rapidoid.serialize;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.AbstractTypeSpecificVisitor;
import org.rapidoid.cls.Cls;
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
public class TypeSpecificSerializer extends AbstractTypeSpecificVisitor<ByteBuffer, Void> {

	public void serialize(ByteBuffer buf, Object value) {
		dispatch(buf, value);
	}

	@Override
	public Void dispatch(ByteBuffer buf, Object value) {
		TypeKind kind = Cls.kindOf(value);
		buf.put(Msc.sbyte(Ser.code(kind)));
		return dispatch(buf, kind, value);
	}

	@Override
	public Void processNull(ByteBuffer buf) {
		return null; // only the kind is enough
	}

	@Override
	public Void processNone(ByteBuffer context) {
		return null; // only the kind is enough
	}

	@Override
	public Void processDeleted(ByteBuffer context) {
		return null; // only the kind is enough
	}

	@Override
	public Void processUnknown(ByteBuffer buf, Object value) {
		throw U.rte("Cannot serialize a value of type: " + Cls.of(value).getName());
	}

	/* PRIMITIVES */

	@Override
	public Void process(ByteBuffer buf, boolean value) {
		buf.put(Ser.bool2byte(value));
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, byte value) {
		buf.put(value);
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, short value) {
		buf.putShort(value);
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, char value) {
		buf.putChar(value);
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, int value) {
		buf.putInt(value);
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, long value) {
		buf.putLong(value);
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, float value) {
		buf.putFloat(value);
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, double value) {
		buf.putDouble(value);
		return null;
	}

	/* OBJECTS */

	@Override
	public Void process(ByteBuffer buf, String value) {
		Ser.writeBytes(buf, value.getBytes());
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, Date value) {
		process(buf, value.getTime());
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, UUID value) {
		process(buf, value.getMostSignificantBits());
		process(buf, value.getLeastSignificantBits());
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, TUUID value) {
		process(buf, value.time());
		process(buf, value.uuidHigh());
		process(buf, value.uuidLow());
		return null;
	}

	/* COLLECTIONS */

	@Override
	public Void process(ByteBuffer buf, List<?> list) {
		Ser.writeNum(buf, list.size());

		for (Object x : list) {
			dispatch(buf, x);
		}

		return null;
	}

	@Override
	public Void process(ByteBuffer buf, Set<?> set) {
		Ser.writeNum(buf, set.size());

		for (Object x : set) {
			dispatch(buf, x);
		}

		return null;
	}

	@Override
	public Void process(ByteBuffer buf, Map<?, ?> map) {
		Ser.writeNum(buf, map.size());

		for (Map.Entry<?, ?> e : map.entrySet()) {
			dispatch(buf, e.getKey());
			dispatch(buf, e.getValue());
		}

		return null;
	}

	/* ARRAYS */

	@Override
	public Void process(ByteBuffer buf, boolean[] arr) {
		Ser.writeNum(buf, arr.length);

		for (boolean x : arr) {
			process(buf, x);
		}

		return null;
	}

	@Override
	public Void process(ByteBuffer buf, byte[] arr) {
		Ser.writeBytes(buf, arr);
		return null;
	}

	@Override
	public Void process(ByteBuffer buf, short[] arr) {
		Ser.writeNum(buf, arr.length);

		for (short x : arr) {
			process(buf, x);
		}

		return null;
	}

	@Override
	public Void process(ByteBuffer buf, char[] arr) {
		Ser.writeNum(buf, arr.length);

		for (char x : arr) {
			process(buf, x);
		}

		return null;
	}

	@Override
	public Void process(ByteBuffer buf, int[] arr) {
		Ser.writeNum(buf, arr.length);

		for (int x : arr) {
			process(buf, x);
		}

		return null;
	}

	@Override
	public Void process(ByteBuffer buf, long[] arr) {
		Ser.writeNum(buf, arr.length);

		for (long x : arr) {
			process(buf, x);
		}

		return null;
	}

	@Override
	public Void process(ByteBuffer buf, float[] arr) {
		Ser.writeNum(buf, arr.length);

		for (float x : arr) {
			process(buf, x);
		}

		return null;
	}

	@Override
	public Void process(ByteBuffer buf, double[] arr) {
		Ser.writeNum(buf, arr.length);

		for (double x : arr) {
			process(buf, x);
		}

		return null;
	}

	@Override
	public Void processArray(ByteBuffer buf, Object[] arr) {
		Ser.writeNum(buf, arr.length);

		for (Object x : arr) {
			dispatch(buf, x);
		}

		return null;
	}

}
