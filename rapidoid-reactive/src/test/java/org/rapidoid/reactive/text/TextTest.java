package org.rapidoid.reactive.text;

import org.junit.Test;
import org.rapidoid.reactive.Text;
import org.rapidoid.test.TestCommons;

public class TextTest extends TestCommons {

	@Test
	public void testText() {

		SimpleText x = new SimpleText("a");
		eq(x.get(), "a");

		Text a = new SimpleText("b");

		Text y = x.plus(a);
		Text z = y.remove(a);

		eq(y.get(), "ab");
		eq(z.get(), "a");

	}

}
