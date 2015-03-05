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

		Bool and1 = t.and(f);
		Bool and2 = f.and(t);
		Bool and3 = t.and(t);
		Bool and4 = f.and(f);

		Bool notT = t.not();
		Bool notF = f.not();

		eq(x1.get(), true);
		eq(x2.get(), true);
		eq(x3.get(), false);
		eq(x4.get(), false);

		eq(or1.get(), true);
		eq(or2.get(), true);
		eq(or3.get(), true);
		eq(or4.get(), false);

		eq(and1.get(), false);
		eq(and2.get(), false);
		eq(and3.get(), true);
		eq(and4.get(), false);

		eq(notT.get(), false);
		eq(notF.get(), true);

	}
}
