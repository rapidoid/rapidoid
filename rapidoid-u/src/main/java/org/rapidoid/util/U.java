package org.rapidoid.util;

/*
 * #%L
 * rapidoid-u
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

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.rapidoid.lambda.Dynamic;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Predicate;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class U {

	private static final Object[] EMPTY_ARRAY = {};

	public static String readable(Object obj) {
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
			return readable((Object[]) obj);
		} else {
			return String.valueOf(obj);
		}
	}

	public static String readable(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < objs.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(readable(objs[i]));
		}

		sb.append("]");

		return sb.toString();
	}

	public static String format(String s, Object... args) {
		return String.format(s, args);
	}

	public static String nice(String format, Object... args) {
		for (int i = 0; i < args.length; i++) {
			args[i] = readable(args[i]);
		}

		return String.format(format, args);
	}

	public static String readable(Iterable<Object> coll) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		boolean first = true;

		for (Object obj : coll) {
			if (!first) {
				sb.append(", ");
			}

			sb.append(readable(obj));
			first = false;
		}

		sb.append("]");
		return sb.toString();
	}

	public static String readable(Iterator<?> it) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		boolean first = true;

		while (it.hasNext()) {
			if (first) {
				sb.append(", ");
				first = false;
			}

			sb.append(readable(it.next()));
		}

		sb.append("]");

		return sb.toString();
	}

	public static String readableln(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < objs.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append("\n  ");
			sb.append(readable(objs[i]));
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

	public static void print(Object... values) {
		String text;

		if (values != null) {
			text = values.length == 1 ? readable(values[0]) : readable(values);
		} else {
			text = "null";
		}

		System.out.println(text);
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
			sb.append(nice(itemFormat, items[i]));
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

			sb.append(nice(itemFormat, item));
			i++;
		}

		return sb.toString();
	}

	public static <T> Iterator<T> iterator(T[] arr) {
		return Arrays.asList(arr).iterator();
	}

	public static <T> T[] array(T... items) {
		return items;
	}

	public static Object[] array(Iterable<?> items) {
		return (items instanceof Collection) ? ((Collection<?>) items).toArray() : list(items).toArray();
	}

	@SuppressWarnings("unchecked")
	public static <T> Set<T> synchronizedSet() {
		return (Set<T>) Collections.synchronizedSet(set());
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

	public static <T> Set<T> set(T... values) {
		Set<T> set = set();

		for (T val : values) {
			set.add(val);
		}

		return set;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> synchronizedList() {
		return (List<T>) Collections.synchronizedList(list());
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
		must(keysAndValues.length % 2 == 0, "Incorrect number of arguments (expected key-value pairs)!");

		Map<K, V> map = map();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			map.put((K) keysAndValues[i * 2], (V) keysAndValues[i * 2 + 1]);
		}

		return map;
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static <K, V> ConcurrentMap<K, V> concurrentMap(Map<? extends K, ? extends V> src, boolean ignoreNullValues) {
		ConcurrentMap<K, V> map = concurrentMap();

		for (Entry<? extends K, ? extends V> e : src.entrySet()) {
			if (!ignoreNullValues || e.getValue() != null) {
				map.put(e.getKey(), e.getValue());
			}
		}

		return map;
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

	public static <K, V> Map<K, V> orderedMap() {
		return new LinkedHashMap<K, V>();
	}

	public static <K, V> Map<K, V> orderedMap(Map<? extends K, ? extends V> src, boolean ignoreNullValues) {
		Map<K, V> map = orderedMap();

		for (Entry<? extends K, ? extends V> e : src.entrySet()) {
			if (!ignoreNullValues || e.getValue() != null) {
				map.put(e.getKey(), e.getValue());
			}
		}

		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key, V value) {
		Map<K, V> map = orderedMap();
		map.put(key, value);
		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key1, V value1, K key2, V value2) {
		Map<K, V> map = orderedMap(key1, value1);
		map.put(key2, value2);
		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key1, V value1, K key2, V value2, K key3, V value3) {
		Map<K, V> map = orderedMap(key1, value1, key2, value2);
		map.put(key3, value3);
		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
		Map<K, V> map = orderedMap(key1, value1, key2, value2, key3, value3);
		map.put(key4, value4);
		return map;
	}

	public static <K, V> Map<K, V> orderedMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4,
			K key5, V value5) {
		Map<K, V> map = orderedMap(key1, value1, key2, value2, key3, value3, key4, value4);
		map.put(key5, value5);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> orderedMap(Object... keysAndValues) {
		must(keysAndValues.length % 2 == 0, "Incorrect number of arguments (expected key-value pairs)!");

		Map<K, V> map = orderedMap();

		for (int i = 0; i < keysAndValues.length / 2; i++) {
			map.put((K) keysAndValues[i * 2], (V) keysAndValues[i * 2 + 1]);
		}

		return map;
	}

	public static <K, V> Map<K, V> synchronizedMap() {
		return Collections.synchronizedMap(U.<K, V> map());
	}

	public static <T> Queue<T> queue() {
		return new ConcurrentLinkedQueue<T>();
	}

	public static <T> BlockingQueue<T> queue(int maxSize) {
		argMust(maxSize > 0, "Maximum queue size must be > 0!");
		return new ArrayBlockingQueue<T>(maxSize);
	}

	public static <T> T or(T value, T fallback) {
		return value != null ? value : fallback;
	}

	public static String safe(String s) {
		return or(s, "");
	}

	public static boolean safe(Boolean b) {
		return or(b, false);
	}

	public static int safe(Integer num) {
		return or(num, 0);
	}

	public static long safe(Long num) {
		return or(num, 0L);
	}

	public static byte safe(Byte num) {
		return or(num, (byte) 0);
	}

	public static float safe(Float num) {
		return or(num, 0.0f);
	}

	public static double safe(Double num) {
		return or(num, 0.0);
	}

	public static Object[] safe(Object[] arr) {
		return or(arr, EMPTY_ARRAY);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> safe(List<T> list) {
		return or(list, Collections.EMPTY_LIST);
	}

	@SuppressWarnings("unchecked")
	public static <T> Set<T> safe(Set<T> list) {
		return or(list, Collections.EMPTY_SET);
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> safe(Map<K, V> list) {
		return or(list, Collections.EMPTY_MAP);
	}

	public static long time() {
		return System.currentTimeMillis();
	}

	public static boolean xor(boolean a, boolean b) {
		return a && !b || b && !a;
	}

	public static boolean eq(Object a, Object b) {
		return a == null ? b == null : a.equals(b);
	}

	public static RuntimeException rte(String message) {
		return new RuntimeException(message);
	}

	public static RuntimeException rte(String message, Throwable cause) {
		return new RuntimeException(message, cause);
	}

	public static RuntimeException rte(Throwable cause) {
		return rte("", cause);
	}

	public static RuntimeException rte(String message, Object... args) {
		return rte(nice(message, args));
	}

	public static RuntimeException cancelled() {
		return rte("This operation was cancelled!");
	}

	public static RuntimeException notExpected() {
		return rte("This operation is not expected to be called!");
	}

	public static void rteIf(boolean failureCondition, String msg) {
		if (failureCondition) {
			throw rte(msg);
		}
	}

	public static boolean must(boolean expectedCondition, String message) {
		if (!expectedCondition) {
			throw rte(message);
		}
		return true;
	}

	public static RuntimeException rte(String message, Throwable cause, Object... args) {
		return rte(nice(message, args), cause);
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
			throw rte(message, readable(arg));
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg1, Object arg2) {
		if (!expectedCondition) {
			throw rte(message, readable(arg1), readable(arg2));
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg1, Object arg2, Object arg3) {
		if (!expectedCondition) {
			throw rte(message, readable(arg1), readable(arg2), readable(arg3));
		}
		return true;
	}

	public static IllegalArgumentException illegalArg(String message, Object... args) {
		return new IllegalArgumentException(format(message, args));
	}

	public static void secure(boolean condition, String msg) {
		if (!condition) {
			throw new SecurityException(readable(msg));
		}
	}

	public static void secure(boolean condition, String msg, Object arg) {
		if (!condition) {
			throw new SecurityException(nice(msg, arg));
		}
	}

	public static void secure(boolean condition, String msg, Object arg1, Object arg2) {
		if (!condition) {
			throw new SecurityException(nice(msg, arg1, arg2));
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

	public static <T> T notNull(T value, String msgOrDesc, Object... descArgs) {
		if (value == null) {
			if (msgOrDesc.endsWith("!")) {
				// message
				throw rte(msgOrDesc, descArgs);
			} else {
				// description
				throw rte("%s must NOT be null!", nice(msgOrDesc, descArgs));
			}
		}

		return value;
	}

	public static RuntimeException notReady() {
		return rte("Not yet implemented!");
	}

	public static RuntimeException notSupported() {
		return rte("This operation is not supported by this implementation!");
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

	public static boolean isEmpty(Iterable<?> iter) {
		return iter.iterator().hasNext();
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
		} else if (value instanceof Iterable<?>) {
			return isEmpty((Iterable<?>) value);
		}
		return false;
	}

	public static String capitalized(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String uncapitalized(String s) {
		return s.isEmpty() ? s : s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static String copyNtimes(String s, int n) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static String mid(String s, int beginIndex, int endIndex) {
		if (endIndex < 0) {
			endIndex = s.length() + endIndex;
		}
		return s.substring(beginIndex, endIndex);
	}

	public static String insert(String target, int atIndex, String insertion) {
		return target.substring(0, atIndex) + insertion + target.substring(atIndex);
	}

	public static String str(Object value) {
		return String.valueOf(value);
	}

	public static int num(String s) {
		return Integer.parseInt(s);
	}

	public static int limited(int min, int value, int max) {
		return Math.min(Math.max(min, value), max);
	}

	public static <T> T single(Iterable<T> coll) {
		Iterator<T> it = coll.iterator();
		must(it.hasNext(), "Expected exactly 1 item, but didn't find any!");
		T item = it.next();
		must(!it.hasNext(), "Expected exactly 1 item, but found more than 1!");
		return item;
	}

	public static <T> T singleOrNone(Iterable<T> coll) {
		Iterator<T> it = coll.iterator();
		T item = it.hasNext() ? it.next() : null;
		must(!it.hasNext(), "Expected 0 or 1 items, but found more than 1!");
		return item;
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

	public static <T> List<T> range(Iterable<T> items, int fromIndex, int toIndex) {
		// TODO more efficient implementation
		List<T> list = list(items);

		fromIndex = limited(0, fromIndex, list.size());
		toIndex = limited(fromIndex, toIndex, list.size());

		return list(list.subList(fromIndex, toIndex));
	}

	public static <T> List<T> page(Iterable<T> items, int page, int pageSize) {
		return range(items, (page - 1) * pageSize, page * pageSize);
	}

	public static String trimr(String s, char suffix) {
		return (!s.isEmpty() && s.charAt(s.length() - 1) == suffix) ? mid(s, 0, -1) : s;
	}

	public static String trimr(String s, String suffix) {
		return s.endsWith(suffix) ? mid(s, 0, -suffix.length()) : s;
	}

	public static String triml(String s, char prefix) {
		return (!s.isEmpty() && s.charAt(0) == prefix) ? s.substring(1) : s;
	}

	public static String triml(String s, String prefix) {
		return s.startsWith(prefix) ? s.substring(prefix.length()) : s;
	}

	public static String bytesAsText(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	public static void argMust(boolean expectedCondition, String message, Object... args) {
		if (!expectedCondition) {
			throw illegalArg(message, args);
		}
	}

	/**
	 * Sleeps (calling Thread.sleep) for the specified period.
	 * 
	 * If the thread is interrupted while sleeping, throws {@link CancellationException} to propagate the interruption.
	 * 
	 * @param millis
	 *            the length of time to sleep in milliseconds.
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new CancellationException();
		}
	}

	public static <K, V> Map<K, V> autoExpandingMap(final Class<?> clazz) {
		return autoExpandingMap(new Mapper<K, V>() {

			@SuppressWarnings("unchecked")
			@Override
			public V map(K src) throws Exception {
				try {
					return (V) clazz.newInstance();
				} catch (Exception e) {
					throw rte(e);
				}
			}
		});
	}

	@SuppressWarnings("serial")
	public static <K, V> Map<K, V> autoExpandingMap(final Mapper<K, V> valueFactory) {
		return Collections.synchronizedMap(new HashMap<K, V>() {

			@SuppressWarnings("unchecked")
			@Override
			public synchronized V get(Object key) {
				V val = super.get(key);

				if (val == null) {
					try {
						val = valueFactory.map((K) key);
					} catch (Exception e) {
						throw rte(e);
					}

					put((K) key, val);
				}

				return val;
			}

		});
	}

	public static <K1, K2, V> Map<K1, Map<K2, V>> mapOfMaps() {
		return autoExpandingMap(new Mapper<K1, Map<K2, V>>() {

			@Override
			public Map<K2, V> map(K1 src) throws Exception {
				return synchronizedMap();
			}

		});
	}

	public static <K, V> Map<K, List<V>> mapOfLists() {
		return autoExpandingMap(new Mapper<K, List<V>>() {

			@Override
			public List<V> map(K src) throws Exception {
				return Collections.synchronizedList(U.<V> list());
			}

		});
	}

	public static <K, V> Map<K, Set<V>> mapOfSets() {
		return autoExpandingMap(new Mapper<K, Set<V>>() {

			@Override
			public Set<V> map(K src) throws Exception {
				return Collections.synchronizedSet(U.<V> set());
			}

		});
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object value) {
		return (T) value;
	}

	public static void wait(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new CancellationException();
		}
	}

	public static void wait(CountDownLatch latch, long timeout, TimeUnit unit) {
		try {
			latch.await(timeout, unit);
		} catch (InterruptedException e) {
			throw new CancellationException();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T dynamic(final Class<T> targetInterface, final Dynamic dynamic) {
		final Object obj = new Object();

		InvocationHandler handler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

				if (method.getDeclaringClass().equals(Object.class)) {
					if (method.getName().equals("toString")) {
						return targetInterface.getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
					}
					return method.invoke(obj, args);
				}

				return dynamic.call(method, safe(args));
			}

		};

		return ((T) Proxy.newProxyInstance(targetInterface.getClassLoader(), new Class[] { targetInterface }, handler));
	}

	public static <T> T evalJS(String js) throws ScriptException {
		return evalJS(js, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T evalJS(String js, Map<String, ?> bindings) throws ScriptException {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		if (bindings != null) {
			Map<String, Object> map = U.cast(bindings);
			return (T) engine.eval(js, new SimpleBindings(map));
		} else {
			return (T) engine.eval(js);
		}
	}

	public static CompiledScript compileJS(String js) throws ScriptException {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		must(engine instanceof Compilable, "The JavaScript engine cannot compile!");

		Compilable compilable = (Compilable) engine;
		return compilable.compile(js);
	}

	public static <T> boolean isIn(T value, T... candidates) {
		for (T candidate : candidates) {
			if (U.eq(value, candidate)) {
				return true;
			}
		}
		return false;
	}

	public static <T> Is<T> is(T value) {
		return new Is<T>(value);
	}

	public static boolean exists(Callable<?> accessChain) {
		try {
			return accessChain != null && accessChain.call() != null;
		} catch (NullPointerException e) {
			return false;
		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	public static <T> List<T> filter(List<T> items, Predicate<T> predicate) {
		List<T> filtered = list();
		addIf(items, filtered, predicate);
		return filtered;
	}

	public static <T> Set<T> filter(Set<T> items, Predicate<T> predicate) {
		Set<T> filtered = set();
		addIf(items, filtered, predicate);
		return filtered;
	}

	public static <T> void addIf(Collection<T> src, Collection<T> dest, Predicate<T> predicate) {
		for (T item : src) {
			boolean shouldAdd;

			try {
				shouldAdd = predicate.eval(item);
			} catch (Exception e) {
				throw rte(e);
			}

			if (shouldAdd) {
				dest.add(item);
			}
		}
	}

	public static String uri(String... parts) {
		return "/" + constructPath("/", false, parts);
	}

	public static String path(String... parts) {
		return constructPath(File.separator, true, parts);
	}

	private static String constructPath(String separator, boolean preserveFirstSegment, String... parts) {
		String s = "";

		for (int i = 0; i < parts.length; i++) {
			String part = U.safe(parts[i]);

			// trim '/'s and '\'s
			if (!preserveFirstSegment || i > 0) {
				part = triml(part, "/");
			}

			if (!preserveFirstSegment || part.length() > 1 || i > 0) {
				part = trimr(part, "/");
				part = trimr(part, "\\");
			}

			if (!U.isEmpty(part)) {
				if (!s.isEmpty() && !s.endsWith(separator)) {
					s += separator;
				}
				s += part;
			}
		}

		return s;
	}

	public static boolean isMap(Object obj) {
		return obj instanceof Map<?, ?>;
	}

	public static boolean isList(Object obj) {
		return obj instanceof List<?>;
	}

	public static boolean isSet(Object obj) {
		return obj instanceof Set<?>;
	}

	public static boolean isCollection(Object obj) {
		return obj instanceof Collection<?>;
	}

}
