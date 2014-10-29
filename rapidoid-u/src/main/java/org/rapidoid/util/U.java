package org.rapidoid.util;

/*
 * #%L
 * rapidoid-u
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Predicate;

public class U {

	public static final LogLevel TRACE = LogLevel.TRACE;
	public static final LogLevel DEBUG = LogLevel.DEBUG;
	public static final LogLevel INFO = LogLevel.INFO;
	public static final LogLevel WARN = LogLevel.WARN;
	public static final LogLevel ERROR = LogLevel.ERROR;
	public static final LogLevel SEVERE = LogLevel.SEVERE;

	protected static LogLevel LOG_LEVEL = INFO;

	protected static final Random RND = new Random();
	private static Appendable LOG_OUTPUT = System.out;
	private static ScheduledThreadPoolExecutor EXECUTOR;
	private static long measureStart;
	protected static String[] ARGS = {};
	private static Properties CONFIG = null;

	private U() {
	}

	public static synchronized void setLogLevel(LogLevel logLevel) {
		LOG_LEVEL = logLevel;
	}

	public static synchronized LogLevel getLogLevel() {
		return LOG_LEVEL;
	}

	private static String getCallingClass() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		for (int i = 2; i < trace.length; i++) {
			String cls = trace[i].getClassName();
			if (!cls.equals(U.class.getCanonicalName())) {
				return cls;
			}
		}

		return U.class.getCanonicalName();
	}

	private static void log(Appendable out, LogLevel level, String msg, String key1, Object value1, String key2,
			Object value2, String key3, Object value3, int paramsN) {
		if (level.ordinal() >= LOG_LEVEL.ordinal()) {
			try {
				synchronized (out) {
					out.append(level.name());
					out.append(" | ");
					out.append(Thread.currentThread().getName());
					out.append(" | ");
					out.append(getCallingClass());
					out.append(" | ");
					out.append(msg);

					switch (paramsN) {
					case 0:
						break;

					case 1:
						printKeyValue(out, key1, value1);
						break;

					case 2:
						printKeyValue(out, key1, value1);
						printKeyValue(out, key2, value2);
						break;

					case 3:
						printKeyValue(out, key1, value1);
						printKeyValue(out, key2, value2);
						printKeyValue(out, key3, value3);
						break;

					default:
						throw notExpected();
					}

					out.append((char) 10);
				}
			} catch (IOException e) {
				throw rte(e);
			}
		}
	}

	private static void printKeyValue(Appendable out, String key, Object value) throws IOException {
		out.append(" | ");
		out.append(key);
		out.append("=");
		out.append(text(value));

		if (value instanceof Throwable) {
			Throwable err = (Throwable) value;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			err.printStackTrace(new PrintStream(stream));
			out.append("\n");
			out.append(stream.toString());
		}
	}

	public static synchronized void setLogOutput(Appendable logOutput) {
		LOG_OUTPUT = logOutput;
	}

	private static void log(LogLevel level, String msg, String key1, Object value1, String key2, Object value2,
			String key3, Object value3, int paramsN) {
		log(LOG_OUTPUT, level, msg, key1, value1, key2, value2, key3, value3, paramsN);
	}

	public static void trace(String msg) {
		log(TRACE, msg, null, null, null, null, null, null, 0);
	}

	public static void trace(String msg, String key, Object value) {
		log(TRACE, msg, key, value, null, null, null, null, 1);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2) {
		log(TRACE, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void trace(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(TRACE, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void debug(String msg) {
		log(DEBUG, msg, null, null, null, null, null, null, 0);
	}

	public static void debug(String msg, String key, Object value) {
		log(DEBUG, msg, key, value, null, null, null, null, 1);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2) {
		log(DEBUG, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void debug(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(DEBUG, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void info(String msg) {
		log(INFO, msg, null, null, null, null, null, null, 0);
	}

	public static void info(String msg, String key, Object value) {
		log(INFO, msg, key, value, null, null, null, null, 1);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2) {
		log(INFO, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void info(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(INFO, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void warn(String msg) {
		log(WARN, msg, null, null, null, null, null, null, 0);
	}

	public static void warn(String msg, String key, Object value) {
		log(WARN, msg, key, value, null, null, null, null, 1);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2) {
		log(WARN, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void warn(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(WARN, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void warn(String msg, Throwable error) {
		warn(msg, "error", error);
	}

	public static void error(String msg) {
		log(ERROR, msg, null, null, null, null, null, null, 0);
	}

	public static void error(String msg, String key, Object value) {
		log(ERROR, msg, key, value, null, null, null, null, 1);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2) {
		log(ERROR, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void error(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(ERROR, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void error(String msg, Throwable error) {
		error(msg, "error", error);
	}

	public static void error(Throwable error) {
		error("error occured!", "error", error);
	}

	public static void severe(String msg) {
		log(SEVERE, msg, null, null, null, null, null, null, 0);
	}

	public static void severe(String msg, String key, Object value) {
		log(SEVERE, msg, key, value, null, null, null, null, 1);
	}

	public static void severe(String msg, String key1, Object value1, String key2, Object value2) {
		log(SEVERE, msg, key1, value1, key2, value2, null, null, 2);
	}

	public static void severe(String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3) {
		log(SEVERE, msg, key1, value1, key2, value2, key3, value3, 3);
	}

	public static void severe(String msg, Throwable error) {
		severe(msg, "error", error);
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
		return new RuntimeException(format(message, args));
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

	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw rte(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Object... args) {
		for (Constructor<?> constr : clazz.getConstructors()) {
			Class<?>[] paramTypes = constr.getParameterTypes();
			if (areAssignable(paramTypes, args)) {
				try {
					return (T) constr.newInstance(args);
				} catch (Exception e) {
					throw rte(e);
				}
			}
		}

		throw rte("Cannot find appropriate constructor for %s with args %s!", clazz, text(args));
	}

	private static boolean areAssignable(Class<?>[] types, Object[] values) {
		if (types.length != values.length) {
			return false;
		}

		for (int i = 0; i < values.length; i++) {
			Object val = values[i];
			if (val != null && !types[i].isAssignableFrom(val.getClass())) {
				return false;
			}
		}

		return true;
	}

	public static <T> T or(T value, T fallback) {
		return value != null ? value : fallback;
	}

	public static String format(String s, Object... args) {
		return String.format(s, args);
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new ThreadDeath();
		}
	}

	public static boolean waitInterruption(long millis) {
		try {
			Thread.sleep(millis);
			return true;
		} catch (InterruptedException e) {
			Thread.interrupted();
			return false;
		}
	}

	public static void waitFor(Object obj) {
		try {
			synchronized (obj) {
				obj.wait();
			}
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	public static void joinThread(Thread thread) {
		try {
			thread.join();
		} catch (InterruptedException e) {
			// do nothing
		}
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

	public static String join(String sep, Object... items) {
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
			sb.append(format(itemFormat, items[i]));
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

			sb.append(format(itemFormat, item));
			i++;
		}

		return sb.toString();
	}

	public static <T> T[] array(T... items) {
		return items;
	}

	public static <T> Set<T> set(T... values) {
		Set<T> set = new HashSet<T>();

		for (T val : values) {
			set.add(val);
		}

		return set;
	}

	public static <T> List<T> list(T... values) {
		List<T> list = new ArrayList<T>();

		for (T item : values) {
			list.add(item);
		}

		return list;
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

	public static <K, V> Map<K, V> concurrentMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static <K, V> Map<K, V> autoExpandingMap(final Class<V> clazz) {
		return autoExpandingMap(new Mapper<K, V>() {
			@Override
			public V map(K src) throws Exception {
				return newInstance(clazz);
			}
		});
	}

	@SuppressWarnings("serial")
	public static <K, V> Map<K, V> autoExpandingMap(final Mapper<K, V> valueFactory) {
		return new ConcurrentHashMap<K, V>() {
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
		};
	}

	public static <T> Queue<T> queue(int maxSize) {
		return maxSize > 0 ? new ArrayBlockingQueue<T>(maxSize) : new ConcurrentLinkedQueue<T>();
	}

	public static URL resource(String filename) {
		return classLoader().getResource(filename);
	}

	public static ClassLoader classLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	private static Enumeration<URL> resources(String name) {

		name = name.replace('.', '/');

		if (name.equals("*")) {
			name = "";
		}

		try {
			return classLoader().getResources(name);
		} catch (IOException e) {
			throw U.rte("Cannot scan: " + name, e);
		}
	}

	public static File file(String filename) {
		File file = new File(filename);

		if (!file.exists()) {
			URL res = resource(filename);
			if (res != null) {
				return new File(res.getFile());
			}
		}

		return file;
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

	public static byte[] loadBytes(InputStream input) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		byte[] buffer = new byte[4 * 1024];

		try {
			int readN = 0;
			while ((readN = input.read(buffer)) != -1) {
				output.write(buffer, 0, readN);
			}
		} catch (IOException e) {
			throw rte(e);
		}

		return output.toByteArray();
	}

	public static byte[] loadBytes(String filename) {
		InputStream input = classLoader().getResourceAsStream(filename);
		return input != null ? loadBytes(input) : null;
	}

	public static byte[] classBytes(String fullClassName) {
		return loadBytes(fullClassName.replace('.', '/') + ".class");
	}

	public static String load(String filename) {
		return new String(loadBytes(filename));
	}

	public static List<String> loadLines(String filename) {
		InputStream input = classLoader().getResourceAsStream(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		List<String> lines = list();

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			throw rte(e);
		}

		return lines;
	}

	public static List<String> loadLines(String filename, final boolean filterEmpty, final String commentPrefix) {

		List<String> lines = loadLines(filename);

		List<String> lines2 = list();

		for (String line : lines) {
			String s = line.trim();
			if ((!filterEmpty || !s.isEmpty()) && (commentPrefix == null || !s.startsWith(commentPrefix))) {
				lines2.add(s);
			}
		}

		return lines2;
	}

	public static void save(String filename, String content) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			out.write(content.getBytes());
			close(out, false);
		} catch (Exception e) {
			close(out, true);
			throw rte(e);
		}
	}

	public static void close(OutputStream out, boolean quiet) {
		try {
			out.close();
		} catch (IOException e) {
			if (!quiet) {
				throw rte(e);
			}
		}
	}

	public static void close(InputStream in, boolean quiet) {
		try {
			in.close();
		} catch (IOException e) {
			if (!quiet) {
				throw rte(e);
			}
		}
	}

	public static void delete(String filename) {
		new File(filename).delete();
	}

	public static <T> T[] expand(T[] arr, int factor) {
		int len = arr.length;

		arr = Arrays.copyOf(arr, len * factor);

		return arr;
	}

	public static <T> T[] expand(T[] arr, T item) {
		int len = arr.length;

		arr = Arrays.copyOf(arr, len + 1);
		arr[len] = item;

		return arr;
	}

	public static <T> T[] subarray(T[] arr, int from, int to) {
		int start = from >= 0 ? from : arr.length + from;
		int end = to >= 0 ? to : arr.length + to;

		if (start < 0) {
			start = 0;
		}

		if (end > arr.length - 1) {
			end = arr.length - 1;
		}

		must(start <= end, "Invalid range: expected form <= to!");

		int size = end - start + 1;

		T[] part = Arrays.copyOf(arr, size);

		System.arraycopy(arr, start, part, 0, size);

		return part;
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
		return new RuntimeException(format(message, args), cause);
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
			throw rte(message, arg);
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message, Object arg1, Object arg2) {
		if (!expectedCondition) {
			throw rte(message, arg1, arg2);
		}
		return true;
	}

	public static void secure(boolean condition, String msg, Object arg) {
		if (!condition) {
			throw new SecurityException(format(msg, arg));
		}
	}

	public static void secure(boolean condition, String msg, Object arg1, Object arg2) {
		if (!condition) {
			throw new SecurityException(format(msg, arg1, arg2));
		}
	}

	public static void notNull(Object... items) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				throw rte("The item[%s] must NOT be null!", i);
			}
		}
	}

	public static void notNull(Object value, String desc) {
		if (value == null) {
			throw rte("%s must NOT be null!", desc);
		}
	}

	public static RuntimeException notReady() {
		return rte("Not yet implemented!");
	}

	public static RuntimeException notSupported() {
		return rte("This operation is not supported by this implementation!");
	}

	public static void show(Object... values) {
		String text = values.length == 1 ? text(values[0]) : text(values);
		print(">" + text + "<");
	}

	public static synchronized void schedule(Runnable task, long delay) {
		if (EXECUTOR == null) {
			EXECUTOR = new ScheduledThreadPoolExecutor(3);
		}

		EXECUTOR.schedule(task, delay, TimeUnit.MILLISECONDS);
	}

	public static void startMeasure() {
		measureStart = time();
	}

	public static void endMeasure() {
		long delta = time() - measureStart;
		show(delta + " ms");
	}

	public static void endMeasure(String info) {
		long delta = time() - measureStart;
		show(info + ": " + delta + " ms");
	}

	public static void print(Object value) {
		System.out.println(value);
	}

	public static void printAll(Collection<?> collection) {
		for (Object item : collection) {
			print(item);
		}
	}

	public static boolean hasOption(String name) {
		notNull(ARGS, "command line arguments");

		for (String op : ARGS) {
			if (op.equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}

	public static String option(String name, String defaultValue) {
		notNull(ARGS, "command line arguments");

		for (String op : ARGS) {
			if (op.startsWith(name + "=")) {
				return op.substring(name.length() + 1);
			}
		}

		return defaultValue;
	}

	public static int option(String name, int defaultValue) {
		String n = option(name, null);
		return n != null ? Integer.parseInt(n) : defaultValue;
	}

	public static long optionL(String name, long defaultValue) {
		String n = option(name, null);
		return n != null ? Long.parseLong(n) : defaultValue;
	}

	public static double option(String name, double defaultValue) {
		String n = option(name, null);
		return n != null ? Double.parseDouble(n) : defaultValue;
	}

	public static int cpus() {
		return option("cpus", Runtime.getRuntime().availableProcessors());
	}

	public static boolean micro() {
		return hasOption("micro");
	}

	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static String capitalized(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static String urlDecode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw rte(e);
		}
	}

	public static String mul(String s, int n) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static int num(String s) {
		return Integer.parseInt(s);
	}

	public static String bytesToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	private static MessageDigest digest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw rte("Cannot find algorithm: " + algorithm);
		}
	}

	public static String md5(byte[] bytes) {
		MessageDigest md5 = digest("MD5");
		md5.update(bytes);
		return bytesToString(md5.digest());
	}

	public static String md5(String data) {
		return md5(data.getBytes());
	}

	public static char rndChar() {
		return (char) (65 + rnd(26));
	}

	public static String rndStr(int length) {
		return rndStr(length, length);
	}

	public static String rndStr(int minLength, int maxLength) {
		int len = minLength + rnd(maxLength - minLength + 1);
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len; i++) {
			sb.append(rndChar());
		}

		return sb.toString();
	}

	public static int rnd(int n) {
		return RND.nextInt(n);
	}

	public static int rndExcept(int n, int except) {
		if (n > 1 || except != 0) {
			while (true) {
				int num = RND.nextInt(n);
				if (num != except) {
					return num;
				}
			}
		} else {
			throw new RuntimeException("Cannot produce such number!");
		}
	}

	public static <T> T rnd(T[] arr) {
		return arr[rnd(arr.length)];
	}

	public static int rnd() {
		return RND.nextInt();
	}

	public static long rndL() {
		return RND.nextLong();
	}

	@SuppressWarnings("resource")
	public static MappedByteBuffer mmap(String filename, MapMode mode, long position, long size) {
		try {
			File file = new File(filename);
			FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
			return fc.map(mode, position, size);
		} catch (Exception e) {
			throw rte(e);
		}
	}

	public static MappedByteBuffer mmap(String filename, MapMode mode) {
		File file = new File(filename);
		must(file.exists());
		return mmap(filename, mode, 0, file.length());
	}

	public static Class<?> getClassIfExists(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static boolean production() {
		return hasOption("production");
	}

	public static synchronized String config(String name) {
		if (CONFIG == null) {
			CONFIG = new Properties();

			try {
				URL config = resource("config");
				if (config != null) {
					CONFIG.load(config.openStream());
				}

				config = resource("config.private");
				if (config != null) {
					CONFIG.load(config.openStream());
				}
			} catch (IOException e) {
				throw rte("Cannot load config!", e);
			}
		}

		return CONFIG.getProperty(name);
	}

	public static String fillIn(String template, String placeholder, String value) {
		return template.replaceAll("\\{\\{" + placeholder + "\\}\\}", value);
	}

	public static ByteBuffer expand(ByteBuffer buf, int newSize) {
		ByteBuffer buf2 = ByteBuffer.allocate(newSize);

		ByteBuffer buff = buf.duplicate();
		buff.rewind();
		buff.limit(buff.capacity());

		buf2.put(buff);

		return buf2;
	}

	public static ByteBuffer expand(ByteBuffer buf) {
		int cap = buf.capacity();

		if (cap <= 1000) {
			cap *= 10;
		} else if (cap <= 10000) {
			cap *= 5;
		} else {
			cap *= 2;
		}

		return expand(buf, cap);
	}

	public static String buf2str(ByteBuffer buf) {
		ByteBuffer buf2 = buf.duplicate();

		buf2.rewind();
		buf2.limit(buf2.capacity());

		byte[] bytes = new byte[buf2.capacity()];
		buf2.get(bytes);

		return new String(bytes);
	}

	public static ByteBuffer buf(String s) {
		byte[] bytes = s.getBytes();

		ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);
		buf.put(bytes);
		buf.rewind();

		return buf;
	}

	public static synchronized void args(String... args) {
		if (args != null) {
			ARGS = args;

			// FIXME: use start-up hook to register the stats thread
			// if (hasOption("stats")) {
			// singleton(StatsThread.class).start();
			// }

			if (hasOption("debug") && getLogLevel().ordinal() > DEBUG.ordinal()) {
				setLogLevel(DEBUG);
			}
		}
	}

	public static void benchmark(String name, int count, Runnable runnable) {
		long start = time();

		for (int i = 0; i < count; i++) {
			runnable.run();
		}

		benchmarkComplete(name, count, start);
	}

	public static void benchmarkComplete(String name, int count, long startTime) {
		long end = time();
		long ms = end - startTime;

		if (ms == 0) {
			ms = 1;
		}

		double avg = ((double) count / (double) ms);

		String avgs = avg > 1 ? Math.round(avg) + "K" : Math.round(avg * 1000) + "";

		String data = format("%s: %s in %s ms (%s/sec)", name, count, ms, avgs);

		print(data + " | " + getCpuMemStats());
	}

	public static void benchmarkMT(int threadsN, final String name, final int count, final Runnable runnable) {
		long time = time();

		final CountDownLatch latch = new CountDownLatch(threadsN);

		for (int i = 1; i <= threadsN; i++) {
			new Thread() {
				public void run() {
					benchmark(name, count, runnable);
					latch.countDown();
				};
			}.start();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw rte(e);
		}

		benchmarkComplete("avg(" + name + ")", threadsN * count, time);
	}

	public static String getCpuMemStats() {
		Runtime rt = Runtime.getRuntime();
		long totalMem = rt.totalMemory();
		long maxMem = rt.maxMemory();
		long freeMem = rt.freeMemory();
		long usedMem = totalMem - freeMem;
		int megs = 1024 * 1024;

		String msg = "MEM [total=%s MB, used=%s MB, max=%s MB]";
		return format(msg, totalMem / megs, usedMem / megs, maxMem / megs);
	}

	public static String replace(String s, String regex, Mapper<String[], String> replacer) {
		StringBuffer output = new StringBuffer();
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(s);

		while (matcher.find()) {
			int len = matcher.groupCount() + 1;
			String[] gr = new String[len];

			for (int i = 0; i < gr.length; i++) {
				gr[i] = matcher.group(i);
			}

			matcher.appendReplacement(output, eval(replacer, gr));
		}

		matcher.appendTail(output);
		return output.toString();
	}

	public static <T> boolean eval(Predicate<T> predicate, T target) {
		try {
			return predicate.eval(target);
		} catch (Exception e) {
			throw U.rte("Cannot evaluate predicate %s on target: %s", e, predicate, target);
		}
	}

	public static <FROM, TO> TO eval(Mapper<FROM, TO> mapper, FROM src) {
		try {
			return mapper.map(src);
		} catch (Exception e) {
			throw U.rte("Cannot evaluate mapper %s on target: %s", e, mapper, src);
		}
	}

	public static List<Class<?>> classpathClasses(String packageName, String nameRegex, Predicate<Class<?>> filter) {

		Pattern regex = Pattern.compile(nameRegex);
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		Enumeration<URL> urls = resources(packageName);

		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			File file = new File(url.getFile());

			getClasses(classes, file, file, regex, filter);
		}

		return classes;
	}

	public static List<File> classpath(String packageName, Predicate<File> filter) {
		ArrayList<File> files = new ArrayList<File>();

		classpath(packageName, files, filter);

		return files;
	}

	public static void classpath(String packageName, Collection<File> files, Predicate<File> filter) {
		Enumeration<URL> urls = resources(packageName);

		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			File file = new File(url.getFile());

			getFiles(files, file, filter);
		}
	}

	private static void getFiles(Collection<File> files, File file, Predicate<File> filter) {
		if (file.isDirectory()) {
			U.debug("scanning directory", "dir", file);
			for (File f : file.listFiles()) {
				if (f.isDirectory()) {
					getFiles(files, f, filter);
				} else {
					U.debug("scanned file", "file", f);
					try {
						if (filter == null || filter.eval(f)) {
							files.add(f);
						}
					} catch (Exception e) {
						throw U.rte(e);
					}
				}
			}
		}
	}

	private static void getClasses(Collection<Class<?>> classes, File root, File parent, Pattern nameRegex,
			Predicate<Class<?>> filter) {

		if (parent.isDirectory()) {
			U.debug("scanning directory", "dir", parent);
			for (File f : parent.listFiles()) {
				if (f.isDirectory()) {
					getClasses(classes, root, f, nameRegex, filter);
				} else {
					U.debug("scanned file", "file", f);
					try {
						if (f.getName().endsWith(".class")) {
							String clsName = f.getAbsolutePath();
							String rootPath = root.getAbsolutePath();
							U.must(clsName.startsWith(rootPath));

							clsName = clsName.substring(rootPath.length() + 1, clsName.length() - 6);
							clsName = clsName.replace(File.separatorChar, '.');

							if (nameRegex.matcher(clsName).matches()) {
								U.info("loading class", "name", clsName);
								Class<?> cls = Class.forName(clsName);
								if (filter == null || filter.eval(cls)) {
									classes.add(cls);
								}
							}
						}
					} catch (Exception e) {
						throw U.rte(e);
					}
				}
			}
		}
	}

}
