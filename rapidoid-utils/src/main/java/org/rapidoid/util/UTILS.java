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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Autocreate;
import org.rapidoid.annotation.Init;
import org.rapidoid.annotation.Inject;
import org.rapidoid.lambda.F1;
import org.rapidoid.lambda.F2;
import org.rapidoid.lambda.F3;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.lambda.Predicate;

public class UTILS implements Constants {

	private static final Method getGarbageCollectorMXBeans;

	static {
		Class<?> manFactory = U.getClassIfExists("java.lang.management.ManagementFactory");
		getGarbageCollectorMXBeans = manFactory != null ? getMethod(manFactory, "getGarbageCollectorMXBeans") : null;
	}

	private static final Map<Class<?>, Object> SINGLETONS = U.map();

	private static final Set<Class<?>> MANAGED_CLASSES = U.set();

	private static final Set<Object> MANAGED_INSTANCES = U.set();

	private static final Map<Object, Object> IOC_INSTANCES = U.map();

	private static final Map<Class<?>, List<Field>> INJECTABLE_FIELDS = autoExpandingMap(new F1<List<Field>, Class<?>>() {
		@Override
		public List<Field> execute(Class<?> clazz) throws Exception {
			List<Field> fields = getFieldsAnnotated(clazz, Inject.class);
			U.debug("Retrieved injectable fields", "class", clazz, "fields", fields);
			return fields;
		}
	});

	private static final Map<Class<?>, Set<Object>> INJECTION_PROVIDERS = U.map();

	private static final Map<String, TypeKind> KINDS = initKinds();

	/* RFC 1123 date-time format, e.g. Sun, 07 Sep 2014 00:17:29 GMT */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	private static final Date CURR_DATE = new Date();
	private static byte[] CURR_DATE_BYTES;
	private static long updateCurrDateAfter = 0;

