package org.rapidoid.pojo.impl;

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

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;

import java.lang.reflect.Method;
import java.util.Map;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DispatchTarget {

	final Object controller;

	final Method method;

	final Map<String, Object> config;

	public DispatchTarget(Object controller, Method method, Map<String, Object> config) {
		this.controller = controller;
		this.method = method;
		this.config = config;
	}

	@Override
	public String toString() {
		return "DispatchTarget [controller=" + controller + ", method=" + method + ", config=" + config + "]";
	}

}
