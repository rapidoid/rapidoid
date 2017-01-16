package org.rapidoid.var;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.var.impl.MandatoryVar;
import org.rapidoid.var.impl.SimpleVar;
import org.rapidoid.var.impl.ValidatingVar;

/*
 * #%L
 * rapidoid-commons
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
@Since("2.0.0")
public class Vars extends RapidoidThing {

	public static <T> Var<T> var(String name, T value) {
		return new SimpleVar<T>(name, value);
	}

	@SuppressWarnings("unchecked")
	public static <T> Var<T>[] vars(String name, T... values) {
		Var<T>[] vars = new Var[values.length];

		for (int i = 0; i < vars.length; i++) {
			vars[i] = var(name + "[" + i + "]", values[i]);
		}

		return vars;
	}

	public static <T> Var<T> mandatory(Var<T> var) {
		return new MandatoryVar<T>(var);
	}

	@SuppressWarnings("unchecked")
	public static <T> T unwrap(T value) {
		return (value instanceof Var) ? (T) ((Var<?>) value).get() : value;
	}

	@SuppressWarnings("unchecked")
	public static <T> Var<T> cast(Object value) {
		return (Var<T>) value;
	}

	public static <T> Var<T> validate(Var<T> var, Predicate<T> isValid, String message) {
		return new ValidatingVar<T>(var, isValid, message);
	}

}
