package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.Text;
import org.rapidoid.reactive.num.SimpleNum;

public class MidText extends AbstractText implements Text {

	private final Text t;
	private final Num beginIndex;
	private Num endIndex;

	public MidText(Text t, Num beginIndex, Num endIndex) {
		this.t = t;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}

	@Override
	public String get() {
		if (endIndex.get() < 0) {
			endIndex = new SimpleNum(t.get().length() + endIndex.get());
		}
		return t.get().substring(beginIndex.get(), endIndex.get());
	}

}
