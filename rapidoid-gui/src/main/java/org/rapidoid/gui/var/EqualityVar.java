package org.rapidoid.gui.var;

/*
 * #%L
 * rapidoid-gui
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
import org.rapidoid.u.U;
import org.rapidoid.var.Var;

@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class EqualityVar extends WidgetVar<Boolean> {

	private static final long serialVersionUID = 6990464844550633598L;

	private final Var<Object> var;

	private final Object val;

	public EqualityVar(String name, Var<Object> var, Object val, boolean initial) {
		super(name, initial);
		this.var = var;
		this.val = val;
		init();
	}

	private void init() {
		if (!initial) {
			set(getBool());
		}
	}

	@Override
	public Boolean get() {
		return U.eq(var.get(), val);
	}

	@Override
	public void doSet(Boolean value) {
		if (value) {
			var.set(val);
		} else {
			if (U.eq(var.get(), val)) {
				var.set(null);
			}
		}
	}

}
