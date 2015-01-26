package org.rapidoid.var.impl;

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

import org.rapidoid.util.ImportExport;
import org.rapidoid.util.U;
import org.rapidoid.var.Var;

public class EqualityVar extends AbstractVar<Boolean> {

	private static final long serialVersionUID = 6990464844550633598L;

	private final Var<Object> var;

	private final Object val;

	public EqualityVar(ImportExport props) {
		var = props.get(A);
		val = props.get(B);
	}

	public EqualityVar(Var<Object> var, Object val) {
		this.var = var;
		this.val = val;
	}

	@Override
	public Boolean get() {
		return U.eq(var.get(), val);
	}

	@Override
	public void set(Boolean value) {
		if (value) {
			var.set(val);
		} else {
			if (U.eq(var.get(), val)) {
				var.set(null);
			}
		}
	}

	@Override
	public void exportTo(ImportExport props) {
		props.put(A, var);
		props.put(B, val);
	}

}
