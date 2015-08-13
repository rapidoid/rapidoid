package org.rapidoid.config;

/*
 * #%L
 * rapidoid-config
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski and contributors
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nikolche Mihajlovski
 * @since 4.1.0
 */
public class Config extends ConcurrentHashMap<String, Object> {

	private static final long serialVersionUID = 2218993389190953636L;

	public Config(Map<String, Object> config) {
		putAll(config);
	}

	public Config() {}

	public String option(String name) {
		Object opt = get(name);
		return opt != null ? opt.toString() : null;
	}

	public String option(String name, String defaultValue) {
		return containsKey(name) ? (String) get(name) : defaultValue;
	}

	public int option(String name, int defaultValue) {
		String n = option(name);
		return n != null ? Integer.parseInt(n) : defaultValue;
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
		Object opt = get(name);
		return opt == value || (opt != null && opt.equals(value));
	}

	public boolean has(String name) {
		return containsKey(name);
	}

	public boolean is(String name) {
		return has(name, true);
	}

	public boolean contains(String name, Object value) {
		Object opt = get(name);

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
		Map<String, Object> submap = (Map<String, Object>) get(name);

		if (submap == null) {
			submap = new Config();
			put(name, submap);
		} else if (submap instanceof Map) {
			if (!(submap instanceof Config)) {
				submap = new Config(submap);
				put(name, submap);
			}
		} else {
			throw new RuntimeException("Invalid submap type: " + submap.getClass());
		}

		return (Config) submap;
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

		return (T) cfg.get(name[name.length - 1]);
	}

}
