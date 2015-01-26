package org.rapidoid.var.impl;

import org.rapidoid.util.ImportExport;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;

/*
 * #%L
 * rapidoid-utils
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

public class MandatoryVar<T> extends AbstractVar<T> {

	private static final long serialVersionUID = 7970150705828178233L;

	private final Var<T> var;

	public MandatoryVar(ImportExport props) {
		var = props.get(A);
	}

	public MandatoryVar(Var<T> var) {
		this.var = var;
	}

	@Override
	public T get() {
		return var.get();
	}

	@Override
	public void set(T value) {
		U.must(!U.isEmpty(value), "Non-empty value is required!");
		var.set(value);
	}

	@Override
	public void exportTo(ImportExport props) {
		props.put(A, var);
	}

}
