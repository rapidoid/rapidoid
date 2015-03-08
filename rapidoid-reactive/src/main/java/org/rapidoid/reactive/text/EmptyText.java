package org.rapidoid.reactive.text;

import org.rapidoid.reactive.Bool;
import org.rapidoid.reactive.Text;
import org.rapidoid.reactive.bool.AbstractBool;

public class EmptyText extends AbstractBool implements Bool {

	private final Text t;

	public EmptyText(Text t) {
		this.t = t;
	}

	@Override
	public boolean get() {
		return t.get().isEmpty();
	}

}
