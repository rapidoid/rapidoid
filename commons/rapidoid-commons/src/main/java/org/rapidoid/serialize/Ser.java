package org.rapidoid.serialize;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.util.Msc;

import java.nio.ByteBuffer;

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
public class Ser extends RapidoidThing {

	protected static void writeNum(ByteBuffer buf, int len) {
		if (len < 255) {
			buf.put(Msc.sbyte(len));

		} else {
			buf.put(Msc.sbyte(255));
			buf.putInt(len);
		}
	}

	protected static int readNum(ByteBuffer buf) {
		int len = Msc.ubyte(buf.get());

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
