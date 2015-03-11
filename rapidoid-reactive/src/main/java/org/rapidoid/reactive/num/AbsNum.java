package org.rapidoid.reactive.num;

import org.rapidoid.reactive.Num;

public class AbsNum extends AbstractNum implements Num {

	private final Num n;

	public AbsNum(Num n) {
		this.n = n;
	}

	@Override
	public int get() {
		return Math.abs(n.get());
	}

}
