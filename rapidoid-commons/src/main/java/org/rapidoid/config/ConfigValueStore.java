package org.rapidoid.config;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.u.U;
import org.rapidoid.value.ValueStore;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ConfigValueStore<T> implements ValueStore<T> {

	private final Config config;

	private final String key;

	public ConfigValueStore(Config config, String key) {
		this.config = config;
		this.key = key;
	}

	@Override
	public T get() {
		return (T) config.get(key);
	}

	@Override
	public void set(T value) {
		config.set(key, value);
	}

	@Override
	public String desc() {
		return U.join(".", config.keys()) + "." + key;
	}

}
