package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Text;

public class RemoveText extends AbstractText implements Text {

	private final Text t1;
	private final Text t2;

	public RemoveText(Text t1, Text t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public String get() {
		return t1.get().replace(t2.get(), "");
	}

}
