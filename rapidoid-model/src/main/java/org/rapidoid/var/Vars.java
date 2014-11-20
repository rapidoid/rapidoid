package org.rapidoid.reactive;

import org.rapidoid.reactive.var.ContainerVar;
import org.rapidoid.reactive.var.EqualityVar;
import org.rapidoid.reactive.var.SimpleVar;

/*
 * #%L
 * rapidoid-reactive
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

public class Vars {

	public static <T> Var<T> var(T value) {
		return new SimpleVar<T>(value);
	}

	@SuppressWarnings("unchecked")
	public static <T> Var<T>[] vars(T... values) {
		Var<T>[] vars = new Var[values.length];

		for (int i = 0; i < vars.length; i++) {
			vars[i] = var(values[i]);
		}

		return vars;
	}

	@SuppressWarnings("unchecked")
	public static Var<Boolean> eq(Var<?> var, Object value) {
		return new EqualityVar((Var<Object>) var, value);
	}

	@SuppressWarnings("unchecked")
	public static Var<Boolean> has(Var<?> container, Object item) {
		return new ContainerVar((Var<Object>) container, item);
	}

}
