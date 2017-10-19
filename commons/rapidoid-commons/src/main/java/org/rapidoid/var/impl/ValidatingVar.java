package org.rapidoid.var.impl;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.lambda.Lmbd;
import org.rapidoid.lambda.Predicate;
import org.rapidoid.log.Log;
import org.rapidoid.var.Var;

import java.util.Set;

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
@Since("5.2.0")
public class ValidatingVar<T> extends DecoratorVar<T> {

	private final Predicate<T> isValid;
	private final String message;

	public ValidatingVar(Var<T> var, Predicate<T> isValid, String message) {
		super(var);
		this.isValid = isValid;
		this.message = message;

		validate(get());
	}

	@Override
	public Set<String> errors() {
		return super.errors();
	}

	@Override
	protected void doSet(T value) {
		var.set(value);
		validate(value);
	}

	private boolean validate(T value) {
		boolean valid;

		try {
			valid = Lmbd.eval(isValid, value);

		} catch (Exception e) {
			Log.error("Validator failed!", e);
			errors().add("Invalid value!");
			return false;
		}

		if (!valid) errors().add(message);

		return valid;
	}

}
