package org.rapidoid.util;

/*
 * #%L
 * rapidoid-u
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class U {

	// regex taken from
	// http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
	private static Pattern CAMEL_SPLITTER_PATTERN = Pattern
			.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])");

	private U() {
	}

	public static String text(Object obj) {
		if (obj == null) {
			return "null";
		} else if (obj instanceof byte[]) {
			return Arrays.toString((byte[]) obj);
		} else if (obj instanceof short[]) {
			return Arrays.toString((short[]) obj);
		} else if (obj instanceof int[]) {
			return Arrays.toString((int[]) obj);
		} else if (obj instanceof long[]) {
			return Arrays.toString((long[]) obj);
		} else if (obj instanceof float[]) {
			return Arrays.toString((float[]) obj);
		} else if (obj instanceof double[]) {
			return Arrays.toString((double[]) obj);
		} else if (obj instanceof boolean[]) {
			return Arrays.toString((boolean[]) obj);
		} else if (obj instanceof char[]) {
			return Arrays.toString((char[]) obj);
		} else if (obj instanceof Object[]) {
			return text((Object[]) obj);
		} else {
			return String.valueOf(obj);
		}
	}

	public static String text(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < objs.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(text(objs[i]));
		}

		sb.append("]");

		return sb.toString();
	}

	public static RuntimeException rte(String message, Object... args) {
		return new RuntimeException(readable(message, args));
	}

	public static RuntimeException rte(Throwable cause) {
		return new RuntimeException(cause);
	}

	public static RuntimeException rte(String message) {
		return new RuntimeException(message);
	}

	public static RuntimeException notExpected() {
		return rte("This operation is not expected to be called!");
	}

	public static IllegalArgumentException illegalArg(String message) {
		return new IllegalArgumentException(message);
	}

	public static <T> T or(T value, T fallback) {
		return value != null ? value : fallback;
	}

	public static String format(String s, Object... args) {
		return String.format(s, args);
	}

	public static String readable(String format, Object... args) {

		for (int i = 0; i < args.length; i++) {
			args[i] = text(args[i]);
		}

		return String.format(format, args);
	}

	public static String text(Collection<Object> coll) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		boolean first = true;

		for (Object obj : coll) {
			if (!first) {
				sb.append(", ");
			}

			sb.append(text(obj));
			first = false;
		}

		sb.append("]");
		return sb.toString();
	}

	public static String text(Iterator<?> it) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		boolean first = true;

		while (it.hasNext()) {
			if (first) {
				sb.append(", ");
				first = false;
			}

			sb.append(text(it.next()));
		}

		sb.append("]");

		return sb.toString();
	}

	public static String textln(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < objs.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append("\n  ");
			sb.append(text(objs[i]));
		}

		sb.append("\n]");

		return sb.toString();
	}

	public static String replaceText(String s, String[][] repls) {
		for (String[] repl : repls) {
			s = s.replaceAll(Pattern.quote(repl[0]), repl[1]);
		}
		return s;
	}

	public static <T> String join(String sep, T... items) {
		return render(items, "%s", sep);
	}

	public static String join(String sep, Iterable<?> items) {
		return render(items, "%s", sep);
	}

	public static String join(String sep, char[][] items) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				sb.append(sep);
			}
			sb.append(items[i]);
		}

		return sb.toString();
	}

	public static String render(Object[] items, String itemFormat, String sep) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				sb.append(sep);
			}
			sb.append(readable(itemFormat, items[i]));
		}

		return sb.toString();
	}

	public static String render(Iterable<?> items, String itemFormat, String sep) {
		StringBuilder sb = new StringBuilder();

		int i = 0;
		Iterator<?> it = items.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			if (i > 0) {
				sb.append(sep);
			}

			sb.append(readable(itemFormat, item));
			i++;
		}

		return sb.toString();
	}

	public static <T> T[] array(T... items) {
		return items;
	}

	public static <T> Iterator<T> iterator(T[] arr) {
		return Arrays.asList(arr).iterator();
	}

	public static <T> Set<T> set(Collection<? extends T> coll) {
		Set<T> set = new LinkedHashSet<T>();
		set.addAll(coll);
		return set;
	}

	public static <T> Set<T> set(T... values) {
		Set<T> set = new LinkedHashSet<T>();

		for (T val : values) {
			set.add(val);
		}

		return set;
	}

	public static <T> List<T> list(Collection<? extends T> coll) {
		List<T> list = new ArrayList<T>();
		list.addAll(coll);
		return list;
	}

	public static <T> List<T> list(T... values) {
		List<T> list = new ArrayList<T>();

		for (T item : values) {
			list.add(item);
		}

		return list;
	}

	public static <K, V> Map<K, V> map(Map<? extends K, ? extends V> src) {
		Map<K, V> map = map();
		map.putAll(src);
		return map;
	}

	public static <K, V> Map<K, V> map() {
		return new HashMap<K, V>();
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
		must(keysAndValues.length % 2 == 0, "Incorrect number of arguments (expected key-value pairs)!");

		Map<K, V> map = map();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			map.put((K) keysAndValues[i * 2], (V) keysAndValues[i * 2 + 1]);
		}

		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(Map<? extends K, ? extends V> src) {
		ConcurrentMap<K, V> map = concurrentMap();
		map.putAll(src);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key, V value) {
		ConcurrentMap<K, V> map = concurrentMap();
		map.put(key, value);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1);
		map.put(key2, value2);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2, K key3, V value3) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1, key2, value2);
		map.put(key3, value3);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2, K key3, V value3,
			K key4, V value4) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1, key2, value2, key3, value3);
		map.put(key4, value4);
		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(K key1, V value1, K key2, V value2, K key3, V value3,
			K key4, V value4, K key5, V value5) {
		ConcurrentMap<K, V> map = concurrentMap(key1, value1, key2, value2, key3, value3, key4, value4);
		map.put(key5, value5);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ConcurrentMap<K, V> concurrentMap(Object... keysAndValues) {
		must(keysAndValues.length % 2 == 0, "Incorrect number of arguments (expected key-value pairs)!");

		ConcurrentMap<K, V> map = concurrentMap();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			map.put((K) keysAndValues[i * 2], (V) keysAndValues[i * 2 + 1]);
		}

		return map;
	}

	public static <T> Queue<T> queue(int maxSize) {
		return maxSize > 0 ? new ArrayBlockingQueue<T>(maxSize) : new ConcurrentLinkedQueue<T>();
	}

	public static long time() {
		return System.currentTimeMillis();
	}

	public static boolean xor(boolean a, boolean b) {
		return a && !b || b && !a;
	}

	public static boolean eq(Object a, Object b) {
		if (a == b) {
			return true;
		}

		if (a == null || b == null) {
			return false;
		}

		return a.equals(b);
	}

	public static void failIf(boolean failureCondition, String msg) {
		if (failureCondition) {
			throw rte(msg);
		}
	}

	public static void failIf(boolean failureCondition, String msg, Object... args) {
		if (failureCondition) {
			throw rte(msg, args);
		}
	}

	public static boolean must(boolean expectedCondition, String message) {
		if (!expectedCondition) {
			throw rte(message);
		}
		return true;
	}

	public static String copyNtimes(String s, int n) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static RuntimeException rte(String message, Throwable cause, Object... args) {
		return new RuntimeException(readable(message, args), cause);
	}

	public static RuntimeException rte(String message, Throwable cause) {
		return new RuntimeException(message, cause);
	}

	public static boolean must(boolean expectedCondition) {
		if (!expectedCondition) {
			throw rte("Expectation failed!");
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, long arg) {
		if (!expectedCondition) {
			throw rte(message, arg);
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg) {
		if (!expectedCondition) {
			throw rte(message, text(arg));
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg1, Object arg2) {
		if (!expectedCondition) {
			throw rte(message, text(arg1), text(arg2));
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg1, Object arg2, Object arg3) {
		if (!expectedCondition) {
			throw rte(message, text(arg1), text(arg2), text(arg3));
		}
		return true;
	}

	public static void secure(boolean condition, String msg) {
		if (!condition) {
			throw new SecurityException(readable(msg));
		}
	}

	public static void secure(boolean condition, String msg, Object arg) {
		if (!condition) {
			throw new SecurityException(readable(msg, arg));
		}
	}

	public static void secure(boolean condition, String msg, Object arg1, Object arg2) {
		if (!condition) {
			throw new SecurityException(readable(msg, arg1, arg2));
		}
	}

	public static void bounds(int value, int min, int max) {
		must(value >= min && value <= max, "%s is not in the range [%s, %s]!", value, min, max);
	}

	public static void notNullAll(Object... items) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				throw rte("The item[%s] must NOT be null!", i);
			}
		}
	}

	public static <T> T notNull(T value, String desc, Object... descArgs) {
		if (value == null) {
			throw rte("%s must NOT be null!", readable(desc, descArgs));
		}

		return value;
	}

	public static RuntimeException notReady() {
		return rte("Not yet implemented!");
	}

	public static RuntimeException notSupported() {
		return rte("This operation is not supported by this implementation!");
	}

	public static void show(Object... values) {
		String text = values.length == 1 ? text(values[0]) : text(values);
		System.out.println(">" + text + "<");
	}

	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static boolean isEmpty(Object[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		} else if (value instanceof String) {
			return isEmpty((String) value);
		} else if (value instanceof Object[]) {
			return isEmpty((Object[]) value);
		} else if (value instanceof Collection<?>) {
			return isEmpty((Collection<?>) value);
		} else if (value instanceof Map<?, ?>) {
			return isEmpty((Map<?, ?>) value);
		}
		return false;
	}

	public static String capitalized(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String uncapitalized(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static String mid(String s, int beginIndex, int endIndex) {
		if (endIndex < 0) {
			endIndex = s.length() + endIndex;
		}
		return s.substring(beginIndex, endIndex);
	}

	public static int num(String s) {
		return Integer.parseInt(s);
	}

	public static String camelSplit(String s) {
		return CAMEL_SPLITTER_PATTERN.matcher(s).replaceAll(" ");
	}

	public static String camelPhrase(String s) {
		return capitalized(camelSplit(s).toLowerCase());
	}

	public static int limit(int min, int value, int max) {
		return Math.min(Math.max(min, value), max);
	}

	public static <T> T single(Collection<T> coll) {
		must(coll.size() == 1, "Expected exactly 1 items, but found: %s!", coll.size());
		return coll.iterator().next();
	}

	public static <T> T singleOrNone(Collection<T> coll) {
		must(coll.size() <= 1, "Expected 0 or 1 items, but found: %s!", coll.size());
		return !coll.isEmpty() ? coll.iterator().next() : null;
	}

	@SuppressWarnings("unchecked")
	public static <T> int compare(T val1, T val2) {
		if (val1 == null && val2 == null) {
			return 0;
		} else if (val1 == null) {
			return -1;
		} else if (val2 == null) {
			return 1;
		} else {
			return ((Comparable<T>) val1).compareTo(val2);
		}
	}

}
