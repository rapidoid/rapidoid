package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.Text;
import org.rapidoid.reactive.num.AbstractNum;

public class ParseIntText extends AbstractNum implements Num {
	private final Text t;

	public ParseIntText(Text t) {
		this.t = t;
	}

	@Override
	public int get() {
		return Integer.parseInt(t.get());
	}

}
