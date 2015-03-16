package org.rapidoid.reactive.num;

import org.rapidoid.reactive.Bool;
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

	@Override
	public Bool eq(Num n) {
		return new EqNum(this, n);
	}

	@Override
	public Num abs() {
		return new AbsNum(this);
	}

	@Override
	public Bool gt(Num than) {
		return new GtNum(this, than);
	}

	@Override
	public Bool gte(Num than) {
		return new GteNum(this, than);
	}

	@Override
	public Bool lt(Num than) {
		return new LtNum(this, than);
	}

	@Override
	public Bool lte(Num than) {
		return new LteNum(this, than);
	}

	@Override
	public Num mod(Num n) {
		return new ModNum(this, n);
	}

}
