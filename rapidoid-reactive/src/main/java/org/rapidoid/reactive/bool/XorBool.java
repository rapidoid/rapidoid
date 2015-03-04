package org.rapidoid.reactive.bool;

import org.rapidoid.reactive.Bool;

public class XorBool extends AbstractBool implements Bool {

	private final Bool v1;
	private final Bool v2;

	public XorBool(Bool v1, Bool v2) {
		this.v1 = v1;
		this.v2 = v2;
	}

	@Override
	public boolean get() {
		return (v1.get() && !v2.get()) || (!v1.get() && v2.get());
	}

}
