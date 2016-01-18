package org.rapidoid.config;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class Config {

	private final Map<String, Object> properties;

	public Config(Map<String, Object> configProperties) {
		this.properties = Collections.synchronizedMap(configProperties);
	}

	public Config() {
		this(U.<String, Object> map());
	}

	public String option(String name) {
		Object opt = properties.get(name);
		return opt != null ? U.str(opt) : null;
	}

	public String option(String name, String defaultValue) {
		Object obj = properties.get(name);
		return obj != null ? U.str(obj) : defaultValue;
	}

	public int option(String name, int defaultValue) {
		String n = option(name);
		return n != null ? U.num(n) : defaultValue;
	}

	public long option(String name, long defaultValue) {
		String n = option(name);
		return n != null ? Long.parseLong(n) : defaultValue;
	}

	public double option(String name, double defaultValue) {
		String n = option(name);
		return n != null ? Double.parseDouble(n) : defaultValue;
	}

	public boolean has(String name, Object value) {
		Object val = properties.get(name);
		return U.eq(val, value);
	}

	public boolean has(String name) {
		return properties.containsKey(name);
	}

	public boolean is(String name) {
		return has(name, true);
	}

	public boolean contains(String name, Object value) {
		Object opt = properties.get(name);

		if (opt != null) {
			if (opt instanceof Collection) {
				return ((Collection<?>) opt).contains(value);
			} else {
				throw new RuntimeException("Expected collection for config entry: " + name);
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public synchronized Config sub(String name) {
		Map<String, Object> submap = (Map<String, Object>) properties.get(name);

		if (submap == null) {
			submap = U.map();
			put(name, submap);
		} else if (!(submap instanceof Map)) {
			throw new RuntimeException("Invalid submap type: " + submap.getClass());
		}

		return new Config(submap);
	}

	public ConfigEntry entry(String... name) {
		return new ConfigEntry(this, name);
	}

	@SuppressWarnings("unchecked")
	public <T> T nested(String... name) {
		Config cfg = this;
		for (int i = 0; i < name.length - 1; i++) {
			cfg = cfg.sub(name[i]);

			if (cfg == null) {
				return null;
			}
		}

		return (T) cfg.option(name[name.length - 1]);
	}

	public Map<String, Object> toMap() {
		return U.map(properties);
	}

	public void clear() {
		properties.clear();
	}

	public void put(String key, Object value) {
		properties.put(key, value);
	}

	public void remove(String name) {
		properties.remove(name);
	}

	public void assign(Map<String, Object> newProperties) {
		synchronized (properties) {
			properties.clear();
			properties.putAll(newProperties);
		}
	}

	@Override
	public String toString() {
		return properties.toString();
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) properties.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T getOrFail(String key, Class<T> clazz) {
		T val = (T) properties.get(key);
		U.must(val != null, "Cannot find the configuration entry: %s", key);
		U.must(Cls.instanceOf(val, clazz), "The configuration entry '%s' must be of type: %s", key,
				clazz.getSimpleName());
		return val;
	}

	public boolean isEmpty() {
		return properties.isEmpty();
	}

}
