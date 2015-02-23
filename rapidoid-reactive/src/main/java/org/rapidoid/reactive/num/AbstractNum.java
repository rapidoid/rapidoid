package org.rapidoid.reactive.num;

import org.rapidoid.reactive.Num;

public abstract class AbstractNum implements Num {

	@Override
	public Num plus(Num n) {
		return new PlusNum(this, n);
	}

	@Override
	public Num minus(Num n) {
		return new MinusNum(this, n);
	}

	@Override
	public Num mul(Num n) {
		return new MulNum(this, n);
	}

	@Override
	public Num div(Num n) {
		return new DivNum(this, n);
	}

}
