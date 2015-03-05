package org.rapidoid.reactive.bool;

import org.rapidoid.reactive.Bool;

public class NotBool extends AbstractBool implements Bool {

	private final Bool v;

	public NotBool(Bool v) {
		this.v = v;
	}

	@Override
	public boolean get() {
		return !v.get();
	}

}
