package org.rapidoid.wire;

/*
 * #%L
 * rapidoid-wire
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Autocreate;
import org.rapidoid.annotation.Init;
import org.rapidoid.annotation.Inject;
import org.rapidoid.annotation.Local;
import org.rapidoid.annotation.Session;
import org.rapidoid.annotation.Since;
import org.rapidoid.beany.Beany;
import org.rapidoid.cls.Cls;
import org.rapidoid.config.Conf;
import org.rapidoid.lambda.F3;
import org.rapidoid.lambda.Lambdas;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.util.Builder;
import org.rapidoid.util.Proxies;
import org.rapidoid.util.U;
import org.rapidoid.util.UTILS;

@Authors("Nikolche Mihajlovski")
@Since("3.1.0")
public class Wire {

	private static final Map<Class<?>, Object> SINGLETONS = U.map();
	private static final Set<Class<?>> MANAGED_CLASSES = U.set();
	private static final Set<Object> MANAGED_INSTANCES = U.set();
	private static final Map<Object, Object> IOC_INSTANCES = U.map();

	private static final Map<Class<?>, List<Field>> INJECTABLE_FIELDS = UTILS
			.autoExpandingMap(new Mapper<Class<?>, List<Field>>() {
				@Override
				public List<Field> map(Class<?> clazz) throws Exception {
					List<Field> fields = Cls.getFieldsAnnotated(clazz, Inject.class);
					Log.debug("Retrieved @Inject fields", "class", clazz, "fields", fields);
					return fields;
				}
			});

	private static final Map<Class<?>, List<Field>> SESSION_FIELDS = UTILS
			.autoExpandingMap(new Mapper<Class<?>, List<Field>>() {
				@Override
				public List<Field> map(Class<?> clazz) throws Exception {
					List<Field> fields = Cls.getFieldsAnnotated(clazz, Session.class);
					Log.debug("Retrieved @Session fields", "class", clazz, "fields", fields);
					return fields;
				}
			});

	private static final Map<Class<?>, List<Field>> LOCAL_FIELDS = UTILS
			.autoExpandingMap(new Mapper<Class<?>, List<Field>>() {
				@Override
				public List<Field> map(Class<?> clazz) throws Exception {
					List<Field> fields = Cls.getFieldsAnnotated(clazz, Local.class);
					Log.debug("Retrieved @Local fields", "class", clazz, "fields", fields);
					return fields;
				}
			});

	private static final Map<Class<?>, Set<Object>> INJECTION_PROVIDERS = U.map();
	private static final Map<Class<?>, List<F3<Object, Object, Method, Object[]>>> INTERCEPTORS = U.map();

	public static synchronized void reset() {
		Log.info("Reseting IoC state");

		Log.setLogLevel(Log.INFO);
		Conf.args();

		Beany.reset();

		SINGLETONS.clear();
		MANAGED_CLASSES.clear();
		MANAGED_INSTANCES.clear();
		IOC_INSTANCES.clear();
		INJECTABLE_FIELDS.clear();
		SESSION_FIELDS.clear();
		LOCAL_FIELDS.clear();
		INJECTION_PROVIDERS.clear();
		INTERCEPTORS.clear();
	}

	public static <K, V> Map<K, V> autoExpandingInjectingMap(final Class<V> clazz) {
		return UTILS.autoExpandingMap(new Mapper<K, V>() {
			@Override
			public V map(K src) throws Exception {
				return inject(Cls.newInstance(clazz));
			}
		});
	}

	public static synchronized void manage(Object... classesOrInstances) {
		List<Class<?>> autocreate = new ArrayList<Class<?>>();

		for (Object classOrInstance : classesOrInstances) {

			boolean isClass = isClass(classOrInstance);
			Class<?> clazz = isClass ? (Class<?>) classOrInstance : classOrInstance.getClass();

			for (Class<?> interfacee : Cls.getImplementedInterfaces(clazz)) {
				addInjectionProvider(interfacee, classOrInstance);
			}

			if (isClass) {
				Log.debug("configuring managed class", "class", classOrInstance);
				MANAGED_CLASSES.add(clazz);

				if (!clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation()) {
					// if the class is annotated, auto-create an instance
					if (clazz.getAnnotation(Autocreate.class) != null) {
						autocreate.add(clazz);
					}
				}
			} else {
				Log.debug("configuring managed instance", "instance", classOrInstance);
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
		Log.debug("Inject", "type", type);
		return provideIoCInstanceOf(null, type, null, null, false);
	}

	public static synchronized <T> T autowire(T target) {
		Log.debug("Autowire", "target", target);
		autowire(target, null, null, null);
		return target;
	}

	public static synchronized <T> T autowire(T target, Mapper<String, Object> session, Mapper<String, Object> bindings) {
		Log.debug("Autowire", "target", target);
		autowire(target, null, session, bindings);
		return target;
	}

	public static synchronized <T> T inject(T target) {
		Log.debug("Inject", "target", target);
		return ioc(target, null);
	}

	public static synchronized <T> T inject(T target, Map<String, Object> properties) {
		Log.debug("Inject", "target", target, "properties", properties);
		return ioc(target, properties);
	}

	private static <T> T provideSessionValue(Object target, Class<T> type, String name, Mapper<String, Object> session) {
		U.notNull(session, "session");
		Object value = Lambdas.eval(session, name);
		return value != null ? Cls.convert(value, type) : null;
	}

	private static <T> T provideBindValue(Object target, Class<T> type, String name, Mapper<String, Object> bindings) {
		U.notNull(bindings, "bindings");
		Object value = Lambdas.eval(bindings, name);
		return value != null ? Cls.convert(value, type) : null;
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
				instance = ioc(Cls.newInstance(type, properties), properties);
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
				instance = Conf.is(name);
			} else {
				String opt = Conf.option(name, (String) null);
				if (opt != null) {
					instance = Cls.convert(opt, type);
				}
			}
		}

		return (T) instance;
	}

	private static void autowire(Object target, Map<String, Object> properties, Mapper<String, Object> session,
			Mapper<String, Object> locals) {

		Log.debug("Autowiring", "target", target, "session", session, "bindings", locals);

		for (Field field : INJECTABLE_FIELDS.get(target.getClass())) {

			boolean optional = isInjectOptional(field);
			Object value = provideIoCInstanceOf(target, field.getType(), field.getName(), properties, optional);

			Log.debug("Injecting field value", "target", target, "field", field.getName(), "value", value);

			if (!optional || value != null) {
				Cls.setFieldValue(target, field.getName(), value);
			}
		}

		for (Field field : SESSION_FIELDS.get(target.getClass())) {

			Object value = provideSessionValue(target, field.getType(), field.getName(), session);

			if (value != null) {
				Log.debug("Injecting session field value", "target", target, "field", field.getName(), "value", value);
				Cls.setFieldValue(target, field.getName(), value);
			}
		}

		for (Field field : LOCAL_FIELDS.get(target.getClass())) {

			Object value = provideBindValue(target, field.getType(), field.getName(), locals);

			if (value != null) {
				Log.debug("Injecting bind field value", "target", target, "field", field.getName(), "value", value);
				Cls.setFieldValue(target, field.getName(), value);
			}
		}
	}

	private static boolean isInjectOptional(Field field) {
		Inject inject = field.getAnnotation(Inject.class);
		return inject != null && inject.optional();
	}

	private static <T> void invokePostConstruct(T target) {
		List<Method> methods = Cls.getMethodsAnnotated(target.getClass(), Init.class);

		for (Method method : methods) {
			Cls.invoke(null, method, target);
		}
	}

	private static <T> T ioc(T target, Map<String, Object> properties) {
		if (!isIocProcessed(target)) {
			IOC_INSTANCES.put(target, null);

			manage(target);

			autowire(target, properties, null, null);

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

		for (Class<?> interf : Cls.getImplementedInterfaces(instance.getClass())) {
			final List<F3<Object, Object, Method, Object[]>> interceptors = INTERCEPTORS.get(interf);

			if (interceptors != null) {
				for (final F3<Object, Object, Method, Object[]> interceptor : interceptors) {
					if (interceptor != null && !done.contains(interceptor)) {
						Log.debug("Creating proxy", "target", instance, "interface", interf, "interceptor", interceptor);

						final T target = instance;
						InvocationHandler handler = new InvocationHandler() {
							@Override
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								return interceptor.execute(target, method, args);
							}
						};

						instance = Proxies.implement(instance, handler, interf);

						done.add(interceptor);
					}
				}
			}
		}

		return instance;
	}

	public static <T, B extends Builder<T>> B builder(final Class<B> builderClass, final Class<T> builtClass,
			final Class<? extends T> implClass) {

		final Map<String, Object> properties = U.map();

		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.getDeclaringClass().equals(Builder.class)) {
					return inject(Cls.newInstance(implClass, properties), properties);
				} else {
					U.must(args.length == 1, "expected 1 argument!");
					properties.put(method.getName(), args[0]);
					return proxy;
				}
			}
		};

		B builder = Proxies.implement(handler, builderClass);
		return builder;
	}

	public static synchronized List<Field> getSessionFields(Object target) {
		return SESSION_FIELDS.get(target.getClass());
	}

	public static synchronized List<Field> getLocalFields(Object target) {
		return LOCAL_FIELDS.get(target.getClass());
	}

}
