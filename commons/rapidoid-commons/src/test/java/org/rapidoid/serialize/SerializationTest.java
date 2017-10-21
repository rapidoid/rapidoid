package org.rapidoid.serialize;

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

import org.junit.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.data.JSON;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;
import org.rapidoid.util.Deleted;
import org.rapidoid.util.None;
import org.rapidoid.util.TUUID;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class SerializationTest extends AbstractCommonsTest {

	@Test
	public void testMiniSerialization() {
		ByteBuffer buf = ByteBuffer.allocateDirect(100);
		Map<?, ?> data = U.map("a", 213, true, "xyz", "f", None.NONE, "g", Deleted.DELETED);

		Serialize.serialize(buf, data);
		buf.rewind();
		Object data2 = Serialize.deserialize(buf);

		String expected = data.toString();
		String real = data2.toString();

		eq(real, expected);
	}

	@Test
	public void testSerialization() {
		ByteBuffer buf = ByteBuffer.allocateDirect(2000);

		Map<?, ?> sub1 = Coll.synchronizedMap();
		fillInData((Map<Object, Object>) sub1);

		Map<?, ?> data = U.map(123, "Foo", "y", U.list(1, "Bar", new int[]{1, 500, 10000}), "sub1", sub1);
		fillInData((Map<Object, Object>) data);

		Serialize.serialize(buf, data);
		buf.rewind();
		Object data2 = Serialize.deserialize(buf);

		String expected = JSON.prettify(data);
		String real = JSON.prettify(data2);

		eq(real, expected);
	}

	private void fillInData(Map<Object, Object> data) {
		data.put("NULL", null);

		data.put("BOOLEAN", true);
		data.put("BYTE", 12);
		data.put("SHORT", 12345);
		data.put("CHAR", 'M');
		data.put("INT", 12345657);
		data.put("LONG", 1234567890);
		data.put("FLOAT", 123.45);
		data.put("DOUBLE", 123123.456346);

		data.put("STRING", "ABCDE foo bar !=-");
		data.put("DATE", new Date());
		data.put("UUID", uuid(1));

		data.put("TUUID1", new TUUID(100, 200, 300));
		data.put("TUUID2", new TUUID(230523650259L, -12304923697L, 12930175223L));

		data.put("BOOLEAN_ARR", new boolean[]{true, false, true});
		data.put("BYTE_ARR", new byte[]{10, 20, 30});
		data.put("SHORT_ARR", new short[]{10, 20, 30});
		data.put("CHAR_ARR", new char[]{'A', 20, 'B'});
		data.put("INT_ARR", new int[]{10, 20, 30});
		data.put("LONG_ARR", new long[]{10, 20, 30});
		data.put("FLOAT_ARR", new float[]{10, 20, 30});
		data.put("DOUBLE_ARR", new double[]{10, 20, 30});

		data.put("SHORT_ARR", new short[][][]{null, {null, {10, 20, 30}}});
		data.put("CHAR_ARR", new char[][][][][]{{{{{'X', 0, 'Z'}, null}, null, null}, null}, null});

		data.put("BOOLEAN_OBJ_ARR", new Boolean[]{true, null, false});
		data.put("BYTE_OBJ_ARR", new Byte[]{10, null, 20, 30});
		data.put("SHORT_OBJ_ARR", new Short[]{10, 20, null, 30});
		data.put("CHAR_OBJ_ARR", new Character[]{10, 20, 30, null});
		data.put("INT_OBJ_ARR", new Integer[]{10, null, 20, 30});
		data.put("LONG_OBJ_ARR", new Long[]{100000000L, null, 200000000L, 300000000L});
		data.put("FLOAT_OBJ_ARR", new Float[]{11.1f, null, 2222.2222f, 333.333f});
		data.put("DOUBLE_OBJ_ARR", new Double[]{1111d, 2020.23, 444.444, null});

		data.put("STRING_ARR", new String[][]{{"a", "bb", null, "ccc"}, {"foo", "bar"}, {""}});
		data.put("OBJECT_ARR", new Object[]{10, 20, 30, null});
		data.put("DATE_ARR", new Date[]{new Date(1020304050), null, new Date(555555555)});
		data.put("UUID_ARR", new java.util.UUID[]{uuid(1), null, uuid(2)});

		data.put("list", U.list(10, 20, U.set(30, null, "Asd", U.set("g", null))));
		data.put("set", U.set(100, null, 200, 300));
		data.put("map", U.list(10, "asd", true, null, "f", 30.123));
	}

	private UUID uuid(int num) {
		return UUID.nameUUIDFromBytes(("UUID" + num).getBytes());
	}

}
