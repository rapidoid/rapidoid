package org.rapidoid.beany;

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
import org.rapidoid.u.U;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

import java.util.Map;

enum ABC {
	A, B, C
};

class Foo {

	public int x = 12;

	public String g = "gg";

	public ABC abc = ABC.B;

	int abcd = 111;

	@SuppressWarnings("unused")
	private int invisible2 = 111;

	protected int invisible3 = 111;
}

class Bar {
	public final Foo ff = new Foo();
}

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class BeanySerializationTest extends BeanyTestCommons {

	@Test
	public void testPrimitivesSerialization() {
		eq(Beany.serialize(123), 123);
		eq(Beany.serialize(324L), 324L);
		eq(Beany.serialize("bb"), "bb");
		eq(Beany.serialize(true), true);
		eq(Beany.serialize(null), null);
	}

	@Test
	public void testSetSerialization() {
		eq(Beany.serialize(U.set(1, 3, 5)), U.set(1, 3, 5));
	}

	@Test
	public void testListSerialization() {
		eq(Beany.serialize(U.list("a", "b", "c")), U.list("a", "b", "c"));
	}

	@Test
	public void testMapSerialization() {
		eq(Beany.serialize(U.map("a", 123, "b", 56)), U.map("a", 123, "b", 56));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testArraySerialization() {
		eq((Object[]) Beany.serialize(U.array("f", 3, true)), U.array("f", 3, true));

		int[] a1 = {1, 2, 3};
		int[] a2 = {1, 2, 3};

		eq((int[]) Beany.serialize(a1), a2);
	}

	@Test
	public void testBeanSerialization() {
		Map<String, ? extends Object> foo = U.map("x", 12, "g", "gg", "abc", ABC.B, "abcd", 111);
		Map<String, ? extends Object> bar = U.map("ff", foo);

		eq(Beany.serialize(new Foo()), foo);
		eq(Beany.serialize(new Bar()), bar);
	}

	@Test
	public void testPlainObjectSerialization() {
		eq(Beany.serialize(new Object()), U.map());
	}

	@Test
	public void testVarsSerialization() {
		Var<?> var = Vars.var("abc", 123);
		Object ser = Beany.serialize(var);
		eq(ser, U.map("abc", 123));
	}

}
