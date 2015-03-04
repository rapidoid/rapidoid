package org.rapidoid.reactive.bool;

import org.rapidoid.reactive.Bool;

public class AndBool extends AbstractBool implements Bool {

	private final Bool v1;
	private final Bool v2;

	public AndBool(Bool v1, Bool v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	@Override
	public boolean get() {
		return v1.get() && v2.get();
	}

}
