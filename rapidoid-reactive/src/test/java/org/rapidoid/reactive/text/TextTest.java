package org.rapidoid.reactive.text;

import org.junit.Test;
import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.Text;
import org.rapidoid.reactive.num.SimpleNum;
import org.rapidoid.test.TestCommons;

public class TextTest extends TestCommons {

	@Test
	public void testText() {

		SimpleText a = new SimpleText("a");
		eq(a.get(), "a");

		Num n1 = new SimpleNum(1);
		Num n3 = new SimpleNum(3);
		Num nNeg = new SimpleNum(-1);

		Text b = new SimpleText("b");
		Text c = new SimpleText("c");
		Text bc = new SimpleText("bc");
		Text d = new SimpleText("d");
		Text m = new SimpleText(" m ");
		Text abca = new SimpleText("abca");
		Text strNb = new SimpleText("17");

		Text abc = a.plus(bc);
		Text ab = abc.remove(c);
		Text ad = ab.replace(b, d);
		Text abU = ab.upper();
		Text abL = abU.lower();
		Text mT = m.trim();
		Text sub = abc.substring(n1, n3);
		Text mid = abc.mid(n1, nNeg);
		Num parse = strNb.parseInt();

		Num plusL = abc.length().plus(ab.length());
		Num minusL = plusL.minus(a.length());
		Num index = abc.indexOf(b);
		Num lastIndex = abca.lastIndexOf(a);

		eq(abc.get(), "abc");
		eq(ab.get(), "ab");
		eq(ad.get(), "ad");
		eq(abU.get(), "AB");
		eq(abL.get(), "ab");
		eq(mT.get(), "m");
		eq(sub.get(), "bc");
		eq(mid.get(), "b");

		eq(plusL.get(), 5);
		eq(minusL.get(), 4);
		eq(index.get(), 1);
		eq(lastIndex.get(), 3);
		eq(parse.get(), 17);
	}

}
