package org.rapidoid.value;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public interface ValueStore<T> {

	T get();

	void set(T value);

	String desc();

}
