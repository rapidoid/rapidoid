package org.rapidoid.util;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.rapidoid.lambda.Mapper;
import org.rapidoid.prop.BeanProp;
import org.rapidoid.prop.BeanProperties;
import org.rapidoid.prop.MapProp;
import org.rapidoid.prop.Prop;
import org.rapidoid.prop.SerializableBean;

public class Cls {

	static final Map<String, TypeKind> KINDS = initKinds();

	protected static final Map<Class<?>, BeanProperties> BEAN_PROPERTIES = U
			.autoExpandingMap(new Mapper<Class<?>, BeanProperties>() {

				@Override
				public BeanProperties map(Class<?> clazz) throws Exception {
					Map<String, BeanProp> properties = new LinkedHashMap<String, BeanProp>();

					getBeanProperties(clazz, properties);

					for (Entry<String, BeanProp> e : properties.entrySet()) {
						e.getValue().init();
					}

					return new BeanProperties(properties);
				}
			});

	private static void getBeanProperties(Class<?> clazz, Map<String, BeanProp> properties) {

		if (clazz == null || clazz == Object.class) {
			return;
		} else {
			getBeanProperties(clazz.getSuperclass(), properties);
			for (Class<?> interf : clazz.getInterfaces()) {
				getBeanProperties(interf, properties);
			}
		}

		if (Proxy.isProxyClass(clazz)) {
			return;
		}

		try {
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				int modif = method.getModifiers();
				Class<?>[] params = method.getParameterTypes();
				Class<?> ret = method.getReturnType();

				if ((modif & Modifier.PUBLIC) != 0 && (modif & Modifier.STATIC) == 0) {

					String name = method.getName();
					if (name.matches("^(get|set|is)[A-Z].*")) {

						String propName;
						if (name.startsWith("is")) {
							propName = name.substring(2, 3).toLowerCase() + name.substring(3);
						} else {
							propName = name.substring(3, 4).toLowerCase() + name.substring(4);
						}

						BeanProp prop = properties.get(propName);

						if (prop == null) {
							prop = new BeanProp(propName);
							properties.put(propName, prop);
						}

						if (name.startsWith("set")) {
							if (params.length == 1) {
								prop.setSetter(method);
								prop.setReadOnly(false);
							}
						} else if (params.length == 0) {
							prop.setGetter(method);
						}
					} else if (!name.matches("^to[A-Z].*") && !name.equals("hashCode")) {

						if (params.length == 0 && !ret.equals(void.class)) {

							String propName = name;
							BeanProp prop = properties.get(propName);

							if (prop == null) {
								prop = new BeanProp(propName);
								properties.put(propName, prop);
							}

							prop.setGetter(method);

						} else if (params.length == 1) {

							String propName = name;
							BeanProp prop = properties.get(propName);

							if (prop == null) {
								prop = new BeanProp(propName);
								properties.put(propName, prop);
							}

							prop.setReadOnly(false);
							prop.setSetter(method);
						}
					}
				}
			}

			// remove properties with setters, without getters
			for (Iterator<Entry<String, BeanProp>> it = properties.entrySet().iterator(); it.hasNext();) {
				Entry<String, BeanProp> entry = (Entry<String, BeanProp>) it.next();
				BeanProp minfo = entry.getValue();
				if (minfo.getGetter() == null && minfo.getSetter() != null) {
					it.remove();
				}
			}

			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {

				int modif = field.getModifiers();
				if ((modif & Modifier.PUBLIC) != 0 && (modif & Modifier.STATIC) == 0) {
					String fieldName = field.getName();
					BeanProp prop = properties.get(fieldName);

					if (prop == null) {
						prop = new BeanProp(fieldName, field, (modif & Modifier.FINAL) != 0);
						properties.put(fieldName, prop);
					}
				}
			}

		} catch (Exception e) {
			throw U.rte(e);
		}
	}

	public static void reset() {
		BEAN_PROPERTIES.clear();
	}

	public static BeanProperties propertiesOf(Class<?> clazz) {
		return BEAN_PROPERTIES.get(clazz);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static BeanProperties propertiesOf(Object obj) {
		if (obj == null) {
			return BeanProperties.NONE;
		} else if (obj instanceof Map) {
			return BeanProperties.from(((Map) obj));
		} else if (obj instanceof Class) {
			return propertiesOf((Class) obj);
		} else {
			return propertiesOf(obj.getClass());
		}
	}

	public static Prop property(Class<?> clazz, String property, boolean mandatory) {
		Prop prop = BEAN_PROPERTIES.get(clazz).get(property);

		if (mandatory && prop == null) {
			throw U.rte("Cannot find the property '%s' in the class '%s'", property, clazz);
		}

		return prop;
	}

	public static Map<String, Object> read(Object bean) {
		Map<String, Object> props = U.map();
		read(bean, props);
		return props;
	}

	public static void read(Object bean, Map<String, Object> dest) {
		for (Prop prop : propertiesOf(bean)) {
			Object value = prop.get(bean);

			if (value instanceof SerializableBean<?>) {
				SerializableBean<?> ser = (SerializableBean<?>) value;
				value = ser.serializeBean();
			}

			dest.put(prop.getName(), value);
		}
	}

	@SuppressWarnings("unchecked")
	public static void update(Map<String, Object> src, Object destBean) {
		if (destBean instanceof Map) {
			((Map<String, Object>) destBean).putAll(src);
			return;
		}

		for (Prop prop : propertiesOf(destBean)) {
			Object value = src.get(prop.getName());

			Object propValue = prop.get(destBean);
			if (propValue != null && propValue instanceof SerializableBean) {
				SerializableBean<Object> ser = (SerializableBean<Object>) propValue;
				ser.deserializeBean(value);
			} else {
				prop.set(destBean, value);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static Prop property(Object obj, String property, boolean mandatory) {
		if (obj instanceof Map) {
			return new MapProp(property, property, of(((Map) obj).get(property)));
		} else {
			return property(obj.getClass(), property, mandatory);
		}
	}

	public static boolean hasProperty(Class<?> clazz, String property) {
		return property(clazz, property, false) != null;
	}

	public static boolean hasProperty(Object obj, String property) {
		return property(obj, property, false) != null;
	}

	protected static Map<String, TypeKind> initKinds() {

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
					Arrays.toString(paramTypes));
		}
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
			if (U.instanceOf(obj, type)) {
				if (instance == null) {
					instance = (T) obj;
				} else {
					throw U.rte("Found more than one instance of %s: %s and %s", type, instance, obj);
				}
			}
		}

		return instance;
	}

	public static void setId(Object obj, long id) {
		setPropValue(obj, "id", id);
	}

	public static long getId(Object obj) {
		Long id = getIdIfExists(obj);

		if (id == null) {
			throw U.rte("The property 'id' cannot be null!");
		}

		return id;
	}

	public static Long getIdIfExists(Object obj) {
		Object id = getPropValue(obj, "id", null);
		if (id == null) {
			return null;
		} else if (id instanceof Number) {
			return ((Number) id).longValue();
		} else {
			throw U.rte("The property 'id' must have numeric type, but it has type: " + id.getClass());
		}
	}

	public static long[] getIds(Object... objs) {
		long[] ids = new long[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ids[i] = getId(objs[i]);
		}
		return ids;
	}

	@SuppressWarnings("unchecked")
	public static void setPropValue(Object instance, String propertyName, Object value) {
		if (instance instanceof Map) {
			((Map<Object, Object>) instance).put(propertyName, value);
		} else {
			property(instance, propertyName, true).set(instance, value);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getPropValue(Object instance, String propertyName, T defaultValue) {
		if (instance instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) instance;
			return (T) (map.containsKey(propertyName) ? map.get(propertyName) : defaultValue);
		} else {
			Prop prop = property(instance, propertyName, false);
			return prop != null ? prop.get(instance, defaultValue) : defaultValue;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getPropValue(Object instance, String propertyName) {
		if (instance instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) instance;
			U.must(map.containsKey(propertyName), "The map must contain key: %s", propertyName);
			return (T) map.get(propertyName);
		} else {
			return property(instance, propertyName, true).get(instance);
		}
	}

	public static Object[] instantiateAll(Class<?>... classes) {
		Object[] instances = new Object[classes.length];

		for (int i = 0; i < instances.length; i++) {
			instances[i] = U.newInstance(classes[i]);
		}

		return instances;
	}

	public static Object[] instantiateAll(Collection<Class<?>> classes) {
		if (classes.isEmpty()) {
			return Constants.EMPTY_ARRAY;
		}
		Object[] instances = new Object[classes.size()];

		int i = 0;
		for (Class<?> clazz : classes) {
			instances[i++] = U.newInstance(clazz);
		}

		return instances;
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(InvocationHandler handler, Class<?>... interfaces) {
		return ((T) Proxy.newProxyInstance(U.classLoader(), interfaces, handler));
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
			throw U.notExpected();

		case BOOLEAN:
		case BOOLEAN_OBJ:
			if (value instanceof Boolean) {
				return (T) value;
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
			} else {
				return (T) U.text(value);
			}

		case OBJECT:
			throw U.rte("Cannot convert the value to type '%s'!", toType);

		case DATE:
			if (value instanceof Date) {
				return (T) value;
			} else if (value instanceof Number) {
				return (T) new Date(((Number) value).longValue());
			} else {
				throw U.rte("Cannot convert the value '%s' to date!", value);
			}

		default:
			throw U.notExpected();
		}
	}

	public static Map<String, Class<?>> classMap(List<Class<?>> classes) {
		Map<String, Class<?>> map = U.map();

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

			if (method.getName().equals(name) && U.areAssignable(paramTypes, args)) {
				return method;
			}
		}

		return null;
	}

	public static String beanToStr(Object bean, boolean allowCustom) {

		Class<? extends Object> clazz = bean.getClass();

		if (allowCustom) {
			Method m = getMethod(clazz, "toString");
			if (!m.getDeclaringClass().equals(Object.class)) {
				return bean.toString();
			}
		}

		BeanProperties props = propertiesOf(bean);
		StringBuilder sb = new StringBuilder();

		for (Prop prop : props) {
			String name = prop.getName();

			if (!name.equalsIgnoreCase("id") && !name.equalsIgnoreCase("version")
					&& prop.getTypeKind() != TypeKind.OBJECT && prop.getTypeKind() != TypeKind.DATE) {

				Object value = prop.get(bean);
				if (value != null) {

					if (sb.length() > 0) {
						sb.append(", ");
					}

					if (value instanceof Number) {
						value = name + ": " + value;
					}

					sb.append(value);
				}
			}
		}

		return sb.toString();
	}

	public static Class<?> clazz(Type type) {
		return type instanceof Class<?> ? (Class<?>) type : Object.class;
	}

	public static Class<?> of(Object obj) {
		return obj != null ? obj.getClass() : Object.class;
	}

	public static <E> Comparator<E> comparator(String orderBy) {
		final int sign = orderBy.startsWith("-") ? -1 : 1;
		final String order = sign == 1 ? orderBy : orderBy.substring(1);

		Comparator<E> comparator = new Comparator<E>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(E o1, E o2) {

				E val1 = getPropValue(o1, order);
				E val2 = getPropValue(o2, order);

				U.must(val1 instanceof Comparable && val2 instanceof Comparable,
						"The property '%s' is not comparable!", order);

				return sign * ((Comparable<E>) val1).compareTo(val2);
			}
		};
		return comparator;
	}

	@SuppressWarnings("unchecked")
	public static <FROM, TO> List<TO> projection(Collection<FROM> coll, String propertyName) {
		List<TO> projection = U.list();

		for (FROM item : coll) {
			projection.add((TO) getPropValue(item, propertyName));
		}

		return projection;
	}

	public static Object str(Object value) {
		return convert(value, String.class);
	}

}
