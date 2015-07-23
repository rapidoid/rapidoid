package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.Insights;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.TypeKind;
import org.rapidoid.ctx.Ctx;
import org.rapidoid.ctx.Ctxs;
import org.rapidoid.lambda.F2;
import org.rapidoid.lambda.Lambdas;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class UTILS implements Constants {

	private static long measureStart;

	private static final Set<String> SPECIAL_PROPERTIES = U.set("id", "version", "createdby", "createdon",
			"lastupdatedby", "lastupdatedon");

	// regex taken from
	// http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
	private static final Pattern CAMEL_SPLITTER_PATTERN = Pattern
			.compile("(?<=[A-Z])(?=[A-Z][a-z])|(?<=[^A-Z])(?=[A-Z])|(?<=[A-Za-z])(?=[^A-Za-z])");

	private UTILS() {}

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

	public static <T> void filter(Collection<T> coll, Predicate<T> predicate) {
		try {
			for (Iterator<T> iterator = coll.iterator(); iterator.hasNext();) {
				T t = (T) iterator.next();
				if (!predicate.eval(t)) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			throw U.rte(e);
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

			matcher.appendReplacement(output, Lambdas.eval(replacer, gr));
		}

		matcher.appendTail(output);
		return output.toString();
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

	public static Object[] flat(Object... arr) {
		List<Object> flat = U.list();
		flatInsertInto(flat, 0, arr);
		return flat.toArray();
	}

	@SuppressWarnings("unchecked")
	public static <T> int flatInsertInto(List<T> dest, int index, Object item) {
		if (index > dest.size()) {
			index = dest.size();
		}
		int inserted = 0;

		if (item instanceof Object[]) {
			Object[] arr = (Object[]) item;
			for (Object obj : arr) {
				inserted += flatInsertInto(dest, index + inserted, obj);
			}
		} else if (item instanceof Collection<?>) {
			Collection<?> coll = (Collection<?>) item;
			for (Object obj : coll) {
				inserted += flatInsertInto(dest, index + inserted, obj);
			}
		} else if (item != null) {
			if (index >= dest.size()) {
				dest.add((T) item);
			} else {
				dest.add(index + inserted, (T) item);
			}
			inserted++;
		}

		return inserted;
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
					Ctxs.attach(ctx);

					benchmark(name, countPerThread, runnable);
					if (outsideLatch == null) {
						latch.countDown();
					}

					Ctxs.close();
				};
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

	public static String camelSplit(String s) {
		return CAMEL_SPLITTER_PATTERN.matcher(s).replaceAll(" ");
	}

	public static String camelPhrase(String s) {
		return U.capitalized(camelSplit(s).toLowerCase());
	}

	public static <T, V extends T> List<T> withoutNulls(V... values) {
		List<T> list = U.list();

		for (T val : values) {
			if (val != null) {
				list.add(val);
			}
		}

		return list;
	}

	public static boolean isSpecialProperty(String name) {
		return SPECIAL_PROPERTIES.contains(name.toLowerCase());
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
					Lambdas.eval(executable, n);
					latch.countDown();
				};
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

	public static String path(String... parts) {
		StringBuilder sb = new StringBuilder();

		for (String part : parts) {
			// trim '/'s
			part = part.replaceAll("^/", "").replaceAll("/$", "");

			if (!U.isEmpty(part)) {
				append(sb, "/", part);
			}
		}

		return sb.toString();
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

}
