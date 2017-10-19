package org.rapidoid.serialize;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;

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
public class Serialize extends RapidoidThing {

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
