package org.rapidoid.reactive.bool;

import org.junit.Test;
import org.rapidoid.reactive.Bool;
import org.rapidoid.test.TestCommons;

public class BoolTest extends TestCommons {

	@Test
	public void testBool() {

		Bool t = new SimpleBool(true);
		Bool f = new SimpleBool(false);

		Bool x1 = t.xor(f);
		Bool x2 = f.xor(t);
		Bool x3 = t.xor(t);
		Bool x4 = f.xor(f);

		Bool or1 = t.or(f);
		Bool or2 = f.or(t);
		Bool or3 = t.or(t);
		Bool or4 = f.or(f);

		eq(x1.get(), true);
		eq(x2.get(), true);
		eq(x3.get(), false);
		eq(x4.get(), false);

		eq(or1.get(), true);
		eq(or2.get(), true);
		eq(or3.get(), true);
		eq(or4.get(), false);

	}
}
