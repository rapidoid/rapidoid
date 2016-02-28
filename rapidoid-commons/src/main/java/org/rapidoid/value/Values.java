package org.rapidoid.value;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class Values {

	public static <T> Value<T> wrap(ValueStore<T> store) {
		return new ValueImpl<T>(store);
	}

	public static <T> Value<T> of(T value) {
		return wrap(new SimpleValueStore<T>(value));
	}

	public static Value<String> none() {
		return of(null);
	}

}
