package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.Text;

public class SubstringText extends AbstractText implements Text {

	private final Text t;
	private final Num beginIndex;
	private final Num endIndex;

	public SubstringText(Text t, Num beginIndex, Num endIndex) {
		this.t = t;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}

	@Override
	public String get() {
		return t.get().substring(beginIndex.get(), endIndex.get());
	}

}
