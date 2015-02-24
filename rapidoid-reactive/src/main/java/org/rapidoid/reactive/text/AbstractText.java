package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Text;

public abstract class AbstractText implements Text {

	@Override
	public Text plus(Text v) {
		return new PlusText(this, v);
	}

	@Override
	public Text remove(Text v) {
		return new RemoveText(this, v);
	}

}
