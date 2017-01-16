package org.rapidoid.var.impl;

import org.rapidoid.RapidoidThing;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
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
@Since("5.1.0")
public abstract class DecoratorVar<T> extends RapidoidThing implements Var<T> {

	protected final Var<T> var;

	public DecoratorVar(Var<T> var) {
		this.var = var;
	}

	@Override
	public String toString() {
		return var.toString();
	}

	@Override
	public String name() {
		return var.name();
	}

	@Override
	public Set<String> errors() {
		return var.errors();
	}

	@Override
	public T get() {
		return var.get();
	}

	protected void doSet(T value) {
		var.set(value);
	}

	@Override
	public void error(Exception e) {
		var.error(e);
	}

	@Override
	public final void set(T value) {
		try {
			doSet(value);
		} catch (Exception e) {
			error(e);
		}
	}

	@Override
	public Object getRawValue() {
		return var.getRawValue();
	}

}
