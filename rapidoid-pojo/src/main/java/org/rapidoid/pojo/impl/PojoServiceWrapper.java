package org.rapidoid.pojo.impl;

/*
 * #%L
 * rapidoid-pojo
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.rapidoid.util.U;

public class PojoServiceWrapper {

	private final Map<String, Method> methods = U.map();

	private final Object service;

	public PojoServiceWrapper(Object service) {
		this.service = service;
	}

	public void init() {
		for (Method method : service.getClass().getMethods()) {
			if (!method.getDeclaringClass().equals(Object.class)) {
				int modifiers = method.getModifiers();
				if (!Modifier.isAbstract(modifiers) && Modifier.isPublic(modifiers)) {
					methods.put(method.getName(), method);
					U.info("Registered web handler method: " + method);
				}
			}
		}

	}

	public Method getMethod(String action) {
		return methods.get(action);
	}

	public Object getTarget() {
		return service;
	}

}
