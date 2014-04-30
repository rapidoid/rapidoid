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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class U {

	public static final Charset UTF8 = Charset.forName("UTF-8");

	private static final int TRACE = 0;
	private static final int DEBUG = 1;
	private static final int INFO = 2;
	private static final int WARN = 3;
	private static final int ERROR = 4;
	private static final int SEVERE = 5;

	private static int LOG_LEVEL = INFO;

	private static final String[] LOG_LEVELS = { "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "SEVERE" };

	private static final Map<Class<?>, Object> SINGLETONS = map();

	private static ScheduledThreadPoolExecutor EXECUTOR;

	private static long measureStart;

	private static String[] args;

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

	private static void log(Appendable out, int level, String msg, String key1, Object value1, String key2,
			Object value2, String key3, Object value3, int paramsN) {
		if (level >= LOG_LEVEL) {
			try {
				synchronized (out) {
					out.append(LOG_LEVELS[level]);
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

					out.append("\n");
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

	private static void log(int level, String msg, String key1, Object value1, String key2, Object value2, String key3,
			Object value3, int paramsN) {
		log(System.out, level, msg, key1, value1, key2, value2, key3, value3, paramsN);
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

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// do nothing
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

	public static String join(Object[] items, String sep) {
		return render(items, "%s", sep);
	}

	public static String join(Iterable<?> items, String sep) {
		return render(items, "%s", sep);
	}

	public static String render(Object[] items, String itemFormat, String sep) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			if (i > 0) {
				sb.append(sep);
			}
			sb.append(String.format(itemFormat, items[i]));
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

			sb.append(String.format(itemFormat, item));
			i++;
		}

		return sb.toString();
	}

	public static URL resource(String filename) {
		return U.class.getClassLoader().getResource(filename);
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

	public static String load(String name) {

		InputStream stream = U.class.getClassLoader().getResourceAsStream(name);

		InputStreamReader reader = new InputStreamReader(stream);

		BufferedReader r = new BufferedReader(reader);

		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = r.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw rte("Cannot read resource: " + name, e);
		}

		return sb.toString();
	}

	public static void save(String filename, String content) {
		// FIXME
		throw notReady();
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

		ensure(start <= end, "Invalid range: expected form <= to!");

		int size = end - start + 1;

		T[] part = Arrays.copyOf(arr, size);

		System.arraycopy(arr, start, part, 0, size);

		return part;
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

	public static void waitFor(AtomicBoolean done) {
		while (!done.get()) {
			U.sleep(5);
		}
	}

	public static void waitFor(AtomicInteger n, int value) {
		while (n.get() != value) {
			U.sleep(5);
		}
	}

	public static byte[] serialize(Object value) {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			ObjectOutputStream out = new ObjectOutputStream(output);
			out.writeObject(value);
			output.close();

			return output.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object deserialize(byte[] buf) {
		try {
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf));
			Object obj = in.readObject();
			in.close();
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void serialize(Object value, ByteBuffer buf) {
		byte[] bytes = serialize(value);
		buf.putInt(bytes.length);
		buf.put(bytes);
	}

	public static Object deserialize(ByteBuffer buf) {
		int len = buf.getInt();
		byte[] bytes = new byte[len];
		buf.get(bytes);
		return deserialize(bytes);
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

		ByteBuffer buf = ByteBuffer.allocate(bytes.length);
		buf.put(bytes);
		buf.rewind();

		return buf;
	}

	public static String copyNtimes(String s, int n) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static RuntimeException rte(String message, Throwable cause) {
		return new RuntimeException(message, cause);
	}

	public static RuntimeException rte(Throwable cause) {
		return new RuntimeException(cause);
	}

	public static RuntimeException rte(String message, Object... args) {
		return new RuntimeException(String.format(message, args));
	}

	public static RuntimeException rte(String message, Throwable cause, Object... args) {
		return new RuntimeException(String.format(message, args), cause);
	}

	public static void ensure(boolean expectedCondition) {
		if (!expectedCondition) {
			throw rte("Expectation failed!");
		}
	}

	public static void ensure(boolean expectedCondition, String message) {
		if (!expectedCondition) {
			throw rte(message);
		}
	}

	public static void ensure(boolean expectedCondition, String message, long arg) {
		if (!expectedCondition) {
			throw rte(message, arg);
		}
	}

	public static void ensure(boolean expectedCondition, String message, Object arg) {
		if (!expectedCondition) {
			throw rte(message, arg);
		}
	}

	public static void ensure(boolean expectedCondition, String message, Object arg1, Object arg2) {
		if (!expectedCondition) {
			throw rte(message, arg1, arg2);
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

	public static RuntimeException notExpected() {
		return rte("This operation is not expected to be called!");
	}

	public static String stackTraceOf(Throwable e) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(output));
		return output.toString();
	}

	public static void benchmark(String name, int count, Runnable runnable) {
		long start = Calendar.getInstance().getTimeInMillis();

		for (int i = 0; i < count; i++) {
			runnable.run();
		}

		long end = Calendar.getInstance().getTimeInMillis();
		long ms = end - start;

		if (ms == 0) {
			ms = 1;
		}

		double avg = ((double) count / (double) ms);

		String avgs = avg > 1 ? Math.round(avg) + "K" : Math.round(avg * 1000) + "";

		String data = String.format("%s: %s in %s ms (%s/sec)", name, count, ms, avgs);

		U.print(data + " | " + getCpuMemStats());
	}

	public static String getCpuMemStats() {
		Runtime rt = Runtime.getRuntime();
		long totalMem = rt.totalMemory();
		long maxMem = rt.maxMemory();
		long freeMem = rt.freeMemory();
		long usedMem = totalMem - freeMem;
		int megs = 1024 * 1024;

		String gcinfo = "";
		List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
		for (GarbageCollectorMXBean gc : gcs) {
			gcinfo += " | " + gc.getName() + " x " + gc.getCollectionCount() + " (" + gc.getCollectionTime() + " ms)";
		}

		String msg = "MEM [total=%s MB, used=%s MB, max=%s MB]%s";
		return String.format(msg, totalMem / megs, usedMem / megs, maxMem / megs, gcinfo);
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(InvocationHandler handler, Class<?>... interfaces) {
		return ((T) Proxy.newProxyInstance(U.class.getClassLoader(), interfaces, handler));
	}

	public static <T> T implementInterfaces(final Object target, final InvocationHandler handler,
			Class<?>... interfaces) {
		final Class<?> targetClass = target.getClass();

		return createProxy(new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

				if (method.getDeclaringClass().isAssignableFrom(targetClass)) {
					return method.invoke(target, args);
				}

				return handler.invoke(proxy, method, args);
			}

		}, interfaces);
	}

	public static <T> T tracer(Class<T> interfacee, T implementation) {
		return createProxy(new InvocationHandler() {
			@Override
			public Object invoke(Object target, Method method, Object[] args) throws Throwable {
				U.print("* called " + method.getName() + " (" + Arrays.toString(args) + ")");
				return method.invoke(target, args);
			}
		}, interfacee);
	}

	public static void show(Object... values) {
		U.print(">" + join(values, ", ") + "<");
	}

	public static <T extends RuntimeException> T rte(Class<T> clazz) {
		return newInstance(clazz);
	}

	public static <T extends RuntimeException> T rte(Class<T> clazz, String msg) {
		return newInstance(clazz, msg);
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

		throw rte("Cannot find appropriate constructor!");
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
		show(delta);
	}

	public static void print(Object value) {
		System.out.println(value);
	}

	@SuppressWarnings("unchecked")
	public static synchronized <T> T singleton(Class<T> clazz) {
		T instance = (T) SINGLETONS.get(clazz);

		if (instance == null) {
			instance = newInstance(clazz);
			SINGLETONS.put(clazz, instance);
		}

		return instance;
	}

	public static void args(String[] args) {
		U.args = args;
	}

	public static boolean hasOption(String name) {
		notNull(args, "command line arguments");

		for (String op : args) {
			if (op.equalsIgnoreCase(name)) {
				return true;
			}
		}

		return false;
	}

	public static String option(String name) {
		notNull(args, "command line arguments");

		for (String op : args) {
			if (op.startsWith(name + "=")) {
				return op.substring(name.length() + 1);
			}
		}

		return null;
	}

	public static int option(String name, int defaultVal) {
		String n = option(name);
		return n != null ? Integer.parseInt(n) : defaultVal;
	}

	public static boolean isEmpty(String value) {
		return value == null || !value.isEmpty();
	}

	public static void connect(String address, int port, F2<Void, BufferedReader, DataOutputStream> logic) {
		Socket clientSocket = null;

		try {
			clientSocket = new Socket(address, port);
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			logic.execute(in, out);

			clientSocket.close();
		} catch (Exception e) {
			throw U.rte(e);
		} finally {
			if (clientSocket != null) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					throw U.rte(e);
				}
			}
		}
	}

}
