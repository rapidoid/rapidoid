package org.rapidoid.beany;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.annotation.ToString;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.util.Cls;
import org.rapidoid.util.Exportable;
import org.rapidoid.util.TypeKind;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;
import org.rapidoid.var.Vars;

/*
 * #%L
 * rapidoid-beany
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

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class Beany {

	private static final String GETTER = "^(get|is)[A-Z].*";

	private static final String SETTER = "^set[A-Z].*";

	protected static final Map<Class<?>, BeanProperties> BEAN_PROPERTIES = UTILS
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

				String name = method.getName();

				if (!name.startsWith("_") && !Modifier.isPrivate(modif) && !Modifier.isProtected(modif)
						&& !Modifier.isStatic(modif)) {

					if ((name.matches(GETTER) && params.length == 0) || (name.matches(SETTER) && params.length == 1)) {

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
							prop.setSetter(method);
							prop.setReadOnly(false);
						} else {
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
				if (!Modifier.isPrivate(modif) && !Modifier.isProtected(modif) && !Modifier.isStatic(modif)) {

					String fieldName = field.getName();

					if (!fieldName.startsWith("_")) {
						BeanProp prop = properties.get(fieldName);

						if (prop == null) {
							prop = new BeanProp(fieldName, field, (modif & Modifier.FINAL) != 0);
							properties.put(fieldName, prop);
						}
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

	public static Object serialize(Object value) {

		if (Cls.kindOf(value) != TypeKind.OBJECT || value instanceof Enum) {
			return value;

		} else if (value instanceof Set) {
			Set<Object> set = U.set();
			for (Object item : ((Set<?>) value)) {
				set.add(serialize(item));
			}
			return set;

		} else if (value instanceof List) {
			List<Object> list = U.list();
			for (Object item : ((List<?>) value)) {
				list.add(serialize(item));
			}
			return list;

		} else if (value instanceof Object[]) {
			Object[] vals = (Object[]) value;
			Object[] arr = new Object[vals.length];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = serialize(vals[i]);
			}
			return arr;

		} else if (value instanceof Map) {
			Map<Object, Object> map = U.map();
			for (Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
				map.put(serialize(e.getKey()), serialize(e.getValue()));
			}
			return map;

		} else if (value.getClass().isArray()) {
			// a primitive array
			return value;
		} else {
			return read(value);
		}
	}

	public static Map<String, Object> read(Object bean) {
		Map<String, Object> props = U.map();
		read(bean, props);
		return props;
	}

	public static void read(Object bean, Map<String, Object> dest) {
		U.notNull(bean, "bean");
		U.must(!(bean instanceof Collection));
		U.must(!bean.getClass().isArray());
		U.must(!bean.getClass().isEnum());
		U.must(Cls.kindOf(bean) == TypeKind.OBJECT);

		if (bean instanceof Exportable) {
			Exportable exportable = (Exportable) bean;
			exportable.exportTo(UTILS.importExport(dest));
			return;
		}

		for (Prop prop : propertiesOf(bean)) {
			Object value = prop.getRaw(bean);

			if (value instanceof SerializableBean<?>) {
				SerializableBean<?> ser = (SerializableBean<?>) value;
				value = ser.serializeBean();
			} else {
				value = serialize(unwrap(value));
			}

			dest.put(prop.getName(), value);
		}
	}

	@SuppressWarnings("unchecked")
	public static void update(Object destBean, Map<String, Object> src, boolean ignoreNullValues) {
		if (destBean instanceof Map) {
			((Map<String, Object>) destBean).putAll(src);
			return;
		}

		for (Prop prop : propertiesOf(destBean)) {
			Object value = src.get(prop.getName());

			if (value != null || !ignoreNullValues) {
				Object propValue = prop.getRaw(destBean);
				if (propValue != null && propValue instanceof SerializableBean) {
					SerializableBean<Object> ser = (SerializableBean<Object>) propValue;
					ser.deserializeBean(value);
				} else {
					prop.set(destBean, value);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static Prop property(Object obj, String property, boolean mandatory) {
		if (obj instanceof Map) {
			Object value = ((Map) obj).get(property);
			return new MapProp(property, property, Cls.of(value));
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
			return prop != null ? (T) prop.get(instance) : defaultValue;
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

	public static <T> T getPropValueOfType(Object obj, String property, Class<T> returnType) {
		T value = getPropValueOfType(obj, property, returnType, null);

		if (value == null) {
			throw U.rte("The property '%s' cannot be null!", property);
		}

		return value;
	}

	public static <T> T getPropValueOfType(Object obj, String property, Class<T> returnType, T defaultValue) {
		Object id = getPropValue(obj, property, null);
		return id != null ? Cls.convert(id, returnType) : defaultValue;
	}

	public static void setId(Object obj, long id) {
		setPropValue(obj, "id", id);
	}

	public static long getId(Object obj) {
		return getPropValueOfType(obj, "id", Long.class);
	}

	public static Long getIdIfExists(Object obj) {
		return getPropValueOfType(obj, "id", Long.class, null);
	}

	public static long[] getIds(Object... objs) {
		long[] ids = new long[objs.length];
		for (int i = 0; i < objs.length; i++) {
			ids[i] = getId(objs[i]);
		}
		return ids;
	}

	public static String beanToStr(Object bean, boolean allowCustom) {

		Class<?> clazz = Cls.unproxy(bean.getClass());

		if (allowCustom) {
			Method m = Cls.getMethod(clazz, "toString");
			if (!m.getDeclaringClass().equals(Object.class)) {
				return bean.toString();
			}
		}

		BeanProperties props = propertiesOf(bean).annotated(ToString.class);
		boolean annotated = true;

		if (props.isEmpty()) {

			Prop nameProp = property(bean, "name", false);
			if (nameProp != null && nameProp.getType() == String.class) {
				return U.or((String) nameProp.get(bean), "");
			}

			annotated = false;
			props = propertiesOf(bean);
		}

		StringBuilder sb = new StringBuilder();

		for (Prop prop : props) {
			String name = prop.getName();

			if (annotated
					|| (!UTILS.isSpecialProperty(name) && prop.getTypeKind() != TypeKind.OBJECT && prop.getTypeKind() != TypeKind.DATE)) {

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

	public static <E> Comparator<E> comparator(final String orderBy) {
		final int sign = orderBy.startsWith("-") ? -1 : 1;
		final String order = sign == 1 ? orderBy : orderBy.substring(1);

		Comparator<E> comparator = new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {

				try {
					E val1 = getPropValue(o1, order);
					E val2 = getPropValue(o2, order);

					U.must(val1 == null || val1 instanceof Comparable, "The property '%s' (%s) is not comparable!",
							order, Cls.of((val1)));
					U.must(val2 == null || val2 instanceof Comparable, "The property '%s' (%s) is not comparable!",
							order, Cls.of((val2)));

					return sign * U.compare(val1, val2);
				} catch (Exception e) {
					Log.error("Cannot compare values by: " + orderBy, e);
					return 0;
				}
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

	public static Object unwrap(Object value) {
		return Vars.unwrap(value);
	}

}
