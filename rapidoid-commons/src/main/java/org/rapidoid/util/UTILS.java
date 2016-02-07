package org.rapidoid.util;

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

import org.rapidoid.activity.AbstractLoopThread;
import org.rapidoid.activity.RapidoidThread;
import org.rapidoid.activity.RapidoidThreadFactory;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.insight.Insights;
import org.rapidoid.io.Res;
import org.rapidoid.lambda.F2;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class UTILS implements Constants {

	private static long measureStart;

	public static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(8,
			new RapidoidThreadFactory("utils"));

	private UTILS() {
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

	// TODO add such utils for other primitive types, as well
	public static void encode(long value, ByteBuffer buf) {
		buf.put((byte) TypeKind.LONG.ordinal());
		buf.putLong(value);
	}

	public static void encode(Object value, ByteBuffer buf) {
		TypeKind kind = Cls.kindOf(value);
		int ordinal = kind.ordinal();
		assert ordinal < 128;

		byte kindCode = (byte) ordinal;
		buf.put(kindCode);

		switch (kind) {

			case NULL:
				// nothing else needed
				break;

			case BOOLEAN:
			case BYTE:
			case SHORT:
			case CHAR:
			case INT:
			case LONG:
			case FLOAT:
			case DOUBLE:
				throw U.notExpected();

			case STRING:
				String str = (String) value;
				byte[] bytes = str.getBytes();
				// 0-255
				int len = bytes.length;
				if (len < 255) {
					buf.put(bytee(len));
				} else {
					buf.put(bytee(255));
					buf.putInt(len);
				}
				buf.put(bytes);
				break;

			case BOOLEAN_OBJ:
				boolean val = (Boolean) value;
				buf.put((byte) (val ? 1 : 0));
				break;

			case BYTE_OBJ:
				buf.put((Byte) value);
				break;

			case SHORT_OBJ:
				buf.putShort((Short) value);
				break;

			case CHAR_OBJ:
				buf.putChar((Character) value);
				break;

			case INT_OBJ:
				buf.putInt((Integer) value);
				break;

			case LONG_OBJ:
				buf.putLong((Long) value);
				break;

			case FLOAT_OBJ:
				buf.putFloat((Float) value);
				break;

			case DOUBLE_OBJ:
				buf.putDouble((Double) value);
				break;

			case OBJECT:
				serialize(value, buf);
				break;

			case DATE:
				buf.putLong(((Date) value).getTime());
				break;

			default:
				throw U.notExpected();
		}
	}

	private static byte bytee(int n) {
		return (byte) (n - 128);
	}

	public static long decodeLong(ByteBuffer buf) {
		U.must(buf.get() == TypeKind.LONG.ordinal());
		return buf.getLong();
	}

	public static Object decode(ByteBuffer buf) {
		byte kindCode = buf.get();

		TypeKind kind = TypeKind.values()[kindCode];

		switch (kind) {

			case NULL:
				return null;

			case BOOLEAN:
			case BOOLEAN_OBJ:
				return buf.get() != 0;

			case BYTE:
			case BYTE_OBJ:
				return buf.get();

			case SHORT:
			case SHORT_OBJ:
				return buf.getShort();

			case CHAR:
			case CHAR_OBJ:
				return buf.getChar();

			case INT:
			case INT_OBJ:
				return buf.getInt();

			case LONG:
			case LONG_OBJ:
				return buf.getLong();

			case FLOAT:
			case FLOAT_OBJ:
				return buf.getFloat();

			case DOUBLE:
			case DOUBLE_OBJ:
				return buf.getDouble();

			case STRING:
				byte len = buf.get();
				int realLen = len + 128;
				if (realLen == 255) {
					realLen = buf.getInt();
				}
				byte[] sbuf = new byte[realLen];
				buf.get(sbuf);
				return new String(sbuf);

			case OBJECT:
				return deserialize(buf);

			case DATE:
				return new Date(buf.getLong());

			default:
				throw U.notExpected();
		}
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

	public static void listen(int port, F2<Void, BufferedReader, DataOutputStream> protocol) {
		listen(null, port, protocol);
	}

	public static void listen(String hostname, int port, F2<Void, BufferedReader, DataOutputStream> protocol) {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket();
			socket.bind(U.isEmpty(hostname) ? new InetSocketAddress(port) : new InetSocketAddress(hostname, port));

			Log.info("Starting TCP/IP server", "host", hostname, "port", port);

			while (true) {
				final Socket conn = socket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());

				try {
					protocol.execute(in, out);
				} catch (Exception e) {
					throw U.rte(e);
				} finally {
					conn.close();
				}
			}
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

	public static void microHttpServer(String hostname, int port, final F2<String, String, List<String>> handler) {
		listen(hostname, port, new F2<Void, BufferedReader, DataOutputStream>() {

			@Override
			public Void execute(BufferedReader in, DataOutputStream out) throws Exception {
				List<String> lines = new ArrayList<String>();

				String line;
				while ((line = in.readLine()) != null) {
					if (line.isEmpty()) {
						break;
					}
					lines.add(line);
				}

				if (!lines.isEmpty()) {
					String req = lines.get(0);
					if (req.startsWith("GET /")) {
						int pos = req.indexOf(' ', 4);
						String path = urlDecode(req.substring(4, pos));
						String response = handler.execute(path, lines);
						out.writeBytes(response);
					} else {
						out.writeBytes("Only GET requests are supported!");
					}
				} else {
					out.writeBytes("Invalid HTTP request!");
				}

				return null;
			}

		});
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

		System.out.println(data + " | " + Insights.getCpuMemStats());
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

	public static boolean isRapidoidType(Class<?> clazz) {
		try {
			for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
				String pkg = c.getCanonicalName();
				if (pkg.startsWith("org.rapidoid.") || pkg.startsWith("org.rapidoidx.")) {
					return !pkg.startsWith("org.rapidoid.docs.");
				}
			}
		} catch (Exception e) {
			throw U.rte(e);
		}

		return false;
	}

	public static Class<?> getCallingClassOf(Class<?>... ignoreClasses) {
		return inferCaller(ignoreClasses);
	}

	public static String getCallingPackageOf(Class<?>... ignoreClasses) {
		Class<?> callerCls = inferCaller(ignoreClasses);

		if (callerCls != null) {
			return callerCls.getPackage() != null ? callerCls.getPackage().getName() : "";
		} else {
			throw U.rte("Couldn't infer the caller!");
		}
	}

	private static Class<?> inferCaller(Class<?>... ignoreClasses) {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		// skip the first 3 elements:
		// [0] java.lang.Thread.getStackTrace
		// [1] THIS METHOD
		// [2] UTILS#getCallingPackageOf or UTILS#getCallingClassOf

		for (int i = 3; i < trace.length; i++) {
			String cls = trace[i].getClassName();
			if (!shouldIgnore(cls, ignoreClasses)) {
				try {
					return Class.forName(cls);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
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

		} else if (obj instanceof File) {
			Res res = Res.from((File) obj);
			res.mustExist();
			return res.getBytes();

		} else if (obj instanceof Res) {
			Res res = (Res) obj;
			res.mustExist();
			return res.getBytes();

		} else {
			return U.str(obj).getBytes();
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

}
