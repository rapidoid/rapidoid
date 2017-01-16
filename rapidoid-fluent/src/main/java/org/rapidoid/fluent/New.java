package org.rapidoid.fluent;

/*
 * #%L
 * rapidoid-fluent
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

import java.util.*;

/**
 * @author Nikolche Mihajlovski
 * @since 5.0.2
 */
public class New {

	@SafeVarargs
	@SuppressWarnings({"varargs", "unchecked"})
	public static <T> T[] array(T... items) {
		return items;
	}

	public static Object[] array(Iterable<?> items) {
		return (items instanceof Collection) ? ((Collection<?>) items).toArray() : list(items).toArray();
	}

	public static <T> Set<T> set() {
		return new LinkedHashSet<T>();
	}

	public static <T> Set<T> set(Iterable<? extends T> values) {
		Set<T> set = set();

		for (T val : values) {
			set.add(val);
		}

		return set;
	}

	@SafeVarargs
	@SuppressWarnings({"varargs", "unchecked"})
	public static <T> Set<T> set(T... values) {
		Set<T> set = set();

		for (T val : values) {
			set.add(val);
		}

		return set;
	}

	public static <T> List<T> list() {
		return new ArrayList<T>();
	}

	public static <T> List<T> list(Iterable<? extends T> values) {
		List<T> list = list();

		for (T item : values) {
			list.add(item);
		}

		return list;
	}

	@SafeVarargs
	@SuppressWarnings({"varargs", "unchecked"})
	public static <T> List<T> list(T... values) {
		List<T> list = list();

		for (T item : values) {
			list.add(item);
		}

		return list;
	}

	public static <K, V> Map<K, V> map() {
		return new LinkedHashMap<K, V>();
	}

	public static <K, V> Map<K, V> map(Map<? extends K, ? extends V> src) {
		Map<K, V> map = map();
		map.putAll(src);
		return map;
	}

	public static <K, V> Map<K, V> map(K key, V value) {
		Map<K, V> map = map();
		map.put(key, value);
		return map;
	}

	public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2) {
		Map<K, V> map = map(key1, value1);
		map.put(key2, value2);
		return map;
	}

	public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3) {
		Map<K, V> map = map(key1, value1, key2, value2);
		map.put(key3, value3);
		return map;
	}

	public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
		Map<K, V> map = map(key1, value1, key2, value2, key3, value3);
		map.put(key4, value4);
		return map;
	}

	public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5,
	                                   V value5) {
		Map<K, V> map = map(key1, value1, key2, value2, key3, value3, key4, value4);
		map.put(key5, value5);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> map(Object... keysAndValues) {
		checkArg(keysAndValues.length % 2 == 0, "Expected even number of arguments (key-value pairs)!");

		Map<K, V> map = map();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			map.put((K) keysAndValues[i * 2], (V) keysAndValues[i * 2 + 1]);
		}

		return map;
	}

	private static void checkArg(boolean expectedCondition, String message) {
		if (!expectedCondition) {
			throw new IllegalArgumentException(message);
		}
	}

}
