package org.rapidoid.reactive.bool;

import org.rapidoid.reactive.Bool;

public class SimpleBool extends AbstractBool implements Bool {

	private boolean value;

	public SimpleBool(boolean value) {
		this.value = value;
	}

	@Override
	public boolean get() {
		return value;
	}

}
