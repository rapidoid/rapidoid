package org.rapidoid.reactive.num;

import org.rapidoid.reactive.Bool;
import org.rapidoid.reactive.Num;
import org.rapidoid.reactive.bool.AbstractBool;

public class LteNum extends AbstractBool implements Bool {

	private final Num n1;
	private final Num n2;

	public LteNum(Num n1, Num n2) {
		this.n1 = n1;
		this.n2 = n2;
	}

	@Override
	public boolean get() {
		return n1.get() <= n2.get();
	}

}
