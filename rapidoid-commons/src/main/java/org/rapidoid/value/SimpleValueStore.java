package org.rapidoid.value;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class SimpleValueStore<T> implements ValueStore<T> {

	private volatile T value;

	public SimpleValueStore(T value) {
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public void set(T value) {
		this.value = value;
	}

	@Override
	public String desc() {
		return null;
	}

}
