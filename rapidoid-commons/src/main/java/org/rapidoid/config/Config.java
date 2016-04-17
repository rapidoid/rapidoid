package org.rapidoid.config;

import org.rapidoid.RapidoidThing;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Arr;
import org.rapidoid.commons.Coll;
import org.rapidoid.lambda.ToMap;
import org.rapidoid.u.U;
import org.rapidoid.value.Value;
import org.rapidoid.value.Values;

import java.util.*;

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

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class Config extends RapidoidThing implements ToMap<String, Object> {

	private final Map<String, Object> properties;

	private final List<String> baseKeys;

	private final Config root;

	private final boolean isRoot;

	private volatile String[] args;

	private Config(Map<String, Object> properties, List<String> baseKeys, Config root) {
		this.properties = properties;
		this.root = root;
		this.baseKeys = Collections.unmodifiableList(U.list(baseKeys));
		this.isRoot = false;
	}

	public Config() {
		this.properties = Coll.synchronizedMap();
		this.root = this;
		this.baseKeys = U.list();
		this.isRoot = true;
	}

	public Value<Object> entry(String key) {
		return Values.wrap(new ConfigValueStore<Object>(this, key));
	}

	private List<String> keyChain(Iterator<String> keys) {
		List<String> keyChain = U.list(this.baseKeys);

		while (keys.hasNext()) {
			String key = keys.next();
			U.notNull(key, "config key");
			Collections.addAll(keyChain, key.split("\\."));
		}

		return keyChain;
	}

	@SuppressWarnings("unchecked")
	public Config sub(String... keys) {
		U.must(U.notEmpty(keys), "Keys must be specified!");
		return new Config(properties, keyChain(U.iterator(keys)), root());
	}

	public Config sub(List<String> keys) {
		U.must(U.notEmpty(keys), "Keys must be specified!");
		return new Config(properties, keyChain(keys.iterator()), root());
	}

	public Object get(String key) {
		Object value;

		synchronized (properties) {
			value = asMap().get(key);
		}

		return value != null ? value : global(key);
	}

	private String global(String key) {
		String fullKey = fullKey(key, ".");

		String value = System.getProperty(fullKey);

		if (value == null) {
			value = System.getenv(fullKey);
		}

		if (value == null) {
			value = System.getenv(fullKey(key, "_").toUpperCase());
		}

		if (value == null) {
			value = System.getenv(fullKey(key, "_").toLowerCase());
		}

		return value;
	}

	private String fullKey(String key, String separator) {
		return U.join(separator, baseKeys) + separator + key;
	}

	public boolean has(String key) {
		synchronized (properties) {
			return asMap().containsKey(key);
		}
	}

	public boolean is(String key) {
		Object value;

		synchronized (properties) {
			value = asMap().get(key);
		}

		return Boolean.TRUE.equals(Cls.convert(value, Boolean.class));
	}

	@Override
	public Map<String, Object> toMap() {
		return Collections.unmodifiableMap(asMap());
	}

	private Map<String, Object> asMap() {
		if (isRoot) {
			return properties;

		} else {
			synchronized (properties) {
				Map<String, Object> props = properties;

				for (String key : baseKeys) {
					Object value = props.get(key);

					if (value == null) {
						value = Coll.synchronizedMap();
						props.put(key, value);
					}

					if (value instanceof Map<?, ?>) {
						props = (Map<String, Object>) value;
					} else {
						throw U.rte("Expected a Map for configuration section '%s', but found value of type: %s",
								sectionTo(key), value.getClass().getSimpleName());
					}
				}

				return props;
			}
		}
	}

	private String sectionTo(String toKey) {
		String section = "";

		for (String key : baseKeys) {
			if (!section.isEmpty()) {
				section += ".";
			}

			section += key;

			if (key.equals(toKey)) {
				break;
			}
		}

		return section;
	}

	public void clear() {
		if (isRoot) {
			properties.clear();
		} else {
			synchronized (properties) {
				asMap().clear();
			}
		}
	}

	public void delete() {
		if (isRoot) {
			properties.clear();
		} else {
			synchronized (properties) {
				parent().remove(lastBaseKey());
			}
		}
	}

	private String lastBaseKey() {
		return baseKeys.get(baseKeys.size() - 1);
	}

	public void set(String key, Object value) {
		synchronized (properties) {
			asMap().put(key, value);
		}
	}

	public void remove(String key) {
		synchronized (properties) {
			asMap().remove(key);
		}
	}

	public void assign(Map<String, Object> entries) {
		synchronized (properties) {
			clear();
			update(entries);
		}
	}

	public boolean isEmpty() {
		synchronized (properties) {
			return asMap().isEmpty();
		}
	}

	public void update(Map<String, ?> entries) {
		synchronized (properties) {
			Map<String, Object> conf = asMap();

			for (Map.Entry<String, ?> e : entries.entrySet()) {
				String name = e.getKey();
				Object value = e.getValue();

				if (value instanceof Map<?, ?>) {
					sub(name).update((Map<String, ?>) value);
				} else {
					conf.put(name, value);
				}
			}
		}
	}

	@Override
	public String toString() {
		synchronized (properties) {
			return asMap().toString();
		}
	}

	public void args(String... args) {
		this.args = args;

		if (args != null) {
			for (String arg : args) {
				String[] parts = arg.split("=", 2);

				if (parts.length > 1) {
					setNested(parts[0], parts[1]);
				} else {
					setNested(parts[0], true);
				}
			}
		}
	}

	private void setNested(String key, Object value) {
		String[] keys = key.split("\\.");
		Config cfg = keys.length > 1 ? sub(Arr.sub(keys, 0, -1)) : this;
		cfg.set(U.last(keys), value);
	}

	public String[] getArgs() {
		return args;
	}

	public Config root() {
		return root;
	}

	public Config parent() {
		return isRoot ? null : root.sub(baseKeys.subList(0, baseKeys.size() - 1));
	}

	public List<String> keys() {
		return baseKeys;
	}

	public Map<String, String> toFlatMap() {
		Map<String, String> flatMap = U.map();
		Map<String, Object> map = toMap();

		traverseToFlat(map, U.list(keys()), flatMap);

		return flatMap;
	}

	private static void traverseToFlat(Map<String, Object> map, List<String> keys, Map<String, String> flatMap) {
		for (Map.Entry<String, Object> e : map.entrySet()) {
			String key = e.getKey();
			Object val = e.getValue();

			if (val instanceof Map<?, ?>) {
				Map<String, Object> mapVal = (Map<String, Object>) val;
				List<String> keys2 = U.list(keys);
				keys2.add(key);
				traverseToFlat(mapVal, keys2, flatMap);

			} else {
				flatMap.put(U.join(".", keys) + "." + key, String.valueOf(val));
			}
		}
	}

	public Properties toProperties() {
		Properties props = new Properties();
		props.putAll(toFlatMap());
		return props;
	}
}
