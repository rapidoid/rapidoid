package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Text;

public class LowerText extends AbstractText implements Text {

	private final Text t;

	public LowerText(Text t) {
		this.t = t;
	}

	@Override
	public String get() {
		return t.get().toLowerCase();
	}

}
