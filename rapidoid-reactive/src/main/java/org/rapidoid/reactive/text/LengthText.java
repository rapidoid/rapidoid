package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.Text;
import org.rapidoid.reactive.num.AbstractNum;

public class LengthText extends AbstractNum implements Num {

	private final Text t;

	public LengthText(Text t) {
		this.t = t;
	}

	@Override
	public int get() {
		return t.get().length();
	}

}
