package org.rapidoid.cls;

import javassist.*;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.rapidoid.RapidoidThing;
import org.rapidoid.beany.Beany;
import org.rapidoid.collection.AutoExpandingMap;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.Arr;
import org.rapidoid.commons.Dates;
import org.rapidoid.commons.Err;
import org.rapidoid.commons.Str;
import org.rapidoid.io.IO;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.TUUID;
import org.rapidoid.var.Var;
import org.rapidoid.var.Vars;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

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

/**
 * @author Nikolche Mihajlovski
 * @since 2.0.0
 */
public class Cls extends RapidoidThing {

	private static Pattern JRE_CLASS_PATTERN = Pattern
		.compile("^(java|javax|javafx|com\\.sun|sun|com\\.oracle|oracle|jdk|org\\.omg|org\\.w3c).*");

	private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = U.map(boolean.class, Boolean.class, byte.class,
		Byte.class, char.class, Character.class, double.class, Double.class, float.class, Float.class, int.class,
		Integer.class, long.class, Long.class, short.class, Short.class, void.class, Void.class);

	private static Set<String> RAPIDOID_CLASSES = U.set(IO.loadLines("rapidoid-classes.txt"));

	private static final Object[] EMPTY_ARRAY = {};

	private Cls() {
	}

	public static TypeKind kindOf(Class<?> type) {
		return TypeKind.ofType(type);
	}

	public static TypeKind kindOf(Object value) {
		return TypeKind.of(value);
	}

	public static void setFieldValue(Object instance, String fieldName, Object value) {
		try {
			for (Class<?> c = instance.getClass(); c.getSuperclass() != null; c = c.getSuperclass()) {
				try {
					Field field = c.getDeclaredField(fieldName);
					field.setAccessible(true);
					field.set(instance, value);
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
		} catch (Exception e) {
			throw U.rte("Cannot set field value!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(Object instance, String fieldName, T defaultValue) {
		try {
			return (T) getFieldValue(instance, fieldName);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static Object getFieldValue(Object instance, String fieldName) {
		try {
			for (Class<?> c = instance.getClass(); c.getSuperclass() != null; c = c.getSuperclass()) {
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

			return value;
		} catch (Exception e) {
			throw U.rte("Cannot get field value!", e);
		}
	}

	public static List<Annotation> getAnnotations(Class<?> clazz) {
		List<Annotation> allAnnotations = U.list();

		try {
			for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
				Annotation[] annotations = c.getDeclaredAnnotations();
				for (Annotation an : annotations) {
					allAnnotations.add(an);
				}
			}

		} catch (Exception e) {
			throw U.rte("Cannot get annotations!", e);
		}

		return allAnnotations;
	}

	public static List<Field> getFields(Class<?> clazz) {
		List<Field> allFields = U.list();

		try {
			for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
				Field[] fields = c.getDeclaredFields();
				for (Field field : fields) {
					allFields.add(field);
				}
			}

		} catch (Exception e) {
			throw U.rte("Cannot get fields!", e);
		}

		return allFields;
	}

	public static List<Field> getFieldsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Field> annotatedFields = U.list();

		try {
			for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
				Field[] fields = c.getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(annotation)) {
						annotatedFields.add(field);
					}
				}
			}

		} catch (Exception e) {
			throw U.rte("Cannot get annotated fields!", e);
		}

		return annotatedFields;
	}

	public static List<Method> getMethods(Class<?> clazz) {
		List<Method> methods = U.list();

		try {
			for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
				for (Method method : c.getDeclaredMethods()) {
					methods.add(method);
				}
			}

		} catch (Exception e) {
			throw U.rte("Cannot get methods!", e);
		}

		return methods;
	}

	public static List<Method> getMethodsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Method> annotatedMethods = U.list();

