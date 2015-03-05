package org.rapidoid.reactive;

public interface Bool {

	boolean get();

	Bool xor(Bool v);

	Bool or(Bool v);

	Bool and(Bool v);

	Bool not();

}