package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.AbstractLoopThread;
import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.activity.RapidoidThreadFactory;
import org.rapidoid.activity.RapidoidThreadLocals;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Profiles;
import org.rapidoid.annotation.Run;
import org.rapidoid.annotation.Since;
import org.rapidoid.cache.Caching;
import org.rapidoid.cls.Cls;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Str;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.config.ConfigOptions;
import org.rapidoid.crypto.Crypto;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.env.Env;
import org.rapidoid.event.Events;
import org.rapidoid.group.Groups;
import org.rapidoid.insight.Insights;
import org.rapidoid.io.IO;
import org.rapidoid.io.Res;
import org.rapidoid.job.Jobs;
import org.rapidoid.lambda.Dynamic;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Operation;
import org.rapidoid.log.GlobalCfg;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.validation.InvalidData;
import org.rapidoid.wrap.BoolWrap;
import org.rapidoid.writable.ReusableWritable;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Msc extends RapidoidThing {

	private static final String SPECIAL_ARG_REGEX = "\\s*(.*?)\\s*(->|<-|:=|<=|=>|==)\\s*(.*?)\\s*";

	public static final String OS_NAME = System.getProperty("os.name");

	private static volatile String uid;

	private static volatile long measureStart;

	private static boolean platform;

	private static boolean mavenBuild;

	public static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(8,
		new RapidoidThreadFactory("utils", true));

	public static final Mapper<Object, Object> TRANSFORM_TO_STRING = new Mapper<Object, Object>() {
		@Override
		public Object map(Object src) throws Exception {
			return src != null ? src.toString() : null;
		}
	};

	public static final Mapper<Object, Object> TRANSFORM_TO_SIMPLE_CLASS_NAME = new Mapper<Object, Object>() {
		@Override
		public Object map(Object src) throws Exception {
			if (src == null) {
				return null;
			}

			if (src instanceof Class<?>) {
				return ((Class<?>) src).getSimpleName();
			} else {
				return src.getClass().getName() + "@" + System.identityHashCode(src);
			}
		}
	};

	private Msc() {
	}

	public static byte[] serialize(Object value) {
		try {
			ReusableWritable output = new ReusableWritable();

			ObjectOutputStream out = new ObjectOutputStream(output);
			out.writeObject(value);
			output.close();

			return output.copy();
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

	public static String stackTraceOf(Throwable e) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(output));
		return output.toString();
	}

	public static short bytesToShort(String s) {
		ByteBuffer buf = Bufs.buf(s);
		U.must(buf.limit() == 2);
		return buf.getShort();
	}

	public static int bytesToInt(String s) {
		ByteBuffer buf = Bufs.buf(s);
		U.must(buf.limit() == 4);
		return buf.getInt();
	}

	public static long bytesToLong(String s) {
		ByteBuffer buf = Bufs.buf(s);
		U.must(buf.limit() == 8);
		return buf.getLong();
	}

	public static int intFrom(byte a, byte b, byte c, byte d) {
		return (a << 24) + (b << 16) + (c << 8) + d;
	}

	public static short shortFrom(byte a, byte b) {
		return (short) ((a << 8) + b);
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

	public static void joinThread(Thread thread) {
		try {
			thread.join();
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	public static void benchmark(String name, int count, final Runnable runnable) {
		doBenchmark(name, count, new BenchmarkOperation() {
			@Override
			public void run(int iteration) {
				runnable.run();
			}
		}, false);
	}

	public static void benchmark(String name, int count, BenchmarkOperation operation) {
		doBenchmark(name, count, operation, false);
	}

	public static void doBenchmark(String name, int count, BenchmarkOperation operation, boolean silent) {
		long start = U.time();

		for (int i = 0; i < count; i++) {
			operation.run(i);
		}

		if (!silent) {
			benchmarkComplete(name, count, start);
		}
	}

	public static void benchmarkComplete(String name, int count, long startTime) {

		long end = U.time();
		long ms = end - startTime;

		if (ms == 0) {
			ms = 1;
		}

		double avg = ((double) count / (double) ms);

		String avgs;

		if (avg > 1) {
			if (avg < 1000) {
				avgs = Math.round(avg) + "K";
			} else {
				avgs = Math.round(avg / 100) / 10.0 + "M";
			}
		} else {
			avgs = Math.round(avg * 1000) + "";
		}

		String data = String.format("%s: %s in %s ms (%s/sec)", name, count, ms, avgs);

		Log.info(data + " | " + Insights.getCpuMemStats());
	}

	public static void benchmarkMT(int threadsN, final String name, final int count, final CountDownLatch outsideLatch,
	                               final BenchmarkOperation operation) {

		U.must(count % threadsN == 0, "The number of thread must be a factor of the total count!");
		final int countPerThread = count / threadsN;

		final CountDownLatch latch = outsideLatch != null ? outsideLatch : new CountDownLatch(threadsN);

		long time = U.time();

		final Ctx ctx = Ctxs.get();

		for (int i = 1; i <= threadsN; i++) {
			new RapidoidThread() {

				@Override
				public void run() {
					Ctxs.attach(ctx != null ? ctx.span() : null);

					try {
						doBenchmark(name, countPerThread, operation, true);
						if (outsideLatch == null) {
							latch.countDown();
						}

					} finally {
						if (ctx != null) {
							Ctxs.close();
						}
					}
				}

			}.start();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw U.rte(e);
		}

		benchmarkComplete("avg(" + name + ")", threadsN * countPerThread, time);
	}

	public static void benchmarkMT(int threadsN, final String name, final int count, final Runnable runnable) {
		benchmarkMT(threadsN, name, count, null, new BenchmarkOperation() {
			@Override
			public void run(int iteration) {
				runnable.run();
			}
		});
	}

	public static void benchmarkMT(int threadsN, final String name, final int count, final BenchmarkOperation operation) {
		benchmarkMT(threadsN, name, count, null, operation);
	}

	public static String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw U.rte(e);
		}
	}

	public static String urlDecode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw U.rte(e);
		}
	}

	public static String urlDecodeOrKeepOriginal(String s) {
		try {
			return urlDecode(s);
		} catch (IllegalArgumentException e) {
			return s;
		}
	}

	public static void startMeasure() {
		measureStart = U.time();
	}

	public static void endMeasure() {
		long delta = U.time() - measureStart;
		Log.info("Benchmark", "time", delta + " ms");
	}

	public static void endMeasure(String info) {
		long delta = U.time() - measureStart;
		Log.info("Benchmark", "info", info, "time", delta + " ms");
	}

	public static void endMeasure(long count, String info) {
		long delta = U.time() - measureStart;
		long freq = Math.round(1000 * (double) count / delta);
		Log.info("Benchmark", "performance", U.frmt("%s %s in %s ms (%s/sec)", count, info, delta, freq));
	}

	public static Throwable rootCause(Throwable e) {
		while (e.getCause() != null) {
			e = e.getCause();
		}
		return e;
	}

	public static String fillIn(String template, String placeholder, String value) {
		return template.replace("{{" + placeholder + "}}", value);
	}

	public static String fillIn(String template, Object... namesAndValues) {
		String text = template.toString();

		for (int i = 0; i < namesAndValues.length / 2; i++) {
			String placeholder = (String) namesAndValues[i * 2];
			String value = Cls.str(namesAndValues[i * 2 + 1]);

			text = fillIn(text, placeholder, value);
		}

		return text;
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> lowercase(Map<String, T> map) {
		Map<String, T> lower = U.map();

		for (Entry<String, T> e : map.entrySet()) {
			T val = e.getValue();
			if (val instanceof String) {
				val = (T) ((String) val).toLowerCase();
			}
			lower.put(e.getKey().toLowerCase(), val);
		}

		return lower;
	}

	public static void multiThreaded(int threadsN, final Mapper<Integer, Void> executable) {

		final CountDownLatch latch = new CountDownLatch(threadsN);

		for (int i = 1; i <= threadsN; i++) {
			final Integer n = i;
			new Thread() {
				@Override
				public void run() {
					Lmbd.eval(executable, n);
					latch.countDown();
				}

				;
			}.start();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void multiThreaded(int threadsN, final Runnable executable) {
		multiThreaded(threadsN, new Mapper<Integer, Void>() {
			@Override
			public Void map(Integer n) throws Exception {
				executable.run();
				return null;
			}

		});
	}

	public static void append(StringBuilder sb, String separator, String value) {
		if (sb.length() > 0) {
			sb.append(separator);
		}
		sb.append(value);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T serializable(Object value) {
		if (value == null || value instanceof Serializable) {
			return (T) value;
		} else {
			throw U.rte("Not serializable: " + value);
		}
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> cast(Map<?, ?> map) {
		return (Map<K, V>) map;
	}

	public static RapidoidThread loop(final Runnable loop) {
		RapidoidThread thread = new AbstractLoopThread() {
			@Override
			protected void loop() {
				loop.run();
			}
		};

		thread.start();

		return thread;
	}

	public static Class<?> getCallingClass(Class<?>... ignoreClasses) {
		return inferCaller(ignoreClasses);
	}

	public static String getCallingPackage(Class<?>... ignoreClasses) {
		Class<?> callerCls = inferCaller(ignoreClasses);

		if (callerCls != null) {
			return callerCls.getPackage() != null ? callerCls.getPackage().getName() : "";
		} else {
			return null;
		}
	}

	private static boolean couldBeCaller(String cls) {
		return !Cls.isRapidoidClass(cls) && !Cls.isJREClass(cls) && !Cls.isIdeOrToolClass(cls);
	}

	private static Class<?> inferCaller(Class<?>... ignoreClasses) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		// skip the first 2 elements:
		// [0] java.lang.Thread.getStackTrace
		// [1] THIS METHOD

		for (int i = 2; i < trace.length; i++) {
			String cls = trace[i].getClassName();
			if (couldBeCaller(cls) && !shouldIgnore(cls, ignoreClasses)) {
				try {
					return Class.forName(cls);
				} catch (ClassNotFoundException e) {
					Log.error("Couldn't load the caller class!", e);
					return null;
				}
			}
		}

		return null;
	}

	public static Class<?> getCallingMainClass() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		// skip the first 2 elements:
		// [0] java.lang.Thread.getStackTrace
		// [1] THIS METHOD

		for (int i = 2; i < trace.length; i++) {
			String cls = trace[i].getClassName();

			if (couldBeCaller(cls) && U.eq(trace[i].getMethodName(), "main")) {
				Class<?> clazz = Cls.getClassIfExists(cls);

				if (clazz != null) {
					Method main = Cls.findMethod(clazz, "main", String[].class);
					if (main != null && main.getReturnType() == void.class
						&& !main.isVarArgs() && main.getDeclaringClass().equals(clazz)) {
						int modif = main.getModifiers();
						if (Modifier.isStatic(modif) && Modifier.isPublic(modif)) {
							return clazz;
						}
					}
				}
			}
		}

		return null;
	}

	private static boolean shouldIgnore(String cls, Class<?>[] ignoreClasses) {
		for (Class<?> ignoreClass : ignoreClasses) {
			if (cls.equals(ignoreClass.getCanonicalName())) {
				return true;
			}
		}

		return false;
	}

	public static byte[] toBytes(Object obj) {

		if (obj instanceof byte[]) {
			return (byte[]) obj;

		} else if (obj instanceof ByteBuffer) {
			return Bufs.buf2bytes((ByteBuffer) obj);

		} else if (obj instanceof InputStream) {
			return IO.loadBytes((InputStream) obj);

		} else if (obj instanceof File) {
			Res res = Res.from((File) obj);
			res.mustExist();
			return res.getBytes();

		} else if (obj instanceof Res) {
			Res res = (Res) obj;
			res.mustExist();
			return res.getBytes();

		} else {

			// this might be a Widget, so rendering it requires double toString:
//			U.str(obj); // 1. data binding and event processing
			return U.str(obj).getBytes(); // 2. actual rendering
		}
	}

	public static boolean isArray(Object value) {
		return value != null && value.getClass().isArray();
	}

	public static Object[] deleteAt(Object[] arr, int index) {

		Object[] res = new Object[arr.length - 1];

		if (index > 0) {
			System.arraycopy(arr, 0, res, 0, index);
		}

		if (index < arr.length - 1) {
			System.arraycopy(arr, index + 1, res, index, res.length - index);
		}

		return res;
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

	public static boolean exists(Callable<?> accessChain) {
		try {
			return accessChain != null && accessChain.call() != null;
		} catch (NullPointerException e) {
			return false;
		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	public static String uri(String... parts) {
		return "/" + constructPath("/", false, false, parts);
	}

	public static String path(String... parts) {
		return constructPath(File.separator, true, false, parts);
	}

	private static String constructPath(String separator, boolean preserveFirstSegment, boolean uriEscape, String... parts) {
		String s = "";

		for (int i = 0; i < parts.length; i++) {
			String part = U.safe(parts[i]);

			// trim '/'s and '\'s
			if (!preserveFirstSegment || i > 0) {
				part = Str.triml(part, "/");
			}

			if (!preserveFirstSegment || part.length() > 1 || i > 0) {
				part = Str.trimr(part, "/");
				part = Str.trimr(part, "\\");
			}

			if (!U.isEmpty(part)) {
				if (!s.isEmpty() && !s.endsWith(separator)) {
					s += separator;
				}

				if (uriEscape) part = urlEncode(part);

				s += part;
			}
		}

		return s;
	}

	public static String refinePath(String path) {
		boolean absolute = path.startsWith("/");
		path = path(path.split("/"));
		return absolute ? "/" + path : path;
	}

	public static int countNonNull(Object... values) {
		int n = 0;

		for (Object value : values) {
			if (value != null) {
				n++;
			}
		}

		return n;
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

				return dynamic.call(method, U.safe(args));
			}

		};

		return ((T) Proxy.newProxyInstance(targetInterface.getClassLoader(), new Class[]{targetInterface}, handler));
	}

	public static boolean withWatchModule() {
		return Cls.getClassIfExists("org.rapidoid.io.watch.Watch") != null;
	}

	public static void terminate(final int afterSeconds) {
		Log.warn("Terminating application in " + afterSeconds + " seconds...");
		new Thread() {
			@Override
			public void run() {
				U.sleep(afterSeconds * 1000);
				terminate();
			}
		}.start();
	}

	public static void terminateIfIdleFor(final int idleSeconds) {
		Log.warn("Will terminate if idle for " + idleSeconds + " seconds...");

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					U.sleep(500);
					long lastUsed = Usage.getLastAppUsedOn();
					long idleSec = (U.time() - lastUsed) / 1000;
					if (idleSec >= idleSeconds) {
						Usage.touchLastAppUsedOn();
						terminate();
					}
				}
			}
		}).start();
	}

	public static void terminate() {
		Log.warn("Terminating application.");
		System.exit(0);
	}

	public static byte sbyte(int n) {
		return (byte) (n - 128);
	}

	public static int ubyte(byte b) {
		return b + 128;
	}

	public static void logSection(String msg) {
		Log.info("!" + Str.mul("-", msg.length()));
		Log.info(msg);
		Log.info("!" + Str.mul("-", msg.length()));
	}

	public static void logProperties(Properties props) {
		for (Entry<Object, Object> p : props.entrySet()) {
			Log.info("Hibernate property", String.valueOf(p.getKey()), p.getValue());
		}
	}

	public static boolean isValidationError(Throwable error) {
		return (error instanceof InvalidData) || error.getClass().getName().startsWith("javax.validation.");
	}

	public static <T> List<T> page(Iterable<T> items, int page, int pageSize) {
		return Coll.range(items, (page - 1) * pageSize, page * pageSize);
	}

	public static List<?> getPage(Iterable<?> items, int page, int pageSize, Integer size, BoolWrap isLastPage) {
		int pageFrom = Math.max((page - 1) * pageSize, 0);
		int pageTo = (page) * pageSize + 1;

		if (size != null) {
			pageTo = Math.min(pageTo, size);
		}

		List<?> range = U.list(Coll.range(items, pageFrom, pageTo));
		isLastPage.value = range.size() < pageSize + 1;

		if (!isLastPage.value && !range.isEmpty()) {
			range.remove(range.size() - 1);
		}

		return range; // 1 item extra, to test if there are more results
	}

	public static void invokeMain(Class<?> clazz, String[] args) {
		Method main = Cls.getMethod(clazz, "main", String[].class);

		U.must(main.getReturnType() == void.class);
		U.must(Modifier.isPublic(main.getModifiers()));
		U.must(Modifier.isStatic(main.getModifiers()));

		Cls.invokeStatic(main, new Object[]{args});
	}

	public static void filterAndInvokeMainClasses(Object[] beans, Set<Class<?>> invoked) {
		List<Class<?>> toInvoke = U.list();

		for (Object bean : beans) {
			U.notNull(bean, "bean");

			if (bean instanceof Class<?>) {
				Class<?> clazz = (Class<?>) bean;
				if (Cls.isAnnotated(clazz, Run.class) && !invoked.contains(clazz)) {
					toInvoke.add(clazz);
				}
			}
		}

		invoked.addAll(toInvoke);

		for (Class<?> clazz : toInvoke) {
			Msc.logSection("Invoking @Run component: " + clazz.getName());
			String[] args = U.arrayOf(String.class, Env.args());
			Msc.invokeMain(clazz, args);
		}
	}

	public static String annotations(Class<? extends Annotation>[] annotations) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		if (annotations != null) {
			for (int i = 0; i < annotations.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append("@");
				sb.append(annotations[i].getSimpleName());
			}
		}

		sb.append("]");
		return sb.toString();
	}

	public static String classes(List<Class<?>> classes) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		if (classes != null) {
			for (int i = 0; i < classes.size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(classes.get(i).getSimpleName());

				if (i >= 100) {
					sb.append("...");
					break;
				}
			}
		}

		sb.append("]");
		return sb.toString();
	}

	public static String classNames(List<String> classes) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");

		if (classes != null) {
			for (int i = 0; i < classes.size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(U.last(classes.get(i).split("\\.")));

				if (i >= 100) {
					sb.append("...");
					break;
				}
			}
		}

		sb.append("]");
		return sb.toString();
	}

	public static String textToId(String s) {
		s = s.replaceAll("[^0-9A-Za-z]+", "-");
		s = Str.triml(s, "-");
		s = Str.trimr(s, "-");
		s = s.toLowerCase();
		return s;
	}

	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> protectSensitiveInfo(Map<String, T> data, T replacement) {
		Map<String, T> copy = U.map();

		for (Map.Entry<String, T> e : data.entrySet()) {
			T value = e.getValue();

			String key = e.getKey().toLowerCase();

			if (value instanceof Map<?, ?>) {
				value = (T) protectSensitiveInfo((Map<String, T>) value, replacement);

			} else if (sensitiveKey(key)) {
				value = replacement;
			}

			copy.put(e.getKey(), value);
		}

		return copy;
	}

	public static boolean sensitiveKey(String key) {
		return key.contains("password") || key.contains("secret") || key.contains("token") || key.contains("private");
	}

	public static int processId() {
		return U.num(processName().split("@")[0]);
	}

	public static String processName() {
		return ManagementFactory.getRuntimeMXBean().getName();
	}

	public static String javaVersion() {
		return System.getProperty("java.version");
	}

	public static boolean matchingProfile(Class<?> clazz) {
		Profiles profiles = clazz.getAnnotation(Profiles.class);
		return profiles == null || Env.hasAnyProfile(profiles.value());
	}

	public static boolean isInsideTest() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		for (StackTraceElement traceElement : trace) {
			String cls = traceElement.getClassName();

			if (cls.startsWith("org.junit.") || cls.startsWith("org.testng.")) {
				return true;
			}
		}

		return false;
	}

	public static Thread thread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.setName("Msc-thread-" + runnable.getClass());
		thread.start();
		return thread;
	}

	public static void reset() {
		Env.reset();
		Events.reset();
		Log.reset();
		Crypto.reset();
		Res.reset();
		AppInfo.reset();
		Conf.reset();
		Groups.reset();
		Jobs.reset();
		Env.reset();
		Caching.reset();

		Ctxs.reset();
		U.must(Ctxs.get() == null);

		resetState();
	}

	private static void resetState() {
		uid = null;
		measureStart = 0;
	}

	public static boolean isAscii(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) > 127) return false;
		}
		return true;
	}

	public static RapidoidThreadLocals locals() {
		return RapidoidThreadLocals.get();
	}

	public static boolean bootService(Config config, String service) {

		List<String> services = config.entry("services").list();

		for (String srvc : services) {
			U.must(ConfigOptions.SERVICE_NAMES.contains(srvc), "Unknown service: '%s'!", srvc);
		}

		return services.contains(service);
	}

	public static boolean dockerized() {
		return U.eq(System.getenv("RAPIDOID_JAR"), "/opt/rapidoid.jar")
			&& U.eq(System.getenv("RAPIDOID_TMP"), "/tmp/rapidoid")
			&& U.notEmpty(System.getenv("RAPIDOID_VERSION"))
			&& hasAppFolder();
	}

	private static boolean hasAppFolder() {
		File app = new File("/app");
		return app.exists() && app.isDirectory();
	}

	public static Object maybeMasked(Object value) {
		return GlobalCfg.uniformOutput() ? "<?>" : value;
	}

	public static synchronized String id() {
		if (uid == null) uid = Conf.ROOT.entry("id").or(processName());
		return uid;
	}

	public static boolean hasConsole() {
		return System.console() != null;
	}

	public static Map<String, Object> parseArgs(List<String> args) {
		Map<String, Object> arguments = U.map();

		for (String arg : U.safe(args)) {
			if (!isSpecialArg(arg)) {

				String[] parts = arg.split("=", 2);
				String name = parts[0];

				if (parts.length > 1) {
					String value = parts[1];
					arguments.put(name, value);
				} else {
					arguments.put(name, true);
				}

			} else {
				processSpecialArg(arguments, arg);
			}
		}

		return arguments;
	}

	private static boolean isSpecialArg(String arg) {
		return arg.matches(SPECIAL_ARG_REGEX);
	}

	private static void processSpecialArg(Map<String, Object> arguments, String arg) {
		Matcher m = Pattern.compile(SPECIAL_ARG_REGEX).matcher(arg);
		U.must(m.matches(), "Invalid argument");

		String left = m.group(1);
		String sep = m.group(2);
		String right = m.group(3);

		switch (sep) {

			case "->":
				left = "proxy." + left;
				break;

			case "<=":
				left = "api." + left;
				break;

			case "<-":
				left = "pages." + left;
				break;

			default:
				throw U.rte("Argument operator not supported: " + sep);
		}

		Log.info("Replacing configuration shortcut", "shortcut", arg, "key", left, "value", right);

		arguments.put(left, right);
	}

	public static boolean isDev() {
		if (Env.isInitialized()) {
			return Env.dev();
		}

		return !Msc.isInsideTest() && Env.dev();
	}

	public static String fileSizeReadable(String filename) {
		long sizeKB = Math.round(new File(filename).length() / 1024.0);
		return sizeKB + " KB";
	}

	public static void watchForChanges(String path, Operation<String> changeListener) {
		Class<?> watch = Cls.getClassIfExists("org.rapidoid.io.watch.Watch");

		if (watch != null) {
			Method dir = Cls.getMethod(watch, "dir", String.class, Operation.class);
			Cls.invokeStatic(dir, path, changeListener);
		}
	}

	public static byte[] uuidToBytes(UUID uuid) {
		ByteBuffer buf = ByteBuffer.wrap(new byte[16]);

		buf.putLong(uuid.getMostSignificantBits());
		buf.putLong(uuid.getLeastSignificantBits());

		return buf.array();
	}

	public static UUID bytesToUUID(byte[] bytes) {
		U.must(bytes.length == 16, "Expected 16 bytes, got: %s", bytes.length);
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		return new UUID(buf.getLong(), buf.getLong());
	}

	public static <T> T normalOrHeavy(T normal, T heavy) {
		return GlobalCfg.is("RAPIDOID_TEST_HEAVY") ? heavy : normal;
	}

	public static Method getTestMethodIfExists() {
		Method method = null;

		for (StackTraceElement trc : Thread.currentThread().getStackTrace()) {
			try {
				Class<?> logCls = Class.forName(trc.getClassName());

				for (Method m : logCls.getMethods()) {
					if (m.getName().equals(trc.getMethodName())) {
						for (Annotation ann : m.getDeclaredAnnotations()) {
							if (ann.annotationType().getSimpleName().equals("Test")) {
								method = m;
							}
						}
					}
				}

			} catch (Exception e) {
				// do nothing
			}
		}

		return method;
	}


	public static void setPlatform(boolean platform) {
		Msc.platform = platform;
	}

	public static boolean isPlatform() {
		return platform;
	}

	public static boolean isMavenBuild() {
		return mavenBuild;
	}

	public static void setMavenBuild(boolean mavenBuild) {
		Msc.mavenBuild = mavenBuild;
	}

	public static String errorMsg(Throwable error) {
		return getErrorCodeAndMsg(error).msg();
	}

	public static ErrCodeAndMsg getErrorCodeAndMsg(Throwable err) {
		Throwable cause = Msc.rootCause(err);

		int code;
		String defaultMsg;
		String msg = cause.getMessage();

		if (cause instanceof SecurityException) {
			code = 403;
			defaultMsg = "Access Denied!";

		} else if (cause.getClass().getSimpleName().equals("NotFound")) {
			code = 404;
			defaultMsg = "The requested resource could not be found!";

		} else if (Msc.isValidationError(cause)) {
			code = 422;
			defaultMsg = "Validation Error!";

			if (cause.getClass().getName().equals("javax.validation.ConstraintViolationException")) {
				Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) cause).getConstraintViolations();

				StringBuilder sb = new StringBuilder();
				sb.append("Validation failed: ");

				for (Iterator<ConstraintViolation<?>> it = U.safe(violations).iterator(); it.hasNext(); ) {
					ConstraintViolation<?> v = it.next();

					sb.append(v.getRootBeanClass().getSimpleName());
					sb.append(".");
					sb.append(v.getPropertyPath());
					sb.append(" (");
					sb.append(v.getMessage());
					sb.append(")");

					if (it.hasNext()) {
						sb.append(", ");
					}
				}

				msg = sb.toString();
			}

		} else {
			code = 500;
			defaultMsg = "Internal Server Error!";
		}

		msg = U.or(msg, defaultMsg);
		return new ErrCodeAndMsg(code, msg);
	}

	public static String detectZipRoot(InputStream zip) {
		Set<String> roots = U.set();

		try {

			ZipInputStream zis = new ZipInputStream(zip);
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				if (ze.isDirectory()) {
					String fileName = ze.getName();
					String parentDir = fileName.split("/|\\\\")[0];
					roots.add(parentDir);
				}

				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException e) {
			throw U.rte(e);
		}

		return roots.size() == 1 ? U.single(roots) : null;
	}

	public static void unzip(InputStream zip, String destFolder) {
		try {
			File folder = new File(destFolder);
			folder.mkdirs();

			ZipInputStream zis = new ZipInputStream(zip);
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				if (!ze.isDirectory()) {
					String fileName = ze.getName();

					File newFile = new File(destFolder + File.separator + fileName);
					newFile.getParentFile().mkdirs();

					IO.save(newFile.getAbsolutePath(), IO.loadBytes(zis));
				}

				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

		} catch (IOException e) {
			throw U.rte(e);
		}
	}

	public static void printRapidoidBanner() {
		U.print(IO.load("rapidoid.txt"));
	}

	public static boolean isSilent() {
		return isMavenBuild();
	}

	public static String specialUriPrefix() {
		return Msc.isPlatform() ? "/rapidoid/" : "/_";
	}

	public static String specialUri(String... suffixes) {
		String uri = uri(suffixes);
		String suffix = Str.triml(uri, '/');
		return specialUriPrefix() + suffix;
	}

	public static String semiSpecialUri(String... suffixes) {
		String uri = uri(suffixes);
		String suffix = Str.triml(uri, '/');
		return Msc.isPlatform() ? "/rapidoid/" + suffix : "/" + suffix;
	}

	public static String http() {
		return MscOpts.isTLSEnabled() ? "https" : "http";
	}

	public static String urlWithProtocol(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		} else {
			return Msc.http() + "://" + url;
		}
	}

	public static boolean timedOut(long since, long timeout) {
		return U.time() - since > timeout;
	}

	public static int log2(int n) {
		U.must(n > 0);

		int factor = 32 - Integer.numberOfLeadingZeros(n - 1);

		U.must(n <= Math.pow(2, factor));

		return factor;
	}

	public static int bitMask(int bits) {
		return (1 << bits) - 1;
	}

	public static boolean hasMainApp() {
		List<String> appContent = IO.find().in("/app").ignoreRegex("(static|\\..*|.*~)").getNames();
		return !appContent.isEmpty();
	}

	public static boolean isAppResource(String filename) {
		String name = new File(filename).getName();
		return !name.startsWith(".") && !name.endsWith("~") && !name.endsWith(".staged");
	}

	public static String mainAppJar() {
		U.must(isPlatform());
		return "/app/app.jar";
	}
}
