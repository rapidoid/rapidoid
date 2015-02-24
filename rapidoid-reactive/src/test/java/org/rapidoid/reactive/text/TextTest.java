package org.rapidoid.reactive.text;

import org.junit.Test;
import org.rapidoid.reactive.Text;
import org.rapidoid.test.TestCommons;

public class TextTest extends TestCommons {

	@Test
	public void testText() {

		SimpleText a = new SimpleText("a");
		eq(a.get(), "a");

		Text b = new SimpleText("b");
		Text bc = new SimpleText("bc");
		Text c = new SimpleText("c");

		Text abc = a.plus(bc);
		Text ab = abc.remove(c);
		Text ad = ab.replace(b);

		eq(abc.get(), "abc");
		eq(ab.get(), "ab");
		eq(ad.get(), "ad");

	}

}