		try {
			for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
				Method[] methods = c.getDeclaredMethods();
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

	public static List<Method> getMethodsNamed(Class<?> clazz, String name) {
		List<Method> methods = U.list();

		try {
			for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
				for (Method method : c.getDeclaredMethods()) {
					if (method.getName().equals(name)) {
						methods.add(method);
					}
				}
			}

		} catch (Exception e) {
			throw U.rte("Cannot instantiate class!", e);
		}

		return methods;
	}

	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			try {
				return clazz.getDeclaredMethod(name, parameterTypes);
			} catch (NoSuchMethodException e1) {
				throw U.rte("Cannot find method: %s", e, name);
			}
		} catch (SecurityException e) {
			throw U.rte("Cannot access method: %s", e, name);
		}
	}

	public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			try {
				return clazz.getDeclaredMethod(name, parameterTypes);
			} catch (NoSuchMethodException e1) {
				return null;
			}
		} catch (SecurityException e) {
			return null;
		}
	}

	public static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getField(name);
		} catch (NoSuchFieldException e) {
			throw U.rte("Cannot find field: %s", e, name);
		} catch (SecurityException e) {
			throw U.rte("Cannot access field: %s", e, name);
		}
	}

	public static Field findField(Class<?> clazz, String name) {
		try {
			return clazz.getField(name);
		} catch (NoSuchFieldException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeStatic(Method m, Object... args) {
		try {
			m.setAccessible(true);
			return (T) m.invoke(null, args);
		} catch (IllegalAccessException e) {
			throw U.rte("Cannot statically invoke method '%s' with args: %s", e, m.getName(), U.str(args));
		} catch (IllegalArgumentException e) {
			throw U.rte("Cannot statically invoke method '%s' with args: %s", e, m.getName(), U.str(args));
		} catch (InvocationTargetException e) {
			throw U.rte("Cannot statically invoke method '%s' with args: %s", e, m.getName(), U.str(args));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Method m, Object target, Object... args) {
		try {
			m.setAccessible(true);
			return (T) m.invoke(target, args);
		} catch (Exception e) {
			throw U.rte("Cannot invoke method '%s' with args: %s", e, m.getName(), U.str(args));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Constructor<?> constructor, Object... args) {
		try {
			constructor.setAccessible(true);
			return (T) constructor.newInstance(args);
		} catch (Exception e) {
			throw U.rte("Cannot invoke constructor '%s' with args: %s", e, constructor.getName(), U.str(args));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeRethrowing(Method method, Object target, Object... args) throws Throwable {
		try {
			return (T) method.invoke(target, args);

		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			throw cause != null ? cause : e;
		}
	}

	public static Class<?>[] getImplementedInterfaces(Class<?> clazz) {
		try {
			List<Class<?>> interfaces = new LinkedList<Class<?>>();

			for (Class<?> c = clazz; c.getSuperclass() != null; c = c.getSuperclass()) {
				for (Class<?> interf : c.getInterfaces()) {
					interfaces.add(interf);
				}
			}

			return interfaces.toArray(new Class<?>[interfaces.size()]);
		} catch (Exception e) {
			throw U.rte("Cannot retrieve implemented interfaces!", e);
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

	public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... paramTypes) {
		try {
			return (Constructor<T>) clazz.getConstructor(paramTypes);
		} catch (Exception e) {
			throw U.rte("Cannot find the constructor for %s with param types: %s", e, clazz,
				U.str(paramTypes));
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Map<String, Object> properties) {
		if (U.isEmpty(properties)) {
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
					throw U.rte(e);
				}
			}
		}

		throw U.rte("Cannot find appropriate constructor for %s with args %s!", clazz, values);
	}

	public static Object[] getAssignableArgs(Class<?>[] types, Collection<?> properties) {
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
			if (instanceOf(obj, type)) {
				if (instance == null) {
					instance = (T) obj;
				} else {
					throw U.rte("Found more than one instance of %s: %s and %s", type, instance, obj);
				}
			}
		}

		return instance;
	}

	public static Object[] instantiateAll(Class<?>... classes) {
		Object[] instances = new Object[classes.length];

		for (int i = 0; i < instances.length; i++) {
			instances[i] = newInstance(classes[i]);
		}

		return instances;
	}

	public static Object[] instantiateAll(Collection<Class<?>> classes) {
		if (classes.isEmpty()) {
			return EMPTY_ARRAY;
		}
		Object[] instances = new Object[classes.size()];

		int i = 0;
		for (Class<?> clazz : classes) {
			instances[i++] = newInstance(clazz);
		}

		return instances;
	}

	public static ClassLoader classLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, Class<T> toType) {

		if (value == null) {
			return null;
		}

		if (toType.equals(Object.class)) {
			return (T) value;
		}

		if (Enum.class.isAssignableFrom(toType)) {
			T[] ens = toType.getEnumConstants();
			Enum<?> en;
			for (T t : ens) {
				en = (Enum<?>) t;
				if (en.name().equalsIgnoreCase(value)) {
					return (T) en;
				}
			}

			throw U.rte("Cannot find the enum constant: %s.%s", toType, value);
		}

		TypeKind targetKind = Cls.kindOf(toType);

		switch (targetKind) {

			case NULL:
				throw Err.notExpected();

			case BOOLEAN:
			case BOOLEAN_OBJ:
				if ("y".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
					return (T) Boolean.TRUE;
				}

				if ("n".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
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

			case UNKNOWN:
				Constructor<T> constructor;

				try {
					constructor = toType.getConstructor(String.class);
				} catch (Exception e) {
					throw U.rte("Cannot convert string value to type '%s'!", toType);
				}

				try {
					return constructor.newInstance(value);
				} catch (Exception e) {
					throw U.rte("Cannot invoke constructor, trying to convert string value to type '%s'!", toType);
				}

			case DATE:
				return (T) Dates.date(value);

			case UUID:
				return (T) UUID.fromString(value);

			case TUUID:
				return (T) TUUID.fromString(value);

			default:
				throw U.rte("Cannot convert String to type '%s'!", toType);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(Object value, Class<T> toType) {

		if (value == null) {
			return null;
		}

		if (toType.isAssignableFrom(value.getClass())) {
			return (T) value;
		}

		if (toType.equals(Object.class)) {
			return (T) value;
		}

		if (value instanceof String) {
			return convert((String) value, toType);
		}

		TypeKind targetKind = Cls.kindOf(toType);
		boolean isNum = value instanceof Number;

		switch (targetKind) {

			case NULL:
				throw Err.notExpected();

			case BOOLEAN:
			case BOOLEAN_OBJ:
				if (value instanceof Boolean) {
					return (T) value;

				} else if (value instanceof Number) {
					Number num = (Number) value;
					Object asBool = num.longValue() != 0L;
					return (T) asBool;

				} else {
					throw U.rte("Cannot convert the value '%s' to boolean!", value);
				}

			case BYTE:
			case BYTE_OBJ:
				if (isNum) {
					return (T) new Byte(((Number) value).byteValue());
				} else {
					throw U.rte("Cannot convert the value '%s' to byte!", value);
				}

			case SHORT:
			case SHORT_OBJ:
				if (isNum) {
					return (T) new Short(((Number) value).shortValue());
				} else {
					throw U.rte("Cannot convert the value '%s' to short!", value);
				}

			case CHAR:
			case CHAR_OBJ:
				if (isNum) {
					return (T) new Character((char) ((Number) value).intValue());
				} else {
					throw U.rte("Cannot convert the value '%s' to char!", value);
				}

			case INT:
			case INT_OBJ:
				if (isNum) {
					return (T) new Integer(((Number) value).intValue());
				} else {
					throw U.rte("Cannot convert the value '%s' to int!", value);
				}

			case LONG:
			case LONG_OBJ:
				if (isNum) {
					return (T) new Long(((Number) value).longValue());
				} else {
					throw U.rte("Cannot convert the value '%s' to long!", value);
				}

			case FLOAT:
			case FLOAT_OBJ:
				if (isNum) {
					return (T) new Float(((Number) value).floatValue());
				} else {
					throw U.rte("Cannot convert the value '%s' to float!", value);
				}

			case DOUBLE:
			case DOUBLE_OBJ:
				if (isNum) {
					return (T) new Double(((Number) value).doubleValue());
				} else {
					throw U.rte("Cannot convert the value '%s' to double!", value);
				}

			case STRING:
				if (value instanceof Date) {
					return (T) Dates.str((Date) value);
				} else if (value instanceof byte[]) {
					return (T) new String((byte[]) value);
				} else if (value instanceof char[]) {
					return (T) new String((char[]) value);
				} else {
					return (T) U.str(value);
				}

			case UNKNOWN:
				throw U.rte("Cannot convert the value to type '%s'!", toType);

			case DATE:
				if (value instanceof Date) {
					return (T) value;
				} else if (value instanceof Number) {
					return (T) new Date(((Number) value).longValue());
				} else {
					throw U.rte("Cannot convert the value '%s' to date!", value);
				}

			case UUID:
				if (value instanceof byte[]) {
					return (T) Msc.bytesToUUID((byte[]) value);
				} else {
					throw U.rte("Cannot convert the value '%s' to UUID!", value);
				}

			case TUUID:
				if (value instanceof byte[]) {
					return (T) TUUID.fromBytes(((byte[]) value));
				} else {
					throw U.rte("Cannot convert the value '%s' to TUUID!", value);
				}


			default:
				throw Err.notExpected();
		}
	}

	public static Map<String, Class<?>> classMap(Iterable<Class<?>> classes) {
		Map<String, Class<?>> map = new LinkedHashMap<String, Class<?>>();

		for (Class<?> cls : classes) {
			map.put(cls.getSimpleName(), cls);
		}

		return map;
	}

	public static Class<?>[] typesOf(Object[] args) {
		Class<?>[] types = new Class<?>[args.length];

		for (int i = 0; i < types.length; i++) {
			types[i] = args[i] != null ? args[i].getClass() : null;
		}

		return types;
	}

	public static Method findMethodByArgs(Class<? extends Object> clazz, String name, Object... args) {

		for (Method method : clazz.getMethods()) {
			Class<?>[] paramTypes = method.getParameterTypes();

			if (method.getName().equals(name) && areAssignable(paramTypes, args)) {
				return method;
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> clazz(Type type) {
		return (Class<T>) (type instanceof Class ? type : Object.class);
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> of(T obj) {
		return (Class<T>) (obj != null ? obj.getClass() : Object.class);
	}

	public static String str(Object value) {
		return convert(value, String.class);
	}

	public static boolean bool(Object value) {
		return U.or(convert(value, Boolean.class), false);
	}

	public static ParameterizedType generic(Type type) {
		return (type instanceof ParameterizedType) ? ((ParameterizedType) type) : null;
	}

	public static boolean isJREClass(String className) {
		return JRE_CLASS_PATTERN.matcher(className).matches();
	}

	public static boolean isRapidoidClass(String className) {
		className = className.split("\\$")[0]; // without inner classes
		return RAPIDOID_CLASSES.contains(className);
	}

	public static boolean isIdeOrToolClass(String className) {
		return className.startsWith("com.intellij.rt.execution.");
	}

	public static Set<String> getRapidoidClasses() {
		return RAPIDOID_CLASSES;
	}

	public static boolean isRapidoidClass(Class<?> clazz) {
		return isRapidoidClass(clazz.getName());
	}

	public static boolean isJREType(Class<?> type) {
		return isJREClass(type.getName());
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getWrapperClass(Class<T> c) {
		U.must(c.isPrimitive());
		return c.isPrimitive() ? (Class<T>) PRIMITIVE_WRAPPERS.get(c) : c;
	}

	public static boolean instanceOf(Object obj, Class<?>... classes) {
		return obj != null ? isAssignableTo(obj.getClass(), classes) : false;
	}

	public static boolean isAssignableTo(Class<?> clazz, Class<?>... targetClasses) {
		for (Class<?> cls : targetClasses) {
			if (cls.isPrimitive()) {
				if (cls.isAssignableFrom(clazz)) {
					return true;
				}
				cls = getWrapperClass(cls);
			}
			if (cls.isAssignableFrom(clazz)) {
				return true;
			}
		}

		return false;
	}

	public static boolean areAssignable(Class<?>[] types, Object[] values) {
		if (types.length != values.length) {
			return false;
		}

		for (int i = 0; i < values.length; i++) {
			Object val = values[i];
			if (val != null && !instanceOf(val, types[i])) {
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz) {

		Err.argMust(clazz != AutoExpandingMap.class, "Cannot instantiate AutoExpandingMap!");

		if (clazz == List.class) {
			return (T) U.list();
		} else if (clazz == Set.class) {
			return (T) U.set();
		} else if (clazz == Map.class) {
			return (T) U.map();
		} else if (clazz == ConcurrentMap.class) {
			return (T) Coll.concurrentMap();
		} else if (clazz.getName().equals("java.util.Collections$SynchronizedSet")) {
			return (T) Coll.synchronizedSet();
		} else if (clazz.getName().equals("java.util.Collections$SynchronizedList")) {
			return (T) Coll.synchronizedList();
		} else if (clazz.getName().equals("java.util.Collections$SynchronizedMap")) {
			return (T) Coll.synchronizedMap();
		} else if (clazz == Var.class) {
			return (T) Vars.var("<new>", null);
		} else if (clazz == Object.class) {
			return (T) new Object();
		}

		return newBeanInstance(clazz);
	}

	public static <T> T newBeanInstance(Class<T> clazz) {
		try {
			Constructor<T> constr = clazz.getDeclaredConstructor();
			constr.setAccessible(true);

			return constr.newInstance();
		} catch (Exception e) {
			throw U.rte("Couldn't instantiate " + clazz, e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz, Object... args) {
		for (Constructor<?> constr : clazz.getConstructors()) {
			Class<?>[] paramTypes = constr.getParameterTypes();
			if (areAssignable(paramTypes, args)) try {

				constr.setAccessible(true);

				return (T) constr.newInstance(args);
			} catch (Exception e) {
				throw U.rte(e);
			}
		}

		throw U.rte("Cannot find appropriate constructor for %s with args %s!", clazz, U.str(args));
	}

	public static <T> T customizable(Class<T> clazz, Object... args) {
		String customClassName = "Customized" + clazz.getSimpleName();

		Class<T> customClass = getClassIfExists(customClassName);

		if (customClass == null) {
			customClass = getClassIfExists("custom." + customClassName);
		}

		if (customClass != null && !clazz.isAssignableFrom(customClass)) {
			customClass = null;
		}

		return newInstance(U.or(customClass, clazz), args);
	}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassIfExists(String className) {
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static <T> Class<T> get(String className) {

		if (className.equals("byte")) return U.cast(byte.class);
		if (className.equals("short")) return U.cast(short.class);
		if (className.equals("int")) return U.cast(int.class);
		if (className.equals("long")) return U.cast(long.class);
		if (className.equals("float")) return U.cast(float.class);
		if (className.equals("double")) return U.cast(double.class);
		if (className.equals("boolean")) return U.cast(boolean.class);

		if (className.endsWith("[]")) {
			String cls = Str.trimr(className, "[]");
			return U.cast(Array.newInstance(get(cls), 0).getClass());
		}

		try {
			return U.cast(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw U.rte(e);
		}
	}

	public static Class<?> unproxy(Class<?> cls) {
		if (Proxy.class.isAssignableFrom(cls)) {
			for (Class<?> interf : cls.getInterfaces()) {
				if (!isJREClass(interf.getCanonicalName())) {
					return interf;
				}
			}
			throw U.rte("Cannot unproxy the class: %s!", cls);
		} else {
			return cls;
		}
	}

	public static String entityName(Class<?> cls) {
		return Cls.unproxy(cls).getSimpleName();
	}

	public static String entityName(Object entity) {
		U.notNull(entity, "entity");
		return entityName(entity.getClass());
	}

	public static boolean isSimple(Object target) {
		return kindOf(target).isConcrete();
	}

	public static boolean isNumber(Object target) {
		return kindOf(target).isNumber();
	}

	public static boolean isDataStructure(Class<?> clazz) {
		return (Collection.class.isAssignableFrom(clazz)) || (Map.class.isAssignableFrom(clazz))
			|| (Object[].class.isAssignableFrom(clazz));
	}

	public static boolean isBeanType(Class<?> clazz) {
		return clazz != null && clazz != Object.class
			&& kindOf(clazz) == TypeKind.UNKNOWN
			&& (!clazz.getName().startsWith("java.") || clazz.getName().startsWith("java.lang.management."))
			&& (!clazz.getName().startsWith("javax.") || clazz.getName().startsWith("javax.management."))
			&& !clazz.isAnnotation()
			&& !clazz.isEnum()
			&& !clazz.isInterface()
			&& !(Collection.class.isAssignableFrom(clazz))
			&& !(Map.class.isAssignableFrom(clazz))
			&& !(Object[].class.isAssignableFrom(clazz));
	}

	public static boolean isAppBeanType(Class<?> clazz) {
		return isBeanType(clazz) && !isJREType(clazz);
	}

	public static boolean isBean(Object target) {
		return target != null && isBeanType(target.getClass());
	}

	public static boolean isAppBean(Object target) {
		return target != null && isAppBeanType(target.getClass());
	}

	public static <T, T2> T struct(Class<T> clazz1, Class<T2> clazz2, Object obj) {
		List<Object> items = U.list();

		if (obj instanceof Map<?, ?>) {
			Map<?, ?> map = U.cast(obj);

			for (Entry<?, ?> e : map.entrySet()) {
				items.add(createFromEntry(clazz2, e, null));
			}

		} else if (obj instanceof List<?>) {
			List<?> list = U.cast(obj);

			for (Object o : list) {

				if (o instanceof Map<?, ?>) {
					Map<?, ?> map = U.cast(o);

					if (!map.isEmpty()) {
						if (map.size() == 1) {
							items.add(createFromEntry(clazz2, map.entrySet().iterator().next(), null));
						} else {
							// more than 1 element
							Map<String, Object> extra = U.map();
							Iterator<Entry<Object, Object>> it = U.cast(map.entrySet().iterator());
							Entry<?, ?> firstEntry = it.next();

							while (it.hasNext()) {
								Entry<?, ?> e = U.cast(it.next());
								extra.put(Cls.str(e.getKey()), e.getValue());
							}

							items.add(createFromEntry(clazz2, firstEntry, extra));
						}
					}

				} else {
					items.add(Cls.newInstance(clazz2, Cls.str(o), null, null));
				}
			}

		} else {
			items.add(Cls.newInstance(clazz2, Cls.str(obj), null, null));
		}

		return Cls.newInstance(clazz1, items);
	}

	private static <T2> T2 createFromEntry(Class<T2> clazz2, Entry<?, ?> e, Map<String, Object> extra) {
		String key = Cls.str(e.getKey());
		T2 item = Cls.newInstance(clazz2, key, e.getValue(), extra);
		return item;
	}

	public static boolean exists(String className) {
		try {
			Class.forName(className, false, Cls.classLoader());
			return true;

		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static Method getLambdaMethod(Serializable lambda) {
		return getLambdaMethod(lambda, "execute");
	}

	public static Method getLambdaMethod(Serializable lambda, String functionalMethodName) {
		Method writeReplace = findMethod(lambda.getClass(), "writeReplace");

		if (writeReplace == null) {
			List<Method> methods = getMethodsNamed(lambda.getClass(), functionalMethodName);

			U.must(U.notEmpty(methods), "Cannot find the lambda method named: %s", functionalMethodName);

			for (Method method : methods) {
				Class<?>[] paramTypes = method.getParameterTypes();
				for (Class<?> paramType : paramTypes) {
					if (!paramType.getName().equals("java.lang.Object")) {
						return method;
					}
				}
			}

			U.must(methods.size() == 1, "Expected one, but found %s lambda methods named: %s", methods.size(), functionalMethodName);

			return methods.get(0);
		}

		Object serializedLambda = invoke(writeReplace, lambda);

		Method getImplClass = Cls.findMethod(serializedLambda.getClass(), "getImplClass");

		if (getImplClass != null) {
			String implClass = Cls.invoke(getImplClass, serializedLambda);
			String className = implClass.replaceAll("/", ".");

			Class<?> cls;
			try {
				cls = Class.forName(className, true, lambda.getClass().getClassLoader());
			} catch (ClassNotFoundException e) {
				throw U.rte("Cannot find or load the lambda class: %s", className);
			}

			Method getImplMethodName = Cls.findMethod(serializedLambda.getClass(), "getImplMethodName");
			String lambdaMethodName = Cls.invoke(getImplMethodName, serializedLambda);

			for (Method method : cls.getDeclaredMethods()) {
				if (method.getName().equals(lambdaMethodName)) {
					return method;
				}
			}

			throw U.rte("Cannot find the lambda method: %s#%s", cls.getName(), lambdaMethodName);
		} else {
			throw U.rte("Cannot find the 'getImplClass' method of the serialized lambda!");
		}
	}

	public static List<Method> getDeclaredMethods(Class<?> clazz) {
		ClassPool cp = new ClassPool();
		cp.insertClassPath(new ClassClassPath(clazz));

		CtClass cc;
		try {
			cc = cp.get(clazz.getName());
		} catch (NotFoundException e) {
			throw U.rte("Cannot find the target class!", e);
		}

		List<Method> methods = U.list();

		for (CtMethod m : cc.getDeclaredMethods()) {
			try {
				methods.add(getMethod(clazz, m.getName(), ctTypes(m.getParameterTypes())));
			} catch (Exception e) {
				throw U.rte(e);
			}
		}

		return methods;
	}

	private static Class<?>[] ctTypes(CtClass[] types) {
		Class<?>[] classes = new Class[types.length];

		for (int i = 0; i < classes.length; i++) {
			classes[i] = get(types[i].getName());
		}

		return classes;
	}

	public static String[] getMethodParameterNames(Method method) {
		Class<?>[] paramTypes = method.getParameterTypes();
		String[] names = new String[paramTypes.length];

		boolean defaultNames = true;
		Method getParameters = Cls.findMethod(method.getClass(), "getParameters");

		if (getParameters != null) {
			Object[] parameters = Cls.invoke(getParameters, method);

			for (int i = 0; i < parameters.length; i++) {
				names[i] = Beany.getPropValue(parameters[i], "name");
				U.notNull(names[i], "parameter name");
				if (!names[i].equals("arg" + i)) {
					defaultNames = false;
				}
			}
		}

		if (defaultNames) {
			boolean useIndexMapping;
			CtMethod cm;

			try {
				ClassPool cp = new ClassPool();
				cp.insertClassPath(new ClassClassPath(method.getDeclaringClass()));
				CtClass cc = cp.get(method.getDeclaringClass().getName());

				useIndexMapping = cc.getClassFile().getMajorVersion() >= 52;

				CtClass[] params = new CtClass[paramTypes.length];
				for (int i = 0; i < params.length; i++) {
					params[i] = cp.get(method.getParameterTypes()[i].getName());
				}

				cm = cc.getDeclaredMethod(method.getName(), params);

			} catch (NotFoundException e) {
				throw U.rte("Cannot find the target method!", e);
			}

			MethodInfo methodInfo = cm.getMethodInfo();
			CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

			if (codeAttribute != null) {
				LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
					.getAttribute(LocalVariableAttribute.tag);

				int offset = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

				for (int i = 0; i < names.length; i++) {
					names[i] = null;
				}

				for (int i = 0; i < attr.tableLength(); i++) {
					int index = i - offset;

					if (useIndexMapping) {
						index = attr.index(index);
					}

					String var = attr.variableName(i);

					if (index >= 0 && index < names.length && !"this".equals(var)) {
						names[index] = var;
					}
				}

				if (!validNames(names)) {
					for (int i = 0; i < names.length; i++) {
						names[i] = null;
					}

					for (int i = 0; i < attr.tableLength(); i++) {
						int index = i - offset;
						String var = attr.variableName(i);

						if (index >= 0 && index < names.length && !"this".equals(var)) {
							names[index] = var;
						}
					}
				}
			}

			U.must(validNames(names), "Couldn't retrieve the parameter names! Please report this problem. " +
				"You can explicitly specify the names using @Param(\"thename\"), " +
				"or configure the option '-parameters' on the Java 8 compiler.");
		}

		return names;
	}

	private static boolean validNames(String[] names) {
		for (String name : names) {
			if (name == null) return false;
		}

		return true;
	}

	public static String[] getLambdaParameterNames(Serializable lambda) {
		Method lambdaMethod = getLambdaMethod(lambda);
		Class<?>[] lambdaTypes = lambdaMethod.getParameterTypes();
		String[] names = getMethodParameterNames(lambdaMethod);

		List<Method> methods = U.list();

		for (Class<?> interf : lambda.getClass().getInterfaces()) {
			for (Method m : interf.getMethods()) {
				Class<?>[] types = m.getParameterTypes();

				if (types.length <= names.length) {
					int diff = names.length - types.length;
					boolean matching = true;

					for (int i = 0; i < types.length; i++) {
						if (!types[i].isAssignableFrom(lambdaTypes[i + diff])) {
							matching = false;
						}
					}

					if (matching) {
						methods.add(m);
					}
				}
			}
		}

		U.must(methods.size() > 0, "Cannot find the lambda target method of the functional interface!");
		U.must(methods.size() == 1, "Found more than one lambda target method of the functional interface: " + methods);

		return Arr.sub(names, names.length - methods.get(0).getParameterTypes().length, names.length);
	}

	public static Class<?> toClass(Object classOrInstance) {
		return (classOrInstance instanceof Class<?>) ? ((Class<?>) classOrInstance) : classOrInstance.getClass();
	}

	public static boolean isAnnotated(Class<?> type, Class<? extends Annotation> annotation) {
		return type.getAnnotation(annotation) != null;
	}

	public static Object invokeStatic(String className, String methodName, Object... args) {
		Class<?> cls = Cls.get(className);
		Method method = findMethodByArgs(cls, methodName, args);
		return invokeStatic(method, args);
	}

}
