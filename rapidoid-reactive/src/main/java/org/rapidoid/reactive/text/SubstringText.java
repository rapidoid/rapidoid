package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Text;

public class SubstringText extends AbstractText implements Text {

	private final Text t;
	private final int beginIndex;
	private final int endIndex;

	public SubstringText(Text t, int beginIndex, int endIndex) {
		this.t = t;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}

	@Override
	public String get() {
		return t.get().substring(beginIndex, endIndex);
	}

}
