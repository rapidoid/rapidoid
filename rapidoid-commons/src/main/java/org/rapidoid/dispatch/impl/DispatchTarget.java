package org.rapidoid.dispatch.impl;

/*
 * #%L
 * rapidoid-dispatch
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

import java.lang.reflect.Method;
import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DispatchTarget {

	final Class<?> clazz;

	final Method method;

	final Map<String, Object> config;

	public DispatchTarget(Class<?> clazz, Method method, Map<String, Object> config) {
		this.clazz = clazz;
		this.method = method;
		this.config = config;
	}

	@Override
	public String toString() {
		return "DispatchTarget [clazz=" + clazz + ", method=" + method + ", config=" + config + "]";
	}

}
