package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Text;

public class PlusText extends AbstractText implements Text {

	private final Text t1;
	private final Text t2;

	public PlusText(Text t1, Text t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public String get() {
		return t1.get() + t2.get();
	}

}
