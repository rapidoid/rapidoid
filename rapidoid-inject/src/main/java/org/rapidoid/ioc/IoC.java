package org.rapidoid.ioc;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.util.Map;

/*
 * #%L
 * rapidoid-inject
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

@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class IoC extends RapidoidThing {

	private static final IoCContext DEFAULT_CONTEXT = createContext();

	public static IoCContext defaultContext() {
		return DEFAULT_CONTEXT;
	}

	public static void manage(Object... classesOrInstances) {
		DEFAULT_CONTEXT.manage(classesOrInstances);
	}

	public static <T> T singleton(Class<T> type) {
		return DEFAULT_CONTEXT.singleton(type);
	}

	public static <T> T autowire(T target) {
		return DEFAULT_CONTEXT.autowire(target);
	}

	public static <T> T inject(T target) {
		return DEFAULT_CONTEXT.inject(target);
	}

	public static <T> T inject(T target, Map<String, Object> properties) {
		return DEFAULT_CONTEXT.inject(target, properties);
	}

	public boolean remove(Object bean) {
		return DEFAULT_CONTEXT.remove(bean);
	}

	public static IoCContext createContext() {
		return new IoCContextWrapper(new IoCContextImpl());
	}

	public static void reset() {
		defaultContext().reset();
	}
}
