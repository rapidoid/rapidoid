package org.rapidoid.reactive;

public interface Num {

	Num plus(Num n);

	Num minus(Num n);

	Num mul(Num n);

	Num div(Num n);

	int get();

}
