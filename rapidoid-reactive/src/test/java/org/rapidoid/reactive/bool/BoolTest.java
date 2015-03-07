package org.rapidoid.reactive.bool;

import org.junit.Test;
import org.rapidoid.reactive.Bool;
import org.rapidoid.reactive.Text;
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

	}

	private void bool(Bool b, boolean bool) {

		Bool x = b;

		eq(x.get(), bool);
	}

}
