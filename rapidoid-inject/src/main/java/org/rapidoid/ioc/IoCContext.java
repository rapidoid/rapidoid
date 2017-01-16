package org.rapidoid.ioc;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.ioc.impl.IoCContextChanges;
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
public interface IoCContext {

	IoCContext name(String name);

	String name();

	void reset();

	void manage(Object... classesOrInstances);

	<T> T singleton(Class<T> type);

	boolean autowire(Object target);

	<T> T autowire(T target, Mapper<String, Object> session, Mapper<String, Object> bindings);

	<T> T inject(T target);

	<T> T inject(T target, Map<String, Object> properties);

	boolean remove(Object bean);

	<K, V> Map<K, V> autoExpandingInjectingMap(Class<V> clazz);

	Object findInstanceOf(String className);

	IoCContextChanges reload(List<Class<?>> modified, List<String> deleted);

	Map<String, Object> info();

	void beanProvider(BeanProvider beanProvider);

	Set<Object> getManagedInstances();

	Set<Class<?>> getManagedClasses();

}
