package org.rapidoid.util;

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
import org.rapidoid.commons.Str;
import org.rapidoid.data.JSON;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;

import java.util.Arrays;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class TUUIDTest extends TestCommons {

	@Test
	public void testTUUIDs() {
		int total = 100000;

		Set<TUUID> ids = U.set();

		for (int i = 0; i < total; i++) {
			TUUID id = new TUUID();
			byte[] bytes = id.toBytes();

			TUUID id2 = TUUID.fromBytes(bytes);
			byte[] bytes2 = id2.toBytes();

			eq(id, id2);
			eq(bytes.length, 24);
			isTrue(Arrays.equals(bytes, bytes2));

			String s1 = id.toString();
			String s2 = id2.toString();

			eq(s1, s2);

			TUUID id3 = TUUID.fromString(s1);
			eq(id, id3);

			ids.add(id);
		}

		eq(ids.size(), total);

		eq(new TUUID(0, 0, 0).toString(), "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
		eq(new TUUID(100000000000L, 200000000L, 3000000L).toString(), "AAAAF0h26AAAAAAAC-vCAAAAAAAALcbA");

		for (String chr : U.list("-", "_", "0", "6", "9", "A", "F", "K", "M", "X")) {
			String id = Str.mul(chr, 32);
			eq(TUUID.fromString(id).toString(), id);
		}
	}

	@Test
	public void testTUUIDasJSON() {
		String json = "{\"x\":\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}";
		eq(JSON.stringify(U.map("x", new TUUID(0, 0, 0))), json);
		eq(JSON.parse(json), U.map("x", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
		eq(JSON.parse(json, BeanWithId.class).x, new TUUID(0, 0, 0));
	}

	public static class BeanWithId {
		public TUUID x;
	}
}
