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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
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
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rapidoid.annotation.Autocreate;
import org.rapidoid.annotation.Init;
import org.rapidoid.annotation.Inject;
import org.rapidoid.lambda.F1;
import org.rapidoid.lambda.F2;
import org.rapidoid.lambda.F3;
import org.rapidoid.lambda.Predicate;

public class U implements Constants {

	public static final LogLevel TRACE = LogLevel.TRACE;
	public static final LogLevel DEBUG = LogLevel.DEBUG;
	public static final LogLevel INFO = LogLevel.INFO;
	public static final LogLevel WARN = LogLevel.WARN;
	public static final LogLevel ERROR = LogLevel.ERROR;
	public static final LogLevel SEVERE = LogLevel.SEVERE;

	private static LogLevel LOG_LEVEL = INFO;

	private static final Map<Class<?>, Object> SINGLETONS = map();

	protected static final Random RND = new Random();

	private static final Map<Class<?>, Map<String, ? extends RuntimeException>> EXCEPTIONS = autoExpandingMap(new F1<Map<String, ? extends RuntimeException>, Class<?>>() {
		@Override
		public Map<String, ? extends RuntimeException> execute(final Class<?> clazz) throws Exception {
			return autoExpandingMap(new F1<RuntimeException, String>() {
				@Override
				public RuntimeException execute(String msg) throws Exception {
					return (RuntimeException) (msg.isEmpty() ? newInstance(clazz) : newInstance(clazz, msg));
				}
			});
		}
	});

	private static final Method getGarbageCollectorMXBeans;

	static {
		Class<?> manFactory = U.getClassIfExists("java.lang.management.ManagementFactory");
		getGarbageCollectorMXBeans = manFactory != null ? U.getMethod(manFactory, "getGarbageCollectorMXBeans") : null;
	}

	private static ScheduledThreadPoolExecutor EXECUTOR;

	private static long measureStart;

	private static String[] ARGS = {};

	private static final Class<U> CLASS = U.class;

	public static final ClassLoader CLASS_LOADER = CLASS.getClassLoader();

	private static final Set<Class<?>> MANAGED_CLASSES = set();

	private static final Set<Object> MANAGED_INSTANCES = set();

	private static final Map<Object, Object> IOC_INSTANCES = map();

	private static final Map<Class<?>, List<Field>> INJECTABLE_FIELDS = autoExpandingMap(new F1<List<Field>, Class<?>>() {
		@Override
		public List<Field> execute(Class<?> clazz) throws Exception {
			List<Field> fields = getFieldsAnnotated(clazz, Inject.class);
			debug("Retrieved injectable fields", "class", clazz, "fields", fields);
			return fields;
		}
	});

	private static final Calendar CALENDAR = Calendar.getInstance();

	private static final Map<Class<?>, Set<Object>> INJECTION_PROVIDERS = map();

	private static final Map<String, TypeKind> KINDS = initKinds();

	/* RFC 1123 date-time format, e.g. Sun, 07 Sep 2014 00:17:29 GMT */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	private static final Date CURR_DATE = new Date();
	private static byte[] CURR_DATE_BYTES;
	private static long updateCurrDateAfter = 0;

	private static Properties CONFIG = null;

