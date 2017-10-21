package org.rapidoid.ioc.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ioc.BeanProvider;
import org.rapidoid.ioc.IoCContext;
import org.rapidoid.lambda.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class IoCContextWrapper extends RapidoidThing implements IoCContext {

	private final IoCContextImpl context;

	public IoCContextWrapper(IoCContextImpl context) {
		this.context = context;
		context.wrapper(this);
	}

	@Override
	public IoCContext name(String name) {
		return context.name(name);
	}

	@Override
	public String name() {
		return context.name();
	}

	@Override
	public synchronized void reset() {
		IoCState backup = context.backup();

		try {
			context.reset();
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized void manage(Object... classesOrInstances) {
		IoCState backup = context.backup();

		try {
			context.manage(classesOrInstances);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized <T> T singleton(Class<T> type) {
		IoCState backup = context.backup();

		try {
			return context.singleton(type);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized boolean autowire(Object target) {
		IoCState backup = context.backup();

		try {
			return context.autowire(target);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized <T> T autowire(T target, Mapper<String, Object> session, Mapper<String, Object> bindings) {
		IoCState backup = context.backup();

		try {
			return context.autowire(target, session, bindings);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized <T> T inject(T target) {
		IoCState backup = context.backup();

		try {
			return context.inject(target);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized <T> T inject(T target, Map<String, Object> properties) {
		IoCState backup = context.backup();

		try {
			return context.inject(target, properties);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized boolean remove(Object bean) {
		IoCState backup = context.backup();

		try {
			return context.remove(bean);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized <K, V> Map<K, V> autoExpandingInjectingMap(Class<V> clazz) {
		IoCState backup = context.backup();

		try {
			return context.autoExpandingInjectingMap(clazz);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized Object findInstanceOf(String className) {
		IoCState backup = context.backup();

		try {
			return context.findInstanceOf(className);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized IoCContextChanges reload(List<Class<?>> modified, List<String> deleted) {
		IoCState backup = context.backup();

		try {
			return context.reload(modified, deleted);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized Map<String, Object> info() {
		IoCState backup = context.backup();

		try {
			return context.info();
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized void beanProvider(BeanProvider beanProvider) {
		IoCState backup = context.backup();

		try {
			context.beanProvider(beanProvider);
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized Set<Object> getManagedInstances() {
		IoCState backup = context.backup();

		try {
			return context.getManagedInstances();

		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized Set<Class<?>> getManagedClasses() {
		IoCState backup = context.backup();

		try {
			return context.getManagedClasses();

		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public synchronized void ready() {
		IoCState backup = context.backup();

		try {
			context.ready();
		} catch (RuntimeException e) {
			context.rollback(backup);
			throw e;
		}
	}

	@Override
	public String toString() {
		return context.toString();
	}
}