	static {
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private UTILS() {
	}

	public static byte[] getDateTimeBytes() {
		long time = System.currentTimeMillis();

		// avoid synchronization for better performance
		if (time > updateCurrDateAfter) {
			CURR_DATE.setTime(time);
			CURR_DATE_BYTES = DATE_FORMAT.format(CURR_DATE).getBytes();
			updateCurrDateAfter = time + 1000;
		}

		return CURR_DATE_BYTES;
	}

	private static final Map<Class<?>, List<F3<Object, Object, Method, Object[]>>> INTERCEPTORS = U.map();

	private static final Map<Class<?>, Map<String, Prop>> BEAN_PROPERTIES = autoExpandingMap(new F1<Map<String, Prop>, Class<?>>() {

		@Override
		public Map<String, Prop> execute(Class<?> clazz) throws Exception {

			Map<String, Prop> properties = U.map();

			try {
				for (Class<?> c = clazz; c != Object.class && c != null; c = c.getSuperclass()) {
					Method[] methods = c.getDeclaredMethods();
					for (Method method : methods) {
						int modif = method.getModifiers();
						if ((modif & Modifier.PUBLIC) > 0 && (modif & Modifier.STATIC) == 0
								&& (modif & Modifier.ABSTRACT) == 0) {
							String name = method.getName();
							if (name.matches("^(get|set|is)[A-Z].*")) {

								String fieldName;
								if (name.startsWith("is")) {
									fieldName = name.substring(2, 3).toLowerCase() + name.substring(3);
								} else {
									fieldName = name.substring(3, 4).toLowerCase() + name.substring(4);
								}

								Prop propInfo = properties.get(fieldName);

								if (propInfo == null) {
									propInfo = new Prop();
									propInfo.setName(fieldName);
									properties.put(fieldName, propInfo);
								}

								if (name.startsWith("set")) {
									propInfo.setSetter(method);
								} else {
									propInfo.setGetter(method);
								}
							}
						}
					}

					for (Iterator<Entry<String, Prop>> it = properties.entrySet().iterator(); it.hasNext();) {
						Entry<String, Prop> entry = (Entry<String, Prop>) it.next();
						Prop minfo = entry.getValue();
						if (minfo.getGetter() == null || minfo.getSetter() == null) {
							it.remove();
						}
					}
				}

				for (Class<?> c = clazz; c != Object.class && c != null; c = c.getSuperclass()) {
					Field[] fields = c.getDeclaredFields();
					for (Field field : fields) {
						int modif = field.getModifiers();
						if ((modif & Modifier.PUBLIC) > 0 && (modif & Modifier.FINAL) == 0
								&& (modif & Modifier.STATIC) == 0) {
							String fieldName = field.getName();

							Prop propInfo = properties.get(fieldName);

							if (propInfo == null) {
								propInfo = new Prop();
								propInfo.setName(fieldName);
								properties.put(fieldName, propInfo);
								propInfo.setField(field);
							}
						}
					}
				}

			} catch (Exception e) {
				throw U.rte(e);
			}

			return properties;
		}

	});

	public static synchronized void reset() {
		U.info("Reset U state");
		U.LOG_LEVEL = U.INFO;
		SINGLETONS.clear();
		U.ARGS = new String[] {};
		MANAGED_CLASSES.clear();
		MANAGED_INSTANCES.clear();
		IOC_INSTANCES.clear();
		INJECTABLE_FIELDS.clear();
		INJECTION_PROVIDERS.clear();
		INTERCEPTORS.clear();
		BEAN_PROPERTIES.clear();
	}

	public static Map<String, Prop> propertiesOf(Class<?> clazz) {
		return BEAN_PROPERTIES.get(clazz);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Prop> propertiesOf(Object obj) {
		return obj != null ? propertiesOf(obj.getClass()) : Collections.EMPTY_MAP;
	}

	private static Map<String, TypeKind> initKinds() {

		Map<String, TypeKind> kinds = new HashMap<String, TypeKind>();

		kinds.put("boolean", TypeKind.BOOLEAN);

		kinds.put("byte", TypeKind.BYTE);

		kinds.put("char", TypeKind.CHAR);

		kinds.put("short", TypeKind.SHORT);

		kinds.put("int", TypeKind.INT);

		kinds.put("long", TypeKind.LONG);

		kinds.put("float", TypeKind.FLOAT);

		kinds.put("double", TypeKind.DOUBLE);

		kinds.put("java.lang.String", TypeKind.STRING);

		kinds.put("java.lang.Boolean", TypeKind.BOOLEAN_OBJ);

		kinds.put("java.lang.Byte", TypeKind.BYTE_OBJ);

		kinds.put("java.lang.Character", TypeKind.CHAR_OBJ);

		kinds.put("java.lang.Short", TypeKind.SHORT_OBJ);

		kinds.put("java.lang.Integer", TypeKind.INT_OBJ);

		kinds.put("java.lang.Long", TypeKind.LONG_OBJ);

		kinds.put("java.lang.Float", TypeKind.FLOAT_OBJ);

		kinds.put("java.lang.Double", TypeKind.DOUBLE_OBJ);

		kinds.put("java.util.Date", TypeKind.DATE);

		return kinds;
	}

	/**
	 * @return Any kind, except NULL
	 */
	public static TypeKind kindOf(Class<?> type) {
		String typeName = type.getName();
		TypeKind kind = KINDS.get(typeName);

		if (kind == null) {
			kind = TypeKind.OBJECT;
		}

		return kind;
	}

	/**
	 * @return Any kind, including NULL
	 */
	public static TypeKind kindOf(Object value) {
		if (value == null) {
			return TypeKind.NULL;
		}

		String typeName = value.getClass().getName();
		TypeKind kind = KINDS.get(typeName);

		if (kind == null) {
			kind = TypeKind.OBJECT;
		}

		return kind;
	}

	public static void setFieldValue(Object instance, String fieldName, Object value) {
		try {
			for (Class<?> c = instance.getClass(); c != Object.class; c = c.getSuperclass()) {
				try {
					Field field = c.getDeclaredField(fieldName);
					field.setAccessible(true);
					field.set(instance, value);
					field.setAccessible(false);
					return;
				} catch (NoSuchFieldException e) {
					// keep searching the filed in the super-class...
				}
			}
		} catch (Exception e) {
			throw U.rte("Cannot set field value!", e);
		}

		throw U.rte("Cannot find the field '%s' in the class '%s'", fieldName, instance.getClass());
	}

	public static void setFieldValue(Field field, Object instance, Object value) {
		try {
			field.setAccessible(true);
			field.set(instance, value);
			field.setAccessible(false);
		} catch (Exception e) {
			throw U.rte("Cannot set field value!", e);
		}
	}

	public static Object getFieldValue(Object instance, String fieldName) {
		try {
			for (Class<?> c = instance.getClass(); c != Object.class; c = c.getSuperclass()) {
				try {
					Field field = c.getDeclaredField(fieldName);
					return getFieldValue(field, instance);
				} catch (NoSuchFieldException e) {
					// keep searching the filed in the super-class...
				}
			}
		} catch (Exception e) {
			throw U.rte("Cannot get field value!", e);
		}

		throw U.rte("Cannot find the field '%s' in the class '%s'", fieldName, instance.getClass());
	}

	public static Object getFieldValue(Field field, Object instance) {
		try {
			field.setAccessible(true);
			Object value = field.get(instance);
			field.setAccessible(false);

			return value;
		} catch (Exception e) {
			throw U.rte("Cannot get field value!", e);
		}
	}

	public static List<Annotation> getAnnotations(Class<?> clazz) {
		List<Annotation> allAnnotations = U.list();

		try {
			for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
				Annotation[] annotations = c.getDeclaredAnnotations();
				for (Annotation an : annotations) {
					allAnnotations.add(an);
				}
			}

		} catch (Exception e) {
			throw U.rte("Cannot instantiate class!", e);
		}

		return allAnnotations;
	}

	public static List<Field> getFields(Class<?> clazz) {
		List<Field> allFields = U.list();

		try {
			for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
				Field[] fields = c.getDeclaredFields();
				for (Field field : fields) {
					allFields.add(field);
				}
			}

		} catch (Exception e) {
			throw U.rte("Cannot instantiate class!", e);
		}

		return allFields;
	}

