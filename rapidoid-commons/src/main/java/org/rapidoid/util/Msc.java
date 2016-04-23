package org.rapidoid.util;

import org.rapidoid.RapidoidThing;
import org.rapidoid.activity.AbstractLoopThread;
import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.activity.RapidoidThreadFactory;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.commons.Coll;
import org.rapidoid.commons.Str;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.insight.Insights;
import org.rapidoid.io.IO;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.Dynamic;
import org.rapidoid.lambda.F2;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;
import org.rapidoid.validation.InvalidData;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

/*
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2016 Nikolche Mihajlovski and contributors
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
public class Msc extends RapidoidThing implements Constants {

	public static final Set<String> SPECIAL_ERRORS = Coll.synchronizedSet(
			SecurityException.class.getName(),
			InvalidData.class.getName(),
			"javax.validation.ConstraintViolationException",
			"javax.validation.ValidationException"
	);

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

	private static long measureStart;

	public static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(8,
			new RapidoidThreadFactory("utils"));

	private Msc() {
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

	public static String stackTraceOf(Throwable e) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(output));
		return output.toString();
	}

	public static void connect(String address, int port, F2<Void, BufferedReader, DataOutputStream> protocol) {
		Socket socket = null;

		try {
			socket = new Socket(address, port);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			protocol.execute(in, out);

			socket.close();
		} catch (Exception e) {
			throw U.rte(e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					throw U.rte(e);
				}
			}
		}
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

	public static void benchmark(String name, int count, Runnable runnable) {
		long start = U.time();

		for (int i = 0; i < count; i++) {
			runnable.run();
		}

		benchmarkComplete(name, count, start);
	}

	public static void benchmarkComplete(String name, int count, long startTime) {
		long end = U.time();
		long ms = end - startTime;

		if (ms == 0) {
			ms = 1;
		}

		double avg = ((double) count / (double) ms);

		String avgs = avg > 1 ? Math.round(avg) + "K" : Math.round(avg * 1000) + "";

		String data = String.format("%s: %s in %s ms (%s/sec)", name, count, ms, avgs);

		U.print(data + " | " + Insights.getCpuMemStats());
	}

	public static void benchmarkMT(int threadsN, final String name, final int count, final CountDownLatch outsideLatch,
	                               final Runnable runnable) {

		final int countPerThread = count / threadsN;

		final CountDownLatch latch = outsideLatch != null ? outsideLatch : new CountDownLatch(threadsN);

		long time = U.time();

		final Ctx ctx = Ctxs.get();

		for (int i = 1; i <= threadsN; i++) {
			new Thread() {
				public void run() {
					Ctxs.attach(ctx != null ? ctx.span() : null);

					try {
						benchmark(name, countPerThread, runnable);
						if (outsideLatch == null) {
							latch.countDown();
						}

					} finally {
						if (ctx != null) {
							Ctxs.close();
						}
					}
				}

				;
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
		benchmarkMT(threadsN, name, count, null, runnable);
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

	public static void startMeasure() {
		measureStart = U.time();
	}

	public static void endMeasure() {
		long delta = U.time() - measureStart;
		D.print(delta + " ms");
	}

	public static void endMeasure(String info) {
		long delta = U.time() - measureStart;
		D.print(info + ": " + delta + " ms");
	}

	public static void endMeasure(long count, String info) {
		long delta = U.time() - measureStart;
		long freq = Math.round(1000 * (double) count / delta);
		D.print(U.frmt("%s %s in %s ms (%s/sec)", count, info, delta, freq));
	}

	public static Throwable rootCause(Throwable e) {
		while (e.getCause() != null) {
			if (SPECIAL_ERRORS.contains(e.getClass().getName())) {
				return e;
			}
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
			throw U.rte("Couldn't infer the caller!");
		}
	}

	private static boolean couldBeCaller(String cls) {
		boolean special = cls.equals("org.rapidoid.standalone.Main");
		return special || (!Cls.isRapidoidClass(cls) && !Cls.isJREClass(cls) && !Cls.isIdeOrToolClass(cls));
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
			U.str(obj); // 1. data binding and event processing
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
				s += part;
			}
		}

		return s;
	}

	public static String refinePath(String path) {
		return path(path.split("/"));
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
		Log.info(Str.mul("-", msg.length()));
		Log.info(msg);
		Log.info(Str.mul("-", msg.length()));
	}

	public static void logProperties(Properties props) {
		for (Entry<Object, Object> p : props.entrySet()) {
			Log.info("Hibernate property", String.valueOf(p.getKey()), p.getValue());
		}
	}

	public static boolean hasValidation() {
		return Cls.exists("javax.validation.Validation");
	}

	public static boolean hasJPA() {
		return Cls.exists("javax.persistence.Entity");
	}

	public static boolean hasRapidoidJPA() {
		return Cls.exists("org.rapidoid.jpa.JPA");
	}

	public static boolean hasRapidoidGUI() {
		return Cls.exists("org.rapidoid.gui.GUI");
	}

	public static boolean hasInject() {
		return Cls.exists("javax.inject.Inject");
	}

	public static boolean isValidationError(Throwable error) {
		return (error instanceof InvalidData) || error.getClass().getName().startsWith("javax.validation.");
	}

	public static ErrCodeAndMsg getErrorCodeAndMsg(Throwable err) {
		Throwable cause = rootCause(err);

		int code;
		String defaultMsg;
		String msg = cause.getMessage();

		if (cause instanceof SecurityException) {
			code = 403;
			defaultMsg = "Access Denied!";

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
}
