package org.rapidoid.reactive.bool;

import org.rapidoid.reactive.Bool;

public abstract class AbstractBool implements Bool {

	@Override
	public Bool xor(Bool v) {
		return new XorBool(this, v);
	}

}