	public static List<Field> getFieldsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Field> annotatedFields = U.list();

		try {
			for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
				Field[] fields = c.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(annotation)) {
						annotatedFields.add(field);
					}
				}
			}

		} catch (Exception e) {
			throw U.rte("Cannot instantiate class!", e);
		}

		return annotatedFields;
	}

	public static List<Method> getMethodsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Method> annotatedMethods = U.list();

		try {
			for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
				Method[] methods = c.getMethods();
				for (Method method : methods) {
					if (method.isAnnotationPresent(annotation)) {
						annotatedMethods.add(method);
					}
				}
			}

		} catch (Exception e) {
			throw U.rte("Cannot instantiate class!", e);
		}

		return annotatedMethods;
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw U.rte("Cannot find method: %s", e, name);
		} catch (SecurityException e) {
			throw U.rte("Cannot access method: %s", e, name);
		}
	}

	public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeStatic(Method m, Object... args) {
		boolean accessible = m.isAccessible();
		try {
			m.setAccessible(true);
			return (T) m.invoke(null, args);
		} catch (IllegalAccessException e) {
			throw U.rte("Cannot statically invoke method '%s' with args: %s", e, m.getName(), Arrays.toString(args));
		} catch (IllegalArgumentException e) {
			throw U.rte("Cannot statically invoke method '%s' with args: %s", e, m.getName(), Arrays.toString(args));
		} catch (InvocationTargetException e) {
			throw U.rte("Cannot statically invoke method '%s' with args: %s", e, m.getName(), Arrays.toString(args));
		} finally {
			m.setAccessible(accessible);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Method m, Object target, Object... args) {
		boolean accessible = m.isAccessible();
		try {
			m.setAccessible(true);
			return (T) m.invoke(target, args);
		} catch (Exception e) {
			throw U.rte("Cannot invoke method '%s' with args: %s", e, m.getName(), Arrays.toString(args));
		} finally {
			m.setAccessible(accessible);
		}
	}

	public static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
		try {
			List<Class<?>> interfaces = new LinkedList<Class<?>>();

			for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
				for (Class<?> interf : c.getInterfaces()) {
					interfaces.add(interf);
				}
			}

			return interfaces.toArray(new Class<?>[interfaces.size()]);
		} catch (Exception e) {
			throw U.rte("Cannot retrieve implemented interfaces!", e);
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... paramTypes) {
		try {
			return (Constructor<T>) clazz.getConstructor(paramTypes);
		} catch (Exception e) {
			throw U.rte("Cannot find the constructor for %s with param types: %s", e, clazz,
					Arrays.toString(paramTypes));
		}
	}

	public static boolean annotatedMethod(Object instance, String methodName, Class<Annotation> annotation) {
		try {
			Method method = instance.getClass().getMethod(methodName);
			return method.getAnnotation(annotation) != null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <K, V> Map<K, V> autoExpandingMap(final Class<V> clazz) {
		return autoExpandingMap(new F1<V, K>() {

			@Override
			public V execute(K param) throws Exception {
				return inject(U.newInstance(clazz));
			}

		});
	}

	@SuppressWarnings("serial")
	public static <K, V> Map<K, V> autoExpandingMap(final F1<V, K> valueFactory) {
		return new ConcurrentHashMap<K, V>() {
			@SuppressWarnings("unchecked")
			@Override
			public synchronized V get(Object key) {
				V val = super.get(key);

				if (val == null) {
					try {
						val = valueFactory.execute((K) key);
					} catch (Exception e) {
						throw U.rte(e);
					}

					put((K) key, val);
				}

				return val;
			}
		};
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
		TypeKind kind = kindOf(value);
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

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(InvocationHandler handler, Class<?>... interfaces) {
		return ((T) Proxy.newProxyInstance(U.CLASS_LOADER, interfaces, handler));
	}

	public static <T> T implement(final Object target, final InvocationHandler handler, Class<?>... interfaces) {
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

	public static <T> T implement(InvocationHandler handler, Class<?>... classes) {
		return implement(new InterceptorProxy(U.text(classes)), handler, classes);
	}

	public static <T> T implementInterfaces(Object target, InvocationHandler handler) {
		return implement(target, handler, getImplementedInterfaces(target.getClass()));
	}

	public static <T> T tracer(Object target) {
		return implementInterfaces(target, new InvocationHandler() {
			@Override
			public Object invoke(Object target, Method method, Object[] args) throws Throwable {
				U.trace("intercepting", "method", method.getName(), "args", Arrays.toString(args));
				return method.invoke(target, args);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Map<String, Object> properties) {
		if (properties == null) {
			return U.newInstance(clazz);
		}

		Collection<Object> values = properties.values();

		for (Constructor<?> constr : clazz.getConstructors()) {
			Class<?>[] paramTypes = constr.getParameterTypes();
			Object[] args = getAssignableArgs(paramTypes, values);
			if (args != null) {
				try {
					return (T) constr.newInstance(args);
				} catch (Exception e) {
					throw U.rte(e);
				}
			}
		}

		throw U.rte("Cannot find appropriate constructor for %s with args %s!", clazz, values);
	}

	private static Object[] getAssignableArgs(Class<?>[] types, Collection<?> properties) {
		Object[] args = new Object[types.length];

		for (int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			args[i] = getUniqueInstanceOf(type, properties);
		}

		return args;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getUniqueInstanceOf(Class<T> type, Collection<?> values) {
		T instance = null;

		for (Object obj : values) {
			if (obj != null && type.isAssignableFrom(obj.getClass())) {
				if (instance == null) {
					instance = (T) obj;
				} else {
					throw U.rte("Found more than one instance of %s: %s and %s", type, instance, obj);
				}
			}
		}

		return instance;
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

	public static void setId(Object obj, long id) {
		setPropValue(obj, "id", id);
	}

	public static long getId(Object obj) {
		Object id = getPropValue(obj, "id");
		if (id == null) {
			throw U.rte("The field 'id' cannot be null!");
		}
		if (id instanceof Long) {
			Long num = (Long) id;
			return num;
		} else {
			throw U.rte("The field 'id' must have type 'long', but it has: " + id.getClass());
		}
	}

	public static long[] getIds(Object... objs) {
		long[] ids = new long[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ids[i] = getId(objs[i]);
		}
		return ids;
	}

	public static String replace(String s, String regex, F1<String, String[]> replacer) {
		StringBuffer output = new StringBuffer();
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(s);

		while (matcher.find()) {
			int len = matcher.groupCount() + 1;
			String[] gr = new String[len];

			for (int i = 0; i < gr.length; i++) {
				gr[i] = matcher.group(i);
			}

			try {
				String rep = replacer.execute(gr);
				matcher.appendReplacement(output, rep);
			} catch (Exception e) {
				throw U.rte("Cannot replace text!", e);
			}
		}

		matcher.appendTail(output);
		return output.toString();
	}

	public static synchronized void manage(Object... classesOrInstances) {
		List<Class<?>> autocreate = new ArrayList<Class<?>>();

		for (Object classOrInstance : classesOrInstances) {

			boolean isClass = isClass(classOrInstance);
			Class<?> clazz = isClass ? (Class<?>) classOrInstance : classOrInstance.getClass();

			for (Class<?> interfacee : getImplementedInterfaces(clazz)) {
				addInjectionProvider(interfacee, classOrInstance);
			}

			if (isClass) {
				U.debug("configuring managed class", "class", classOrInstance);
				MANAGED_CLASSES.add(clazz);

				if (!clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation()) {
					System.out.println(":" + clazz);

					// if the class is annotated, auto-create an instance
					if (clazz.getAnnotation(Autocreate.class) != null) {
						autocreate.add(clazz);
					}
				}
			} else {
				U.debug("configuring managed instance", "instance", classOrInstance);
				addInjectionProvider(clazz, classOrInstance);
				MANAGED_INSTANCES.add(classOrInstance);
			}
		}

		for (Class<?> clazz : autocreate) {
			singleton(clazz);
		}
	}

	private static void addInjectionProvider(Class<?> type, Object provider) {
		Set<Object> providers = INJECTION_PROVIDERS.get(type);

		if (providers == null) {
			providers = U.set();
			INJECTION_PROVIDERS.put(type, providers);
		}

		providers.add(provider);
	}

	public static synchronized <T> T singleton(Class<T> type) {
		U.info("Inject", "type", type);
		return provideIoCInstanceOf(null, type, null, null, false);
	}

	public static synchronized <T> T inject(T target) {
		U.info("Inject", "target", target);
		return ioc(target, null);
	}

	public static synchronized <T> T inject(T target, Map<String, Object> properties) {
		U.info("Inject", "target", target, "properties", properties);
		return ioc(target, properties);
	}

	private static <T> T provideIoCInstanceOf(Object target, Class<T> type, String name,
			Map<String, Object> properties, boolean optional) {
		T instance = null;

		if (name != null) {
			instance = provideInstanceByName(target, type, name, properties);
		}

		if (instance == null) {
			instance = provideIoCInstanceByType(type, properties);
		}

		if (instance == null && canInjectNew(type)) {
			instance = provideNewIoCInstanceOf(type, properties);
		}

		if (!optional) {
			if (instance == null) {
				if (name != null) {
					throw U.rte("Didn't find a value for type '%s' and name '%s'!", type, name);
				} else {
					throw U.rte("Didn't find a value for type '%s'!", type);
				}
			}
		}

		return instance != null ? ioc(instance, properties) : null;
	}

	private static boolean canInjectNew(Class<?> type) {
		return !type.isAnnotation() && !type.isEnum() && !type.isInterface() && !type.isPrimitive()
				&& !type.equals(String.class) && !type.equals(Object.class) && !type.equals(Boolean.class)
				&& !Number.class.isAssignableFrom(type);
	}

	@SuppressWarnings("unchecked")
	private static <T> T provideNewIoCInstanceOf(Class<T> type, Map<String, Object> properties) {
		// instantiation if it's real class
		if (!type.isInterface() && !type.isEnum() && !type.isAnnotation()) {
			T instance = (T) SINGLETONS.get(type);

			if (instance == null) {
				instance = ioc(newInstance(type, properties), properties);
			}

			return instance;
		} else {
			return null;
		}
	}

	private static <T> T provideIoCInstanceByType(Class<T> type, Map<String, Object> properties) {
		Set<Object> providers = INJECTION_PROVIDERS.get(type);

		if (providers != null && !providers.isEmpty()) {

			Object provider = null;

			for (Object pr : providers) {
				if (provider == null) {
					provider = pr;
				} else {
					if (isClass(provider) && !isClass(pr)) {
						provider = pr;
					} else if (isClass(provider) || !isClass(pr)) {
						throw U.rte("Found more than 1 injection candidates for type '%s': %s", type, providers);
					}
				}
			}

			if (provider != null) {
				return provideFrom(provider, properties);
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> T provideFrom(Object classOrInstance, Map<String, Object> properties) {
		T instance;
		if (isClass(classOrInstance)) {
			instance = provideNewIoCInstanceOf((Class<T>) classOrInstance, properties);
		} else {
			instance = (T) classOrInstance;
		}
		return instance;
	}

	private static boolean isClass(Object obj) {
		return obj instanceof Class;
	}

	private static <T> T provideInstanceByName(Object target, Class<T> type, String name, Map<String, Object> properties) {
		T instance = getInjectableByName(type, name, properties, false);

		if (target != null) {
			instance = getInjectableByName(type, target.getClass().getSimpleName() + "." + name, properties, true);
		}

		if (instance == null) {
			instance = getInjectableByName(type, name, properties, true);
		}

		return (T) instance;
	}

	@SuppressWarnings("unchecked")
	private static <T> T getInjectableByName(Class<T> type, String name, Map<String, Object> properties,
			boolean useConfig) {
		Object instance = properties != null ? properties.get(name) : null;

		if (instance == null && useConfig) {
			if (type.equals(Boolean.class) || type.equals(boolean.class)) {
				instance = U.hasOption(name);
			} else {
				String opt = U.option(name, null);
				if (opt != null) {
					instance = convert(opt, type);
				}
			}
		}

		return (T) instance;
	}

	private static void autowire(Object target, Map<String, Object> properties) {
		U.debug("Autowiring", "target", target);

		for (Field field : INJECTABLE_FIELDS.get(target.getClass())) {

			boolean optional = isInjectOptional(field);
			Object value = provideIoCInstanceOf(target, field.getType(), field.getName(), properties, optional);

			U.debug("Injecting field value", "target", target, "field", field.getName(), "value", value);

			if (!optional || value != null) {
				setFieldValue(target, field.getName(), value);
			}
		}
	}

	private static boolean isInjectOptional(Field field) {
		Inject inject = field.getAnnotation(Inject.class);
		return inject != null && inject.optional();
	}

	private static <T> void invokePostConstruct(T target) {
		List<Method> methods = getMethodsAnnotated(target.getClass(), Init.class);

		for (Method method : methods) {
			invoke(method, target);
		}
	}

	private static <T> T ioc(T target, Map<String, Object> properties) {
		if (!isIocProcessed(target)) {
			IOC_INSTANCES.put(target, null);

			manage(target);

			autowire(target, properties);

			invokePostConstruct(target);

			T proxy = proxyWrap(target);

			IOC_INSTANCES.put(target, proxy);

			manage(proxy);

			target = proxy;
		}

		return target;
	}

	private static boolean isIocProcessed(Object target) {
		for (Entry<Object, Object> e : IOC_INSTANCES.entrySet()) {
			if (e.getKey() == target || e.getValue() == target) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private static <T> T proxyWrap(T instance) {
		Set<F3<Object, Object, Method, Object[]>> done = U.set();

		for (Class<?> interf : getImplementedInterfaces(instance.getClass())) {
			final List<F3<Object, Object, Method, Object[]>> interceptors = INTERCEPTORS.get(interf);

			if (interceptors != null) {
				for (final F3<Object, Object, Method, Object[]> interceptor : interceptors) {
					if (interceptor != null && !done.contains(interceptor)) {
						U.debug("Creating proxy", "target", instance, "interface", interf, "interceptor", interceptor);

						final T target = instance;
						InvocationHandler handler = new InvocationHandler() {
							@Override
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								return interceptor.execute(target, method, args);
							}
						};

						instance = implement(instance, handler, interf);

						done.add(interceptor);
					}
				}
			}
		}

		return instance;
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, Class<T> toType) {
		TypeKind targetKind = kindOf(toType);

		switch (targetKind) {

		case NULL:
			throw U.notExpected();

		case BOOLEAN:
		case BOOLEAN_OBJ:
			if ("y".equalsIgnoreCase(value) || "t".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value)
					|| "true".equalsIgnoreCase(value)) {
				return (T) Boolean.TRUE;
			}

			if ("n".equalsIgnoreCase(value) || "f".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value)
					|| "false".equalsIgnoreCase(value)) {
				return (T) Boolean.FALSE;
			}

			throw U.rte("Cannot convert the string value '%s' to boolean!", value);

		case BYTE:
		case BYTE_OBJ:
			return (T) new Byte(value);

		case SHORT:
		case SHORT_OBJ:
			return (T) new Short(value);

		case CHAR:
		case CHAR_OBJ:
			return (T) new Character(value.charAt(0));

		case INT:
		case INT_OBJ:
			return (T) new Integer(value);

		case LONG:
		case LONG_OBJ:
			return (T) new Long(value);

		case FLOAT:
		case FLOAT_OBJ:
			return (T) new Float(value);

		case DOUBLE:
		case DOUBLE_OBJ:
			return (T) new Double(value);

		case STRING:
			return (T) value;

		case OBJECT:
			throw U.rte("Cannot convert string value to type '%s'!", toType);

		case DATE:
			return (T) Dates.date(value);

		default:
			throw U.notExpected();
		}
	}

	private static Enumeration<URL> resources(String name) {

		name = name.replace('.', '/');

		if (name.equals("*")) {
			name = "";
		}

		try {
			return Thread.currentThread().getContextClassLoader().getResources(name);
		} catch (IOException e) {
			throw U.rte("Cannot scan: " + name, e);
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

	public static void setPropValue(Object instance, String propertyName, Object value) {
		String propertyNameCap = U.capitalized(propertyName);
		try {
			for (Class<?> c = instance.getClass(); c != Object.class; c = c.getSuperclass()) {
				try {
					invoke(c.getDeclaredMethod(propertyName), instance, value);
					return;
				} catch (NoSuchMethodException e) {
					try {
						invoke(c.getDeclaredMethod("set" + propertyNameCap), instance, value);
						return;
					} catch (NoSuchMethodException e2) {
						try {
							setFieldValue(c.getDeclaredField(propertyName), instance, value);
							return;
						} catch (NoSuchFieldException e4) {
							// keep searching in the super-class...
						}
					}
				}
			}
		} catch (Exception e) {
			throw U.rte("Cannot get property value!", e);
		}
		throw U.rte("Cannot find the property '%s' in the class '%s'", propertyName, instance.getClass());
	}

	public static Object getPropValue(Object instance, String propertyName) {
		String propertyNameCap = U.capitalized(propertyName);
		try {
			for (Class<?> c = instance.getClass(); c != Object.class; c = c.getSuperclass()) {
				try {
					return invoke(c.getDeclaredMethod(propertyName), instance);
				} catch (NoSuchMethodException e) {
					try {
						return invoke(c.getDeclaredMethod("get" + propertyNameCap), instance);
					} catch (NoSuchMethodException e2) {
						try {
							return invoke(c.getDeclaredMethod("is" + propertyNameCap), instance);
						} catch (NoSuchMethodException e3) {
							try {
								return getFieldValue(c.getDeclaredField(propertyName), instance);
							} catch (NoSuchFieldException e4) {
								// keep searching in the super-class...
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw U.rte("Cannot get property value!", e);
		}
		throw U.rte("Cannot find the property '%s' in the class '%s'", propertyName, instance.getClass());
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

	public static <T, B extends Builder<T>> B builder(final Class<B> builderClass, final Class<T> builtClass,
			final Class<? extends T> implClass) {

		final Map<String, Object> properties = U.map();

		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.getDeclaringClass().equals(Builder.class)) {
					return inject(newInstance(implClass, properties), properties);
				} else {
					U.must(args.length == 1, "expected 1 argument!");
					properties.put(method.getName(), args[0]);
					return proxy;
				}
			}
		};

		B builder = implement(handler, builderClass);
		return builder;
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

	public static Object[] instantiateAll(Class<?>... classes) {
		Object[] instances = new Object[classes.length];

		for (int i = 0; i < instances.length; i++) {
			instances[i] = U.newInstance(classes[i]);
		}

		return instances;
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

	public static String gcInfo() {
		String gcinfo = "";

		if (getGarbageCollectorMXBeans != null) {
			List<?> gcs = invokeStatic(getGarbageCollectorMXBeans);

			for (Object gc : gcs) {
				gcinfo += " | " + getPropValue(gc, "name") + " x" + getPropValue(gc, "collectionCount") + ":"
						+ getPropValue(gc, "collectionTime") + "ms";
			}
		}
		return gcinfo;
	}

}
