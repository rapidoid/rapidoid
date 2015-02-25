package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Text;

public class ReplaceText extends AbstractText implements Text {

	private final Text t1;
	private final Text t2;
	private final Text t3;

	public ReplaceText(Text t1, Text t2, Text t3) {
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
	}

	@Override
	public String get() {
		return t1.get().replace(t2.get(), t3.get());
	}

}
