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
		Text b = new SimpleText("b");
		Text d = new SimpleText("d");

		Text abc = a.plus(bc);
		Text ab = abc.remove(c);
		Text ad = ab.replace(b, d);
		Text abU = ab.upper();
		Text abL = abU.lower();

		eq(abc.get(), "abc");
		eq(ab.get(), "ab");
		eq(ad.get(), "ad");
		eq(abU.get(), "AB");
		eq(abL.get(), "ab");

	}

}
