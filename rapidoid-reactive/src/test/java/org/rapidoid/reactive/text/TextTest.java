package org.rapidoid.reactive.text;

import org.junit.Test;
import org.rapidoid.reactive.Text;
import org.rapidoid.test.TestCommons;

public class TextTest extends TestCommons {

	@Test
	public void testText() {

		SimpleText a = new SimpleText("a");
		eq(a.get(), "a");

		Text bc = new SimpleText("bc");
		Text c = new SimpleText("c");
		Text mn1 = new SimpleText("mn");

		Text abc = a.plus(bc);
		Text ab = abc.remove(c);
		Text mn2 = ab.replace(mn1);
		Text abU = ab.upper();

		eq(abc.get(), "abc");
		eq(ab.get(), "ab");
		eq(mn2.get(), "mn");
		eq(abU.get(), "AB");
	}

}
