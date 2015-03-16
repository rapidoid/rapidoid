package org.rapidoid.reactive.num;

import org.junit.Test;
import org.rapidoid.reactive.Num;
import org.rapidoid.test.TestCommons;

public class NumTest extends TestCommons {

	@Test
	public void testNum() {

		SimpleNum x = new SimpleNum(10);
		eq(x.get(), 10);

		Num v5 = new SimpleNum(5);
		Num v2 = new SimpleNum(2);
		Num neg2 = new SimpleNum(-2);

		Num y = x.plus(v5);
		Num z = x.minus(v5);
		Num w = z.mul(v2);
		Num q = w.div(v2);
		Num n = x.plus(y);
		Num absN = neg2.abs();
		Num mod = v5.mod(v2);

		eq(y.get(), 15);
		eq(z.get(), 5);
		eq(w.get(), 10);
		eq(q.get(), 5);
		eq(n.get(), 25);

		x.set(200);

		eq(x.get(), 200);
		eq(y.get(), 205);
		eq(z.get(), 195);

		eq(w.get(), 390);
		eq(q.get(), 195);
		eq(absN.get(), 2);
		eq(mod.get(), 1);

	}

}
