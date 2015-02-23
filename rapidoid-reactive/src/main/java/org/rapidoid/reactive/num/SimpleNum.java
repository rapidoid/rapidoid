package org.rapidoid.reactive.num;

import org.rapidoid.reactive.Num;

public class SimpleNum extends AbstractNum implements Num {

	protected int value;

	@Override
	public int get() {
		return value;
	}

	public SimpleNum(int value) {
		this.value = value;
	}

	public void set(int value) {
		this.value = value;
	}

}
