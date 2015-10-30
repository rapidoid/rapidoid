package org.rapidoid.http;

/*
 * #%L
 * rapidoid-http
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

import java.util.Map;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.cls.Cls;
import org.rapidoid.u.U;

@Authors("Nikolche Mihajlovski")
@Since("4.0.0")
public class Scopes {

	@SuppressWarnings("unchecked")
	public static <T> T get(String scopeName, Map<String, ?> scope, String name, T defaultValue) {
		T val = (T) (scope != null ? scope.get(name) : null);
		return U.or(val, defaultValue);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(String scopeName, Map<String, ?> scope, String name) {
		T value = (T) (scope != null ? scope.get(name) : null);

		if (value == null) {
			throw U.rte("The attribute '%s' in scope '%s' must NOT be null!", name, scopeName);
		}

		return value;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getOrCreate(String scopeName, Map<String, ?> scope, String name, Class<T> valueClass,
			Object... constructorArgs) {

		T value = (T) scope.get(name);

		if (value == null) {
			synchronized (scope) {
				value = (T) scope.get(name);

				if (value == null) {
					value = Cls.newInstance(valueClass, constructorArgs);
					((Map<String, Object>) scope).put(name, (Object) value);
				}
			}
		}

		return value;
	}

}
