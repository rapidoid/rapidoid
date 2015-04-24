package org.rapidoid.dict;

/*
 * #%L
 * rapidoid-dict
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.util.Map;

/**
 * @author Nikolche Mihajlovski
 * @since 2.4.0
 */
public class Dicts {

	public static Dict dict(Map<? extends String, ? extends Object> src) {
		Dict dict = dict();
		dict.putAll(src);
		return dict;
	}

	public static Dict dict() {
		return new HashDict();
	}

	public static Dict dict(String key, Object value) {
		Dict dict = dict();
		dict.put(key, value);
		return dict;
	}

	public static Dict dict(String key1, Object value1, String key2, Object value2) {
		Dict dict = dict(key1, value1);
		dict.put(key2, value2);
		return dict;
	}

	public static Dict dict(String key1, Object value1, String key2, Object value2, String key3, Object value3) {
		Dict dict = dict(key1, value1, key2, value2);
		dict.put(key3, value3);
		return dict;
	}

	public static Dict dict(String key1, Object value1, String key2, Object value2, String key3, Object value3,
			String key4, Object value4) {
		Dict dict = dict(key1, value1, key2, value2, key3, value3);
		dict.put(key4, value4);
		return dict;
	}

	public static Dict dict(String key1, Object value1, String key2, Object value2, String key3, Object value3,
			String key4, Object value4, String key5, Object value5) {
		Dict dict = dict(key1, value1, key2, value2, key3, value3, key4, value4);
		dict.put(key5, value5);
		return dict;
	}

	public static Dict dict(Object... keysAndValues) {
		if (keysAndValues.length % 2 != 0) {
			throw new IllegalArgumentException("Incorrect number of arguments (expected key-value pairs)!");
		}

		Dict dict = dict();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			dict.put((String) keysAndValues[i * 2], (Object) keysAndValues[i * 2 + 1]);
		}

		return dict;
	}

}
