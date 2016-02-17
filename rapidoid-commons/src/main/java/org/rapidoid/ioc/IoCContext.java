package org.rapidoid.ioc;

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

import org.rapidoid.annotation.*;
import org.rapidoid.cls.Cls;
import org.rapidoid.cls.Proxies;
import org.rapidoid.commons.Coll;
import org.rapidoid.config.Conf;
import org.rapidoid.lambda.F3;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.u.U;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class IoCContext {

	private final Map<Class<?>, Object> singletons = U.map();

	private final Set<Class<?>> managedClasses = U.set();

	private final Set<Object> managedInstances = U.set();

	private final Map<Object, Object> iocInstances = U.map();

	private final Map<Class<?>, ClassMetadata> metadata = Coll
			.autoExpandingMap(new Mapper<Class<?>, ClassMetadata>() {
				@Override
				public ClassMetadata map(Class<?> clazz) throws Exception {
					return new ClassMetadata(clazz);
				}
			});

	public synchronized void reset() {
		Log.info("Resetting IoC context", "context", this);

		singletons.clear();
		managedClasses.clear();
		managedInstances.clear();
		iocInstances.clear();
		metadata.clear();
	}

	private ClassMetadata meta(Class<?> type) {
		return metadata.get(type);
	}

	public synchronized void manage(Object... classesOrInstances) {
		List<Class<?>> autocreate = new ArrayList<Class<?>>();

		for (Object classOrInstance : classesOrInstances) {

			boolean isClass = isClass(classOrInstance);
			Class<?> clazz = isClass ? (Class<?>) classOrInstance : classOrInstance.getClass();

			for (Class<?> interfacee : Cls.getImplementedInterfaces(clazz)) {
				addInjectionProvider(interfacee, classOrInstance);
			}

			if (isClass) {
				Log.debug("configuring managed class", "class", classOrInstance);
				managedClasses.add(clazz);

				if (!clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation()) {
					// if the class is annotated, auto-create an instance
					if (clazz.getAnnotation(Autocreate.class) != null) {
						autocreate.add(clazz);
					}
				}
			} else {
				Log.debug("configuring managed instance", "instance", classOrInstance);
				addInjectionProvider(clazz, classOrInstance);
				managedInstances.add(classOrInstance);
			}
		}

		for (Class<?> clazz : autocreate) {
			singleton(clazz);
		}
	}

	private void addInjectionProvider(Class<?> type, Object provider) {
		meta(type).injectors.add(provider);
	}

	public synchronized <T> T singleton(Class<T> type) {
		Log.debug("Singleton", "type", type);
		return provideIoCInstanceOf(null, type, null, null, false);
	}

	public synchronized <T> T autowire(T target) {
		Log.debug("Autowire", "target", target);
		autowire(target, null, null, null);
		return target;
	}

	public synchronized <T> T autowire(T target, Mapper<String, Object> session, Mapper<String, Object> bindings) {
		Log.debug("Autowire", "target", target);
		autowire(target, null, session, bindings);
		return target;
	}

	public synchronized <T> T inject(T target) {
		Log.debug("Inject", "target", target);
		return ioc(target, null);
	}

	public synchronized <T> T inject(T target, Map<String, Object> properties) {
		Log.debug("Inject", "target", target, "properties", properties);
		return ioc(target, properties);
	}

	private <T> T provideSessionValue(Object target, Class<T> type, String name, Mapper<String, Object> session) {
		U.notNull(session, "session");
		Object value = Lmbd.eval(session, name);
		return value != null ? Cls.convert(value, type) : null;
	}

	private <T> T provideBindValue(Object target, Class<T> type, String name, Mapper<String, Object> bindings) {
		U.notNull(bindings, "bindings");
		Object value = Lmbd.eval(bindings, name);
		return value != null ? Cls.convert(value, type) : null;
	}

	private <T> T provideIoCInstanceOf(Object target, Class<T> type, String name,
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

	private boolean canInjectNew(Class<?> type) {
		return !type.isAnnotation() && !type.isEnum() && !type.isInterface() && !type.isPrimitive()
				&& !type.equals(String.class) && !type.equals(Object.class) && !type.equals(Boolean.class)
				&& !Number.class.isAssignableFrom(type);
	}

	@SuppressWarnings("unchecked")
	private <T> T provideNewIoCInstanceOf(Class<T> type, Map<String, Object> properties) {
		// instantiation if it's real class
		if (!type.isInterface() && !type.isEnum() && !type.isAnnotation()) {
			T instance = (T) singletons.get(type);

			if (instance == null) {
				instance = ioc(Cls.newInstance(type, properties), properties);
			}

			return instance;
		} else {
			return null;
		}
	}

	private <T> T provideIoCInstanceByType(Class<T> type, Map<String, Object> properties) {
		Set<Object> providers = meta(type).injectors;

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
	private <T> T provideFrom(Object classOrInstance, Map<String, Object> properties) {
		T instance;
		if (isClass(classOrInstance)) {
			instance = provideNewIoCInstanceOf((Class<T>) classOrInstance, properties);
		} else {
			instance = (T) classOrInstance;
		}
		return instance;
	}

	private boolean isClass(Object obj) {
		return obj instanceof Class;
	}

	private <T> T provideInstanceByName(Object target, Class<T> type, String name, Map<String, Object> properties) {
		T instance = getInjectableByName(type, name, properties, false);

		if (target != null) {
			instance = getInjectableByName(type, name, properties, true);
		}

		if (instance == null) {
			instance = getInjectableByName(type, name, properties, true);
		}

		return (T) instance;
	}

	@SuppressWarnings("unchecked")
	private <T> T getInjectableByName(Class<T> type, String name, Map<String, Object> properties,
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

	private void autowire(Object target, Map<String, Object> properties, Mapper<String, Object> session,
	                      Mapper<String, Object> locals) {

		Log.debug("Autowiring", "target", target, "session", session, "bindings", locals);

		for (Field field : meta(target.getClass()).injectableFields) {

			boolean optional = isInjectOptional(field);
			Object value = provideIoCInstanceOf(target, field.getType(), field.getName(), properties, optional);

			Log.debug("Injecting field value", "target", target, "field", field.getName(), "value", value);

			if (!optional || value != null) {
				Cls.setFieldValue(target, field.getName(), value);
			}
		}

		for (Field field : meta(target.getClass()).sessionFields) {

			Object value = provideSessionValue(target, field.getType(), field.getName(), session);

			if (value != null) {
				Log.debug("Injecting session field value", "target", target, "field", field.getName(), "value", value);
				Cls.setFieldValue(target, field.getName(), value);
			}
		}

		for (Field field : meta(target.getClass()).localFields) {

			Object value = provideBindValue(target, field.getType(), field.getName(), locals);

			if (value != null) {
				Log.debug("Injecting bind field value", "target", target, "field", field.getName(), "value", value);
				Cls.setFieldValue(target, field.getName(), value);
			}
		}
	}

	private boolean isInjectOptional(Field field) {
		Inject inject = field.getAnnotation(Inject.class);
		return inject != null && inject.optional();
	}

	private <T> void invokePostConstruct(T target) {
		List<Method> methods = Cls.getMethodsAnnotated(target.getClass(), Init.class);

		for (Method method : methods) {
			Cls.invoke(method, target);
		}
	}

	private <T> T ioc(T target, Map<String, Object> properties) {
		if (!isIocProcessed(target)) {
			iocInstances.put(target, null);

			manage(target);

			autowire(target, properties, null, null);

			invokePostConstruct(target);

			T proxy = proxyWrap(target);

			iocInstances.put(target, proxy);

			manage(proxy);

			target = proxy;
		}

		return target;
	}

	private boolean isIocProcessed(Object target) {
		for (Map.Entry<Object, Object> e : iocInstances.entrySet()) {
			if (e.getKey() == target || e.getValue() == target) {
				return true;
			}
		}

		return false;
	}

	private <T> T proxyWrap(T instance) {
		Set<F3<Object, Object, Method, Object[]>> done = U.set();

		for (Class<?> interf : Cls.getImplementedInterfaces(instance.getClass())) {
			final List<F3<Object, Object, Method, Object[]>> interceptors = meta(interf).interceptors;

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

	public <K, V> Map<K, V> autoExpandingInjectingMap(final Class<V> clazz) {
		return Coll.autoExpandingMap(new Mapper<K, V>() {
			@Override
			public V map(K src) throws Exception {
				return inject(Cls.newInstance(clazz));
			}
		});
	}

}