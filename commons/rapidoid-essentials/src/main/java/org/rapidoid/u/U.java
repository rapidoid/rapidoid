package org.rapidoid.u;

/*
 * #%L
 * rapidoid-essentials
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

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CancellationException;

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class U extends RapidoidThing {

	private static final int PRINTABLE_ARR_MAX_SIZE = 30;

	@SuppressWarnings("ImplicitArrayToString")
	public static String str(Object obj) {

		if (obj instanceof String) {
			return (String) obj;

		} else if (obj == null) {
			return null;

		} else if (obj instanceof byte[]) {
			byte[] arr = (byte[]) obj;
			return arr.length < PRINTABLE_ARR_MAX_SIZE ? Arrays.toString(arr) : arr.toString();

		} else if (obj instanceof short[]) {
			short[] arr = (short[]) obj;
			return arr.length < PRINTABLE_ARR_MAX_SIZE ? Arrays.toString(arr) : arr.toString();

		} else if (obj instanceof int[]) {
			int[] arr = (int[]) obj;
			return arr.length < PRINTABLE_ARR_MAX_SIZE ? Arrays.toString(arr) : arr.toString();

		} else if (obj instanceof long[]) {
			long[] arr = (long[]) obj;
			return arr.length < PRINTABLE_ARR_MAX_SIZE ? Arrays.toString(arr) : arr.toString();

		} else if (obj instanceof float[]) {
			float[] arr = (float[]) obj;
			return arr.length < PRINTABLE_ARR_MAX_SIZE ? Arrays.toString(arr) : arr.toString();

		} else if (obj instanceof double[]) {
			double[] arr = (double[]) obj;
			return arr.length < PRINTABLE_ARR_MAX_SIZE ? Arrays.toString(arr) : arr.toString();

		} else if (obj instanceof boolean[]) {
			boolean[] arr = (boolean[]) obj;
			return arr.length < PRINTABLE_ARR_MAX_SIZE ? Arrays.toString(arr) : arr.toString();

		} else if (obj instanceof char[]) {
			char[] arr = (char[]) obj;
			return arr.length < PRINTABLE_ARR_MAX_SIZE ? Arrays.toString(arr) : arr.toString();

		} else if (obj instanceof Object[]) {
			Object[] arr = (Object[]) obj;
			return arr.length < PRINTABLE_ARR_MAX_SIZE ? str(arr) : arr.toString();

		} else {
			return String.valueOf(obj);
		}
	}

	public static String str(Object[] objs) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		for (int i = 0; i < objs.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(str(objs[i]));
		}

		sb.append("]");

		return sb.toString();
	}

	public static String str(Iterable<Object> coll) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		boolean first = true;

		for (Object obj : coll) {
			if (!first) {
				sb.append(", ");
			}

			sb.append(str(obj));
			first = false;
		}

		sb.append("]");
		return sb.toString();
	}

	public static String str(Iterator<?> it) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		boolean first = true;

		while (it.hasNext()) {
			if (first) {
				sb.append(", ");
				first = false;
			}

			sb.append(str(it.next()));
		}

		sb.append("]");

		return sb.toString();
	}

	public static String frmt(String format, Object... args) {
		for (int i = 0; i < args.length; i++) {
			if (!(args[i] instanceof Number)) {
				args[i] = str(args[i]);
			}
		}

		return String.format(format, args);
	}

	public static void print(Object... values) {
		String text;

		if (values != null) {
			text = values.length == 1 ? str(values[0]) : str(values);
		} else {
			text = "null";
		}

		System.out.println(text);
	}

	@SuppressWarnings({"varargs"})
	public static <T> String join(String sep, T... items) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				sb.append(sep);
			}
			sb.append(items[i]);
		}

		return sb.toString();
	}

	public static String join(String sep, Iterable<?> items) {
		StringBuilder sb = new StringBuilder();

		int i = 0;
		Iterator<?> it = items.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			if (i > 0) {
				sb.append(sep);
			}

			sb.append(item);
			i++;
		}

		return sb.toString();
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

	public static <T> Iterator<T> iterator(T[] arr) {
		return Arrays.asList(arr).iterator();
	}

	@SuppressWarnings({"varargs"})
	public static <T> T[] array(T... items) {
		return items;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] arrayOf(Class<T> type, T... elements) {
		T[] array = (T[]) Array.newInstance(type, elements.length);
		System.arraycopy(elements, 0, array, 0, elements.length);
		return array;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] arrayOf(Class<T> type, Iterable<? extends T> items) {
		Collection<T> coll = (items instanceof Collection) ? (Collection<T>) items : list(items);

		T[] array = (T[]) Array.newInstance(type, coll.size());

		coll.toArray(array);
		return array;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] arrayOf(Iterable<? extends T> items) {
		Iterator<? extends T> it = items.iterator();

		must(it.hasNext(), "Cannot infer the array type from empty collection!");
		Class<T> type = (Class<T>) it.next().getClass();

		return arrayOf(type, items);
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

	@SuppressWarnings({"varargs"})
	public static <T> Set<T> set(T... values) {
		Set<T> set = set();

		Collections.addAll(set, values);

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

	@SuppressWarnings({"varargs"})
	public static <T> List<T> list(T... values) {
		List<T> list = list();

		Collections.addAll(list, values);

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

	public static <T> T or(T value, T fallback) {
		return value != null ? value : fallback;
	}

	public static <T> T or(T value, T fallback1, T fallback2) {
		return value != null ? value : fallback1 != null ? fallback1 : fallback2;
	}

	public static <T> T or(T value, T fallback1, T fallback2, T fallback3) {
		return value != null ? value : fallback1 != null ? fallback1 : fallback2 != null ? fallback2 : fallback3;
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
		return arr != null ? arr : new Object[0];
	}

	public static <T> List<T> safe(List<T> list) {
		return list != null ? list : U.<T>list();
	}

	public static <T> Set<T> safe(Set<T> set) {
		return set != null ? set : U.<T>set();
	}

	public static <T> Collection<T> safe(Collection<T> coll) {
		return coll != null ? coll : U.<T>list();
	}

	public static <K, V> Map<K, V> safe(Map<K, V> map) {
		return map != null ? map : U.<K, V>map();
	}

	public static long time() {
		return System.currentTimeMillis();
	}

	public static boolean eq(Object a, Object b) {
		return a == null ? b == null : a.equals(b);
	}

	public static boolean neq(Object a, Object b) {
		return !eq(a, b);
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
		return rte(frmt(message, args));
	}

	public static RuntimeException rte(String message, Throwable cause, Object... args) {
		return rte(frmt(message, args), cause);
	}

	public static IllegalArgumentException illegal(String message, Object... args) {
		return new IllegalArgumentException(frmt(message, args));
	}

	public static boolean must(boolean expectedCondition, String message) {
		if (!expectedCondition) {
			throw illegal(message);
		}
		return true;
	}

	public static boolean must(boolean expectedCondition) {
		if (!expectedCondition) {
			throw illegal("Expectation failed!");
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, long arg) {
		if (!expectedCondition) {
			throw illegal(message, arg);
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg) {
		if (!expectedCondition) {
			throw illegal(message, str(arg));
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg1, Object arg2) {
		if (!expectedCondition) {
			throw illegal(message, str(arg1), str(arg2));
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg1, Object arg2, Object arg3) {
		if (!expectedCondition) {
			throw illegal(message, str(arg1), str(arg2), str(arg3));
		}
		return true;
	}

	public static <T> T notNull(T value, String msgOrDesc, Object... descArgs) {
		if (value == null) {
			throw illegal("%s must NOT be null!", frmt(msgOrDesc, descArgs));
		}

		return value;
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
		} else if (value instanceof byte[]) {
			return ((byte[]) value).length == 0;
		} else if (value instanceof short[]) {
			return ((short[]) value).length == 0;
		} else if (value instanceof int[]) {
			return ((int[]) value).length == 0;
		} else if (value instanceof long[]) {
			return ((long[]) value).length == 0;
		} else if (value instanceof float[]) {
			return ((float[]) value).length == 0;
		} else if (value instanceof double[]) {
			return ((double[]) value).length == 0;
		} else if (value instanceof boolean[]) {
			return ((boolean[]) value).length == 0;
		} else if (value instanceof char[]) {
			return ((char[]) value).length == 0;
		} else if (value instanceof Object[]) {
			return ((Object[]) value).length == 0;
		} else if (value instanceof Collection<?>) {
			return isEmpty((Collection<?>) value);
		} else if (value instanceof Map<?, ?>) {
			return isEmpty((Map<?, ?>) value);
		} else if (value instanceof Iterable<?>) {
			return isEmpty((Iterable<?>) value);
		}
		return false;
	}

	public static boolean notEmpty(String value) {
		return !isEmpty(value);
	}

	public static boolean notEmpty(Object[] arr) {
		return !isEmpty(arr);
	}

	public static boolean notEmpty(Collection<?> coll) {
		return !isEmpty(coll);
	}

	public static boolean notEmpty(Iterable<?> iter) {
		return !isEmpty(iter);
	}

	public static boolean notEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}

	public static boolean notEmpty(Object value) {
		return !isEmpty(value);
	}

	public static int num(String s) {
		return Integer.parseInt(s);
	}

	public static boolean bool(Object o) {
		return Boolean.TRUE.equals(o);
	}

	public static int limit(int min, int value, int max) {
		return Math.min(Math.max(min, value), max);
	}

	public static long limit(long min, long value, long max) {
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

	public static <T> T first(T[] values) {
		return values != null && values.length > 0 ? values[0] : null;
	}

	public static <T> T first(List<T> values) {
		return values != null && values.size() > 0 ? values.get(0) : null;
	}

	public static <T> T last(T[] values) {
		return values != null && values.length > 0 ? values[values.length - 1] : null;
	}

	public static <T> T last(List<T> values) {
		return values != null && values.size() > 0 ? values.get(values.size() - 1) : null;
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

	/**
	 * Sleeps (calling Thread.sleep) for the specified period.
	 * <p>
	 * If the thread is interrupted while sleeping, throws {@link CancellationException} to propagate the interruption.
	 *
	 * @param millis the length of time to sleep in milliseconds.
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new CancellationException();
		}
	}

	/**
	 * Simpler casts, less warnings.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object value) {
		return (T) value;
	}

	public static <T> List<List<T>> groupsOf(int groupSize, Iterable<T> items) {
		List<List<T>> segments = list();
		Iterator<T> it = items.iterator();

		while (it.hasNext()) {
			List<T> segment = list();

			for (int i = 0; i < groupSize; i++) {
				if (it.hasNext()) {
					segment.add(it.next());
				}
			}

			segments.add(segment);
		}

		return segments;
	}

	public static <T> List<List<T>> groupsOf(int groupSize, T... items) {
		return groupsOf(groupSize, list(items));
	}

}
