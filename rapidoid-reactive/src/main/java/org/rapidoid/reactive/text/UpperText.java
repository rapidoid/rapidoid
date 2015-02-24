package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Text;

public class UpperText extends AbstractText implements Text {

	private final Text t;

	public UpperText(Text t) {
		this.t = t;
	}

	@Override
	public String get() {
		return t.get().toUpperCase();
	}

}
