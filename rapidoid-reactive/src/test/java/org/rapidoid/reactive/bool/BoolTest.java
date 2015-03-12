package org.rapidoid.reactive.bool;

import org.junit.Test;
import org.rapidoid.reactive.Bool;
import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.Text;
import org.rapidoid.reactive.num.SimpleNum;
import org.rapidoid.reactive.text.SimpleText;
import org.rapidoid.test.TestCommons;

public class BoolTest extends TestCommons {

	@Test
	public void testBool() {

		Bool t = new SimpleBool(true);
		Bool f = new SimpleBool(false);

		Text abc = new SimpleText("abc");
		Text a = new SimpleText("a");
		Text bc = new SimpleText("bc");

		Num n1 = new SimpleNum(1);
		Num n2 = new SimpleNum(2);

		bool(t.xor(f), true);
		bool(f.xor(t), true);
		bool(t.xor(t), false);
		bool(f.xor(f), false);

		bool(t.or(f), true);
		bool(f.or(t), true);
		bool(t.or(t), true);
		bool(f.or(f), false);

		bool(t.and(f), false);
		bool(f.and(t), false);
		bool(t.and(t), true);
		bool(f.and(f), false);

		bool(t.not(), false);
		bool(f.not(), true);

		bool(abc.contains(a), true);

		bool(abc.endsWith(bc), true);

		bool(abc.startsWith(a), true);

		bool(abc.isEmpty(), false);

		bool(abc.eq(bc), false);

		bool(n1.eq(n2), false);

		bool(n1.gt(n2), false);

		bool(n1.gte(n1), true);

		bool(n1.lt(n2), true);

		bool(n2.lte(n2), true);

	}

	private void bool(Bool b, boolean bool) {

		Bool x = b;

		eq(x.get(), bool);
	}

}