	static {
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private U() {
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

	private static final Map<Class<?>, List<F3<Object, Object, Method, Object[]>>> INTERCEPTORS = map();

	private static final Map<Class<?>, Map<String, Prop>> BEAN_PROPERTIES = autoExpandingMap(new F1<Map<String, Prop>, Class<?>>() {

		@Override
		public Map<String, Prop> execute(Class<?> clazz) throws Exception {

			Map<String, Prop> properties = map();

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

	public static synchronized void setLogLevel(LogLevel logLevel) {
		LOG_LEVEL = logLevel;
	}

	public static synchronized LogLevel getLogLevel() {
		return LOG_LEVEL;
	}

	public static synchronized void reset() {
		info("Reset U state");
		LOG_LEVEL = INFO;
		SINGLETONS.clear();
		ARGS = new String[] {};
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

	private static String getCallingClass() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		for (int i = 2; i < trace.length; i++) {
			String cls = trace[i].getClassName();
			if (!cls.equals(CLASS.getCanonicalName())) {
				return cls;
			}
		}

		return CLASS.getCanonicalName();
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

	private static void log(LogLevel level, String msg, String key1, Object value1, String key2, Object value2,
			String key3, Object value3, int paramsN) {
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
			throw rte("Cannot set field value!", e);
		}

		throw rte("Cannot find the field '%s' in the class '%s'", fieldName, instance.getClass());
	}

	public static void setFieldValue(Field field, Object instance, Object value) {
		try {
			field.setAccessible(true);
			field.set(instance, value);
			field.setAccessible(false);
		} catch (Exception e) {
			throw rte("Cannot set field value!", e);
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
			throw rte("Cannot get field value!", e);
		}

		throw rte("Cannot find the field '%s' in the class '%s'", fieldName, instance.getClass());
	}

	public static Object getFieldValue(Field field, Object instance) {
		try {
			field.setAccessible(true);
			Object value = field.get(instance);
			field.setAccessible(false);

			return value;
		} catch (Exception e) {
			throw rte("Cannot get field value!", e);
		}
	}

	public static List<Annotation> getAnnotations(Class<?> clazz) {
		List<Annotation> allAnnotations = list();

		try {
			for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
				Annotation[] annotations = c.getDeclaredAnnotations();
				for (Annotation an : annotations) {
					allAnnotations.add(an);
				}
			}

		} catch (Exception e) {
			throw rte("Cannot instantiate class!", e);
		}

		return allAnnotations;
	}

	public static List<Field> getFields(Class<?> clazz) {
		List<Field> allFields = list();

		try {
			for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
				Field[] fields = c.getDeclaredFields();
				for (Field field : fields) {
					allFields.add(field);
				}
			}

		} catch (Exception e) {
			throw rte("Cannot instantiate class!", e);
		}

		return allFields;
	}

	public static List<Field> getFieldsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Field> annotatedFields = list();

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
			throw rte("Cannot instantiate class!", e);
		}

		return annotatedFields;
	}

	public static List<Method> getMethodsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Method> annotatedMethods = list();

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
			throw rte("Cannot instantiate class!", e);
		}

