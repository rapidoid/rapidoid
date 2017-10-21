package org.rapidoid.ioc.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.collection.Coll;
import org.rapidoid.commons.AnyObj;
import org.rapidoid.commons.Deep;
import org.rapidoid.config.Conf;
import org.rapidoid.config.Config;
import org.rapidoid.ioc.*;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Mapper;
import org.rapidoid.log.Log;
import org.rapidoid.scan.Scan;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;
import org.rapidoid.util.MscOpts;
import org.rapidoid.util.Once;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/*
 * #%L
 * rapidoid-inject
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class IoCContextImpl extends RapidoidThing implements IoCContext {

	public IoCContextImpl() {
	}

	private volatile String name;

	private volatile IoCState state = new IoCState();

	private volatile BeanProvider beanProvider;

	private volatile IoCContextWrapper wrapper;

	private final Map<Class<?>, ClassMetadata> metadata = Coll
		.autoExpandingMap(new Mapper<Class<?>, ClassMetadata>() {
			@Override
			public ClassMetadata map(Class<?> clazz) throws Exception {
				return new ClassMetadata(clazz);
			}
		});

	private final Once activate = new Once();

	@Override
	public IoCContext name(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public synchronized void reset() {
		if (!state.isEmpty()) {
			Log.info("Resetting IoC context", "context", this);
		} else {
			Log.debug("Resetting IoC context", "context", this);
		}

		state.reset();
		metadata.clear();
		beanProvider = null;
		activate.reset();
	}

	private ClassMetadata meta(Class<?> type) {
		return metadata.get(type);
	}

	@Override
	public synchronized void manage(Object... classesOrInstances) {

		classesOrInstances = AnyObj.flat(classesOrInstances);

		List<Class<?>> autoCreate = U.list();

		for (Object classOrInstance : classesOrInstances) {

			boolean isClass = isClass(classOrInstance);
			Class<?> clazz = Cls.toClass(classOrInstance);

			if (Msc.matchingProfile(clazz)) {

				for (Class<?> interfacee : Cls.getImplementedInterfaces(clazz)) {
					addProvider(interfacee, classOrInstance);
				}

				if (isClass) {
					Log.debug("configuring managed class", "class", classOrInstance);
					state.providedClasses.add(clazz);

					if (!clazz.isInterface() && !clazz.isEnum() && !clazz.isAnnotation()) {
						// if the class is annotated, auto-create an instance
						if (clazz.getAnnotation(Autocreate.class) != null) {
							autoCreate.add(clazz);
						}
					}
				} else {
					Object instance = classOrInstance;
					Log.debug("configuring provided instance", "instance", instance);
					addProvider(clazz, instance);
					state.providedInstances.add(instance);
					state.instances.add(instance);
				}
			}
		}

		for (Class<?> clazz : autoCreate) {
			singleton(clazz);
		}
	}

	private void addProvider(Class<?> type, Object provider) {
		state.providersByType.get(type).add(provider);
	}

	@Override
	public synchronized <T> T singleton(Class<T> type) {
		Log.debug("Singleton", "type", type);
		return provideIoCInstanceOf(null, type, null, null, false);
	}

	@Override
	public synchronized boolean autowire(Object target) {
		Log.debug("Autowire", "target", target);
		return autowire(target, null, null, null);
	}

	@Override
	public synchronized <T> T autowire(T target, Mapper<String, Object> session, Mapper<String, Object> bindings) {
		Log.debug("Autowire", "target", target);
		autowire(target, null, session, bindings);
		return target;
	}

	@Override
	public synchronized <T> T inject(T target) {
		Log.debug("Inject", "target", target);
		return register(target, null);
	}

	@Override
	public synchronized <T> T inject(T target, Map<String, Object> properties) {
		Log.debug("Inject", "target", target, "properties", properties);
		return register(target, properties);
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

	@SuppressWarnings("unchecked")
	private <T> T provideIoCInstanceOf(Object target, Class<T> type, String name,
	                                   Map<String, Object> properties, boolean optional) {

		if (target != null) {
			processMetadata(meta(target.getClass()));
		}

		if (type != null) {
			processMetadata(meta(type));
		}

		T instance = (T) provideSpecialInstance(type, name);

		if (instance == null && name != null) {
			instance = provideInstanceByName(target, type, name, properties);
		}

		if (instance == null) {
			instance = provideInstanceByType(type, properties);
		}

		BeanProvider provider = this.beanProvider;
		if (instance == null && provider != null) {
			instance = provider.getBean(type, name);
		}

		if (instance == null && Cls.isAppBeanType(type)) {
			instance = provideNewInstanceOf(type, properties);
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

		return Cls.isAppBean(instance) ? register(instance, properties) : null;
	}

	private boolean processMetadata(ClassMetadata meta) {
		boolean processed = false;

		if (U.notEmpty(meta.typesToManage)) {
			manage(meta.typesToManage);
			processed = true;
		}

		if (U.notEmpty(meta.packagesToScan)) {
			List<Class<?>> classes = Scan.annotated(IoC.ANNOTATIONS).in(meta.packagesToScan).loadAll();

			if (U.notEmpty(classes)) {
				manage(classes);
				processed = true;
			}
		}

		return processed;
	}

	@SuppressWarnings("unchecked")
	private <T> T provideNewInstanceOf(Class<T> type, Map<String, Object> properties) {
		// instantiation if it's real class
		if (!type.isInterface() && !type.isEnum() && !type.isAnnotation()) {
			if (Msc.matchingProfile(type)) {
				T instance = instantiate(type, properties);
				return register(instance, properties);
			}
		}

		return null;
	}

	private <T> T instantiate(Class<T> type, Map<String, Object> properties) {

		ClassMetadata meta = meta(type);

		if (U.notEmpty(meta.injectableConstructors)) {
			U.must(meta.injectableConstructors.size() == 1, "Found more than 1 @Inject-annotated constructor for: %s", type);

			Constructor<?> constr = U.single(meta.injectableConstructors);
			Class<?>[] paramTypes = constr.getParameterTypes();
			Object[] args = new Object[paramTypes.length];

			for (int i = 0; i < args.length; i++) {
				Class<?> paramType = paramTypes[i];
				String paramName = null; // FIXME inject by name
				args[i] = provideIoCInstanceOf(null, paramType, paramName, properties, false);
			}

			return Cls.invoke(constr, args);

		} else if (meta.defaultConstructor != null) {
			return Cls.invoke(meta.defaultConstructor);

		} else {
			throw U.illegal("Cannot find a default nor @Inject-annotated constructor for: %s", type);
		}
	}

	private <T> T provideInstanceByType(Class<T> type, Map<String, Object> properties) {

		Set<Object> providers = state.providersByType.get(type);
		Object provider = null;

		for (Object candidate : providers) {
			if (provider == null) {
				provider = candidate;
			} else {
				if (isClass(provider) && !isClass(candidate)) {
					provider = candidate;
				} else if (isClass(provider) || !isClass(candidate)) {
					throw U.rte("Found more than 1 candidates for type '%s': %s", type, providers);
				}
			}
		}

		if (provider != null) {
			return provideFrom(provider, properties);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> T provideFrom(Object provider, Map<String, Object> properties) {
		T instance;
		if (isClass(provider)) {
			instance = provideNewInstanceOf((Class<T>) provider, properties);
		} else {
			instance = (T) provider;
		}
		return instance;
	}

	private boolean isClass(Object obj) {
		return obj instanceof Class;
	}

	private Object provideSpecialInstance(Class<?> type, String name) {

		String cls = type.getName();

		if (type.equals(IoCContext.class)) {
			return U.or(wrapper, this);
		}

		if (cls.equals("javax.persistence.EntityManager") && MscOpts.hasRapidoidJPA()) {
			return OptionalJPAUtil.getSharedContextAwareEntityManagerProxy();
		}

		if (cls.equals("javax.persistence.EntityManagerFactory") && MscOpts.hasRapidoidJPA()) {
			return OptionalJPAUtil.getSharedEntityManagerFactoryProxy();
		}

		return null;
	}

	private <T> T provideInstanceByName(Object target, Class<T> type, String name, Map<String, Object> properties) {
		T instance = getInjectableByName(target, type, name, properties, false);

		if (target != null) {
			instance = getInjectableByName(target, type, name, properties, true);
		}

		if (instance == null) {
			instance = getInjectableByName(target, type, name, properties, true);
		}

		return (T) instance;
	}

	@SuppressWarnings("unchecked")
	private <T> T getInjectableByName(Object target, Class<T> type, String name,
	                                  Map<String, Object> properties, boolean useConfig) {

		Object instance = properties != null ? properties.get(name) : null;

		if (instance == null && target != null && useConfig) {
			Config config = Conf.section(target.getClass());

			if (type.equals(Boolean.class) || type.equals(boolean.class)) {
				instance = config.is(name);
			} else {
				String opt = config.entry(name).str().getOrNull();
				if (opt != null) {
					instance = Cls.convert(opt, type);
				}
			}
		}

		return (T) instance;
	}

	private boolean autowire(Object target, Map<String, Object> properties, Mapper<String, Object> session,
	                         Mapper<String, Object> locals) {

		Log.debug("Autowiring", "target", target, "session", session, "bindings", locals);

		ClassMetadata meta = meta(target.getClass());

		boolean processed = processMetadata(meta);

		for (Field field : meta.injectableFields) {

			boolean optional = isInjectOptional(field);
			Object value = provideIoCInstanceOf(target, field.getType(), field.getName(), properties, optional);

			Log.debug("Injecting field value", "target", target, "field", field.getName(), "value", value);

			if (!optional || value != null) {
				Cls.setFieldValue(target, field.getName(), value);
				processed = true;
			}
		}

		return processed;
	}

	private boolean isInjectOptional(Field field) {
		Wired wired = field.getAnnotation(Wired.class);
		return wired != null && wired.optional();
	}

	private <T> T register(T target, Map<String, Object> properties) {
		U.must(Cls.isAppBean(target), "Not a bean: %s", target);

		if (!isManaged(target)) {
			add(target);
			autowire(target, properties, null, null);

			if (activate.isDone()) {
				invokePostConstruct(target);
			}
		}

		return target;
	}

	private boolean isManaged(Object instance) {
		return state.instances.contains(instance) || state.providedInstances.contains(instance);
	}

	private void add(Object instance) {
		Class<?> clazz = instance.getClass();

		for (Class<?> interfacee : Cls.getImplementedInterfaces(clazz)) {
			addProvider(interfacee, instance);
		}

		addProvider(clazz, instance);
		state.instances.add(instance);
	}

	@Override
	public synchronized boolean remove(Object bean) {
		boolean removedProvided = state.providedInstances.remove(bean);
		boolean removedInstance = state.instances.remove(bean);
		boolean removed = removedProvided || removedInstance;

		if (removed) {
			Class<?> clazz = bean.getClass();
			state.providedClasses.remove(clazz);
			metadata.remove(clazz);

			for (Map.Entry<Class<?>, Set<Object>> e : state.providersByType.entrySet()) {
				Iterator<Object> it = e.getValue().iterator();
				while (it.hasNext()) {
					Object provider = it.next();
					if (Cls.instanceOf(provider, bean.getClass())) {
						it.remove();
					}
				}
			}
		}

		return removed;
	}

	@Override
	public <K, V> Map<K, V> autoExpandingInjectingMap(final Class<V> clazz) {
		return Coll.autoExpandingMap(new Mapper<K, V>() {
			@Override
			public V map(K src) throws Exception {
				return inject(Cls.newInstance(clazz));
			}
		});
	}

	@Override
	public synchronized Object findInstanceOf(String className) {
		for (Object instance : state.providedInstances) {
			if (instance.getClass().getName().equals(className)) {
				return instance;
			}
		}

		for (Map.Entry<Class<?>, Set<Object>> e : state.providersByType.entrySet()) {
			for (Object provider : e.getValue()) {
				if (provider.getClass().getName().equals(className)) {
					return provider;
				}
			}
		}

		return null;
	}

	@Override
	public synchronized IoCContextChanges reload(List<Class<?>> modified, List<String> deleted) {

		ClassLoader classLoader = !U.isEmpty(modified) ? U.last(modified).getClassLoader() : null;

		List<Object> loadedInstances = U.list();
		List<Object> removedInstances = U.list();

		for (String className : deleted) {
			Object bean = findInstanceOf(className);
			if (bean != null) {
				remove(bean);
				removedInstances.add(bean);
			} else {
				Log.warn("Couldn't find the target class to deregister!", "class", className, "context", this);
			}
		}

		if (classLoader != null) {
			List<Object> toRefresh = U.list(state.instances);
			toRefresh.removeAll(state.providedInstances);

			for (Object oldBean : toRefresh) {
				String className = oldBean.getClass().getName();

				remove(oldBean);

				Class<?> cls;
				try {
					cls = classLoader.loadClass(className);
				} catch (ClassNotFoundException e) {
					Log.error("Couldn't find the class to reload!", "class", className);
					continue;
				}

				Object newBean = singleton(cls);

				removedInstances.add(oldBean);
				loadedInstances.add(newBean);
			}
		}

		for (Class<?> cls : modified) {
			Object newBean = singleton(cls);
			loadedInstances.add(newBean);
		}

		return new IoCContextChanges(loadedInstances, removedInstances);
	}

	@Override
	public synchronized Map<String, Object> info() {
		return state.info();
	}

	public synchronized IoCState backup() {
		return state.copy();
	}

	public synchronized void rollback(IoCState backup) {
		this.state = backup;
	}

	@Override
	public void beanProvider(BeanProvider beanProvider) {
		this.beanProvider = beanProvider;
	}

	@Override
	public Set<Object> getManagedInstances() {
		Set<Object> beans = U.set(state.instances);
		beans.addAll(state.providedInstances);
		return Collections.unmodifiableSet(beans);
	}

	@Override
	public Set<Class<?>> getManagedClasses() {
		return Collections.unmodifiableSet(state.providedClasses);
	}

	@Override
	public String toString() {
		return Deep.copyOf(state.instances, Msc.TRANSFORM_TO_SIMPLE_CLASS_NAME).toString();
	}

	@Override
	public void ready() {
		if (activate.go()) {
			onReady();
		}
	}

	private void onReady() {
		for (Object bean : getManagedInstances()) {
			invokePostConstruct(bean);
		}
	}

	private <T> void invokePostConstruct(T target) {
		List<Method> methods = Cls.getMethodsAnnotated(target.getClass(), PostConstruct.class);

		for (Method method : methods) {
			Cls.invoke(method, target);
		}
	}

	void wrapper(IoCContextWrapper wrapper) {
		this.wrapper = wrapper;
	}
}
