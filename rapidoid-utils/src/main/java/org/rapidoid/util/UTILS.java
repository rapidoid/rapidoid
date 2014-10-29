package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.rapidoid.lambda.Predicate;

public class UTILS implements Constants {

	private static final Method getGarbageCollectorMXBeans;

	static {
		Class<?> manFactory = U.getClassIfExists("java.lang.management.ManagementFactory");
		getGarbageCollectorMXBeans = manFactory != null ? Cls.getMethod(manFactory, "getGarbageCollectorMXBeans")
				: null;
	}

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

			U.info("Starting TCP/IP server", "host", hostname, "port", port);

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
						String path = U.urlDecode(req.substring(4, pos));
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

	public static String gcInfo() {
		String gcinfo = "";

		if (getGarbageCollectorMXBeans != null) {
			List<?> gcs = Cls.invokeStatic(getGarbageCollectorMXBeans);

			for (Object gc : gcs) {
				gcinfo += " | " + Cls.getPropValue(gc, "name") + " x" + Cls.getPropValue(gc, "collectionCount") + ":"
						+ Cls.getPropValue(gc, "collectionTime") + "ms";
			}
		}
		return gcinfo;
	}

	public static short bytesToShort(String s) {
		ByteBuffer buf = U.buf(s);
		U.must(buf.limit() == 2);
		return buf.getShort();
	}

	public static int bytesToInt(String s) {
		ByteBuffer buf = U.buf(s);
		U.must(buf.limit() == 4);
		return buf.getInt();
	}

	public static long bytesToLong(String s) {
		ByteBuffer buf = U.buf(s);
		U.must(buf.limit() == 8);
		return buf.getLong();
	}

	public static int intFrom(byte a, byte b, byte c, byte d) {
		return (a << 24) + (b << 16) + (c << 8) + d;
	}

	public static short shortFrom(byte a, byte b) {
		return (short) ((a << 8) + b);
	}

}