		return annotatedMethods;
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw rte("Cannot find method: %s", e, name);
		} catch (SecurityException e) {
			throw rte("Cannot access method: %s", e, name);
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
			throw rte("Cannot statically invoke method '%s' with args: %s", e, m.getName(), Arrays.toString(args));
		} catch (IllegalArgumentException e) {
			throw rte("Cannot statically invoke method '%s' with args: %s", e, m.getName(), Arrays.toString(args));
		} catch (InvocationTargetException e) {
			throw rte("Cannot statically invoke method '%s' with args: %s", e, m.getName(), Arrays.toString(args));
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
			throw rte("Cannot invoke method '%s' with args: %s", e, m.getName(), Arrays.toString(args));
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
			throw rte("Cannot retrieve implemented interfaces!", e);
		}
	}

	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... paramTypes) {
		try {
			return (Constructor<T>) clazz.getConstructor(paramTypes);
		} catch (Exception e) {
			throw rte("Cannot find the constructor for %s with param types: %s", e, clazz, Arrays.toString(paramTypes));
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

	public static URL resource(String filename) {
		return CLASS_LOADER.getResource(filename);
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

	// a duplicate without varargs, to avoid creating array
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
		InputStream input = CLASS_LOADER.getResourceAsStream(filename);
		return input != null ? loadBytes(input) : null;
	}

	public static byte[] classBytes(String fullClassName) {
		return U.loadBytes(fullClassName.replace('.', '/') + ".class");
	}

	public static String load(String filename) {
		return new String(loadBytes(filename));
	}

	public static List<String> loadLines(String filename) {
		InputStream input = CLASS_LOADER.getResourceAsStream(filename);
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
						throw rte(e);
					}

					put((K) key, val);
				}

				return val;
			}
		};
	}

	public static <K, V> Map<K, V> autoExpandingMap(final Class<V> clazz) {
		return autoExpandingMap(new F1<V, K>() {

			@Override
			public V execute(K param) throws Exception {
				return inject(newInstance(clazz));
			}

		});
	}

	public static void waitFor(AtomicBoolean done) {
		while (!done.get()) {
			sleep(5);
		}
	}

	public static void waitFor(AtomicInteger n, int value) {
		while (n.get() != value) {
			sleep(5);
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
			throw notExpected();

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
			throw notExpected();
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
			throw notExpected();
		}
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

	public static String copyNtimes(String s, int n) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static RuntimeException rte(String message, Object... args) {
		return new RuntimeException(format(message, args));
	}

	public static RuntimeException rte(String message, Throwable cause, Object... args) {
		return new RuntimeException(format(message, args), cause);
	}

	public static RuntimeException rte(String message, Throwable cause) {
		return new RuntimeException(message, cause);
	}

	public static RuntimeException rte(Throwable cause) {
		return new RuntimeException(cause);
	}

	public static RuntimeException rte(String message) {
		return cachedRTE(RuntimeException.class, message);
	}

	public static <T extends RuntimeException> T rte(Class<T> clazz) {
		return cachedRTE(clazz, null);
	}

	public static <T extends RuntimeException> T rte(Class<T> clazz, String msg) {
		return cachedRTE(clazz, msg);
	}

	@SuppressWarnings("unchecked")
	private static synchronized <T extends RuntimeException> T cachedRTE(Class<T> clazz, String msg) {
		return (T) EXCEPTIONS.get(clazz).get(U.or(msg, ""));
	}

	public static boolean must(boolean expectedCondition) {
		if (!expectedCondition) {
			throw rte("Expectation failed!");
		}
		return true;
	}

	public static boolean must(boolean expectedCondition, String message) {
		if (!expectedCondition) {
			throw rte(message);
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

		String data = format("%s: %s in %s ms (%s/sec)", name, count, ms, avgs);

		print(data + " | " + getCpuMemStats());
	}

	public static void benchmarkMT(int threadsN, final String name, final int count, final Runnable runnable) {
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
			throw U.rte(e);
		}
	}

	public static String getCpuMemStats() {
		Runtime rt = Runtime.getRuntime();
		long totalMem = rt.totalMemory();
		long maxMem = rt.maxMemory();
		long freeMem = rt.freeMemory();
		long usedMem = totalMem - freeMem;
		int megs = 1024 * 1024;

		String msg = "MEM [total=%s MB, used=%s MB, max=%s MB]%s";
		return format(msg, totalMem / megs, usedMem / megs, maxMem / megs, gcInfo());
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

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(InvocationHandler handler, Class<?>... interfaces) {
		return ((T) Proxy.newProxyInstance(CLASS_LOADER, interfaces, handler));
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
				trace("intercepting", "method", method.getName(), "args", Arrays.toString(args));
				return method.invoke(target, args);
			}
		});
	}

	public static void show(Object... values) {
		String text = values.length == 1 ? text(values[0]) : text(values);
		print(">" + text + "<");
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

		throw rte("Cannot find appropriate constructor for %s with args %s!", clazz, U.text(args));
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

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Map<String, Object> properties) {
		if (properties == null) {
			return newInstance(clazz);
		}

		Collection<Object> values = properties.values();

		for (Constructor<?> constr : clazz.getConstructors()) {
			Class<?>[] paramTypes = constr.getParameterTypes();
			Object[] args = getAssignableArgs(paramTypes, values);
			if (args != null) {
				try {
					return (T) constr.newInstance(args);
				} catch (Exception e) {
					throw rte(e);
				}
			}
		}

		throw rte("Cannot find appropriate constructor for %s with args %s!", clazz, values);
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
					throw rte("Found more than one instance of %s: %s and %s", type, instance, obj);
				}
			}
		}

		return instance;
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

	public static synchronized void args(String... args) {
		if (args != null) {
			ARGS = args;

			if (hasOption("stats")) {
				singleton(StatsThread.class).start();
			}

			if (hasOption("debug") && getLogLevel().ordinal() > DEBUG.ordinal()) {
				setLogLevel(DEBUG);
			}
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

	public static void connect(String address, int port, F2<Void, BufferedReader, DataOutputStream> protocol) {
		Socket socket = null;

		try {
			socket = new Socket(address, port);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			protocol.execute(in, out);

			socket.close();
		} catch (Exception e) {
			throw rte(e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					throw rte(e);
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
			socket.bind(isEmpty(hostname) ? new InetSocketAddress(port) : new InetSocketAddress(hostname, port));

			info("Starting TCP/IP server", "host", hostname, "port", port);

			while (true) {
				final Socket conn = socket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());

				try {
					protocol.execute(in, out);
				} catch (Exception e) {
					throw rte(e);
				} finally {
					conn.close();
				}
			}
		} catch (Exception e) {
			throw rte(e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					throw rte(e);
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

	public static String capitalized(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static void setId(Object obj, long id) {
		setPropValue(obj, "id", id);
	}

	public static long getId(Object obj) {
		Object id = getPropValue(obj, "id");
		if (id == null) {
			throw rte("The field 'id' cannot be null!");
		}
		if (id instanceof Long) {
			Long num = (Long) id;
			return num;
		} else {
			throw rte("The field 'id' must have type 'long', but it has: " + id.getClass());
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
				throw rte("Cannot replace text!", e);
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
				debug("configuring managed class", "class", classOrInstance);
				MANAGED_CLASSES.add(clazz);

				if (!clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation()) {
					System.out.println(":" + clazz);

					// if the class is annotated, auto-create an instance
					if (clazz.getAnnotation(Autocreate.class) != null) {
						autocreate.add(clazz);
					}
				}
			} else {
				debug("configuring managed instance", "instance", classOrInstance);
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
			providers = set();
			INJECTION_PROVIDERS.put(type, providers);
		}

		providers.add(provider);
	}

	public static synchronized <T> T singleton(Class<T> type) {
		info("Inject", "type", type);
		return provideIoCInstanceOf(null, type, null, null, false);
	}

	public static synchronized <T> T inject(T target) {
		info("Inject", "target", target);
		return ioc(target, null);
	}

	public static synchronized <T> T inject(T target, Map<String, Object> properties) {
		info("Inject", "target", target, "properties", properties);
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
					throw rte("Didn't find a value for type '%s' and name '%s'!", type, name);
				} else {
					throw rte("Didn't find a value for type '%s'!", type);
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
						throw rte("Found more than 1 injection candidates for type '%s': %s", type, providers);
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
				instance = hasOption(name);
			} else {
				String opt = option(name, null);
				if (opt != null) {
					instance = convert(opt, type);
				}
			}
		}

		return (T) instance;
	}

	private static void autowire(Object target, Map<String, Object> properties) {
		debug("Autowiring", "target", target);

		for (Field field : INJECTABLE_FIELDS.get(target.getClass())) {

			boolean optional = isInjectOptional(field);
			Object value = provideIoCInstanceOf(target, field.getType(), field.getName(), properties, optional);

			debug("Injecting field value", "target", target, "field", field.getName(), "value", value);

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
		Set<F3<Object, Object, Method, Object[]>> done = set();

		for (Class<?> interf : getImplementedInterfaces(instance.getClass())) {
			final List<F3<Object, Object, Method, Object[]>> interceptors = INTERCEPTORS.get(interf);

			if (interceptors != null) {
				for (final F3<Object, Object, Method, Object[]> interceptor : interceptors) {
					if (interceptor != null && !done.contains(interceptor)) {
						debug("Creating proxy", "target", instance, "interface", interf, "interceptor", interceptor);

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
			throw notExpected();

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

			throw rte("Cannot convert the string value '%s' to boolean!", value);

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
			throw rte("Cannot convert string value to type '%s'!", toType);

		case DATE:
			return (T) date(value);

		default:
			throw notExpected();
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
			throw rte("Cannot scan: " + name, e);
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
			debug("scanning directory", "dir", file);
			for (File f : file.listFiles()) {
				if (f.isDirectory()) {
					getFiles(files, f, filter);
				} else {
					debug("scanned file", "file", f);
					try {
						if (filter == null || filter.eval(f)) {
							files.add(f);
						}
					} catch (Exception e) {
						throw rte(e);
					}
				}
			}
		}
	}

	private static void getClasses(Collection<Class<?>> classes, File root, File parent, Pattern nameRegex,
			Predicate<Class<?>> filter) {

		if (parent.isDirectory()) {
			debug("scanning directory", "dir", parent);
			for (File f : parent.listFiles()) {
				if (f.isDirectory()) {
					getClasses(classes, root, f, nameRegex, filter);
				} else {
					debug("scanned file", "file", f);
					try {
						if (f.getName().endsWith(".class")) {
							String clsName = f.getAbsolutePath();
							String rootPath = root.getAbsolutePath();
							U.must(clsName.startsWith(rootPath));

							clsName = clsName.substring(rootPath.length() + 1, clsName.length() - 6);
							clsName = clsName.replace(File.separatorChar, '.');

							if (nameRegex.matcher(clsName).matches()) {
								info("loading class", "name", clsName);
								Class<?> cls = Class.forName(clsName);
								if (filter == null || filter.eval(cls)) {
									classes.add(cls);
								}
							}
						}
					} catch (Exception e) {
						throw rte(e);
					}
				}
			}
		}
	}

	public static String urlDecode(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw U.rte(e);
		}
	}

	public static void setPropValue(Object instance, String propertyName, Object value) {
		String propertyNameCap = capitalized(propertyName);
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
			throw rte("Cannot get property value!", e);
		}
		throw rte("Cannot find the property '%s' in the class '%s'", propertyName, instance.getClass());
	}

	public static Object getPropValue(Object instance, String propertyName) {
		String propertyNameCap = capitalized(propertyName);
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
			throw rte("Cannot get property value!", e);
		}
		throw rte("Cannot find the property '%s' in the class '%s'", propertyName, instance.getClass());
	}

	public static String mul(String s, int n) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < n; i++) {
			sb.append(s);
		}

		return sb.toString();
	}

	public static Date date(String value) {
		String[] parts = value.split("(\\.|-|/)");

		int a = parts.length > 0 ? num(parts[0]) : -1;
		int b = parts.length > 1 ? num(parts[1]) : -1;
		int c = parts.length > 2 ? num(parts[2]) : -1;

		switch (parts.length) {
		case 3:
			if (isDay(a) && isMonth(b) && isYear(c)) {
				return date(a, b, c);
			} else if (isYear(a) && isMonth(b) && isDay(c)) {
				return date(c, b, a);
			}
			break;
		case 2:
			if (isDay(a) && isMonth(b)) {
				return date(a, b, thisYear());
			}
			break;
		default:
		}

		throw rte("Invalid date: " + value);
	}

	private static boolean isDay(int day) {
		return day >= 1 && day <= 31;
	}

	private static boolean isMonth(int month) {
		return month >= 1 && month <= 12;
	}

	private static boolean isYear(int year) {
		return year >= 1000;
	}

	public static int num(String s) {
		return Integer.parseInt(s);
	}

	public static synchronized Date date(int day, int month, int year) {
		CALENDAR.set(year, month - 1, day - 1);
		return CALENDAR.getTime();
	}

	public static synchronized int thisYear() {
		CALENDAR.setTime(new Date());
		return CALENDAR.get(Calendar.YEAR);
	}

	public static short bytesToShort(String s) {
		ByteBuffer buf = buf(s);
		must(buf.limit() == 2);
		return buf.getShort();
	}

	public static int bytesToInt(String s) {
		ByteBuffer buf = buf(s);
		must(buf.limit() == 4);
		return buf.getInt();
	}

	public static long bytesToLong(String s) {
		ByteBuffer buf = buf(s);
		must(buf.limit() == 8);
		return buf.getLong();
	}

	public static int intFrom(byte a, byte b, byte c, byte d) {
		return (a << 24) + (b << 16) + (c << 8) + d;
	}

	public static short shortFrom(byte a, byte b) {
		return (short) ((a << 8) + b);
	}

	public static String format(String s, Object... args) {
		return String.format(s, args);
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
			throw U.rte(e);
		}
	}

	public static MappedByteBuffer mmap(String filename, MapMode mode) {
		File file = new File(filename);
		U.must(file.exists());
		return mmap(filename, mode, 0, file.length());
	}

	public static Class<?> getClassIfExists(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static <T, B extends Builder<T>> B builder(final Class<B> builderClass, final Class<T> builtClass,
			final Class<? extends T> implClass) {

		final Map<String, Object> properties = map();

		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.getDeclaringClass().equals(Builder.class)) {
					return inject(newInstance(implClass, properties), properties);
				} else {
					must(args.length == 1, "expected 1 argument!");
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
			instances[i] = newInstance(classes[i]);
		}

		return instances;
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

}
