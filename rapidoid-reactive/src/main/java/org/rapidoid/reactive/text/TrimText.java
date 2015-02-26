package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Text;

public class TrimText extends AbstractText implements Text {

	private final Text t;

	public TrimText(Text t) {
		this.t = t;
	}

	@Override
	public String get() {
		return t.get().trim();
	}

}
