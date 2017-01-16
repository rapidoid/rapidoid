package org.rapidoid.env;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2017 Nikolche Mihajlovski and contributors
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

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.collection.Coll;
import org.rapidoid.u.U;

import java.util.Collections;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("5.2.5")
public class EnvProperties extends RapidoidThing {

	final Map<String, Object> props = Coll.synchronizedMap();

	public EnvProperties() {
		init();
	}

	private void init() {
		for (Map.Entry<String, String> e : System.getenv().entrySet()) {
			set(e.getKey(), e.getValue());
		}

		for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
			set(U.str(e.getKey()), e.getValue());
		}
	}

	private void set(String key, Object value) {
		String propKey = normalize(U.str(key));
		props.put(propKey, value);
	}

	public Object get(String key) {
		U.notNull(key, "key");

		return props.get(normalize(key));
	}

	public boolean has(String key, Object value) {
		return String.valueOf(get(key)).equalsIgnoreCase(String.valueOf(value));
	}

	private String normalize(String key) {
		return key.toUpperCase().replace('.', '_');
	}

	@Override
	public String toString() {
		return props.toString();
	}

	public Map<String, Object> asMap() {
		return Collections.unmodifiableMap(props);
	}

	public boolean hasPrefix(String prefix) {
		prefix = normalize(prefix);

		for (String key : props.keySet()) {
			if (key.startsWith(prefix)) return true;
		}

		return false;
	}
}
