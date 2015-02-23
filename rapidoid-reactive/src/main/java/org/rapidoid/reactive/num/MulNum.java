package org.rapidoid.reactive.num;

import org.rapidoid.reactive.Num;

public class MulNum extends AbstractNum implements Num {

	private final Num n1;
	private final Num n2;

	public MulNum(Num n1, Num n2) {
		this.n1 = n1;
		this.n2 = n2;
	}

	@Override
	public int get() {
		return n1.get() * n2.get();
	}

}
